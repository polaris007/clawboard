# 优化效果示例

## 场景说明

当OpenClaw session transcript中出现"list index out of range"错误时，优化后的脚本会在报告中显示**原始的完整JSON行记录**（从文件中直接读取的原始字符串，而非重新序列化后的内容），便于调试和问题定位。

**核心改进**：
- ✓ 保持原始格式不变（空格、缩进、键顺序等）
- ✓ 避免JSON序列化可能带来的差异
- ✓ 可以直接与原始文件进行精确对比

## 示例对比

### 优化前的报告

```markdown
### 问题 #5

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **姓名**: 鹿宽
- **工号**: 11001640
- **部门**: 产品部
- **用户输入**: `[Fri 2026-04-17 10:53 GMT+8] 刚刚我把二号文件 - 副本.csv放到了./2026-04-17目录下。我的需求是： 该数据`
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `/datafs/openclaw/.../sessions/bcb09ec8-73c3-492f-bce2-ac6043ccaf9f.jsonl`
- **Session ID**: `bcb09ec8-73c3-492f-bce2-ac6043ccaf9f`
- **行号**: 81
- **时间戳**: 2026-04-17T02:56:46.078Z
```

**问题**：只知道有错误，但无法看到完整的上下文信息，难以定位根本原因。

---

### 优化后的报告

```markdown
### 问题 #5

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **姓名**: 鹿宽
- **工号**: 11001640
- **部门**: 产品部
- **用户输入**: `[Fri 2026-04-17 10:53 GMT+8] 刚刚我把二号文件 - 副本.csv放到了./2026-04-17目录下。我的需求是： 该数据`
- **错误信息**: 
````
list index out of range
````
- **完整行记录**: 
```json
{"type":"message","id":"msg_abc123","parentId":"parent_xyz","timestamp":"2026-04-17T02:56:46.078Z","message":{"role":"assistant","content":[{"type":"text","text":"正在处理数据..."}],"errorMessage":"list index out of range","stopReason":"error","provider":"my-qwen-provider","model":"qwen35-397b-claw-normal","usage":{"promptTokens":1234,"completionTokens":567}}}
```

**注意**：上面显示的是原始文件中的JSON字符串，保持了原始的格式、空格和键顺序，没有任何修改。
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `/datafs/openclaw/.../sessions/bcb09ec8-73c3-492f-bce2-ac6043ccaf9f.jsonl`
- **Session ID**: `bcb09ec8-73c3-492f-bce2-ac6043ccaf9f`
- **行号**: 81
- **时间戳**: 2026-04-17T02:56:46.078Z
```

**优势**：
✓ 可以看到完整的消息结构
✓ 包含message ID和parent ID，便于追踪消息链
✓ 显示provider和model信息
✓ 包含token使用统计
✓ 可以看到content内容，了解错误发生时的上下文
✓ 所有信息都在一个地方，无需再查看原始文件

---

## 实际应用场景

### 场景1：调试Python代码中的索引错误

当Assistant执行Python代码时出现"list index out of range"，完整行记录可以显示：
- 执行的代码内容
- 使用的模型和provider
- Token使用情况
- 消息的父子关系

### 场景2：分析并发问题

通过完整行记录中的timestamp和id，可以：
- 精确排序事件发生顺序
- 识别可能的竞态条件
- 追踪消息的处理链路

### 场景3：性能分析

从完整记录中可以看到：
- promptTokens和completionTokens
- 模型响应时间（通过timestamp计算）
- 不同模型的错误率对比

---

## 技术细节

### 触发条件

仅当错误信息（errorMessage或data.error）**精确包含** "list index out of range" 字符串时才会显示原始完整行记录。

### 实现方式

#### 步骤1: 解析时保存原始行
```python
# 在 analyze_transcript() 函数中
for i, line in enumerate(lines):
    event = json.loads(line)
    messages.append({
        'lineNum': i + 1,
        'event': event,
        'rawLine': line,  # 保存原始行内容
    })
```

#### 步骤2: 检测时使用原始行
```python
# 在检测函数中
if 'list index out of range' in error_message.lower():
    issue['fullLineRecord'] = msg.get('rawLine', '')  # 使用原始行，不是json.dumps()
```

### 报告渲染

```python
# 在generate_markdown_report函数中
if issue.get('fullLineRecord'):
    markdown += '- **完整行记录**: \n```json\n%s\n```\n' % issue['fullLineRecord']
```

使用Markdown的JSON代码块语法，确保：
- 正确的语法高亮
- 良好的可读性
- 方便复制粘贴

---

## 注意事项

1. **性能影响**：仅在特定错误时添加额外字段，对正常流程无影响
2. **报告大小**：完整行记录会增加报告大小，但仅在必要时显示
3. **隐私考虑**：完整记录可能包含敏感信息，分享报告时需注意
4. **兼容性**：完全向后兼容，旧版本的报告阅读器会忽略新字段

---

## 未来扩展

可以考虑对其他类型的错误也显示完整行记录，例如：
- JSON解析错误
- 网络超时错误
- 权限错误

只需修改触发条件即可：

```python
# 扩展示例：对多种错误类型显示完整记录
critical_errors = ['list index out of range', 'json parse error', 'connection timeout']
if any(err in error_message.lower() for err in critical_errors):
    issue['fullLineRecord'] = json.dumps(event, ensure_ascii=False)
```
