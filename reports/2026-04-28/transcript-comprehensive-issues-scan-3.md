# OpenClaw Session Transcript 综合错误检测报告

**时间范围**: Scan ID: 3

**生成时间**: 2026-04-28T22:14:11.967Z

## 📊 统计概览

- **总错误数**: 2
- **总对话轮数**: 13 （排除系统消息）
- **有错误轮数**: 2 （存在任何类型错误的轮次）
- **错误率**: 15.38% （有错误轮数 / 总对话轮数）

### 错误类型分布

| 错误类型 | 数量 | 说明 |
|---------|------|------|
| abnormal_stop | 2 | 异常停止 |

---

## abnormal_stop - 异常停止 (2)

### 错误 #1

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100732
- **姓名**: 牛伟峰
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Mon 2026-04-13 15:42 GMT+8] 使用imap-smtp-email技能，帮我查询邮箱中最新的10封邮件，帮我列出查询结果`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54128 input tokens (16384 > 65536 - 54128). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `G:\Workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\44b23a7e-471e-4d06-b7d3-9c354e67b2f9.jsonl.reset.2026-04-13T07-43-22.366Z`
- **Session ID**: `44b23a7e-471e-4d06-b7d3-9c354e67b2f9`
- **行号**: 104
- **时间戳**: 2026-04-13T15:42:15.399

---

### 错误 #2

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100732
- **姓名**: 牛伟峰
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Mon 2026-04-13 15:42 GMT+8] 使用imap-smtp-email技能，帮我查询邮箱中最新的10封邮件，帮我列出查询结果`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54307 input tokens (16384 > 65536 - 54307). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `G:\Workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\44b23a7e-471e-4d06-b7d3-9c354e67b2f9.jsonl.reset.2026-04-13T07-43-22.366Z`
- **Session ID**: `44b23a7e-471e-4d06-b7d3-9c354e67b2f9`
- **行号**: 107
- **时间戳**: 2026-04-13T15:42:35.657

---

