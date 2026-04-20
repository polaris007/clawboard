# 快速参考指南

## 🎯 核心功能

当检测到"list index out of range"错误时，自动在报告中显示**原始完整行记录**。

---

## 📋 使用方法

### 基本用法
```bash
python detect-all-transcript-issues.py [transcript_dir]
```

### 带时间范围
```bash
python detect-all-transcript-issues.py --start 2026-04-01 --end 2026-04-15
```

### 指定目录和时间
```bash
python detect-all-transcript-issues.py logs/session-transcript/openclaw-logs --start 2026-04-10
```

---

## 🔍 触发条件

仅当错误信息包含以下字符串时（不区分大小写）：
```
list index out of range
```

---

## 📊 报告示例

### 在Markdown报告中查找
搜索关键词：`完整行记录`

### 显示格式
```markdown
- **完整行记录**: 
```json
{"type":"message","id":"xxx",...}  // 原始JSON字符串
```
```

---

## 💡 关键改进点

| 项目 | 说明 |
|------|------|
| **数据来源** | session transcript文件中的原始行 |
| **保存位置** | `msg['rawLine']` |
| **使用方式** | `issue['fullLineRecord'] = msg.get('rawLine', '')` |
| **避免操作** | ❌ 不使用 `json.dumps()` 重新序列化 |

---

## 🔧 代码位置

### 主要修改点

1. **数据收集** - `analyze_transcript()` 第791行
   ```python
   'rawLine': line,  # 保存原始行内容
   ```

2. **custom事件检测** - `detect_known_errors()` 第653行
   ```python
   issue['fullLineRecord'] = msg.get('rawLine', '')
   ```

3. **message事件检测** - `detect_known_errors()` 第693行
   ```python
   issue['fullLineRecord'] = msg.get('rawLine', '')
   ```

4. **异常停止检测** - `detect_abnormal_stops()` 第737行
   ```python
   issue['fullLineRecord'] = msg.get('rawLine', '')
   ```

---

## ✅ 验证方法

### 1. 运行测试脚本
```bash
python test_list_index_error.py
```

### 2. 检查输出
应该看到：
```
✓ 包含完整行记录
✓ 完整行记录类型: str (或 unicode)
✓ 完整行记录是有效的JSON
```

### 3. 查看生成的报告
打开 `transcript-comprehensive-issues.md`，搜索"完整行记录"

---

## 🐛 常见问题

### Q: 为什么有些错误没有显示完整行记录？
A: 只有错误信息包含"list index out of range"时才会显示。

### Q: 完整行记录和解析后的event有什么区别？
A: 
- **完整行记录**：文件中的原始字符串，100%准确
- **解析后的event**：Python字典对象，可能丢失格式细节

### Q: 可以扩展到其他错误类型吗？
A: 可以，修改触发条件即可：
```python
if any(err in error_message.lower() for err in ['list index out of range', 'other error']):
    issue['fullLineRecord'] = msg.get('rawLine', '')
```

---

## 📚 相关文档

- [OPTIMIZATION_SUMMARY.md](OPTIMIZATION_SUMMARY.md) - 优化完成总结
- [OPTIMIZATION_NOTES.md](OPTIMIZATION_NOTES.md) - 详细优化说明
- [OPTIMIZATION_EXAMPLE.md](OPTIMIZATION_EXAMPLE.md) - 效果示例对比

---

## 🚀 快速开始

1. 确保Python环境就绪（Python 2.7+）
2. 准备session transcript文件
3. 运行脚本
4. 查看生成的报告
5. 搜索"完整行记录"查看详细信息

就是这么简单！✨
