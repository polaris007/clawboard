# detect-all-transcript-issues.py 优化说明

## 优化内容

### 功能增强：显示原始完整行记录

当检测到错误信息中包含 **"list index out of range"** 时，脚本会在生成的报告中额外显示该行的**原始JSON字符串**（从session transcript文件中直接读取的原始内容，而非重新序列化后的内容）。

**关键优势**：
- ✓ 保持原始格式不变（空格、缩进、顺序等）
- ✓ 避免JSON序列化可能带来的差异
- ✓ 便于与原始文件进行精确对比
- ✓ 保留所有原始细节

## 修改详情

### 1. 数据收集阶段增强

#### `analyze_transcript()` - 分析transcript文件
在解析每一行时保存原始行内容：
```python
messages.append({
    'lineNum': i + 1,
    'event': event,
    'rawLine': line,  # 保存原始行内容
})
```

### 2. 检测逻辑增强

在以下三个检测函数中添加了完整行记录的捕获逻辑：

#### `detect_known_errors()` - 已知错误模式检测
- 检测custom事件中的错误
- 检测message事件中的错误（assistant角色）
- 当错误信息包含"list index out of range"时，添加`fullLineRecord`字段

#### `detect_abnormal_stops()` - 异常停止检测
- 检测异常的stopReason
- 当errorMessage包含"list index out of range"时，添加`fullLineRecord`字段

### 3. 报告生成增强

#### `generate_markdown_report()` - Markdown报告生成
在生成每个问题的详细信息时：
```python
# 如果存在完整行记录（针对list index out of range错误），显示它
if issue.get('fullLineRecord'):
    markdown += '- **完整行记录**: \n```json\n%s\n```\n' % issue['fullLineRecord']
```

## 使用效果

### 优化前
```markdown
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败...
```

### 优化后
```markdown
- **错误信息**: 
````
list index out of range
````
- **完整行记录**: 
```json
{"type":"message","id":"xxx","timestamp":"2026-04-17T02:56:46.078Z","message":{"role":"assistant","errorMessage":"list index out of range",...}}
```
- **原因分析**: 模型API调用失败...
```

## 技术实现

### 关键代码片段

#### 步骤1: 保存原始行内容
```python
# 在 analyze_transcript() 函数中
for i, line in enumerate(lines):
    try:
        event = json.loads(line)
        if event.get('type') in ['message', 'custom']:
            messages.append({
                'lineNum': i + 1,
                'event': event,
                'rawLine': line,  # 保存原始行内容
            })
```

#### 步骤2: 在检测时使用原始行
```python
# 在检测函数中
issue = {
    'id': generate_id(),
    'errorType': category,
    # ... 其他字段 ...
}

# 如果错误信息包含"list index out of range"，添加原始的完整行记录
if 'list index out of range' in error_message.lower():
    issue['fullLineRecord'] = msg.get('rawLine', '')  # 使用原始行，不是json.dumps()

issues.append(issue)
```

### 优势

1. **原始性保证**：保存的是文件中的原始字符串，没有任何修改
2. **调试友好**：可以直接与原始文件进行对比，无需担心序列化差异
3. **上下文完整**：保留了事件的所有元数据和原始格式
4. **格式规范**：使用JSON代码块格式化，便于阅读和复制
5. **条件触发**：仅对特定错误类型显示，避免报告过于冗长
6. **性能优化**：避免了不必要的json.dumps()调用

## 测试

提供了测试脚本 `test_list_index_error.py` 用于验证功能：

```bash
python test_list_index_error.py
```

测试内容包括：
- ✓ 检测known errors中的list index out of range
- ✓ 检测abnormal stops中的list index out of range  
- ✓ 验证完整行记录的JSON有效性
- ✓ 验证Markdown报告中正确显示完整行记录

## 兼容性

- 完全向后兼容，不影响现有功能
- 对其他类型的错误没有影响
- Python 2.7 兼容（使用basestring而非str）

## 注意事项

1. 完整行记录可能会比较长，但在JSON代码块中会保持良好的可读性
2. 仅在确实需要时才显示（list index out of range错误）
3. 使用`ensure_ascii=False`确保中文等非ASCII字符正确显示
