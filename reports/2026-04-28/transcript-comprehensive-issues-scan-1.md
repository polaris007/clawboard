# OpenClaw Session Transcript 综合错误检测报告

**时间范围**: Scan ID: 1

**生成时间**: 2026-04-28T13:58:41.026Z

## 📊 统计概览

- **总错误数**: 122
- **总对话轮数**: 546 （排除系统消息）
- **有错误轮数**: 58 （存在任何类型错误的轮次）
- **错误率**: 10.62% （有错误轮数 / 总对话轮数）

### 错误类型分布

| 错误类型 | 数量 | 说明 |
|---------|------|------|
| abnormal_stop | 88 | 异常停止 |
| timeoutErrors | 22 | 超时错误 |
| modelErrors | 12 | 模型API错误 |

---

## abnormal_stop - 异常停止 (88)

### 错误 #1

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `System: [2026-03-25 05:23:05 UTC] Gateway restart restart ok (gateway.restart)
System: Fixed MCP server configuration - corrected 'transpor' typo and moved to mcp.servers section
System: Run: openclaw...`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 6
- **时间戳**: 2026-03-25T13:25:06.530

---

### 错误 #2

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `System: [2026-03-25 05:23:05 UTC] Gateway restart restart ok (gateway.restart)
System: Fixed MCP server configuration - corrected 'transpor' typo and moved to mcp.servers section
System: Run: openclaw...`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 7
- **时间戳**: 2026-03-25T13:25:10.162

---

### 错误 #3

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `System: [2026-03-25 05:23:05 UTC] Gateway restart restart ok (gateway.restart)
System: Fixed MCP server configuration - corrected 'transpor' typo and moved to mcp.servers section
System: Run: openclaw...`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 8
- **时间戳**: 2026-03-25T13:25:15.519

---

### 错误 #4

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `System: [2026-03-25 05:23:05 UTC] Gateway restart restart ok (gateway.restart)
System: Fixed MCP server configuration - corrected 'transpor' typo and moved to mcp.servers section
System: Run: openclaw...`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 9
- **时间戳**: 2026-03-25T13:25:25.002

---

### 错误 #5

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-03-25 05:25 UTC] 你好`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 11
- **时间戳**: 2026-03-25T13:25:48.230

---

### 错误 #6

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-03-25 05:25 UTC] 你好`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 12
- **时间戳**: 2026-03-25T13:25:51.608

---

### 错误 #7

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-03-25 05:25 UTC] 你好`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 13
- **时间戳**: 2026-03-25T13:25:57.208

---

### 错误 #8

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-03-25 05:25 UTC] 你好`
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 14
- **时间戳**: 2026-03-25T13:26:06.675

---

### 错误 #9

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Thu 2026-03-26 01:52 UTC] CLAWHUB_SITE=http://10.38.0.203:32476 CLAWHUB_REGISTRY=http://1...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57554 input tokens (8192 > 65536 - 57554). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 244
- **时间戳**: 2026-03-26T09:53:01.352

---

### 错误 #10

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Thu 2026-03-26 02:00 UTC] nihao`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57852 input tokens (8192 > 65536 - 57852). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 246
- **时间戳**: 2026-03-26T10:00:40.310

---

### 错误 #11

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-26.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58165 input tokens (8192 > 65536 - 58165). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 248
- **时间戳**: 2026-03-26T10:01:46.212

---

### 错误 #12

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Thu 2026-03-26 02:01 UTC] nihao`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57910 input tokens (8192 > 65536 - 57910). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 250
- **时间戳**: 2026-03-26T10:01:46.640

---

### 错误 #13

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `System: [2026-03-30 11:28:31 UTC] [Post-compaction context refresh]
System: 
System: Session was just compacted. The conversation summary above is a hint, NOT a substitute for your startup sequence. E...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57546 input tokens (8192 > 65536 - 57546). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 249
- **时间戳**: 2026-03-30T19:59:24.039

---

### 错误 #14

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58049 input tokens (8192 > 65536 - 58049). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 251
- **时间戳**: 2026-03-30T20:00:40.178

---

### 错误 #15

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `System: [2026-03-30 11:59:24 UTC] [Post-compaction context refresh]
System: 
System: Session was just compacted. The conversation summary above is a hint, NOT a substitute for your startup sequence. E...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57568 input tokens (8192 > 65536 - 57568). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 269
- **时间戳**: 2026-03-30T20:02:07.417

---

### 错误 #16

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58542 input tokens (8192 > 65536 - 58542). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 271
- **时间戳**: 2026-03-30T20:36:20.820

---

### 错误 #17

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `System: [2026-03-30 12:02:07 UTC] [Post-compaction context refresh]
System: 
System: Session was just compacted. The conversation summary above is a hint, NOT a substitute for your startup sequence. E...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58834 input tokens (8192 > 65536 - 58834). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 273
- **时间戳**: 2026-03-30T20:36:21.257

---

### 错误 #18

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Tue 2026-03-31 09:23 UTC] 根据上面发送的信息 构建一整个技能`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57883 input tokens (8192 > 65536 - 57883). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\5a7e6f9d-4c43-4a9a-820e-5ba304317da6.jsonl`
- **Session ID**: `5a7e6f9d-4c43-4a9a-820e-5ba304317da6`
- **行号**: 121
- **时间戳**: 2026-03-31T17:25:55.543

---

### 错误 #19

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-31.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58180 input tokens (8192 > 65536 - 58180). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\5a7e6f9d-4c43-4a9a-820e-5ba304317da6.jsonl`
- **Session ID**: `5a7e6f9d-4c43-4a9a-820e-5ba304317da6`
- **行号**: 123
- **时间戳**: 2026-03-31T17:26:11.102

---

### 错误 #20

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **用户输入**: `System: [2026-03-31 09:24:04 UTC] [Post-compaction context refresh]
System: 
System: Session was just compacted. The conversation summary above is a hint, NOT a substitute for your startup sequence. E...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58297 input tokens (8192 > 65536 - 58297). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\5a7e6f9d-4c43-4a9a-820e-5ba304317da6.jsonl`
- **Session ID**: `5a7e6f9d-4c43-4a9a-820e-5ba304317da6`
- **行号**: 125
- **时间戳**: 2026-03-31T17:26:11.408

---

### 错误 #21

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100988
- **姓名**: 冯丽媛
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:21 GMT+8] 你现在分析完了吗`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54822 input tokens (16384 > 65536 - 54822). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\55b3dbad-7082-44c9-8556-9346043c798d.jsonl`
- **Session ID**: `55b3dbad-7082-44c9-8556-9346043c798d`
- **行号**: 38
- **时间戳**: 2026-04-03T14:22:54.695

---

### 错误 #22

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: aborted
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:22 GMT+8] 上传到哪儿了 我没有看到啊`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 94
- **时间戳**: 2026-04-03T14:23:37.240

---

### 错误 #23

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:45 GMT+8] 使用pptx技能； 处理到哪儿了`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 73149 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=73149)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 144
- **时间戳**: 2026-04-03T14:45:37.736

---

### 错误 #24

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100988
- **姓名**: 冯丽媛
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-03 14:50 GMT+8] 你完成了吗`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54850 input tokens (16384 > 65536 - 54850). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\55b3dbad-7082-44c9-8556-9346043c798d.jsonl`
- **Session ID**: `55b3dbad-7082-44c9-8556-9346043c798d`
- **行号**: 40
- **时间戳**: 2026-04-03T14:50:47.215

---

### 错误 #25

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 15:01 GMT+8] 再看看进度`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 73204 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=73204)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 146
- **时间戳**: 2026-04-03T15:01:01.727

---

### 错误 #26

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 15:13 GMT+8] 使用km-operation-prod技能，地址为km.clic； 工作区找一个文件 上传到个人知识库`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 73462 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=73462)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 148
- **时间戳**: 2026-04-03T15:13:44.202

---

### 错误 #27

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100988
- **姓名**: 冯丽媛
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-03 15:18 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54920 input tokens (16384 > 65536 - 54920). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\55b3dbad-7082-44c9-8556-9346043c798d.jsonl`
- **Session ID**: `55b3dbad-7082-44c9-8556-9346043c798d`
- **行号**: 42
- **时间戳**: 2026-04-03T15:18:27.167

---

### 错误 #28

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 17:15 GMT+8] 这个技能是不是会有安全问题 如果随便用别人的工号 是不是也可以获取别人知识库的文件 下载 或者是上传 你觉得应该如何规避这个问题`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50053 input tokens (16384 > 65536 - 50053). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\21c20430-e74b-4ea9-8370-5b818e07807f.jsonl`
- **Session ID**: `21c20430-e74b-4ea9-8370-5b818e07807f`
- **行号**: 116
- **时间戳**: 2026-04-03T17:15:16.675

---

### 错误 #29

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 17:17 GMT+8] 使用km-operation-prod技能，地址为km.clic； 分析一下，这个技能如何做到用户的鉴权隔离。如何让用户只能上传自己的个人知识库`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50321 input tokens (16384 > 65536 - 50321). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\21c20430-e74b-4ea9-8370-5b818e07807f.jsonl`
- **Session ID**: `21c20430-e74b-4ea9-8370-5b818e07807f`
- **行号**: 118
- **时间戳**: 2026-04-03T17:17:19.115

---

### 错误 #30

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 17:32 GMT+8] 使用km-operation-prod技能，地址为km.clic； 分析一下，这个技能如何做到用户的鉴权隔离。如何让用户只能上传自己的个人知识库`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50589 input tokens (16384 > 65536 - 50589). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\21c20430-e74b-4ea9-8370-5b818e07807f.jsonl`
- **Session ID**: `21c20430-e74b-4ea9-8370-5b818e07807f`
- **行号**: 120
- **时间戳**: 2026-04-03T17:32:46.144

---

### 错误 #31

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100799
- **姓名**: 费远
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Tue 2026-04-07 10:07 GMT+8] 请帮我用skill-vetter，扫描一下当前所有skill`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53947 input tokens (16384 > 65536 - 53947). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\d29befe28f6d1a440997a29a90d297a20ea5c6b0effffb94967c082745c678e4b4efc8f29e2c81246b38e206e09c98183755650d5db5fc6f525f9d8928b67e24\agents\main\sessions\452b6522-ab61-4cb5-9e12-993c22302827.jsonl`
- **Session ID**: `452b6522-ab61-4cb5-9e12-993c22302827`
- **行号**: 38
- **时间戳**: 2026-04-07T10:07:42.006

---

### 错误 #32

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100937
- **姓名**: 李一鸣
- **部门**: 18100000
- **用户输入**: `[Mon 2026-04-13 10:15 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 使用正确的 Base URL 调用 pol...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 93398 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=93398)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\fa65c292bbe559bed0e01c9bf4ab7206fea633ad01ff275cb3653e15b8eeb8392f5c407ef79ffee6a7cc913e6e645c52665f51d413888ea1d0a0d252182dc6a8\agents\main\sessions\8a7a7bbf-e23c-4c9c-bee2-e3b0d0f793dd.jsonl`
- **Session ID**: `8a7a7bbf-e23c-4c9c-bee2-e3b0d0f793dd`
- **行号**: 38
- **时间戳**: 2026-04-13T10:18:16.605

---

### 错误 #33

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101142
- **用户输入**: `System: [2026-04-13 14:13:23 GMT+8] Gateway restart restart ok (gateway.restart)
System: 配置已更新，baseUrl 已指向集群内网 LiteLLM 服务（http://litellm-service.langfuse.svc.cluster.local:4000/v1），Gateway 正在重启...
Sys...`
- **错误信息**: 
````
400 {'error': '/chat/completions: Invalid model name passed in model=AIAPLLM-vision-nothink. Call `/v1/models` to view available models for your key.'}
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\6fca9aa611cf469e15161f2b342062f7c621c962e44d14a57ee1d61d972f9135cd6f8797feb2302283695088f655118edd65a6768f2159207fd01f575a80e207\agents\main\sessions\ecf6d23a-a5ba-4838-a8bc-de4291d68a48.jsonl`
- **Session ID**: `ecf6d23a-a5ba-4838-a8bc-de4291d68a48`
- **行号**: 40
- **时间戳**: 2026-04-13T14:13:30.391

---

### 错误 #34

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101142
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 14:14 GMT+8] hi，请输出当前的json配置。`
- **错误信息**: 
````
400 {'error': '/chat/completions: Invalid model name passed in model=AIAPLLM-vision-nothink. Call `/v1/models` to view available models for your key.'}
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\6fca9aa611cf469e15161f2b342062f7c621c962e44d14a57ee1d61d972f9135cd6f8797feb2302283695088f655118edd65a6768f2159207fd01f575a80e207\agents\main\sessions\ecf6d23a-a5ba-4838-a8bc-de4291d68a48.jsonl`
- **Session ID**: `ecf6d23a-a5ba-4838-a8bc-de4291d68a48`
- **行号**: 42
- **时间戳**: 2026-04-13T14:14:13.521

---

### 错误 #35

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
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\44b23a7e-471e-4d06-b7d3-9c354e67b2f9.jsonl.reset.2026-04-13T07-43-22.366Z`
- **Session ID**: `44b23a7e-471e-4d06-b7d3-9c354e67b2f9`
- **行号**: 104
- **时间戳**: 2026-04-13T15:42:15.399

---

### 错误 #36

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
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\44b23a7e-471e-4d06-b7d3-9c354e67b2f9.jsonl.reset.2026-04-13T07-43-22.366Z`
- **Session ID**: `44b23a7e-471e-4d06-b7d3-9c354e67b2f9`
- **行号**: 107
- **时间戳**: 2026-04-13T15:42:35.657

---

### 错误 #37

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

[Mon 2026-04-13 15:46 GMT+8] 使用imap-smtp-email技能，帮我查询邮箱中最近的10封邮件`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54129 input tokens (16384 > 65536 - 54129). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\7b971aed-e825-456c-a609-bdb2463e6ccc.jsonl.reset.2026-04-13T07-46-28.872Z`
- **Session ID**: `7b971aed-e825-456c-a609-bdb2463e6ccc`
- **行号**: 32
- **时间戳**: 2026-04-13T15:46:19.487

---

### 错误 #38

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

[Mon 2026-04-13 15:49 GMT+8] 使用imap-smtp-email技能，帮我查询收件箱中最近的10封邮件，列出查询到的邮件`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 57458 input tokens (16384 > 65536 - 57458). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\46ab4208-374c-4215-853c-5c7987c2e791.jsonl.reset.2026-04-13T07-49-48.941Z`
- **Session ID**: `46ab4208-374c-4215-853c-5c7987c2e791`
- **行号**: 26
- **时间戳**: 2026-04-13T15:49:35.801

---

### 错误 #39

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: aborted
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:22 GMT+8] 将一下数据转换成json格式，李卫 11000469 10000004
康春芳 11000492 10000004
苏航 ...`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 68
- **时间戳**: 2026-04-13T16:22:37.342

---

### 错误 #40

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: aborted
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:23 GMT+8] 将以下下数据转换成json，格式为 {"uid": "工号", "userName": "姓名", "orgCode": ...`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 72
- **时间戳**: 2026-04-13T16:23:59.633

---

### 错误 #41

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 15:05 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请读取 /home/node/.openc...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 92360 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=92360)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\9ccfae6c-1ba2-4215-b07c-f16eebaee938.jsonl`
- **Session ID**: `9ccfae6c-1ba2-4215-b07c-f16eebaee938`
- **行号**: 8
- **时间戳**: 2026-04-14T15:05:53.408

---

### 错误 #42

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 15:06 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请读取 /home/node/.openc...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 92483 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=92483)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\f15427eb-5cbe-4649-b5e5-ff97dbf69934.jsonl`
- **Session ID**: `f15427eb-5cbe-4649-b5e5-ff97dbf69934`
- **行号**: 8
- **时间戳**: 2026-04-14T15:06:12.042

---

### 错误 #43

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 15:18 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请检查 /home/node/.openc...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 93196 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=93196)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\57655182-1fa9-4dca-aafc-f16e69319ef6.jsonl`
- **Session ID**: `57655182-1fa9-4dca-aafc-f16e69319ef6`
- **行号**: 8
- **时间戳**: 2026-04-14T15:18:49.170

---

### 错误 #44

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Tue 2026-04-14 17:03 GMT+8] 将待完成工作分拆任务执行`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51191 input tokens (16384 > 65536 - 51191). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 113
- **时间戳**: 2026-04-14T17:04:06.892

---

### 错误 #45

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:06 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49625 input tokens (16384 > 65536 - 49625). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 149
- **时间戳**: 2026-04-14T17:11:39.838

---

### 错误 #46

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49635 input tokens (16384 > 65536 - 49635). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 152
- **时间戳**: 2026-04-14T17:11:41.699

---

### 错误 #47

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50137 input tokens (16384 > 65536 - 50137). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 155
- **时间戳**: 2026-04-14T17:11:43.717

---

### 错误 #48

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51119 input tokens (16384 > 65536 - 51119). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 158
- **时间戳**: 2026-04-14T17:11:46.092

---

### 错误 #49

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51677 input tokens (16384 > 65536 - 51677). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 161
- **时间戳**: 2026-04-14T17:11:48.126

---

### 错误 #50

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52235 input tokens (16384 > 65536 - 52235). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 164
- **时间戳**: 2026-04-14T17:11:51.704

---

### 错误 #51

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51945 input tokens (16384 > 65536 - 51945). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 167
- **时间戳**: 2026-04-14T17:11:53.494

---

### 错误 #52

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52954 input tokens (16384 > 65536 - 52954). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 170
- **时间戳**: 2026-04-14T17:11:55.797

---

### 错误 #53

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53315 input tokens (16384 > 65536 - 53315). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 173
- **时间戳**: 2026-04-14T17:11:57.824

---

### 错误 #54

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:07 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53676 input tokens (16384 > 65536 - 53676). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 176
- **时间戳**: 2026-04-14T17:11:59.840

---

### 错误 #55

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:11 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53166 input tokens (16384 > 65536 - 53166). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 179
- **时间戳**: 2026-04-14T17:12:01.920

---

### 错误 #56

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:11 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54424 input tokens (16384 > 65536 - 54424). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 182
- **时间戳**: 2026-04-14T17:12:04.425

---

### 错误 #57

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:11 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54798 input tokens (16384 > 65536 - 54798). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 185
- **时间戳**: 2026-04-14T17:12:06.719

---

### 错误 #58

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:11 GMT+8] <<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>
OpenClaw runtime context (internal):
This context is runtime-generated, not user-authored. Keep internal details private.

[Internal ...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55172 input tokens (16384 > 65536 - 55172). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 188
- **时间戳**: 2026-04-14T17:12:08.956

---

### 错误 #59

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Read HEARTBEAT.md if it exists (workspace context). Follow it strictly. Do not infer or repeat old tasks from prior chats. If nothing needs attention, reply HEARTBEAT_OK.
When reading HEARTBEAT.md, us...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54355 input tokens (16384 > 65536 - 54355). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 191
- **时间戳**: 2026-04-14T17:20:57.648

---

### 错误 #60

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Read HEARTBEAT.md if it exists (workspace context). Follow it strictly. Do not infer or repeat old tasks from prior chats. If nothing needs attention, reply HEARTBEAT_OK.
When reading HEARTBEAT.md, us...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55606 input tokens (16384 > 65536 - 55606). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 194
- **时间戳**: 2026-04-14T17:21:00.303

---

### 错误 #61

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Read HEARTBEAT.md if it exists (workspace context). Follow it strictly. Do not infer or repeat old tasks from prior chats. If nothing needs attention, reply HEARTBEAT_OK.
When reading HEARTBEAT.md, us...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55737 input tokens (16384 > 65536 - 55737). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 197
- **时间戳**: 2026-04-14T17:21:02.083

---

### 错误 #62

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Read HEARTBEAT.md if it exists (workspace context). Follow it strictly. Do not infer or repeat old tasks from prior chats. If nothing needs attention, reply HEARTBEAT_OK.
When reading HEARTBEAT.md, us...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55868 input tokens (16384 > 65536 - 55868). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 200
- **时间戳**: 2026-04-14T17:21:03.875

---

### 错误 #63

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100732
- **姓名**: 牛伟峰
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Tue 2026-04-14 21:43 GMT+8] 找到stopReason不是正常结果的记录`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 59325 input tokens (16384 > 65536 - 59325). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\eb05b9da-88b1-4e96-8e91-5bab2fdeb854.jsonl.reset.2026-04-14T13-43-54.534Z`
- **Session ID**: `eb05b9da-88b1-4e96-8e91-5bab2fdeb854`
- **行号**: 68
- **时间戳**: 2026-04-14T21:43:37.793

---

### 错误 #64

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100732
- **姓名**: 牛伟峰
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Tue 2026-04-14 21:43 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 59431 input tokens (16384 > 65536 - 59431). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\eb05b9da-88b1-4e96-8e91-5bab2fdeb854.jsonl.reset.2026-04-14T13-43-54.534Z`
- **Session ID**: `eb05b9da-88b1-4e96-8e91-5bab2fdeb854`
- **行号**: 71
- **时间戳**: 2026-04-14T21:43:49.812

---

### 错误 #65

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100732
- **姓名**: 牛伟峰
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Tue 2026-04-14 21:49 GMT+8] 搜索~/.openclaw/agents/main/sessions下面的所有文件，不止.jsonl`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50183 input tokens (16384 > 65536 - 50183). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\c4803ea6-54cc-4d56-a500-bc98653190ca.jsonl.reset.2026-04-14T13-49-35.396Z`
- **Session ID**: `c4803ea6-54cc-4d56-a500-bc98653190ca`
- **行号**: 50
- **时间戳**: 2026-04-14T21:49:25.684

---

### 错误 #66

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: aborted
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:43 GMT+8] 帮我查看下现有的k8s 集群的运行状况 以及问题`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 38
- **时间戳**: 2026-04-15T09:44:08.758

---

### 错误 #67

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:56 GMT+8] 使用k8s-pilot 巡检所有k8s 集群`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49813 input tokens (16384 > 65536 - 49813). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 118
- **时间戳**: 2026-04-15T09:56:58.252

---

### 错误 #68

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:58 GMT+8] 查看下 包含aiap的pod`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49935 input tokens (16384 > 65536 - 49935). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 121
- **时间戳**: 2026-04-15T09:58:42.615

---

### 错误 #69

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:59 GMT+8] 查看下 包含aiap的pod`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50012 input tokens (16384 > 65536 - 50012). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 124
- **时间戳**: 2026-04-15T09:59:10.120

---

### 错误 #70

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:59 GMT+8] 查看下 包含aiap的pod`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50089 input tokens (16384 > 65536 - 50089). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 127
- **时间戳**: 2026-04-15T09:59:14.367

---

### 错误 #71

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:59 GMT+8] 查看下 包含aiap的pod`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50166 input tokens (16384 > 65536 - 50166). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 130
- **时间戳**: 2026-04-15T09:59:17.530

---

### 错误 #72

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:59 GMT+8] 查看下 包含aiap的pod`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50243 input tokens (16384 > 65536 - 50243). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 133
- **时间戳**: 2026-04-15T09:59:19.665

---

### 错误 #73

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:15 GMT+8] 刚刚我把ppt-master.tar放到了./2026-04-15目录下。我的需求是： 帮我把这个skills解压安装到用户的skills目录下`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64170 input tokens (16384 > 65536 - 64170). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 10
- **时间戳**: 2026-04-15T13:15:49.987

---

### 错误 #74

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:17 GMT+8] nihao`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64329 input tokens (16384 > 65536 - 64329). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 13
- **时间戳**: 2026-04-15T13:17:31.348

---

### 错误 #75

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:17 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64391 input tokens (16384 > 65536 - 64391). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 16
- **时间戳**: 2026-04-15T13:17:56.148

---

### 错误 #76

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:17 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64453 input tokens (16384 > 65536 - 64453). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 19
- **时间戳**: 2026-04-15T13:17:58.358

---

### 错误 #77

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:17 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64515 input tokens (16384 > 65536 - 64515). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 22
- **时间戳**: 2026-04-15T13:18:00.663

---

### 错误 #78

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 13:17 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64577 input tokens (16384 > 65536 - 64577). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e.jsonl`
- **Session ID**: `c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e`
- **行号**: 25
- **时间戳**: 2026-04-15T13:18:02.344

---

### 错误 #79

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18101151
- **姓名**: 程缤瑶
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 14:24 GMT+8] 开始吧`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51784 input tokens (16384 > 65536 - 51784). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a995c7a073fd97cd533a17b62717f0d48e1c6a2d3597fb0668c16564788bde06a2ca13b88f6f674596a9d3f0ae0194307a36103251a8b8cc2ac7cbcc8717ee82\agents\main\sessions\8e991737-22bf-448e-8bbe-c62186c39811.jsonl`
- **Session ID**: `8e991737-22bf-448e-8bbe-c62186c39811`
- **行号**: 40
- **时间戳**: 2026-04-15T14:24:52.655

---

### 错误 #80

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 17:11 GMT+8] 刚刚我把image-1776231556399.png放到了./2026-04-15目录下。我的需求是： 请把图片的文字都写出来`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52189 input tokens (16384 > 65536 - 52189). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 102
- **时间戳**: 2026-04-15T17:11:32.707

---

### 错误 #81

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Wed 2026-04-15 17:31 GMT+8] 使用understand-image技能，用多模态大模型进行图片理解； 你的根目录有哪些文件`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52195 input tokens (16384 > 65536 - 52195). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 105
- **时间戳**: 2026-04-15T17:31:33.226

---

### 错误 #82

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Thu 2026-04-16 09:12 GMT+8] 请下载svn://10.38.0.171:30369/accbp/2026-version/ACCBPv1.7.45/设计/目录下的内容，账号信息为 lishanxiu/lishanxi...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52317 input tokens (16384 > 65536 - 52317). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 108
- **时间戳**: 2026-04-16T09:12:45.708

---

### 错误 #83

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Thu 2026-04-16 09:12 GMT+8] 请下载svn://10.38.0.171:30369/accbp/2026-version/ACCBPv1.7.45/设计/目录下的内容，账号信息为 lishanxiu/lishanxi...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52617 input tokens (16384 > 65536 - 52617). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 111
- **时间戳**: 2026-04-16T09:12:47.860

---

### 错误 #84

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Thu 2026-04-16 09:12 GMT+8] 请下载svn://10.38.0.171:30369/accbp/2026-version/ACCBPv1.7.45/设计/目录下的内容，账号信息为 lishanxiu/lishanxi...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52738 input tokens (16384 > 65536 - 52738). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 114
- **时间戳**: 2026-04-16T09:12:49.549

---

### 错误 #85

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Thu 2026-04-16 09:12 GMT+8] 请下载svn://10.38.0.171:30369/accbp/2026-version/ACCBPv1.7.45/设计/目录下的内容，账号信息为 lishanxiu/lishanxi...`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52859 input tokens (16384 > 65536 - 52859). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\b57d8f72-a5ec-4f01-b83b-4c1f823cc564.jsonl`
- **Session ID**: `b57d8f72-a5ec-4f01-b83b-4c1f823cc564`
- **行号**: 117
- **时间戳**: 2026-04-16T09:12:51.493

---

### 错误 #86

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

[Thu 2026-04-16 11:17 GMT+8] 刚刚我把国寿人险发〔2023〕439号 关于印发《中国人寿保险股份有限公司风险应急管理办法（试行）》的通知.pdf,质量中心2026年1月-2026年4月代码效能与2026年3月版本效能...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 155181 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=155181)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\837503ae-5e31-4723-ac29-12e02f7b233a.jsonl.reset.2026-04-16T03-31-16.010Z`
- **Session ID**: `837503ae-5e31-4723-ac29-12e02f7b233a`
- **行号**: 25
- **时间戳**: 2026-04-16T11:17:18.799

---

### 错误 #87

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

[Thu 2026-04-16 11:31 GMT+8] 刚刚我把国寿人险发〔2023〕439号 关于印发《中国人寿保险股份有限公司风险应急管理办法（试行）》的通知.pdf,质量中心2026年1月-2026年4月代码效能与2026年3月版本效能...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 154708 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=154708)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\e4d8b732-26fa-474d-ae18-a7c32a69cd7e.jsonl.reset.2026-04-23T07-55-44.446Z`
- **Session ID**: `e4d8b732-26fa-474d-ae18-a7c32a69cd7e`
- **行号**: 16
- **时间戳**: 2026-04-16T11:31:51.085

---

### 错误 #88

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

[Thu 2026-04-16 13:40 GMT+8] 刚刚我把国寿人险发〔2023〕439号 关于印发《中国人寿保险股份有限公司风险应急管理办法（试行）》的通知.pdf放到了./2026-04-16目录下。我的需求是： 帮我总结附件内功，生...`
- **错误信息**: 
````
400 This model's maximum context length is 65536 tokens. However, your request has 154940 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=154940)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\068ac7a06a47c7fdc26656446b63d7e17dc09d94203abb2c92d6bcf41c33f56705d20342347e3b18cfae39e7a2940bae5fb6ca5293e374cbceee772548768613\agents\main\sessions\e4d8b732-26fa-474d-ae18-a7c32a69cd7e.jsonl.reset.2026-04-23T07-55-44.446Z`
- **Session ID**: `e4d8b732-26fa-474d-ae18-a7c32a69cd7e`
- **行号**: 19
- **时间戳**: 2026-04-16T13:40:43.917

---

## timeoutErrors - 超时错误 (22)

### 错误 #89

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `[Mon 2026-04-13 18:24 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 创建一个简化但完整的购物网站项目，使用最简...`
- **错误信息**: 
````
{"timestamp":1776077460469,"runId":"b8a86d98-7887-4263-90d8-d5e5c0153909","sessionId":"0ee5ff89-79d5-41f8-a93f-49146d0f3722","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\0ee5ff89-79d5-41f8-a93f-49146d0f3722.jsonl`
- **Session ID**: `0ee5ff89-79d5-41f8-a93f-49146d0f3722`
- **行号**: 114
- **时间戳**: 2026-04-28T13:58:36.792
- **Run ID**: `b8a86d98-7887-4263-90d8-d5e5c0153909`

---

### 错误 #90

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100988
- **姓名**: 冯丽媛
- **部门**: 18100000
- **用户输入**: `[Wed 2026-04-15 15:29 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请读取并分析文件 /home/node/....`
- **错误信息**: 
````
{"timestamp":1776238413231,"runId":"bb3c513f-d87e-448f-8014-614e40c21906","sessionId":"f1aced44-6c24-42f6-aa51-3909db1ff629","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\f1aced44-6c24-42f6-aa51-3909db1ff629.jsonl`
- **Session ID**: `f1aced44-6c24-42f6-aa51-3909db1ff629`
- **行号**: 22
- **时间戳**: 2026-04-28T13:58:37.203
- **Run ID**: `bb3c513f-d87e-448f-8014-614e40c21906`

---

### 错误 #91

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100506
- **姓名**: 李山秀
- **部门**: 18100000
- **用户输入**: `[Mon 2026-04-13 17:49 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 创建一个完整的购物网站项目，包括：

1....`
- **错误信息**: 
````
{"timestamp":1776075848008,"runId":"aba0cdf6-68d5-4842-a735-b4adad95ff4c","sessionId":"c2dadcbe-f4b0-472d-aafe-122d0e670ede","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\13c13153a543ecba2ba0adb5b621795367f9130736913b4d3bbb5b8244184d6163cd24120cba49ff7f7a07a9b5bb27cc263a5db4d6fc3a9b80b2cf24df09952d\agents\main\sessions\c2dadcbe-f4b0-472d-aafe-122d0e670ede.jsonl`
- **Session ID**: `c2dadcbe-f4b0-472d-aafe-122d0e670ede`
- **行号**: 130
- **时间戳**: 2026-04-28T13:58:37.335
- **Run ID**: `aba0cdf6-68d5-4842-a735-b4adad95ff4c`

---

### 错误 #92

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100988
- **姓名**: 冯丽媛
- **部门**: 18100000
- **用户输入**: `[Wed 2026-04-15 13:10 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请分析文件 /home/node/.ope...`
- **错误信息**: 
````
{"timestamp":1776229930967,"runId":"0da67fde-8212-48e1-aaec-2bf06e64800d","sessionId":"fe368a91-4216-43d0-9bf1-dfa1cceed4bc","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\fe368a91-4216-43d0-9bf1-dfa1cceed4bc.jsonl`
- **Session ID**: `fe368a91-4216-43d0-9bf1-dfa1cceed4bc`
- **行号**: 18
- **时间戳**: 2026-04-28T13:58:37.472
- **Run ID**: `0da67fde-8212-48e1-aaec-2bf06e64800d`

---

### 错误 #93

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18101142
- **用户输入**: `[Mon 2026-04-13 16:13 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请调用 official-doc-writ...`
- **错误信息**: 
````
{"timestamp":1776068086326,"runId":"237cc3e6-bd84-4004-8086-704bedb2fe42","sessionId":"39028978-7dfa-4c83-ac08-4a49ed087310","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\6fca9aa611cf469e15161f2b342062f7c621c962e44d14a57ee1d61d972f9135cd6f8797feb2302283695088f655118edd65a6768f2159207fd01f575a80e207\agents\main\sessions\39028978-7dfa-4c83-ac08-4a49ed087310.jsonl`
- **Session ID**: `39028978-7dfa-4c83-ac08-4a49ed087310`
- **行号**: 10
- **时间戳**: 2026-04-28T13:58:37.535
- **Run ID**: `237cc3e6-bd84-4004-8086-704bedb2fe42`

---

### 错误 #94

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 pipeline/...`
- **错误信息**: 
````
{"timestamp":1776157562548,"runId":"bd352a63-b3a1-40de-ad85-384f60bb7a9a","sessionId":"0b6f9e7d-6192-44d8-b925-2c94cc74d371","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0b6f9e7d-6192-44d8-b925-2c94cc74d371.jsonl`
- **Session ID**: `0b6f9e7d-6192-44d8-b925-2c94cc74d371`
- **行号**: 33
- **时间戳**: 2026-04-28T13:58:37.861
- **Run ID**: `bd352a63-b3a1-40de-ad85-384f60bb7a9a`

---

### 错误 #95

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Tue 2026-04-14 14:24 GMT+8] 分析下这个项目目录结构和主要功能 如果有疑问可以跟我问答 将分析结果整理成文档存在.claw中`
- **错误信息**: 
````
{"timestamp":1776147922297,"runId":"req_1776147850337_tyub0lfc0","sessionId":"0f678300-9756-4ea9-b283-9cf231eaba5f","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 72
- **时间戳**: 2026-04-28T13:58:38.072
- **Run ID**: `req_1776147850337_tyub0lfc0`

---

### 错误 #96

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Tue 2026-04-14 14:24 GMT+8] 分析下这个项目目录结构和主要功能 如果有疑问可以跟我问答 将分析结果整理成文档存在.claw中`
- **错误信息**: 
````
{"timestamp":1776148975899,"runId":"req_1776148910958_kbpe7zfuk","sessionId":"0f678300-9756-4ea9-b283-9cf231eaba5f","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 74
- **时间戳**: 2026-04-28T13:58:38.072
- **Run ID**: `req_1776148910958_kbpe7zfuk`

---

### 错误 #97

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 15:06 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请使用 docx 技能处理 /home/n...`
- **错误信息**: 
````
{"timestamp":1776151087475,"runId":"010bceeb-4f2b-4b81-acf0-7a01daee7b26","sessionId":"9a0af35c-6303-4ae7-a932-54396b74e799","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\9a0af35c-6303-4ae7-a932-54396b74e799.jsonl`
- **Session ID**: `9a0af35c-6303-4ae7-a932-54396b74e799`
- **行号**: 126
- **时间戳**: 2026-04-28T13:58:38.149
- **Run ID**: `010bceeb-4f2b-4b81-acf0-7a01daee7b26`

---

### 错误 #98

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 sharelib/...`
- **错误信息**: 
````
{"timestamp":1776157575144,"runId":"574deee7-91d2-4251-8ab6-348eb9cadac3","sessionId":"3bf2a8e5-33de-4ef6-b677-a0ea9a3fbee3","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\3bf2a8e5-33de-4ef6-b677-a0ea9a3fbee3.jsonl`
- **Session ID**: `3bf2a8e5-33de-4ef6-b677-a0ea9a3fbee3`
- **行号**: 47
- **时间戳**: 2026-04-28T13:58:38.554
- **Run ID**: `574deee7-91d2-4251-8ab6-348eb9cadac3`

---

### 错误 #99

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:30 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请分析 cicd 项目的 pipeline...`
- **错误信息**: 
````
{"timestamp":1776159101107,"runId":"25f6a0f7-6100-45bf-a238-3c1bde61470d","sessionId":"495e09f3-443a-40ad-b26f-edc30ebcf118","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\495e09f3-443a-40ad-b26f-edc30ebcf118.jsonl`
- **Session ID**: `495e09f3-443a-40ad-b26f-edc30ebcf118`
- **行号**: 21
- **时间戳**: 2026-04-28T13:58:38.660
- **Run ID**: `25f6a0f7-6100-45bf-a238-3c1bde61470d`

---

### 错误 #100

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 sharelib/...`
- **错误信息**: 
````
{"timestamp":1776157550615,"runId":"a68d9714-a191-40b6-9d65-30d26303535a","sessionId":"66a18763-dcc3-4f3f-8838-88ce893158a4","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\66a18763-dcc3-4f3f-8838-88ce893158a4.jsonl`
- **Session ID**: `66a18763-dcc3-4f3f-8838-88ce893158a4`
- **行号**: 25
- **时间戳**: 2026-04-28T13:58:38.897
- **Run ID**: `a68d9714-a191-40b6-9d65-30d26303535a`

---

### 错误 #101

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:06 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 ansible/ ...`
- **错误信息**: 
````
{"timestamp":1776157659822,"runId":"421add1e-43ff-4965-894d-176cf2f736d0","sessionId":"8011363c-3210-4c83-a4d6-13c03b465220","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\8011363c-3210-4c83-a4d6-13c03b465220.jsonl`
- **Session ID**: `8011363c-3210-4c83-a4d6-13c03b465220`
- **行号**: 22
- **时间戳**: 2026-04-28T13:58:39.120
- **Run ID**: `421add1e-43ff-4965-894d-176cf2f736d0`

---

### 错误 #102

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:25 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请分析 cicd 项目的 sharelib...`
- **错误信息**: 
````
{"timestamp":1776158835603,"runId":"27ca7b27-88b7-4ee2-8d53-d0c795bfe759","sessionId":"a5d510bb-1b47-4314-9446-1732cc207874","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\a5d510bb-1b47-4314-9446-1732cc207874.jsonl`
- **Session ID**: `a5d510bb-1b47-4314-9446-1732cc207874`
- **行号**: 29
- **时间戳**: 2026-04-28T13:58:39.383
- **Run ID**: `27ca7b27-88b7-4ee2-8d53-d0c795bfe759`

---

### 错误 #103

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:06 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 快速分析 cicd 项目的 shareli...`
- **错误信息**: 
````
{"timestamp":1776157670999,"runId":"c4c8ea24-93a8-431a-aa6f-3f891ee544d9","sessionId":"acee90b3-b877-42fd-abeb-3700b4b5fd57","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\acee90b3-b877-42fd-abeb-3700b4b5fd57.jsonl`
- **Session ID**: `acee90b3-b877-42fd-abeb-3700b4b5fd57`
- **行号**: 15
- **时间戳**: 2026-04-28T13:58:39.425
- **Run ID**: `c4c8ea24-93a8-431a-aa6f-3f891ee544d9`

---

### 错误 #104

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 sharelib/...`
- **错误信息**: 
````
{"timestamp":1776157553878,"runId":"f05dfe06-c8f8-4a25-b16e-01468e47c033","sessionId":"b622c006-2698-4967-9e4c-0a44c6c9457c","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\b622c006-2698-4967-9e4c-0a44c6c9457c.jsonl`
- **Session ID**: `b622c006-2698-4967-9e4c-0a44c6c9457c`
- **行号**: 30
- **时间戳**: 2026-04-28T13:58:39.452
- **Run ID**: `f05dfe06-c8f8-4a25-b16e-01468e47c033`

---

### 错误 #105

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Wed 2026-04-15 17:19 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 根据 /home/node/.opencl...`
- **错误信息**: 
````
{"timestamp":1776244851687,"runId":"bbae6408-de89-479f-90f0-235dd832faed","sessionId":"b7865994-0c4a-4761-ace1-c637f4fe4ab5","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\b7865994-0c4a-4761-ace1-c637f4fe4ab5.jsonl`
- **Session ID**: `b7865994-0c4a-4761-ace1-c637f4fe4ab5`
- **行号**: 8
- **时间戳**: 2026-04-28T13:58:39.561
- **Run ID**: `bbae6408-de89-479f-90f0-235dd832faed`

---

### 错误 #106

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 ansible/ ...`
- **错误信息**: 
````
{"timestamp":1776157545740,"runId":"4020997d-ba23-4765-be3d-419acf130ddc","sessionId":"d66da86c-8415-45d4-b226-3f67b20e6c72","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\d66da86c-8415-45d4-b226-3f67b20e6c72.jsonl`
- **Session ID**: `d66da86c-8415-45d4-b226-3f67b20e6c72`
- **行号**: 23
- **时间戳**: 2026-04-28T13:58:39.583
- **Run ID**: `4020997d-ba23-4765-be3d-419acf130ddc`

---

### 错误 #107

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:31 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请分析 cicd 项目的 ansible/...`
- **错误信息**: 
````
{"timestamp":1776159184696,"runId":"68d03430-23ec-4958-b0fa-9b1f2fe9325e","sessionId":"efe3c556-5c92-4323-b1dc-9d80cadd71fb","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\efe3c556-5c92-4323-b1dc-9d80cadd71fb.jsonl`
- **Session ID**: `efe3c556-5c92-4323-b1dc-9d80cadd71fb`
- **行号**: 32
- **时间戳**: 2026-04-28T13:58:39.781
- **Run ID**: `68d03430-23ec-4958-b0fa-9b1f2fe9325e`

---

### 错误 #108

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:28 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 请分析 cicd 项目的 steps/ 目...`
- **错误信息**: 
````
{"timestamp":1776159017499,"runId":"a169213c-b705-4a42-8164-7f40fc703801","sessionId":"f21ffaa1-f08e-4c01-bf00-fc674c1ad6c6","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\f21ffaa1-f08e-4c01-bf00-fc674c1ad6c6.jsonl`
- **Session ID**: `f21ffaa1-f08e-4c01-bf00-fc674c1ad6c6`
- **行号**: 42
- **时间戳**: 2026-04-28T13:58:39.810
- **Run ID**: `a169213c-b705-4a42-8164-7f40fc703801`

---

### 错误 #109

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Wed 2026-04-15 17:21 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 根据 /home/node/.opencl...`
- **错误信息**: 
````
{"timestamp":1776244938505,"runId":"766d9b83-aada-4e2e-9b95-75c228b3b61d","sessionId":"f2d7f49d-9571-4cc1-a3de-fb002d6fb441","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\f2d7f49d-9571-4cc1-a3de-fb002d6fb441.jsonl`
- **Session ID**: `f2d7f49d-9571-4cc1-a3de-fb002d6fb441`
- **行号**: 8
- **时间戳**: 2026-04-28T13:58:39.844
- **Run ID**: `766d9b83-aada-4e2e-9b95-75c228b3b61d`

---

### 错误 #110

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 18100719
- **姓名**: 黄怡然
- **部门**: 18100000
- **用户输入**: `[Tue 2026-04-14 17:04 GMT+8] [Subagent Context] You are running as a subagent (depth 1/1). Results auto-announce to your requester; do not busy-poll for status.

[Subagent Task]: 分析 cicd 项目的 steps/ 目录...`
- **错误信息**: 
````
{"timestamp":1776157570791,"runId":"62ca17d5-cbc7-45a4-a5ea-7d5faeeb11d0","sessionId":"fe866c45-f880-4daa-b46e-4db9ee164372","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\fe866c45-f880-4daa-b46e-4db9ee164372.jsonl`
- **Session ID**: `fe866c45-f880-4daa-b46e-4db9ee164372`
- **行号**: 40
- **时间戳**: 2026-04-28T13:58:39.862
- **Run ID**: `62ca17d5-cbc7-45a4-a5ea-7d5faeeb11d0`

---

## modelErrors - 模型API错误 (12)

### 错误 #111

- **事件类型**: `message`
- **描述**: 在message事件中检测到模型API错误
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:22 GMT+8] 上传到哪儿了 我没有看到啊`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 94
- **时间戳**: 2026-04-03T14:23:37.240

---

### 错误 #112

- **事件类型**: `message`
- **描述**: 在message事件中检测到模型API错误
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:22 GMT+8] 将一下数据转换成json格式，李卫 11000469 10000004
康春芳 11000492 10000004
苏航 ...`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 68
- **时间戳**: 2026-04-13T16:22:37.342

---

### 错误 #113

- **事件类型**: `message`
- **描述**: 在message事件中检测到模型API错误
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:23 GMT+8] 将以下下数据转换成json，格式为 {"uid": "工号", "userName": "姓名", "orgCode": ...`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 72
- **时间戳**: 2026-04-13T16:23:59.633

---

### 错误 #114

- **事件类型**: `message`
- **描述**: 在message事件中检测到模型API错误
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:43 GMT+8] 帮我查看下现有的k8s 集群的运行状况 以及问题`
- **错误信息**: 
````
Request was aborted
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 38
- **时间戳**: 2026-04-15T09:44:08.758

---

### 错误 #115

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:22 GMT+8] 将一下数据转换成json格式，李卫 11000469 10000004
康春芳 11000492 10000004
苏航 ...`
- **错误信息**: 
````
{"timestamp":1776068563124,"runId":"bc2b3f7b-2fae-4774-92b5-a36dc673385d","sessionId":"2b9f7ba4-e50c-4f33-bf96-85367fa6cebf","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"This operation was aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 67
- **时间戳**: 2026-04-28T13:58:36.785
- **Run ID**: `bc2b3f7b-2fae-4774-92b5-a36dc673385d`

---

### 错误 #116

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18182000
- **姓名**: 毕馨月
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 16:23 GMT+8] 将以下下数据转换成json，格式为 {"uid": "工号", "userName": "姓名", "orgCode": ...`
- **错误信息**: 
````
{"timestamp":1776068642172,"runId":"2e6ad39f-3981-4dfa-9e0e-8454d3961af2","sessionId":"2b9f7ba4-e50c-4f33-bf96-85367fa6cebf","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"This operation was aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\0f8907022d9c7513b586d400ab3c57fb25659eee8f8b5017dd1e9cc094f4ce3a7cc87cb548522993c391f86e956c13838fbfec56464aa0879ce3c468c4aedbdc\agents\main\sessions\2b9f7ba4-e50c-4f33-bf96-85367fa6cebf.jsonl`
- **Session ID**: `2b9f7ba4-e50c-4f33-bf96-85367fa6cebf`
- **行号**: 71
- **时间戳**: 2026-04-28T13:58:36.785
- **Run ID**: `2e6ad39f-3981-4dfa-9e0e-8454d3961af2`

---

### 错误 #117

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
{"timestamp":1774868696556,"runId":"req_1774868684378_4e84zalrb","sessionId":"0af83cd4-10a3-4966-8f3c-2b581a53bf99","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 130
- **时间戳**: 2026-04-28T13:58:37.048
- **Run ID**: `req_1774868684378_4e84zalrb`

---

### 错误 #118

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18100293
- **姓名**: 张凯磊
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Thu 2026-04-16 09:14 GMT+8] 我的龙虾项目式什么时间`
- **错误信息**: 
````
{"timestamp":1776302272673,"runId":"req_1776302087795_5cms510hh","sessionId":"4f250dc6-3ebe-4fff-90ba-3497bbb9fe07","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"This operation was aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\619aa316b92e3dfc3ebb94deaebb6af6052bc3f8c9557530ddd4b1f4525e8cdcb3775f71c6fb0ac96b744c11e1c8b5accbc8b356f3d2f75bdd5b75efed4ce0c0\agents\main\sessions\4f250dc6-3ebe-4fff-90ba-3497bbb9fe07.jsonl`
- **Session ID**: `4f250dc6-3ebe-4fff-90ba-3497bbb9fe07`
- **行号**: 23
- **时间戳**: 2026-04-28T13:58:37.347
- **Run ID**: `req_1776302087795_5cms510hh`

---

### 错误 #119

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18101138
- **姓名**: 任健鑫
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:22 GMT+8] 上传到哪儿了 我没有看到啊`
- **错误信息**: 
````
{"timestamp":1775197418192,"runId":"req_1775197362262_n7z2xlxi6","sessionId":"c124a8ac-1e3d-4b27-a6e6-e558938ce159","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\a793d94b6ad1c388bc785ea54e450926b729a9ed21fd7f5685e549542317b191889efa017a1d6c1cea1b952519ba7f227fe5499937e9452e032463addb26e3de\agents\main\sessions\c124a8ac-1e3d-4b27-a6e6-e558938ce159.jsonl`
- **Session ID**: `c124a8ac-1e3d-4b27-a6e6-e558938ce159`
- **行号**: 93
- **时间戳**: 2026-04-28T13:58:38.539
- **Run ID**: `req_1775197362262_n7z2xlxi6`

---

### 错误 #120

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18182001
- **姓名**: 李潇
- **部门**: 18100000
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Wed 2026-04-15 09:43 GMT+8] 帮我查看下现有的k8s 集群的运行状况 以及问题`
- **错误信息**: 
````
{"timestamp":1776217458902,"runId":"f73d774c-9773-48ae-a324-5d1e18eddad4","sessionId":"8ef546cf-18a4-43a7-baec-ed0207c28996","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"This operation was aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\f222336474c3c33b45b015cca3fdcf24fbfc8a597f351d79bc150829f53504e5d7819658cde4a8f7af659e260af6be27b33dcadf21b2b5928bbdc265681b3e6d\agents\main\sessions\8ef546cf-18a4-43a7-baec-ed0207c28996.jsonl`
- **Session ID**: `8ef546cf-18a4-43a7-baec-ed0207c28996`
- **行号**: 37
- **时间戳**: 2026-04-28T13:58:38.679
- **Run ID**: `f73d774c-9773-48ae-a324-5d1e18eddad4`

---

### 错误 #121

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **错误信息**: 
````
{"timestamp":1775198102235,"runId":"req_1775197972491_55uwzwguf","sessionId":"54355af5-ac92-4baf-a0df-42f72ff7c497","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\54355af5-ac92-4baf-a0df-42f72ff7c497.jsonl`
- **Session ID**: `d4678ca9-d333-45fc-b9d5-9197b9cf2cea`
- **行号**: 5
- **时间戳**: 2026-04-28T13:58:38.964
- **Run ID**: `req_1775197972491_55uwzwguf`

---

### 错误 #122

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **错误信息**: 
````
{"timestamp":1775122040261,"runId":"req_1775122020273_g1x9hzjom","sessionId":"c5c862a7-da7a-4e74-ad62-5c3afec2c9e2","provider":"my-qwen-provider","model":"AIAPLLM-vision-nothink","api":"openai-completions","error":"aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\c5c862a7-da7a-4e74-ad62-5c3afec2c9e2.jsonl`
- **Session ID**: `b5018140-32f9-4102-879a-7853821a47d1`
- **行号**: 5
- **时间戳**: 2026-04-28T13:58:39.691
- **Run ID**: `req_1775122020273_g1x9hzjom`

---

