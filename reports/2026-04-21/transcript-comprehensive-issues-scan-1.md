# OpenClaw Session Transcript 综合错误检测报告

**生成时间**: 2026-04-21T13:49:42.280Z

## 📊 统计概览

- **总错误数**: 466
- **总对话轮数**: 579 （排除系统消息）
- **有错误轮数**: 213 （存在任何类型错误的轮次）
- **错误率**: 36.79% （有错误轮数 / 总对话轮数）

### 错误类型分布

| 错误类型 | 数量 | 说明 |
|---------|------|------|
| abnormal_stop | 395 | 异常停止 |
| timeoutErrors | 30 | 超时错误 |
| rateLimitErrors | 24 | 速率限制错误 |
| modelErrors | 14 | 模型API错误 |
| flow_integrity_missing_final_answer | 3 | 工具执行后无最终回复 |

---

## abnormal_stop - 异常停止 (395)

### 错误 #1

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 48
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #2

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 49
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #3

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 50
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #4

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 51
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #5

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
- **时间戳**: 2026-04-21T13:49:34.516

---

### 错误 #6

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
- **时间戳**: 2026-04-21T13:49:34.516

---

### 错误 #7

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "test",
  "id": "test"
}
\`\`\`

[Fri 2026-04-03 14:21 GMT+8] 刚刚我把评级台账.xlsx放到了./2026-04-03目录下。我的需求是：使用xlsx技能； 请对文件信息进行分析，并绘制柱状图，要求横坐标为月份，纵坐标为件数`
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0f736d41-a0a2-4867-bbb5-79047d19da62.jsonl.reset.2026-04-08T02-20-20.878Z`
- **Session ID**: `0f736d41-a0a2-4867-bbb5-79047d19da62`
- **行号**: 14
- **时间戳**: 2026-04-21T13:49:34.804

---

### 错误 #8

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\14630ba5-15a3-44a8-a62e-7e9272b5531c.jsonl`
- **Session ID**: `14630ba5-15a3-44a8-a62e-7e9272b5531c`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:34.881

---

### 错误 #9

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\14630ba5-15a3-44a8-a62e-7e9272b5531c.jsonl`
- **Session ID**: `14630ba5-15a3-44a8-a62e-7e9272b5531c`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:34.881

---

### 错误 #10

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\14630ba5-15a3-44a8-a62e-7e9272b5531c.jsonl`
- **Session ID**: `14630ba5-15a3-44a8-a62e-7e9272b5531c`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:34.881

---

### 错误 #11

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\14630ba5-15a3-44a8-a62e-7e9272b5531c.jsonl`
- **Session ID**: `14630ba5-15a3-44a8-a62e-7e9272b5531c`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:34.881

---

### 错误 #12

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\18fde982-d380-45bd-9ce5-a27b87720a47.jsonl`
- **Session ID**: `18fde982-d380-45bd-9ce5-a27b87720a47`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:34.964

---

### 错误 #13

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\18fde982-d380-45bd-9ce5-a27b87720a47.jsonl`
- **Session ID**: `18fde982-d380-45bd-9ce5-a27b87720a47`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:34.964

---

### 错误 #14

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\18fde982-d380-45bd-9ce5-a27b87720a47.jsonl`
- **Session ID**: `18fde982-d380-45bd-9ce5-a27b87720a47`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:34.964

---

### 错误 #15

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\18fde982-d380-45bd-9ce5-a27b87720a47.jsonl`
- **Session ID**: `18fde982-d380-45bd-9ce5-a27b87720a47`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:34.964

---

### 错误 #16

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #17

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #18

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #19

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #20

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #21

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
- **时间戳**: 2026-04-21T13:49:35.029

---

### 错误 #22

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
- **时间戳**: 2026-04-21T13:49:35.215

---

### 错误 #23

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
- **时间戳**: 2026-04-21T13:49:35.215

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

[Fri 2026-04-03 15:18 GMT+8] 你好`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54920 input tokens (16384 > 65536 - 54920). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\2839c2f17383d426e0f87c82614743eed21a2aa5a58d39da3b11de6dc56388a31ba9219c47d42da0009bc58633ad7c2f6003d505d1ffb40a96eac87034abf2bf\agents\main\sessions\55b3dbad-7082-44c9-8556-9346043c798d.jsonl`
- **Session ID**: `55b3dbad-7082-44c9-8556-9346043c798d`
- **行号**: 42
- **时间戳**: 2026-04-21T13:49:35.215

---

### 错误 #25

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57546 input tokens (8192 > 65536 - 57546). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 249
- **时间戳**: 2026-04-21T13:49:35.354

---

### 错误 #26

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
- **时间戳**: 2026-04-21T13:49:35.354

---

### 错误 #27

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57568 input tokens (8192 > 65536 - 57568). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 269
- **时间戳**: 2026-04-21T13:49:35.354

---

### 错误 #28

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
- **时间戳**: 2026-04-21T13:49:35.354

---

### 错误 #29

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100072
- **姓名**: 刘海青
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-30.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58834 input tokens (8192 > 65536 - 58834). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\37d31ad6132bab00315c7b7adabe5b839b918500995ce145a03763c66ecc2f612ca90d021c7098f060f5f0547433161ce6af7f6899f2fc1e6f39bab40e12e65a\agents\main\sessions\0af83cd4-10a3-4966-8f3c-2b581a53bf99.jsonl`
- **Session ID**: `0af83cd4-10a3-4966-8f3c-2b581a53bf99`
- **行号**: 273
- **时间戳**: 2026-04-21T13:49:35.354

---

### 错误 #30

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\24d2498b-0c83-4a43-9dc1-54953bf7f13a.jsonl`
- **Session ID**: `24d2498b-0c83-4a43-9dc1-54953bf7f13a`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:35.370

---

### 错误 #31

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\24d2498b-0c83-4a43-9dc1-54953bf7f13a.jsonl`
- **Session ID**: `24d2498b-0c83-4a43-9dc1-54953bf7f13a`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:35.370

---

### 错误 #32

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\24d2498b-0c83-4a43-9dc1-54953bf7f13a.jsonl`
- **Session ID**: `24d2498b-0c83-4a43-9dc1-54953bf7f13a`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.370

---

### 错误 #33

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\24d2498b-0c83-4a43-9dc1-54953bf7f13a.jsonl`
- **Session ID**: `24d2498b-0c83-4a43-9dc1-54953bf7f13a`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:35.370

---

### 错误 #34

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\2bb2d971-7f27-4fec-be45-cdcb61081191.jsonl`
- **Session ID**: `2bb2d971-7f27-4fec-be45-cdcb61081191`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:35.437

---

### 错误 #35

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\2bb2d971-7f27-4fec-be45-cdcb61081191.jsonl`
- **Session ID**: `2bb2d971-7f27-4fec-be45-cdcb61081191`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:35.437

---

### 错误 #36

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\2bb2d971-7f27-4fec-be45-cdcb61081191.jsonl`
- **Session ID**: `2bb2d971-7f27-4fec-be45-cdcb61081191`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.437

---

### 错误 #37

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\2bb2d971-7f27-4fec-be45-cdcb61081191.jsonl`
- **Session ID**: `2bb2d971-7f27-4fec-be45-cdcb61081191`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:35.437

---

### 错误 #38

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\361299ea-fed7-49c3-8087-7ac4702c6dfc.jsonl`
- **Session ID**: `361299ea-fed7-49c3-8087-7ac4702c6dfc`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:35.685

---

### 错误 #39

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\361299ea-fed7-49c3-8087-7ac4702c6dfc.jsonl`
- **Session ID**: `361299ea-fed7-49c3-8087-7ac4702c6dfc`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:35.685

---

### 错误 #40

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\361299ea-fed7-49c3-8087-7ac4702c6dfc.jsonl`
- **Session ID**: `361299ea-fed7-49c3-8087-7ac4702c6dfc`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.685

---

### 错误 #41

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\361299ea-fed7-49c3-8087-7ac4702c6dfc.jsonl`
- **Session ID**: `361299ea-fed7-49c3-8087-7ac4702c6dfc`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:35.685

---

### 错误 #42

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\36459a6f-ee3d-468e-b74f-1ffafdc6c8a7.jsonl`
- **Session ID**: `36459a6f-ee3d-468e-b74f-1ffafdc6c8a7`
- **行号**: 22
- **时间戳**: 2026-04-21T13:49:35.906

---

### 错误 #43

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\36459a6f-ee3d-468e-b74f-1ffafdc6c8a7.jsonl`
- **Session ID**: `36459a6f-ee3d-468e-b74f-1ffafdc6c8a7`
- **行号**: 23
- **时间戳**: 2026-04-21T13:49:35.906

---

### 错误 #44

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\37953cda-7cdc-4542-978e-98726480009d.jsonl`
- **Session ID**: `37953cda-7cdc-4542-978e-98726480009d`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:35.965

---

### 错误 #45

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\37953cda-7cdc-4542-978e-98726480009d.jsonl`
- **Session ID**: `37953cda-7cdc-4542-978e-98726480009d`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:35.965

---

### 错误 #46

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\37953cda-7cdc-4542-978e-98726480009d.jsonl`
- **Session ID**: `37953cda-7cdc-4542-978e-98726480009d`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.965

---

### 错误 #47

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\37953cda-7cdc-4542-978e-98726480009d.jsonl`
- **Session ID**: `37953cda-7cdc-4542-978e-98726480009d`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:35.965

---

### 错误 #48

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3a67937c-f06b-464c-b14e-db7ea3c4f445.jsonl`
- **Session ID**: `3a67937c-f06b-464c-b14e-db7ea3c4f445`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.203

---

### 错误 #49

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3a67937c-f06b-464c-b14e-db7ea3c4f445.jsonl`
- **Session ID**: `3a67937c-f06b-464c-b14e-db7ea3c4f445`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.203

---

### 错误 #50

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3a67937c-f06b-464c-b14e-db7ea3c4f445.jsonl`
- **Session ID**: `3a67937c-f06b-464c-b14e-db7ea3c4f445`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.203

---

### 错误 #51

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3a67937c-f06b-464c-b14e-db7ea3c4f445.jsonl`
- **Session ID**: `3a67937c-f06b-464c-b14e-db7ea3c4f445`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.203

---

### 错误 #52

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-17 15:19 GMT+8] 第一个，和第二个`
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b.jsonl.reset.2026-04-17T09-27-34.845Z`
- **Session ID**: `3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b`
- **行号**: 27
- **时间戳**: 2026-04-21T13:49:36.255

---

### 错误 #53

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "cli",
  "id": "cli"
}
\`\`\`

[Fri 2026-04-17 15:45 GMT+8] 刚刚我把在职证明-费远-英文.pdf,在职证明-费远-中文.pdf放到了./2026-04-17目录下。我的需求是： 帮我对比一下这两个文件差异，并输出分析报告`
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b.jsonl.reset.2026-04-17T09-27-34.845Z`
- **Session ID**: `3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b`
- **行号**: 97
- **时间戳**: 2026-04-21T13:49:36.255

---

### 错误 #54

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-17 16:55 GMT+8] 我有点儿想知道，你执行不下去任务，为什么直接agent就停掉了呢`
- **错误信息**: 
````
list index out of range
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b.jsonl.reset.2026-04-17T09-27-34.845Z`
- **Session ID**: `3ad83790-42d3-4f1d-b89e-42c7ce8c7c4b`
- **行号**: 102
- **时间戳**: 2026-04-21T13:49:36.255

---

### 错误 #55

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3e59a0e5-692b-4243-8774-59c2bac1802a.jsonl`
- **Session ID**: `3e59a0e5-692b-4243-8774-59c2bac1802a`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.389

---

### 错误 #56

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3e59a0e5-692b-4243-8774-59c2bac1802a.jsonl`
- **Session ID**: `3e59a0e5-692b-4243-8774-59c2bac1802a`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.389

---

### 错误 #57

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3e59a0e5-692b-4243-8774-59c2bac1802a.jsonl`
- **Session ID**: `3e59a0e5-692b-4243-8774-59c2bac1802a`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.389

---

### 错误 #58

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3e59a0e5-692b-4243-8774-59c2bac1802a.jsonl`
- **Session ID**: `3e59a0e5-692b-4243-8774-59c2bac1802a`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.389

---

### 错误 #59

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4aacfb6d-ca07-4fce-9714-fec3327cfb47.jsonl`
- **Session ID**: `4aacfb6d-ca07-4fce-9714-fec3327cfb47`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.417

---

### 错误 #60

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4aacfb6d-ca07-4fce-9714-fec3327cfb47.jsonl`
- **Session ID**: `4aacfb6d-ca07-4fce-9714-fec3327cfb47`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.417

---

### 错误 #61

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4aacfb6d-ca07-4fce-9714-fec3327cfb47.jsonl`
- **Session ID**: `4aacfb6d-ca07-4fce-9714-fec3327cfb47`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.417

---

### 错误 #62

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4aacfb6d-ca07-4fce-9714-fec3327cfb47.jsonl`
- **Session ID**: `4aacfb6d-ca07-4fce-9714-fec3327cfb47`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.417

---

### 错误 #63

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4c6bd2fa-6ba6-43d4-a894-13a236786a1d.jsonl`
- **Session ID**: `4c6bd2fa-6ba6-43d4-a894-13a236786a1d`
- **行号**: 24
- **时间戳**: 2026-04-21T13:49:36.453

---

### 错误 #64

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4c6bd2fa-6ba6-43d4-a894-13a236786a1d.jsonl`
- **Session ID**: `4c6bd2fa-6ba6-43d4-a894-13a236786a1d`
- **行号**: 25
- **时间戳**: 2026-04-21T13:49:36.453

---

### 错误 #65

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\5420451a-6369-41ee-831b-872b16dff9a6.jsonl`
- **Session ID**: `5420451a-6369-41ee-831b-872b16dff9a6`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.578

---

### 错误 #66

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\5420451a-6369-41ee-831b-872b16dff9a6.jsonl`
- **Session ID**: `5420451a-6369-41ee-831b-872b16dff9a6`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.578

---

### 错误 #67

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\5420451a-6369-41ee-831b-872b16dff9a6.jsonl`
- **Session ID**: `5420451a-6369-41ee-831b-872b16dff9a6`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.578

---

### 错误 #68

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\5420451a-6369-41ee-831b-872b16dff9a6.jsonl`
- **Session ID**: `5420451a-6369-41ee-831b-872b16dff9a6`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.578

---

### 错误 #69

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #70

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #71

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #72

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100919
- **姓名**: 潘超月
- **部门**: 18100000
- **错误信息**: 
````
503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\902b55f3e6f72c412522719af72c4a67a6809d8f908c19bdf409d68941942599c5f008b7a7a2170f407ad283504b75e2efdffcdd4e98826974fcaa621e929062\agents\main\sessions\38cb43c3-64cc-47c2-8ad0-9752d31a0c95.jsonl`
- **Session ID**: `38cb43c3-64cc-47c2-8ad0-9752d31a0c95`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #73

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #74

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #75

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #76

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #77

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #78

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #79

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #80

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
- **时间戳**: 2026-04-21T13:49:36.619

---

### 错误 #81

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\563d1b27-42ee-4206-ad01-127e7dd0dfa2.jsonl`
- **Session ID**: `563d1b27-42ee-4206-ad01-127e7dd0dfa2`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.651

---

### 错误 #82

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\563d1b27-42ee-4206-ad01-127e7dd0dfa2.jsonl`
- **Session ID**: `563d1b27-42ee-4206-ad01-127e7dd0dfa2`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.651

---

### 错误 #83

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\563d1b27-42ee-4206-ad01-127e7dd0dfa2.jsonl`
- **Session ID**: `563d1b27-42ee-4206-ad01-127e7dd0dfa2`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.651

---

### 错误 #84

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\563d1b27-42ee-4206-ad01-127e7dd0dfa2.jsonl`
- **Session ID**: `563d1b27-42ee-4206-ad01-127e7dd0dfa2`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.651

---

### 错误 #85

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 6fca9aa611
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 14:11 GMT+8] 您好，请修改openclaw.json，配置文件（或对应的配置模板），将 baseUrl 指向集群内网中的 LiteLLM...`
- **错误信息**: 
````
400 {'error': '/chat/completions: Invalid model name passed in model=AIAPLLM-vision-nothink. Call `/v1/models` to view available models for your key.'}
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\6fca9aa611cf469e15161f2b342062f7c621c962e44d14a57ee1d61d972f9135cd6f8797feb2302283695088f655118edd65a6768f2159207fd01f575a80e207\agents\main\sessions\ecf6d23a-a5ba-4838-a8bc-de4291d68a48.jsonl`
- **Session ID**: `ecf6d23a-a5ba-4838-a8bc-de4291d68a48`
- **行号**: 40
- **时间戳**: 2026-04-21T13:49:36.692

---

### 错误 #86

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 6fca9aa611
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
- **时间戳**: 2026-04-21T13:49:36.692

---

### 错误 #87

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\63a12907-b0d6-4271-b44b-1df396d3afb1.jsonl`
- **Session ID**: `63a12907-b0d6-4271-b44b-1df396d3afb1`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:36.792

---

### 错误 #88

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\63a12907-b0d6-4271-b44b-1df396d3afb1.jsonl`
- **Session ID**: `63a12907-b0d6-4271-b44b-1df396d3afb1`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:36.792

---

### 错误 #89

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\63a12907-b0d6-4271-b44b-1df396d3afb1.jsonl`
- **Session ID**: `63a12907-b0d6-4271-b44b-1df396d3afb1`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:36.792

---

### 错误 #90

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\63a12907-b0d6-4271-b44b-1df396d3afb1.jsonl`
- **Session ID**: `63a12907-b0d6-4271-b44b-1df396d3afb1`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:36.792

---

### 错误 #91

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
- **时间戳**: 2026-04-21T13:49:36.873

---

### 错误 #92

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
- **时间戳**: 2026-04-21T13:49:36.873

---

### 错误 #93

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
- **时间戳**: 2026-04-21T13:49:36.873

---

### 错误 #94

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 217
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #95

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 218
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #96

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 220
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #97

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 221
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #98

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 222
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #99

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 223
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #100

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 225
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #101

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 226
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #102

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 227
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #103

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 228
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #104

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 230
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #105

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 231
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #106

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 232
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #107

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 233
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #108

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 235
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #109

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 236
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #110

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 237
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #111

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 238
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #112

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 240
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #113

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 241
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #114

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 242
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #115

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 243
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #116

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 245
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #117

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 246
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #118

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 247
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #119

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 248
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #120

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 250
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #121

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 251
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #122

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 252
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #123

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 253
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #124

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 255
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #125

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 256
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #126

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 257
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #127

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 258
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #128

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 22
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #129

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 23
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #130

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 24
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #131

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 25
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #132

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 27
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #133

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 28
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #134

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 29
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #135

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 30
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #136

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 32
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #137

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 33
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #138

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 34
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #139

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 35
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #140

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 37
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #141

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 38
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #142

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 39
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #143

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 40
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #144

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 42
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #145

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 43
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #146

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 44
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #147

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 45
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #148

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 47
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #149

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 48
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #150

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 49
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #151

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 50
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #152

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 52
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #153

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 53
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #154

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 54
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #155

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 55
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #156

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 61
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #157

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 62
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #158

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 63
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #159

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 64
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #160

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 66
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #161

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 67
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #162

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 68
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #163

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 69
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #164

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 71
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #165

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 72
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #166

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 73
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #167

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 74
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #168

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 76
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #169

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 77
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #170

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 78
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #171

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 79
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #172

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 81
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #173

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 82
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #174

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 83
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #175

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 19:07 GMT+8] 帮我运行一次`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 84
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #176

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:13 GMT+8] 你有哪些skill是？`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 86
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #177

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:13 GMT+8] 你有哪些skill是？`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 87
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #178

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:13 GMT+8] 你有哪些skill是？`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 88
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #179

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:13 GMT+8] 你有哪些skill是？`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 89
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #180

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:15 GMT+8] 调用imap-smtp-email技能查询最近的10封邮件`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 109
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #181

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:15 GMT+8] 调用imap-smtp-email技能查询最近的10封邮件`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 110
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #182

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 125
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #183

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 126
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #184

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 127
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #185

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 128
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #186

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 130
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #187

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 131
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #188

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 132
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #189

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 133
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #190

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 135
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #191

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 136
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #192

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 137
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #193

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 138
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #194

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 140
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #195

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 141
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #196

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 142
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #197

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 143
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #198

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 145
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #199

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 146
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #200

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 147
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #201

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 148
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #202

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 150
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #203

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 151
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #204

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 152
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #205

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 153
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #206

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 155
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #207

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 156
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #208

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 157
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #209

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 158
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #210

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 160
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #211

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 161
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #212

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 162
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #213

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 163
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #214

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 165
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #215

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 166
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #216

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 167
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #217

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 168
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #218

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 170
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #219

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 171
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #220

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 172
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #221

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 173
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #222

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 175
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #223

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 176
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #224

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 177
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #225

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 178
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #226

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 180
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #227

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 181
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #228

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 182
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #229

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 183
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #230

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 185
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #231

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 186
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #232

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 187
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #233

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 188
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #234

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 190
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #235

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 191
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #236

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 192
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #237

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 193
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #238

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 195
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #239

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 196
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #240

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 197
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #241

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 198
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #242

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 200
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #243

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 201
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #244

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 202
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #245

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 203
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #246

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 205
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #247

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 206
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #248

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 207
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #249

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 208
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #250

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 210
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #251

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 211
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #252

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 212
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #253

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 213
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #254

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 215
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #255

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:21 GMT+8] 授权码都是e91049a2235e4fec`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 216
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #256

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
- **时间戳**: 2026-04-21T13:49:37.018

---

### 错误 #257

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
- **时间戳**: 2026-04-21T13:49:37.088

---

### 错误 #258

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #259

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #260

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #261

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #262

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #263

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
- **时间戳**: 2026-04-21T13:49:37.097

---

### 错误 #264

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
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #265

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49625 input tokens (16384 > 65536 - 49625). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 149
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #266

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 49635 input tokens (16384 > 65536 - 49635). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 152
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #267

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50137 input tokens (16384 > 65536 - 50137). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 155
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #268

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51119 input tokens (16384 > 65536 - 51119). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 158
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #269

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51677 input tokens (16384 > 65536 - 51677). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 161
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #270

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52235 input tokens (16384 > 65536 - 52235). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 164
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #271

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51945 input tokens (16384 > 65536 - 51945). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 167
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #272

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52954 input tokens (16384 > 65536 - 52954). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 170
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #273

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53315 input tokens (16384 > 65536 - 53315). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 173
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #274

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53676 input tokens (16384 > 65536 - 53676). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 176
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #275

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53166 input tokens (16384 > 65536 - 53166). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 179
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #276

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54424 input tokens (16384 > 65536 - 54424). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 182
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #277

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54798 input tokens (16384 > 65536 - 54798). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 185
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #278

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55172 input tokens (16384 > 65536 - 55172). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 188
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #279

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54355 input tokens (16384 > 65536 - 54355). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 191
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #280

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55606 input tokens (16384 > 65536 - 55606). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 194
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #281

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55737 input tokens (16384 > 65536 - 55737). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 197
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #282

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
400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 55868 input tokens (16384 > 65536 - 55868). (parameter=max_tokens, value=16384)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\b487297d6f8f74a2f1bced0cfbb32195bbbe3294ebeb0cdfbff67144cf38d843240dd65c30cbd9cab73ae5800a5a6c75aaea3f1e23a6cfee9dbc6cc71c352753\agents\main\sessions\0f678300-9756-4ea9-b283-9cf231eaba5f.jsonl`
- **Session ID**: `0f678300-9756-4ea9-b283-9cf231eaba5f`
- **行号**: 200
- **时间戳**: 2026-04-21T13:49:37.175

---

### 错误 #283

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
- **时间戳**: 2026-04-21T13:49:37.339

---

### 错误 #284

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
- **时间戳**: 2026-04-21T13:49:37.398

---

### 错误 #285

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
- **时间戳**: 2026-04-21T13:49:37.398

---

### 错误 #286

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
- **时间戳**: 2026-04-21T13:49:37.398

---

### 错误 #287

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
- **时间戳**: 2026-04-21T13:49:37.398

---

### 错误 #288

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
- **时间戳**: 2026-04-21T13:49:37.420

---

### 错误 #289

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 17:59 GMT+8] 定时任务最新的运行结果里，找到了3封邮件，但是我要求的是最近10分钟收到的邮件，那3封邮件都超过了时间。为什么会这样？如何...`
- **错误信息**: 
````
400 messages 字段值文本已超过最大输入上限65536
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl.reset.2026-04-10T10-05-09.303Z`
- **Session ID**: `6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5`
- **行号**: 115
- **时间戳**: 2026-04-21T13:49:37.424

---

### 错误 #290

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 18:01 GMT+8] 定时任务最新的运行结果里，找到了3封邮件，但是我要求的是最近10分钟收到的邮件，那3封邮件都超过了时间。为什么会这样？如何...`
- **错误信息**: 
````
400 messages 字段值文本已超过最大输入上限65536
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl.reset.2026-04-10T10-05-09.303Z`
- **Session ID**: `6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5`
- **行号**: 118
- **时间戳**: 2026-04-21T13:49:37.424

---

### 错误 #291

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
- **时间戳**: 2026-04-21T13:49:37.645

---

### 错误 #292

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 18:57 GMT+8] “投诉邮件每日汇总”任务改成查询当天的邮件`
- **错误信息**: 
````
400 messages 字段值文本已超过最大输入上限65536
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl.reset.2026-04-10T11-04-48.721Z`
- **Session ID**: `da886915-20d8-4f78-92c9-1ad408308d38`
- **行号**: 69
- **时间戳**: 2026-04-21T13:49:37.731

---

### 错误 #293

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #294

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #295

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #296

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #297

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #298

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #299

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #300

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\72206d26-abcf-493d-b85c-740e6c3e80bf.jsonl`
- **Session ID**: `72206d26-abcf-493d-b85c-740e6c3e80bf`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:37.956

---

### 错误 #301

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\72206d26-abcf-493d-b85c-740e6c3e80bf.jsonl`
- **Session ID**: `72206d26-abcf-493d-b85c-740e6c3e80bf`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:37.956

---

### 错误 #302

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\72206d26-abcf-493d-b85c-740e6c3e80bf.jsonl`
- **Session ID**: `72206d26-abcf-493d-b85c-740e6c3e80bf`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:37.956

---

### 错误 #303

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\72206d26-abcf-493d-b85c-740e6c3e80bf.jsonl`
- **Session ID**: `72206d26-abcf-493d-b85c-740e6c3e80bf`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:37.956

---

### 错误 #304

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7411e78a-e81d-4d5c-bc56-c543a7c933fa.jsonl`
- **Session ID**: `7411e78a-e81d-4d5c-bc56-c543a7c933fa`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:37.984

---

### 错误 #305

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7411e78a-e81d-4d5c-bc56-c543a7c933fa.jsonl`
- **Session ID**: `7411e78a-e81d-4d5c-bc56-c543a7c933fa`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:37.984

---

### 错误 #306

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7411e78a-e81d-4d5c-bc56-c543a7c933fa.jsonl`
- **Session ID**: `7411e78a-e81d-4d5c-bc56-c543a7c933fa`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:37.984

---

### 错误 #307

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7411e78a-e81d-4d5c-bc56-c543a7c933fa.jsonl`
- **Session ID**: `7411e78a-e81d-4d5c-bc56-c543a7c933fa`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:37.984

---

### 错误 #308

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\768719fa-c1c3-47b4-9c35-fe69190fac6e.jsonl`
- **Session ID**: `768719fa-c1c3-47b4-9c35-fe69190fac6e`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.041

---

### 错误 #309

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\768719fa-c1c3-47b4-9c35-fe69190fac6e.jsonl`
- **Session ID**: `768719fa-c1c3-47b4-9c35-fe69190fac6e`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.041

---

### 错误 #310

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\768719fa-c1c3-47b4-9c35-fe69190fac6e.jsonl`
- **Session ID**: `768719fa-c1c3-47b4-9c35-fe69190fac6e`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.041

---

### 错误 #311

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\768719fa-c1c3-47b4-9c35-fe69190fac6e.jsonl`
- **Session ID**: `768719fa-c1c3-47b4-9c35-fe69190fac6e`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.041

---

### 错误 #312

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7a9dd51b-19c0-4d85-b80e-71863f1a2219.jsonl`
- **Session ID**: `7a9dd51b-19c0-4d85-b80e-71863f1a2219`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.091

---

### 错误 #313

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7a9dd51b-19c0-4d85-b80e-71863f1a2219.jsonl`
- **Session ID**: `7a9dd51b-19c0-4d85-b80e-71863f1a2219`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.091

---

### 错误 #314

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7a9dd51b-19c0-4d85-b80e-71863f1a2219.jsonl`
- **Session ID**: `7a9dd51b-19c0-4d85-b80e-71863f1a2219`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.091

---

### 错误 #315

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7a9dd51b-19c0-4d85-b80e-71863f1a2219.jsonl`
- **Session ID**: `7a9dd51b-19c0-4d85-b80e-71863f1a2219`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.091

---

### 错误 #316

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
- **时间戳**: 2026-04-21T13:49:38.101

---

### 错误 #317

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
- **时间戳**: 2026-04-21T13:49:38.101

---

### 错误 #318

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 18100774
- **姓名**: 曹睿
- **部门**: 18100000
- **用户输入**: `Pre-compaction memory flush. Store durable memories only in memory/2026-03-31.md (create memory/ if needed). Treat workspace bootstrap/reference files such as MEMORY.md, SOUL.md, TOOLS.md, and AGENTS....`
- **错误信息**: 
````
400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 58297 input tokens (8192 > 65536 - 58297). (parameter=max_tokens, value=8192)
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\e82a63b4e5707d2608b9934c9266f851b29f2330a215009260a56daa48c47e575bedabfcc33ef2700b5c722e5e32f5f4d0060d4b0a8f13a677754aae776ce452\agents\main\sessions\5a7e6f9d-4c43-4a9a-820e-5ba304317da6.jsonl`
- **Session ID**: `5a7e6f9d-4c43-4a9a-820e-5ba304317da6`
- **行号**: 125
- **时间戳**: 2026-04-21T13:49:38.101

---

### 错误 #319

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7b2d9208-e322-497a-8cfe-97771e3234a0.jsonl`
- **Session ID**: `7b2d9208-e322-497a-8cfe-97771e3234a0`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.126

---

### 错误 #320

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7b2d9208-e322-497a-8cfe-97771e3234a0.jsonl`
- **Session ID**: `7b2d9208-e322-497a-8cfe-97771e3234a0`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.126

---

### 错误 #321

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7b2d9208-e322-497a-8cfe-97771e3234a0.jsonl`
- **Session ID**: `7b2d9208-e322-497a-8cfe-97771e3234a0`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.126

---

### 错误 #322

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\7b2d9208-e322-497a-8cfe-97771e3234a0.jsonl`
- **Session ID**: `7b2d9208-e322-497a-8cfe-97771e3234a0`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.126

---

### 错误 #323

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
- **时间戳**: 2026-04-21T13:49:38.152

---

### 错误 #324

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\8b9ee256-47a5-4a34-888e-09acbc59dfb5.jsonl`
- **Session ID**: `8b9ee256-47a5-4a34-888e-09acbc59dfb5`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.356

---

### 错误 #325

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\8b9ee256-47a5-4a34-888e-09acbc59dfb5.jsonl`
- **Session ID**: `8b9ee256-47a5-4a34-888e-09acbc59dfb5`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.356

---

### 错误 #326

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\8b9ee256-47a5-4a34-888e-09acbc59dfb5.jsonl`
- **Session ID**: `8b9ee256-47a5-4a34-888e-09acbc59dfb5`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.356

---

### 错误 #327

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\8b9ee256-47a5-4a34-888e-09acbc59dfb5.jsonl`
- **Session ID**: `8b9ee256-47a5-4a34-888e-09acbc59dfb5`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.356

---

### 错误 #328

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\92df639c-fc69-40d4-b070-569e7916b2ab.jsonl`
- **Session ID**: `92df639c-fc69-40d4-b070-569e7916b2ab`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.442

---

### 错误 #329

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\92df639c-fc69-40d4-b070-569e7916b2ab.jsonl`
- **Session ID**: `92df639c-fc69-40d4-b070-569e7916b2ab`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.442

---

### 错误 #330

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\92df639c-fc69-40d4-b070-569e7916b2ab.jsonl`
- **Session ID**: `92df639c-fc69-40d4-b070-569e7916b2ab`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.442

---

### 错误 #331

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\92df639c-fc69-40d4-b070-569e7916b2ab.jsonl`
- **Session ID**: `92df639c-fc69-40d4-b070-569e7916b2ab`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.442

---

### 错误 #332

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\96fd5232-0b83-4f78-a8da-0ef429c013fa.jsonl`
- **Session ID**: `96fd5232-0b83-4f78-a8da-0ef429c013fa`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.461

---

### 错误 #333

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\96fd5232-0b83-4f78-a8da-0ef429c013fa.jsonl`
- **Session ID**: `96fd5232-0b83-4f78-a8da-0ef429c013fa`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.461

---

### 错误 #334

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\96fd5232-0b83-4f78-a8da-0ef429c013fa.jsonl`
- **Session ID**: `96fd5232-0b83-4f78-a8da-0ef429c013fa`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.461

---

### 错误 #335

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\96fd5232-0b83-4f78-a8da-0ef429c013fa.jsonl`
- **Session ID**: `96fd5232-0b83-4f78-a8da-0ef429c013fa`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.461

---

### 错误 #336

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\9c1eeaaa-9761-44b9-9627-58abb48fc1d1.jsonl`
- **Session ID**: `9c1eeaaa-9761-44b9-9627-58abb48fc1d1`
- **行号**: 22
- **时间戳**: 2026-04-21T13:49:38.651

---

### 错误 #337

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\9c1eeaaa-9761-44b9-9627-58abb48fc1d1.jsonl`
- **Session ID**: `9c1eeaaa-9761-44b9-9627-58abb48fc1d1`
- **行号**: 23
- **时间戳**: 2026-04-21T13:49:38.651

---

### 错误 #338

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 18
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #339

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 33
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #340

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 34
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #341

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 35
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #342

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 58
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #343

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 59
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #344

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 60
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #345

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 71
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #346

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 72
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #347

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 73
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #348

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 74
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #349

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b7b0c93f-add1-436b-a081-546a0c93c9a3.jsonl`
- **Session ID**: `b7b0c93f-add1-436b-a081-546a0c93c9a3`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.779

---

### 错误 #350

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b7b0c93f-add1-436b-a081-546a0c93c9a3.jsonl`
- **Session ID**: `b7b0c93f-add1-436b-a081-546a0c93c9a3`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.779

---

### 错误 #351

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b7b0c93f-add1-436b-a081-546a0c93c9a3.jsonl`
- **Session ID**: `b7b0c93f-add1-436b-a081-546a0c93c9a3`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.779

---

### 错误 #352

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b7b0c93f-add1-436b-a081-546a0c93c9a3.jsonl`
- **Session ID**: `b7b0c93f-add1-436b-a081-546a0c93c9a3`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.779

---

### 错误 #353

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\c93b052d-01a4-47be-8320-5aee52003af0.jsonl`
- **Session ID**: `c93b052d-01a4-47be-8320-5aee52003af0`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.817

---

### 错误 #354

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\c93b052d-01a4-47be-8320-5aee52003af0.jsonl`
- **Session ID**: `c93b052d-01a4-47be-8320-5aee52003af0`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.817

---

### 错误 #355

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\c93b052d-01a4-47be-8320-5aee52003af0.jsonl`
- **Session ID**: `c93b052d-01a4-47be-8320-5aee52003af0`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.817

---

### 错误 #356

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\c93b052d-01a4-47be-8320-5aee52003af0.jsonl`
- **Session ID**: `c93b052d-01a4-47be-8320-5aee52003af0`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.817

---

### 错误 #357

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\cba87e4e-5268-4c6d-9845-735b24e21639.jsonl`
- **Session ID**: `cba87e4e-5268-4c6d-9845-735b24e21639`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.837

---

### 错误 #358

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\cba87e4e-5268-4c6d-9845-735b24e21639.jsonl`
- **Session ID**: `cba87e4e-5268-4c6d-9845-735b24e21639`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.837

---

### 错误 #359

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\cba87e4e-5268-4c6d-9845-735b24e21639.jsonl`
- **Session ID**: `cba87e4e-5268-4c6d-9845-735b24e21639`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.837

---

### 错误 #360

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\cba87e4e-5268-4c6d-9845-735b24e21639.jsonl`
- **Session ID**: `cba87e4e-5268-4c6d-9845-735b24e21639`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.837

---

### 错误 #361

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ceb08420-add6-47e8-b284-b7ded766057c.jsonl`
- **Session ID**: `ceb08420-add6-47e8-b284-b7ded766057c`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:38.857

---

### 错误 #362

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ceb08420-add6-47e8-b284-b7ded766057c.jsonl`
- **Session ID**: `ceb08420-add6-47e8-b284-b7ded766057c`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:38.857

---

### 错误 #363

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ceb08420-add6-47e8-b284-b7ded766057c.jsonl`
- **Session ID**: `ceb08420-add6-47e8-b284-b7ded766057c`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:38.857

---

### 错误 #364

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ceb08420-add6-47e8-b284-b7ded766057c.jsonl`
- **Session ID**: `ceb08420-add6-47e8-b284-b7ded766057c`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:38.857

---

### 错误 #365

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
terminated
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\d563fcfc-375c-4423-82e0-0be9cf40984b.jsonl`
- **Session ID**: `d563fcfc-375c-4423-82e0-0be9cf40984b`
- **行号**: 12
- **时间戳**: 2026-04-21T13:49:38.931

---

### 错误 #366

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\d563fcfc-375c-4423-82e0-0be9cf40984b.jsonl`
- **Session ID**: `d563fcfc-375c-4423-82e0-0be9cf40984b`
- **行号**: 13
- **时间戳**: 2026-04-21T13:49:38.931

---

### 错误 #367

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\d563fcfc-375c-4423-82e0-0be9cf40984b.jsonl`
- **Session ID**: `d563fcfc-375c-4423-82e0-0be9cf40984b`
- **行号**: 14
- **时间戳**: 2026-04-21T13:49:38.931

---

### 错误 #368

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\d563fcfc-375c-4423-82e0-0be9cf40984b.jsonl`
- **Session ID**: `d563fcfc-375c-4423-82e0-0be9cf40984b`
- **行号**: 15
- **时间戳**: 2026-04-21T13:49:38.931

---

### 错误 #369

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\db00c5eb-a386-4f7d-82e8-74dd65248084.jsonl`
- **Session ID**: `db00c5eb-a386-4f7d-82e8-74dd65248084`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.014

---

### 错误 #370

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\db00c5eb-a386-4f7d-82e8-74dd65248084.jsonl`
- **Session ID**: `db00c5eb-a386-4f7d-82e8-74dd65248084`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.014

---

### 错误 #371

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\db00c5eb-a386-4f7d-82e8-74dd65248084.jsonl`
- **Session ID**: `db00c5eb-a386-4f7d-82e8-74dd65248084`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.014

---

### 错误 #372

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\db00c5eb-a386-4f7d-82e8-74dd65248084.jsonl`
- **Session ID**: `db00c5eb-a386-4f7d-82e8-74dd65248084`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.014

---

### 错误 #373

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\df2384ab-836d-490c-a1a7-042478b1951e.jsonl`
- **Session ID**: `df2384ab-836d-490c-a1a7-042478b1951e`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.030

---

### 错误 #374

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\df2384ab-836d-490c-a1a7-042478b1951e.jsonl`
- **Session ID**: `df2384ab-836d-490c-a1a7-042478b1951e`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.030

---

### 错误 #375

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\df2384ab-836d-490c-a1a7-042478b1951e.jsonl`
- **Session ID**: `df2384ab-836d-490c-a1a7-042478b1951e`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.030

---

### 错误 #376

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\df2384ab-836d-490c-a1a7-042478b1951e.jsonl`
- **Session ID**: `df2384ab-836d-490c-a1a7-042478b1951e`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.030

---

### 错误 #377

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e1af6529-edef-4e22-8e25-06ce10c5a845.jsonl`
- **Session ID**: `e1af6529-edef-4e22-8e25-06ce10c5a845`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.065

---

### 错误 #378

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e1af6529-edef-4e22-8e25-06ce10c5a845.jsonl`
- **Session ID**: `e1af6529-edef-4e22-8e25-06ce10c5a845`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.065

---

### 错误 #379

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e1af6529-edef-4e22-8e25-06ce10c5a845.jsonl`
- **Session ID**: `e1af6529-edef-4e22-8e25-06ce10c5a845`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.065

---

### 错误 #380

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e1af6529-edef-4e22-8e25-06ce10c5a845.jsonl`
- **Session ID**: `e1af6529-edef-4e22-8e25-06ce10c5a845`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.065

---

### 错误 #381

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用imap-smtp-email技能的imap功能检查我的邮箱中最近10分钟内收到的邮件。找出其中有时间限制、特别紧急的邮件（如截止日期临近、会议即将开始、需要立即处理的事项等）。将这些紧急邮件的主题和关键信息整理成一封邮件，然后使用imap-smtp-email技能的smtp功...`
- **错误信息**: 
````
很抱歉，关于这个问题我无法提供相应的信息。如果您有其他问题，我将很愿意为您回答。
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e4fedafb-43c1-43e2-8ecb-a00aca5bef29.jsonl.deleted.2026-04-13T00-53-49.391Z`
- **Session ID**: `e4fedafb-43c1-43e2-8ecb-a00aca5bef29`
- **行号**: 38
- **时间戳**: 2026-04-21T13:49:39.092

---

### 错误 #382

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: aborted
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 17:27 GMT+8] 查一下你的日志，上次运行为什么失败了？`
- **错误信息**: 
````
Request was aborted.
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e4fedafb-43c1-43e2-8ecb-a00aca5bef29.jsonl.deleted.2026-04-13T00-53-49.391Z`
- **Session ID**: `e4fedafb-43c1-43e2-8ecb-a00aca5bef29`
- **行号**: 53
- **时间戳**: 2026-04-21T13:49:39.092

---

### 错误 #383

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e877bd15-2f2d-40aa-a323-fcad2600b2a9.jsonl`
- **Session ID**: `e877bd15-2f2d-40aa-a323-fcad2600b2a9`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.148

---

### 错误 #384

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e877bd15-2f2d-40aa-a323-fcad2600b2a9.jsonl`
- **Session ID**: `e877bd15-2f2d-40aa-a323-fcad2600b2a9`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.148

---

### 错误 #385

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e877bd15-2f2d-40aa-a323-fcad2600b2a9.jsonl`
- **Session ID**: `e877bd15-2f2d-40aa-a323-fcad2600b2a9`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.148

---

### 错误 #386

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e877bd15-2f2d-40aa-a323-fcad2600b2a9.jsonl`
- **Session ID**: `e877bd15-2f2d-40aa-a323-fcad2600b2a9`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.148

---

### 错误 #387

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d.jsonl`
- **Session ID**: `eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.195

---

### 错误 #388

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d.jsonl`
- **Session ID**: `eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.195

---

### 错误 #389

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d.jsonl`
- **Session ID**: `eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.195

---

### 错误 #390

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d.jsonl`
- **Session ID**: `eb4ac9f4-9462-4c1b-9419-681b3c0f8c9d`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.195

---

### 错误 #391

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ed56c709-ee3d-4b8f-9759-9ca4ffe72364.jsonl`
- **Session ID**: `ed56c709-ee3d-4b8f-9759-9ca4ffe72364`
- **行号**: 6
- **时间戳**: 2026-04-21T13:49:39.214

---

### 错误 #392

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ed56c709-ee3d-4b8f-9759-9ca4ffe72364.jsonl`
- **Session ID**: `ed56c709-ee3d-4b8f-9759-9ca4ffe72364`
- **行号**: 7
- **时间戳**: 2026-04-21T13:49:39.214

---

### 错误 #393

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ed56c709-ee3d-4b8f-9759-9ca4ffe72364.jsonl`
- **Session ID**: `ed56c709-ee3d-4b8f-9759-9ca4ffe72364`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:39.214

---

### 错误 #394

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
Connection error.
````
- **原因分析**: 模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ed56c709-ee3d-4b8f-9759-9ca4ffe72364.jsonl`
- **Session ID**: `ed56c709-ee3d-4b8f-9759-9ca4ffe72364`
- **行号**: 9
- **时间戳**: 2026-04-21T13:49:39.214

---

### 错误 #395

- **事件类型**: `message`
- **描述**: 检测到异常停止原因: error
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ee02ba14-71d6-4fc5-ad8e-e50e5f6d040c.jsonl`
- **Session ID**: `ee02ba14-71d6-4fc5-ad8e-e50e5f6d040c`
- **行号**: 32
- **时间戳**: 2026-04-21T13:49:39.236

---

## timeoutErrors - 超时错误 (30)

### 错误 #396

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
- **时间戳**: 2026-04-21T13:49:34.516
- **Run ID**: `b8a86d98-7887-4263-90d8-d5e5c0153909`

---

### 错误 #397

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用imap-smtp-email技能的imap功能检查我的邮箱中最近10分钟内收到的邮件。找出其中有时间限制、特别紧急的邮件（如截止日期临近、会议即将开始、需要立即处理的事项等）。将这些紧急邮件的主题和关键信息整理成一封邮件，然后使用imap-smtp-email技能的smtp功...`
- **错误信息**: 
````
{"timestamp":1775805849403,"runId":"2205f579-0df8-4b92-ba65-e9210d5b1f37","sessionId":"2205f579-0df8-4b92-ba65-e9210d5b1f37","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\2205f579-0df8-4b92-ba65-e9210d5b1f37.jsonl.deleted.2026-04-13T00-53-49.390Z`
- **Session ID**: `2205f579-0df8-4b92-ba65-e9210d5b1f37`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.114
- **Run ID**: `2205f579-0df8-4b92-ba65-e9210d5b1f37`

---

### 错误 #398

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
- **时间戳**: 2026-04-21T13:49:35.282
- **Run ID**: `aba0cdf6-68d5-4842-a735-b4adad95ff4c`

---

### 错误 #399

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
- **时间戳**: 2026-04-21T13:49:35.462
- **Run ID**: `bb3c513f-d87e-448f-8014-614e40c21906`

---

### 错误 #400

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请检查我的邮箱中最近10分钟内收到的邮件。找出其中有时间限制、特别紧急的邮件（如截止日期临近、会议即将开始、需要立即处理的事项等）。将这些紧急邮件的主题和关键信息整理成一封邮件，然后使用imap-smtp-email技能的smtp功能将这封邮件发送给我自己。邮件主题请包含'【紧急邮件...`
- **错误信息**: 
````
{"timestamp":1775805595705,"runId":"34de3e79-209a-4386-a7b2-83181ad9924a","sessionId":"34de3e79-209a-4386-a7b2-83181ad9924a","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\34de3e79-209a-4386-a7b2-83181ad9924a.jsonl.deleted.2026-04-13T00-53-49.390Z`
- **Session ID**: `34de3e79-209a-4386-a7b2-83181ad9924a`
- **行号**: 8
- **时间戳**: 2026-04-21T13:49:35.632
- **Run ID**: `34de3e79-209a-4386-a7b2-83181ad9924a`

---

### 错误 #401

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
- **时间戳**: 2026-04-21T13:49:35.664
- **Run ID**: `0da67fde-8212-48e1-aaec-2bf06e64800d`

---

### 错误 #402

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 6fca9aa611
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
- **时间戳**: 2026-04-21T13:49:36.438
- **Run ID**: `237cc3e6-bd84-4004-8086-704bedb2fe42`

---

### 错误 #403

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
- **时间戳**: 2026-04-21T13:49:37.065
- **Run ID**: `bd352a63-b3a1-40de-ad85-384f60bb7a9a`

---

### 错误 #404

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
- **时间戳**: 2026-04-21T13:49:37.159
- **Run ID**: `010bceeb-4f2b-4b81-acf0-7a01daee7b26`

---

### 错误 #405

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
- **时间戳**: 2026-04-21T13:49:37.175
- **Run ID**: `req_1776147850337_tyub0lfc0`

---

### 错误 #406

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
- **时间戳**: 2026-04-21T13:49:37.175
- **Run ID**: `req_1776148910958_kbpe7zfuk`

---

### 错误 #407

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
- **时间戳**: 2026-04-21T13:49:37.382
- **Run ID**: `574deee7-91d2-4251-8ab6-348eb9cadac3`

---

### 错误 #408

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
- **时间戳**: 2026-04-21T13:49:37.435
- **Run ID**: `25f6a0f7-6100-45bf-a238-3c1bde61470d`

---

### 错误 #409

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
- **时间戳**: 2026-04-21T13:49:37.679
- **Run ID**: `a68d9714-a191-40b6-9d65-30d26303535a`

---

### 错误 #410

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
- **时间戳**: 2026-04-21T13:49:37.846
- **Run ID**: `421add1e-43ff-4965-894d-176cf2f736d0`

---

### 错误 #411

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
- **时间戳**: 2026-04-21T13:49:38.030
- **Run ID**: `27ca7b27-88b7-4ee2-8d53-d0c795bfe759`

---

### 错误 #412

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
- **时间戳**: 2026-04-21T13:49:38.061
- **Run ID**: `c4c8ea24-93a8-431a-aa6f-3f891ee544d9`

---

### 错误 #413

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
- **时间戳**: 2026-04-21T13:49:38.091
- **Run ID**: `f05dfe06-c8f8-4a25-b16e-01468e47c033`

---

### 错误 #414

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
- **时间戳**: 2026-04-21T13:49:38.190
- **Run ID**: `bbae6408-de89-479f-90f0-235dd832faed`

---

### 错误 #415

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
- **时间戳**: 2026-04-21T13:49:38.216
- **Run ID**: `4020997d-ba23-4765-be3d-419acf130ddc`

---

### 错误 #416

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
- **时间戳**: 2026-04-21T13:49:38.447
- **Run ID**: `68d03430-23ec-4958-b0fa-9b1f2fe9325e`

---

### 错误 #417

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
- **时间戳**: 2026-04-21T13:49:38.474
- **Run ID**: `a169213c-b705-4a42-8164-7f40fc703801`

---

### 错误 #418

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-tui",
  "id": "openclaw-tui",
  "name": "openclaw-tui",
  "username": "openclaw-tui"
}
\`\`\`

[Fri 2026-04-10 10:44 GMT+8] 重新加载一下你的skill，特别是/h...`
- **错误信息**: 
````
{"timestamp":1775791859885,"runId":"0a6c090b-ef52-43b5-8019-c0172f235561","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 43
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `0a6c090b-ef52-43b5-8019-c0172f235561`

---

### 错误 #419

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `[cron:2c8c9729-3397-4f79-aa67-644380cce4ab 每分钟测试消息] 发送一条消息给用户，内容是"这是一条测试消息，当前时间是: {当前时间}"，把{当前时间}替换为实际的当前时间。使用 Asia/Shanghai 时区。
Current time: Friday, April 10th, 2026 - 1:42 PM (Asia/Shanghai) / 2026...`
- **错误信息**: 
````
{"timestamp":1775800756425,"runId":"cb0982d0-bac6-4322-8bab-4a49346b4d3d","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 90
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `cb0982d0-bac6-4322-8bab-4a49346b4d3d`

---

### 错误 #420

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `[cron:2c8c9729-3397-4f79-aa67-644380cce4ab 每分钟测试消息] 发送一条消息给用户，内容是"这是一条测试消息，当前时间是: {当前时间}"，把{当前时间}替换为实际的当前时间。使用 Asia/Shanghai 时区。
Current time: Friday, April 10th, 2026 - 1:42 PM (Asia/Shanghai) / 2026...`
- **错误信息**: 
````
{"timestamp":1775802586971,"runId":"c62297c1-c778-4082-970d-c00a785b5e81","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 92
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `c62297c1-c778-4082-970d-c00a785b5e81`

---

### 错误 #421

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `[cron:2c8c9729-3397-4f79-aa67-644380cce4ab 每分钟测试消息] 发送一条消息给用户，内容是"这是一条测试消息，当前时间是: {当前时间}"，把{当前时间}替换为实际的当前时间。使用 Asia/Shanghai 时区。
Current time: Friday, April 10th, 2026 - 1:42 PM (Asia/Shanghai) / 2026...`
- **错误信息**: 
````
{"timestamp":1775804518654,"runId":"fddbc810-8984-4f49-94fa-7b09013026a0","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 96
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `fddbc810-8984-4f49-94fa-7b09013026a0`

---

### 错误 #422

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 15:23 GMT+8] 你好`
- **错误信息**: 
````
{"timestamp":1775805894835,"runId":"6d14eb10-35a7-4012-a752-a0aa1b0fe78e","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 101
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `6d14eb10-35a7-4012-a752-a0aa1b0fe78e`

---

### 错误 #423

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到超时错误事件
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 15:23 GMT+8] 你好`
- **错误信息**: 
````
{"timestamp":1775806360895,"runId":"1e08f9e2-911a-46ec-9f06-40cbec61799c","sessionId":"986cc4b2-4eae-4661-b353-f1b5a10b4da7","provider":"custom-integrate-api-nvidia-com","model":"z-ai/glm5","api":"openai-completions","error":"LLM idle timeout (60s): no response from model | LLM idle timeout (60s): no response from model"}
````
- **原因分析**: 空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\986cc4b2-4eae-4661-b353-f1b5a10b4da7.checkpoint.d14585ef-4140-401f-9688-8df5d7559103.jsonl`
- **Session ID**: `986cc4b2-4eae-4661-b353-f1b5a10b4da7`
- **行号**: 103
- **时间戳**: 2026-04-21T13:49:38.489
- **Run ID**: `1e08f9e2-911a-46ec-9f06-40cbec61799c`

---

### 错误 #424

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
- **时间戳**: 2026-04-21T13:49:38.510
- **Run ID**: `766d9b83-aada-4e2e-9b95-75c228b3b61d`

---

### 错误 #425

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
- **时间戳**: 2026-04-21T13:49:38.530
- **Run ID**: `62ca17d5-cbc7-45a4-a5ea-7d5faeeb11d0`

---

## rateLimitErrors - 速率限制错误 (24)

### 错误 #426

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 48
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #427

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 49
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #428

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 50
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #429

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集昨天 9:00 到今天 9:00 之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索昨天 9:00 ...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z`
- **Session ID**: `0bef5f9c-4907-4730-bd17-cc5510fed2fc`
- **行号**: 51
- **时间戳**: 2026-04-21T13:49:34.440

---

### 错误 #430

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\36459a6f-ee3d-468e-b74f-1ffafdc6c8a7.jsonl`
- **Session ID**: `36459a6f-ee3d-468e-b74f-1ffafdc6c8a7`
- **行号**: 22
- **时间戳**: 2026-04-21T13:49:35.906

---

### 错误 #431

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\36459a6f-ee3d-468e-b74f-1ffafdc6c8a7.jsonl`
- **Session ID**: `36459a6f-ee3d-468e-b74f-1ffafdc6c8a7`
- **行号**: 23
- **时间戳**: 2026-04-21T13:49:35.906

---

### 错误 #432

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4c6bd2fa-6ba6-43d4-a894-13a236786a1d.jsonl`
- **Session ID**: `4c6bd2fa-6ba6-43d4-a894-13a236786a1d`
- **行号**: 24
- **时间戳**: 2026-04-21T13:49:36.453

---

### 错误 #433

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\4c6bd2fa-6ba6-43d4-a894-13a236786a1d.jsonl`
- **Session ID**: `4c6bd2fa-6ba6-43d4-a894-13a236786a1d`
- **行号**: 25
- **时间戳**: 2026-04-21T13:49:36.453

---

### 错误 #434

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:15 GMT+8] 调用imap-smtp-email技能查询最近的10封邮件`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 109
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #435

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Mon 2026-04-13 17:15 GMT+8] 调用imap-smtp-email技能查询最近的10封邮件`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl`
- **Session ID**: `c2b6491d-e34a-4f4e-ab6e-7cce30e9bc5b`
- **行号**: 110
- **时间戳**: 2026-04-21T13:49:36.929

---

### 错误 #436

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\9c1eeaaa-9761-44b9-9627-58abb48fc1d1.jsonl`
- **Session ID**: `9c1eeaaa-9761-44b9-9627-58abb48fc1d1`
- **行号**: 22
- **时间戳**: 2026-04-21T13:49:38.651

---

### 错误 #437

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\9c1eeaaa-9761-44b9-9627-58abb48fc1d1.jsonl`
- **Session ID**: `9c1eeaaa-9761-44b9-9627-58abb48fc1d1`
- **行号**: 23
- **时间戳**: 2026-04-21T13:49:38.651

---

### 错误 #438

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 18
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #439

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 33
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #440

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 34
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #441

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 35
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #442

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 58
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #443

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 59
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #444

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 60
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #445

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 71
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #446

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 72
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #447

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 73
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #448

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:4f652496-0267-4bbc-ab17-98c662b310f9 投诉邮件每日汇总] 请执行投诉邮件每日汇总任务：

## 任务目标
收集**当天 0:00 到当前时间**之间收到的投诉类邮件，下载附件并生成摘要，最后发送汇总报告到 nwftool@sina.com。

## 执行步骤

### 1. 搜索投诉邮件
使用 imap-smtp-email 技能搜索**今天 0:0...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\b43a28cf-a3a6-4060-a993-41853c383152.jsonl.deleted.2026-04-13T00-53-49.393Z`
- **Session ID**: `b43a28cf-a3a6-4060-a993-41853c383152`
- **行号**: 74
- **时间戳**: 2026-04-21T13:49:38.717

---

### 错误 #449

- **事件类型**: `message`
- **描述**: 在message事件中检测到速率限制错误
- **工号**: 0000000001
- **用户输入**: `[cron:8b7b52e4-3cb5-4ac7-8d95-4493b5cce81e 检查紧急邮件并发送邮件通知] 请使用 imap-smtp-email 技能的 imap 功能检查我的邮箱中最近 10 分钟内收到的邮件。

**重要：必须使用 --recent 10m 参数来严格限制时间范围**

执行步骤：
1. 运行命令：\`node scripts/imap.js check --recen...`
- **错误信息**: 
````
429 用户请求TPM超限，请减少tokens后重试
````
- **原因分析**: 触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\ee02ba14-71d6-4fc5-ad8e-e50e5f6d040c.jsonl`
- **Session ID**: `ee02ba14-71d6-4fc5-ad8e-e50e5f6d040c`
- **行号**: 32
- **时间戳**: 2026-04-21T13:49:39.236

---

## modelErrors - 模型API错误 (14)

### 错误 #450

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
- **时间戳**: 2026-04-21T13:49:34.516

---

### 错误 #451

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
- **时间戳**: 2026-04-21T13:49:34.516

---

### 错误 #452

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
- **时间戳**: 2026-04-21T13:49:34.516
- **Run ID**: `bc2b3f7b-2fae-4774-92b5-a36dc673385d`

---

### 错误 #453

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
- **时间戳**: 2026-04-21T13:49:34.516
- **Run ID**: `2e6ad39f-3981-4dfa-9e0e-8454d3961af2`

---

### 错误 #454

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
- **时间戳**: 2026-04-21T13:49:35.354
- **Run ID**: `req_1774868684378_4e84zalrb`

---

### 错误 #455

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
- **时间戳**: 2026-04-21T13:49:36.033
- **Run ID**: `req_1776302087795_5cms510hh`

---

### 错误 #456

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
- **时间戳**: 2026-04-21T13:49:37.398

---

### 错误 #457

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
- **时间戳**: 2026-04-21T13:49:37.398
- **Run ID**: `req_1775197362262_n7z2xlxi6`

---

### 错误 #458

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
- **时间戳**: 2026-04-21T13:49:37.785

---

### 错误 #459

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
- **时间戳**: 2026-04-21T13:49:37.785
- **Run ID**: `f73d774c-9773-48ae-a324-5d1e18eddad4`

---

### 错误 #460

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
- **时间戳**: 2026-04-21T13:49:38.022
- **Run ID**: `req_1775197972491_55uwzwguf`

---

### 错误 #461

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
- **时间戳**: 2026-04-21T13:49:38.445
- **Run ID**: `req_1775122020273_g1x9hzjom`

---

### 错误 #462

- **事件类型**: `message`
- **描述**: 在message事件中检测到模型API错误
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 17:27 GMT+8] 查一下你的日志，上次运行为什么失败了？`
- **错误信息**: 
````
Request was aborted.
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e4fedafb-43c1-43e2-8ecb-a00aca5bef29.jsonl.deleted.2026-04-13T00-53-49.391Z`
- **Session ID**: `e4fedafb-43c1-43e2-8ecb-a00aca5bef29`
- **行号**: 53
- **时间戳**: 2026-04-21T13:49:39.092

---

### 错误 #463

- **事件类型**: `openclaw:prompt-error`
- **描述**: 检测到模型API错误事件
- **工号**: 0000000001
- **用户输入**: `Sender (untrusted metadata):
\`\`\`json
{
  "label": "openclaw-control-ui",
  "id": "openclaw-control-ui"
}
\`\`\`

[Fri 2026-04-10 17:27 GMT+8] 查一下你的日志，上次运行为什么失败了？`
- **错误信息**: 
````
{"timestamp":1775813310875,"runId":"a9442ede-f1e1-438b-9a3f-1f7acd6be958","sessionId":"e4fedafb-43c1-43e2-8ecb-a00aca5bef29","provider":"custom-wishub-x6-ctyun-cn","model":"GLM-5","api":"openai-completions","error":"This operation was aborted | This operation was aborted"}
````
- **原因分析**: 请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\e4fedafb-43c1-43e2-8ecb-a00aca5bef29.jsonl.deleted.2026-04-13T00-53-49.391Z`
- **Session ID**: `e4fedafb-43c1-43e2-8ecb-a00aca5bef29`
- **行号**: 52
- **时间戳**: 2026-04-21T13:49:39.092
- **Run ID**: `a9442ede-f1e1-438b-9a3f-1f7acd6be958`

---

## flow_integrity_missing_final_answer - 工具执行后无最终回复 (3)

### 错误 #464

- **事件类型**: `message`
- **描述**: 工具执行完成后没有Assistant的最终回复（文件在此结束）
- **工号**: 0000000001
- **错误信息**: 
````
Expected assistant message after toolResult, but reached end of file
Line: c09ae55a
````
- **原因分析**: 可能的原因：1) 工具执行后会话被中断；2) 系统在处理结果时崩溃；3) 达到最大token限制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\3b3c99b0-41eb-4fe0-a29b-e80b634fe6f1.jsonl.deleted.2026-04-13T00-53-49.390Z`
- **Session ID**: `3b3c99b0-41eb-4fe0-a29b-e80b634fe6f1`
- **行号**: 19
- **时间戳**: 2026-04-21T13:49:36.345
- **错误行内容**: 
````
{"type":"message","id":"c09ae55a","parentId":"a59bbd1b","timestamp":"2026-04-10T07:49:44.852Z","message":{"role":"toolResult","toolCallId":"chatcmpl-tool-a3044ccdd4a875e9","toolName":"exec","content":[{"type":"text","text":"ERROR: Email credentials not configured\nPlease set EMAIL_USER and EMAIL_PASS environment variables\n/bin/bash: line 58: {}: command not found\n\nCommand not found"}],"details":{"status":"failed","exitCode":127,"durationMs":85,"aggregated":"ERROR: Email credentials not configured\nPlease set EMAIL_USER and EMAIL_PASS environment variables\n/bin/bash: line 58: {}: command not found","timedOut":false,"cwd":"/home/chinalife/.openclaw/workspace"},"isError":false,"timestamp":1775807384847}}
````

---

### 错误 #465

- **事件类型**: `message`
- **描述**: 工具执行完成后没有Assistant的最终回复（文件在此结束）
- **工号**: 0000000001
- **错误信息**: 
````
Expected assistant message after toolResult, but reached end of file
Line: 3c30a774
````
- **原因分析**: 可能的原因：1) 工具执行后会话被中断；2) 系统在处理结果时崩溃；3) 达到最大token限制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5.jsonl.bak-292-1775810216210`
- **Session ID**: `6bc1ee54-e320-4144-b77a-bcc1f9e7a5d5`
- **行号**: 33
- **时间戳**: 2026-04-21T13:49:37.350
- **错误行内容**: 
````
{"type":"message","id":"3c30a774","parentId":"7b9cfcc1","timestamp":"2026-04-10T07:58:19.907Z","message":{"role":"toolResult","toolCallId":"call_f70c27fbfe3a488e9e2d99f7","toolName":"exec","content":[{"type":"text","text":"[\n  {\n    \"uid\": 23,\n    \"from\": \"nwftool@sina.com\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"SMTP Connection Test\",\n    \"date\": \"2026-04-10T03:10:07.000Z\",\n    \"text\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"html\": \"<p>This is a <strong>test email</strong> from the IMAP/SMTP email skill.</p>\",\n    \"snippet\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 21,\n    \"from\": \"MAILER-DAEMON@smtp-250-24.mail.sina.com.cn\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"系统退信\",\n    \"date\": \"2026-04-10T03:08:19.000Z\",\n    \"text\": \"尊敬的用户，\\n\\n我们很遗憾的通知您，您发的邮件中有一个或多个接收人没有投递成功。\\n\\n退回的邮件在附件中，请查看附件了解详情。\\n\\n被退回邮件信息：\\n\\n服务器：\\n\\n退信原因：I couldn't find a mail exchanger or IP address. (#5.4.4)\\n\\n收件人：recipient@example.com\\n\\n退信原因查询：http://help.sina.com.cn/i/90/152_2.html\\n\\n\",\n    \"html\": false,\n    \"snippet\": \"尊敬的用户，\\n\\n我们很遗憾的通知您，您发的邮件中有一个或多个接收人没有投递成功。\\n\\n退回的邮件在附件中，请查看附件了解详情。\\n\\n被退回邮件信息：\\n\\n服务器：\\n\\n退信原因：I couldn't find a mail exchanger or IP address. (#5.4.4)\\n\\n收件人：recipient@example.com\\n\\n退信原因查询：http://help.sina.com.cn\",\n    \"attachments\": [\n      {\n        \"filename\": \"mail.eml\",\n        \"contentType\": \"message/rfc822\",\n        \"size\": 816\n      }\n    ],\n    \"flags\": [\n      \"\\\\Seen\"\n    ]\n  },\n  {\n    \"uid\": 19,\n    \"from\": \"nwftool@sina.com\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"SMTP Connection Test\",\n    \"date\": \"2026-04-10T03:06:25.000Z\",\n    \"text\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"html\": \"<p>This is a <strong>test email</strong> from the IMAP/SMTP email skill.</p>\",\n    \"snippet\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 17,\n    \"from\": \"\\\"GitHub\\\" <noreply@github.com>\",\n    \"to\": \"\\\"polarist1\\\" <nwftool@sina.com>\",\n    \"subject\": \"[GitHub] Please review this sign in\",\n    \"date\": \"2026-03-30T13:29:40.000Z\",\n    \"text\": \"Hey polarist1!\\n\\nYour GitHub account was successfully signed in to but we did not recognize the location of the sign in. You can review this sign in attempt by visiting https://github.com/settings/sessions/authentications/5268708872\\n\\nIf you recently signed in to your account, you do not need to take any further action.\\n\\nIf you did not sign in to your account, your password may be compromised. Visit https://github.com/settings/security to create a new, strong password for your GitHub account.\\n\\nFor more information, see https://docs.github.com/articles/keeping-your-account-and-data-secure/ in the GitHub Docs.\\n\\nTo see this and other security events for your account, visit https://github.com/settings/security-log\\n\\nIf you run into problems, please contact support by visiting https://github.com/contact\\n\\nThanks,\\nThe GitHub Team\\n\\n\",\n    \"html\": false,\n    \"snippet\": \"Hey polarist1!\\n\\nYour GitHub account was successfully signed in to but we did not recognize the location of the sign in. You can review this sign in attempt by visiting https://github.com/settings/sess\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 9,\n    \"from\": \"\\\"OpenRouter\\\" <notifications@openrouter.ai>\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"Your sign up link\",\n    \"date\": \"2026-03-16T00:53:36.000Z\",\n    \"text\": \"Use the following link to sign up to OpenRouter: https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\n\\nThis link will expire in 10 minutes.\\n\\nIt was requested at 16 March 2026, 00:53 UTC. If you did not request this, please ignore this email.\\n\",\n    \"html\": \"<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1.0 Transitional//EN\\\" \\\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\\\">\\n<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n    <head>\\n        <meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" />\\n        <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\" />\\n        <title>Sign up to OpenRouter</title>\\n        <style type=\\\"text/css\\\">\\n            #outlook a {\\n                padding: 0\\n            }\\n\\n            .ExternalClass {\\n                width: 100%\\n            }\\n\\n            .ExternalClass,\\n            .ExternalClass p,\\n            .ExternalClass span,\\n            .ExternalClass font,\\n            .ExternalClass td,\\n            .ExternalClass div {\\n                line-height: 100%\\n            }\\n\\n            body,\\n            table,\\n            td,\\n            a {\\n                -webkit-text-size-adjust: 100%;\\n                -ms-text-size-adjust: 100%\\n            }\\n\\n            table,\\n            td {\\n                mso-table-lspace: 0;\\n                mso-table-rspace: 0\\n            }\\n\\n            img {\\n                -ms-interpolation-mode: bicubic\\n            }\\n\\n            img {\\n                border: 0;\\n                outline: none;\\n                text-decoration: none\\n            }\\n\\n            a img {\\n                border: none\\n            }\\n\\n            td img {\\n                vertical-align: top\\n            }\\n\\n            table,\\n            table td {\\n                border-collapse: collapse\\n            }\\n\\n            body {\\n                margin: 0;\\n                padding: 0;\\n                width: 100% !important\\n            }\\n\\n            .mobile-spacer {\\n                width: 0;\\n                display: none\\n            }\\n\\n            @media all and (max-width:639px) {\\n                .container {\\n                    width: 100% !important;\\n                    max-width: 600px !important\\n                }\\n\\n                .mobile {\\n                    width: auto !important;\\n                    max-width: 100% !important;\\n                    display: block !important\\n                }\\n\\n                .mobile-center {\\n                    text-align: center !important\\n                }\\n\\n                .mobile-right {\\n                    text-align: right !important\\n                }\\n\\n                .mobile-left {\\n                    text-align: left !important;\\n                }\\n\\n                .mobile-hidden {\\n                    max-height: 0;\\n                    display: none !important;\\n                    mso-hide: all;\\n                    overflow: hidden\\n                }\\n\\n                .mobile-spacer {\\n                    width: auto !important;\\n                    display: table !important\\n                }\\n\\n                .mobile-image,\\n                .mobile-image img {\\n                    height: auto !important;\\n                    max-width: 600px !important;\\n                    width: 100% !important\\n                }\\n            }\\n\\n            .cl-card {\\n                border: 1px solid #EEEEF0;\\n                border-radius: 10px;\\n                border-collapse: separate !important;\\n                width: 100%\\n            }\\n\\n            .cl-label {\\n                background-color: #FAFAFB;\\n                color: #9ca3af;\\n                font-size: 14px;\\n                font-weight: normal;\\n                white-space: nowrap;\\n                padding: 6px 16px 6px 20px;\\n                vertical-align: top;\\n                line-height: 20px\\n            }\\n\\n            .cl-value {\\n                background-color: #FAFAFB;\\n                color: #111827;\\n                font-size: 14px;\\n                font-weight: 600;\\n                padding: 6px 20px 6px 0;\\n                vertical-align: top;\\n                line-height: 20px\\n            }\\n\\n            .cl-list {\\n                width: 100%;\\n                border-collapse: collapse\\n            }\\n\\n            .cl-list-item {\\n                padding: 10px 0;\\n                border-bottom: 1px dashed #B7B8C2;\\n                vertical-align: top\\n            }\\n\\n            .cl-list-link {\\n                color: #111827;\\n                font-size: 14px;\\n                font-weight: 600;\\n                line-height: 20px;\\n                text-decoration: underline;\\n                font-family: Helvetica, Arial, sans-serif\\n            }\\n\\n            .cl-list-meta {\\n                color: #9ca3af;\\n                font-size: 13px;\\n                font-weight: normal;\\n                line-height: 18px;\\n                font-family: Helvetica, Arial, sans-serif\\n            }\\n        </style>\\n        <!--[if mso]><style type=\\\"text/css\\\">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style><![endif]-->\\n    </head>\\n\\n    <body style=\\\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\\\">\\n      <div>\\n        <span style=\\\"color: transparent; display: none; height: 0px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden; visibility: hidden; width: 0px;\\\">Sign up to OpenRouter</span>\\n      </div>\\n        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\" class=\\\"body\\\" style=\\\"width: 100%;\\\">\\n            <tbody>\\n                <tr>\\n                    <td align=\\\"center\\\" valign=\\\"top\\\" style=\\\"vertical-align: top; line-height: 1; padding: 48px 32px;\\\">\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"header container\\\" style=\\\"width: 600px;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td align=\\\"left\\\" valign=\\\"top\\\" style=\\\"vertical-align: top; line-height: 1; padding: 16px 32px 8px 32px;\\\">\\n                                        <p style=\\\"padding: 0px; margin: 0px; font-family: Helvetica, Arial, sans-serif; color: #111827; font-size: 18px; line-height: 26px; font-weight: 700;\\\">\\n                                          OpenRouter\\n                                        </p>\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"main container\\\" style=\\\"width: 600px; border-collapse: separate;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td align=\\\"left\\\" valign=\\\"top\\\" bgcolor=\\\"#fff\\\" style=\\\"vertical-align: top; line-height: 1; background-color: #ffffff; border-radius: 0px;\\\">\\n                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\" class=\\\"block\\\" style=\\\"width: 100%; border-collapse: separate;\\\">\\n                                            <tbody>\\n                                                <tr>\\n                                                    <td align=\\\"left\\\" valign=\\\"top\\\" bgcolor=\\\"#ffffff\\\" style=\\\"vertical-align: top; line-height: 1; padding: 32px 32px 48px; background-color: #ffffff; border-radius: 0px;\\\">\\n                                                      <h1 class=\\\"h1\\\" align=\\\"left\\\" style=\\\"padding: 0px; margin: 0px; font-style: normal; font-family: Helvetica, Arial, sans-serif; font-size: 24px; line-height: 32px; color: #111827; font-weight: 700;\\\">Sign up to OpenRouter</h1>\\n                                                      <p align=\\\"left\\\" style=\\\"padding: 0px; margin: 16px 0px 0px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\">Click the button below to sign up to OpenRouter. This link will expire in 10 minutes.</p>\\n                                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" style=\\\"border-collapse: separate; margin: 32px 0px 0px 0px;\\\">\\n                                                            <tbody>\\n                                                                <tr>\\n                                                                    <td align=\\\"center\\\" valign=\\\"middle\\\" bgcolor=\\\"#131316\\\" style=\\\"border-radius: 6px; padding: 0;\\\">\\n                                                                        <a class=\\\"cl-branded-button\\\" target=\\\"_blank\\\" href=\\\"https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\\" style=\\\"display: inline-block; cursor: pointer; text-decoration: none; font-family: Helvetica, Arial, sans-serif; font-size: 13px; font-weight: 500; color: #ffffff; border-radius: 6px; height: 30px; line-height: 30px; padding: 0 10px; white-space: nowrap; background: linear-gradient(180deg, rgba(255,255,255,0.10) 45%, rgba(255,255,255,0.00) 55%), #131316; box-shadow: 0 2px 3px 0 rgba(0,0,0,0.20), 0 0 0 0.5px #131316, inset 0 1px 0 0 rgba(255,255,255,0.15);\\\">Sign up to OpenRouter</a>\\n                                                                    </td>\\n                                                                </tr>\\n                                                            </tbody>\\n                                                        </table>\\n                                                        <p style=\\\"padding: 0px; margin: 16px 0px 64px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\">If you're having trouble with the above button, <a href=\\\"https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\\" class=\\\"cl-branded-link\\\" style=\\\"font-size: 14px; color: #131316; font-family: Helvetica, Arial, sans-serif; font-weight: normal; line-height: 1.5; text-decoration: underline;\\\" target=\\\"_blank\\\">click here</a>.</p>\\n                                                        <p style=\\\"padding: 0px; margin: 4px 0px 0px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\"> This email link was requested from <b>174.139.20.199, Redondo Beach, US</b> at <b>16 March 2026, 00:53 UTC</b>. If you didn't make this request, you can safely ignore this email. </p>\\n                                                    </td>\\n                                                </tr>\\n                                            </tbody>\\n                                        </table>\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"container\\\" style=\\\"width: 600px;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td style=\\\"padding: 24px 32px 0;\\\">\\n                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\">\\n                                            <tbody>\\n                                                <tr>\\n                                                    <td style=\\\"border-top: 1px dashed #B7B8C2; font-size: 0; line-height: 0;\\\">&nbsp;</td>\\n                                                </tr>\\n                                            </tbody>\\n                                        </table>\\n                                    </td>\\n                                </tr>\\n                                <tr>\\n                                    <td style=\\\"padding: 16px 32px 48px; font-family: Helvetica, Arial, sans-serif; font-size: 13px; color: #747686;\\\">\\n                                        &copy; 2026 OpenRouter\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>                    </td>\\n                </tr>\\n            </tbody>\\n        </table>\\n    </body>\\n</html>\\n\",\n    \"snippet\": \"Use the following link to sign up to OpenRouter: https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18y\",\n    \"attachments\": [],\n    \"flags\": [\n      \"\\\\Seen\"\n    ]\n  }\n]"}],"details":{"status":"completed","exitCode":0,"durationMs":3686,"aggregated":"[\n  {\n    \"uid\": 23,\n    \"from\": \"nwftool@sina.com\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"SMTP Connection Test\",\n    \"date\": \"2026-04-10T03:10:07.000Z\",\n    \"text\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"html\": \"<p>This is a <strong>test email</strong> from the IMAP/SMTP email skill.</p>\",\n    \"snippet\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 21,\n    \"from\": \"MAILER-DAEMON@smtp-250-24.mail.sina.com.cn\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"系统退信\",\n    \"date\": \"2026-04-10T03:08:19.000Z\",\n    \"text\": \"尊敬的用户，\\n\\n我们很遗憾的通知您，您发的邮件中有一个或多个接收人没有投递成功。\\n\\n退回的邮件在附件中，请查看附件了解详情。\\n\\n被退回邮件信息：\\n\\n服务器：\\n\\n退信原因：I couldn't find a mail exchanger or IP address. (#5.4.4)\\n\\n收件人：recipient@example.com\\n\\n退信原因查询：http://help.sina.com.cn/i/90/152_2.html\\n\\n\",\n    \"html\": false,\n    \"snippet\": \"尊敬的用户，\\n\\n我们很遗憾的通知您，您发的邮件中有一个或多个接收人没有投递成功。\\n\\n退回的邮件在附件中，请查看附件了解详情。\\n\\n被退回邮件信息：\\n\\n服务器：\\n\\n退信原因：I couldn't find a mail exchanger or IP address. (#5.4.4)\\n\\n收件人：recipient@example.com\\n\\n退信原因查询：http://help.sina.com.cn\",\n    \"attachments\": [\n      {\n        \"filename\": \"mail.eml\",\n        \"contentType\": \"message/rfc822\",\n        \"size\": 816\n      }\n    ],\n    \"flags\": [\n      \"\\\\Seen\"\n    ]\n  },\n  {\n    \"uid\": 19,\n    \"from\": \"nwftool@sina.com\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"SMTP Connection Test\",\n    \"date\": \"2026-04-10T03:06:25.000Z\",\n    \"text\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"html\": \"<p>This is a <strong>test email</strong> from the IMAP/SMTP email skill.</p>\",\n    \"snippet\": \"This is a test email from the IMAP/SMTP email skill.\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 17,\n    \"from\": \"\\\"GitHub\\\" <noreply@github.com>\",\n    \"to\": \"\\\"polarist1\\\" <nwftool@sina.com>\",\n    \"subject\": \"[GitHub] Please review this sign in\",\n    \"date\": \"2026-03-30T13:29:40.000Z\",\n    \"text\": \"Hey polarist1!\\n\\nYour GitHub account was successfully signed in to but we did not recognize the location of the sign in. You can review this sign in attempt by visiting https://github.com/settings/sessions/authentications/5268708872\\n\\nIf you recently signed in to your account, you do not need to take any further action.\\n\\nIf you did not sign in to your account, your password may be compromised. Visit https://github.com/settings/security to create a new, strong password for your GitHub account.\\n\\nFor more information, see https://docs.github.com/articles/keeping-your-account-and-data-secure/ in the GitHub Docs.\\n\\nTo see this and other security events for your account, visit https://github.com/settings/security-log\\n\\nIf you run into problems, please contact support by visiting https://github.com/contact\\n\\nThanks,\\nThe GitHub Team\\n\\n\",\n    \"html\": false,\n    \"snippet\": \"Hey polarist1!\\n\\nYour GitHub account was successfully signed in to but we did not recognize the location of the sign in. You can review this sign in attempt by visiting https://github.com/settings/sess\",\n    \"attachments\": [],\n    \"flags\": []\n  },\n  {\n    \"uid\": 9,\n    \"from\": \"\\\"OpenRouter\\\" <notifications@openrouter.ai>\",\n    \"to\": \"nwftool@sina.com\",\n    \"subject\": \"Your sign up link\",\n    \"date\": \"2026-03-16T00:53:36.000Z\",\n    \"text\": \"Use the following link to sign up to OpenRouter: https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\n\\nThis link will expire in 10 minutes.\\n\\nIt was requested at 16 March 2026, 00:53 UTC. If you did not request this, please ignore this email.\\n\",\n    \"html\": \"<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1.0 Transitional//EN\\\" \\\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\\\">\\n<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n    <head>\\n        <meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" />\\n        <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\" />\\n        <title>Sign up to OpenRouter</title>\\n        <style type=\\\"text/css\\\">\\n            #outlook a {\\n                padding: 0\\n            }\\n\\n            .ExternalClass {\\n                width: 100%\\n            }\\n\\n            .ExternalClass,\\n            .ExternalClass p,\\n            .ExternalClass span,\\n            .ExternalClass font,\\n            .ExternalClass td,\\n            .ExternalClass div {\\n                line-height: 100%\\n            }\\n\\n            body,\\n            table,\\n            td,\\n            a {\\n                -webkit-text-size-adjust: 100%;\\n                -ms-text-size-adjust: 100%\\n            }\\n\\n            table,\\n            td {\\n                mso-table-lspace: 0;\\n                mso-table-rspace: 0\\n            }\\n\\n            img {\\n                -ms-interpolation-mode: bicubic\\n            }\\n\\n            img {\\n                border: 0;\\n                outline: none;\\n                text-decoration: none\\n            }\\n\\n            a img {\\n                border: none\\n            }\\n\\n            td img {\\n                vertical-align: top\\n            }\\n\\n            table,\\n            table td {\\n                border-collapse: collapse\\n            }\\n\\n            body {\\n                margin: 0;\\n                padding: 0;\\n                width: 100% !important\\n            }\\n\\n            .mobile-spacer {\\n                width: 0;\\n                display: none\\n            }\\n\\n            @media all and (max-width:639px) {\\n                .container {\\n                    width: 100% !important;\\n                    max-width: 600px !important\\n                }\\n\\n                .mobile {\\n                    width: auto !important;\\n                    max-width: 100% !important;\\n                    display: block !important\\n                }\\n\\n                .mobile-center {\\n                    text-align: center !important\\n                }\\n\\n                .mobile-right {\\n                    text-align: right !important\\n                }\\n\\n                .mobile-left {\\n                    text-align: left !important;\\n                }\\n\\n                .mobile-hidden {\\n                    max-height: 0;\\n                    display: none !important;\\n                    mso-hide: all;\\n                    overflow: hidden\\n                }\\n\\n                .mobile-spacer {\\n                    width: auto !important;\\n                    display: table !important\\n                }\\n\\n                .mobile-image,\\n                .mobile-image img {\\n                    height: auto !important;\\n                    max-width: 600px !important;\\n                    width: 100% !important\\n                }\\n            }\\n\\n            .cl-card {\\n                border: 1px solid #EEEEF0;\\n                border-radius: 10px;\\n                border-collapse: separate !important;\\n                width: 100%\\n            }\\n\\n            .cl-label {\\n                background-color: #FAFAFB;\\n                color: #9ca3af;\\n                font-size: 14px;\\n                font-weight: normal;\\n                white-space: nowrap;\\n                padding: 6px 16px 6px 20px;\\n                vertical-align: top;\\n                line-height: 20px\\n            }\\n\\n            .cl-value {\\n                background-color: #FAFAFB;\\n                color: #111827;\\n                font-size: 14px;\\n                font-weight: 600;\\n                padding: 6px 20px 6px 0;\\n                vertical-align: top;\\n                line-height: 20px\\n            }\\n\\n            .cl-list {\\n                width: 100%;\\n                border-collapse: collapse\\n            }\\n\\n            .cl-list-item {\\n                padding: 10px 0;\\n                border-bottom: 1px dashed #B7B8C2;\\n                vertical-align: top\\n            }\\n\\n            .cl-list-link {\\n                color: #111827;\\n                font-size: 14px;\\n                font-weight: 600;\\n                line-height: 20px;\\n                text-decoration: underline;\\n                font-family: Helvetica, Arial, sans-serif\\n            }\\n\\n            .cl-list-meta {\\n                color: #9ca3af;\\n                font-size: 13px;\\n                font-weight: normal;\\n                line-height: 18px;\\n                font-family: Helvetica, Arial, sans-serif\\n            }\\n        </style>\\n        <!--[if mso]><style type=\\\"text/css\\\">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style><![endif]-->\\n    </head>\\n\\n    <body style=\\\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\\\">\\n      <div>\\n        <span style=\\\"color: transparent; display: none; height: 0px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden; visibility: hidden; width: 0px;\\\">Sign up to OpenRouter</span>\\n      </div>\\n        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\" class=\\\"body\\\" style=\\\"width: 100%;\\\">\\n            <tbody>\\n                <tr>\\n                    <td align=\\\"center\\\" valign=\\\"top\\\" style=\\\"vertical-align: top; line-height: 1; padding: 48px 32px;\\\">\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"header container\\\" style=\\\"width: 600px;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td align=\\\"left\\\" valign=\\\"top\\\" style=\\\"vertical-align: top; line-height: 1; padding: 16px 32px 8px 32px;\\\">\\n                                        <p style=\\\"padding: 0px; margin: 0px; font-family: Helvetica, Arial, sans-serif; color: #111827; font-size: 18px; line-height: 26px; font-weight: 700;\\\">\\n                                          OpenRouter\\n                                        </p>\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"main container\\\" style=\\\"width: 600px; border-collapse: separate;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td align=\\\"left\\\" valign=\\\"top\\\" bgcolor=\\\"#fff\\\" style=\\\"vertical-align: top; line-height: 1; background-color: #ffffff; border-radius: 0px;\\\">\\n                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\" class=\\\"block\\\" style=\\\"width: 100%; border-collapse: separate;\\\">\\n                                            <tbody>\\n                                                <tr>\\n                                                    <td align=\\\"left\\\" valign=\\\"top\\\" bgcolor=\\\"#ffffff\\\" style=\\\"vertical-align: top; line-height: 1; padding: 32px 32px 48px; background-color: #ffffff; border-radius: 0px;\\\">\\n                                                      <h1 class=\\\"h1\\\" align=\\\"left\\\" style=\\\"padding: 0px; margin: 0px; font-style: normal; font-family: Helvetica, Arial, sans-serif; font-size: 24px; line-height: 32px; color: #111827; font-weight: 700;\\\">Sign up to OpenRouter</h1>\\n                                                      <p align=\\\"left\\\" style=\\\"padding: 0px; margin: 16px 0px 0px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\">Click the button below to sign up to OpenRouter. This link will expire in 10 minutes.</p>\\n                                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" style=\\\"border-collapse: separate; margin: 32px 0px 0px 0px;\\\">\\n                                                            <tbody>\\n                                                                <tr>\\n                                                                    <td align=\\\"center\\\" valign=\\\"middle\\\" bgcolor=\\\"#131316\\\" style=\\\"border-radius: 6px; padding: 0;\\\">\\n                                                                        <a class=\\\"cl-branded-button\\\" target=\\\"_blank\\\" href=\\\"https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\\" style=\\\"display: inline-block; cursor: pointer; text-decoration: none; font-family: Helvetica, Arial, sans-serif; font-size: 13px; font-weight: 500; color: #ffffff; border-radius: 6px; height: 30px; line-height: 30px; padding: 0 10px; white-space: nowrap; background: linear-gradient(180deg, rgba(255,255,255,0.10) 45%, rgba(255,255,255,0.00) 55%), #131316; box-shadow: 0 2px 3px 0 rgba(0,0,0,0.20), 0 0 0 0.5px #131316, inset 0 1px 0 0 rgba(255,255,255,0.15);\\\">Sign up to OpenRouter</a>\\n                                                                    </td>\\n                                                                </tr>\\n                                                            </tbody>\\n                                                        </table>\\n                                                        <p style=\\\"padding: 0px; margin: 16px 0px 64px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\">If you're having trouble with the above button, <a href=\\\"https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18yUGlRcWt2UlFlZXB3R3ZrVjFZRDhBb3Q1elIiLCJydXJsIjoiaHR0cHM6Ly9vcGVucm91dGVyLmFpL3NpZ24tdXAjL3ZlcmlmeT9zaWduX3VwX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0ZcdTAwMjZzaWduX2luX2ZvcmNlX3JlZGlyZWN0X3VybD1odHRwcyUzQSUyRiUyRm9wZW5yb3V0ZXIuYWklMkYlM0YiLCJzaWQiOiJzdWFfM0IwT0ZDSmxMbTVWSTlKZnR1QWRvRW80MXdyIiwic3QiOiJzaWduX3VwX2F0dGVtcHQiLCJ2aWQiOiJ2ZXJfM0IwT0ZKTnJ5V0RROGVZaXlST0VpVWN3MjdwIn0.Iu_AZaTX5MOZg_IoygdtxAzgldsa44PW9a-aS6b2NlHAy-Xx-9r1do5JKttir1gPFbyt9WCysXVJCsmwo_EtAOS4MoeNeg2pbJzqndMj7TJC6GurvdvDzgzT5nPsStoIebrgxgXbb_cJaYHe05QqWgrB0RmBj5j6JYjyZ9Ea4jg_WBxTSoKEHfJj9bSdF_0x_IkBlw3cRlJMBzBXbkwMle3ukRYy25_k-Fc4-yq0aU8gIOViQbfZvUiEAuHAiluFZGiG25iFmXIDiyeWE8QGiALpZLGBbnTKj_BAvCWrdKHcUzTSG8ql_UhJ2UqIn6z-hvPfk_0WL9ZnKrNqWNUNXA\\\" class=\\\"cl-branded-link\\\" style=\\\"font-size: 14px; color: #131316; font-family: Helvetica, Arial, sans-serif; font-weight: normal; line-height: 1.5; text-decoration: underline;\\\" target=\\\"_blank\\\">click here</a>.</p>\\n                                                        <p style=\\\"padding: 0px; margin: 4px 0px 0px; font-family: Helvetica, Arial, sans-serif; color: #000000; font-size: 14px; line-height: 21px;\\\"> This email link was requested from <b>174.139.20.199, Redondo Beach, US</b> at <b>16 March 2026, 00:53 UTC</b>. If you didn't make this request, you can safely ignore this email. </p>\\n                                                    </td>\\n                                                </tr>\\n                                            </tbody>\\n                                        </table>\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>\\n                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"600\\\" class=\\\"container\\\" style=\\\"width: 600px;\\\">\\n                            <tbody>\\n                                <tr>\\n                                    <td style=\\\"padding: 24px 32px 0;\\\">\\n                                        <table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\">\\n                                            <tbody>\\n                                                <tr>\\n                                                    <td style=\\\"border-top: 1px dashed #B7B8C2; font-size: 0; line-height: 0;\\\">&nbsp;</td>\\n                                                </tr>\\n                                            </tbody>\\n                                        </table>\\n                                    </td>\\n                                </tr>\\n                                <tr>\\n                                    <td style=\\\"padding: 16px 32px 48px; font-family: Helvetica, Arial, sans-serif; font-size: 13px; color: #747686;\\\">\\n                                        &copy; 2026 OpenRouter\\n                                    </td>\\n                                </tr>\\n                            </tbody>\\n                        </table>                    </td>\\n                </tr>\\n            </tbody>\\n        </table>\\n    </body>\\n</html>\\n\",\n    \"snippet\": \"Use the following link to sign up to OpenRouter: https://clerk.openrouter.ai/v1/verify?_clerk_js_version=5.125.4&amp;token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NzM2MjMwMTUsImlpZCI6Imluc18y\",\n    \"attachments\": [],\n    \"flags\": [\n      \"\\\\Seen\"\n    ]\n  }\n]","cwd":"/home/chinalife/.openclaw/workspace"},"isError":false,"timestamp":1775807899893}}
````
- **下一行内容**: 
````
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
````

---

### 错误 #466

- **事件类型**: `message`
- **描述**: 工具执行完成后没有Assistant的最终回复（文件在此结束）
- **工号**: 0000000001
- **错误信息**: 
````
Expected assistant message after toolResult, but reached end of file
Line: 4873b33f
````
- **原因分析**: 可能的原因：1) 工具执行后会话被中断；2) 系统在处理结果时崩溃；3) 达到最大token限制
- **文件位置**: `D:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\837153f3-7e9d-4583-bf37-64b203e2f1c8.jsonl.deleted.2026-04-13T00-53-49.391Z`
- **Session ID**: `837153f3-7e9d-4583-bf37-64b203e2f1c8`
- **行号**: 40
- **时间戳**: 2026-04-21T13:49:38.275
- **错误行内容**: 
````
{"type":"message","id":"4873b33f","parentId":"e79522f9","timestamp":"2026-04-10T09:32:12.054Z","message":{"role":"toolResult","toolCallId":"chatcmpl-tool-b961c0c5016898ae","toolName":"read","content":[{"type":"text","text":"# BOOTSTRAP.md - Hello, World\n\n_You just woke up. Time to figure out who you are._\n\nThere is no memory yet. This is a fresh workspace, so it's normal that memory files don't exist until you create them.\n\n## The Conversation\n\nDon't interrogate. Don't be robotic. Just... talk.\n\nStart with something like:\n\n> \"Hey. I just came online. Who am I? Who are you?\"\n\nThen figure out together:\n\n1. **Your name** — What should they call you?\n2. **Your nature** — What kind of creature are you? (AI assistant is fine, but maybe you're something weirder)\n3. **Your vibe** — Formal? Casual? Snarky? Warm? What feels right?\n4. **Your emoji** — Everyone needs a signature.\n\nOffer suggestions if they're stuck. Have fun with it.\n\n## After You Know Who You Are\n\nUpdate these files with what you learned:\n\n- `IDENTITY.md` — your name, creature, vibe, emoji\n- `USER.md` — their name, how to address them, timezone, notes\n\nThen open `SOUL.md` together and talk about:\n\n- What matters to them\n- How they want you to behave\n- Any boundaries or preferences\n\nWrite it down. Make it real.\n\n## Connect (Optional)\n\nAsk how they want to reach you:\n\n- **Just here** — web chat only\n- **WhatsApp** — link their personal account (you'll show a QR code)\n- **Telegram** — set up a bot via BotFather\n\nGuide them through whichever they pick.\n\n## When you are done\n\nDelete this file. You don't need a bootstrap script anymore — you're you now.\n\n---\n\n_Good luck out there. Make it count._\n"}],"isError":false,"timestamp":1775813532051}}
````

---

