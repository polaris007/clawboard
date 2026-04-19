# Transcript Comprehensive Issues Report

**Scan Time**: 2026-04-19T11:36:03.916

**Total Issues**: 19

## Summary by Severity

- **High**: 17
- **Medium**: 2
- **Low**: 0

## Detailed Issues

### Issue #1

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**0af83cd4-10a3-4966-8f3c-2b581a53bf99
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57546 input tokens (8192 > 65536 - 57546). (parameter=max_tokens, value=8192)`

---

### Issue #2

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**55b3dbad-7082-44c9-8556-9346043c798d
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 54822 input tokens (16384 > 65536 - 54822). (parameter=max_tokens, value=16384)`

---

### Issue #3

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**b57d8f72-a5ec-4f01-b83b-4c1f823cc564
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 52189 input tokens (16384 > 65536 - 52189). (parameter=max_tokens, value=16384)`

---

### Issue #4

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**38cb43c3-64cc-47c2-8ad0-9752d31a0c95
- **Description**: Assistant returned an error message
- **Error Message**: `503 upstream connect error or disconnect/reset before headers. reset reason: remote connection failure, transport failure reason: delayed connect error: 111`

---

### Issue #5

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**ecf6d23a-a5ba-4838-a8bc-de4291d68a48
- **Description**: Assistant returned an error message
- **Error Message**: `400 {'error': '/chat/completions: Invalid model name passed in model=AIAPLLM-vision-nothink. Call `/v1/models` to view available models for your key.'}`

---

### Issue #6

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**21c20430-e74b-4ea9-8370-5b818e07807f
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 50053 input tokens (16384 > 65536 - 50053). (parameter=max_tokens, value=16384)`

---

### Issue #7

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**8e991737-22bf-448e-8bbe-c62186c39811
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51784 input tokens (16384 > 65536 - 51784). (parameter=max_tokens, value=16384)`

---

### Issue #8

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**0f678300-9756-4ea9-b283-9cf231eaba5f
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 51191 input tokens (16384 > 65536 - 51191). (parameter=max_tokens, value=16384)`

---

### Issue #9

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**c4600d75-8dd6-4f40-814c-a0ce5cbfbc5e
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 64170 input tokens (16384 > 65536 - 64170). (parameter=max_tokens, value=16384)`

---

### Issue #10

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**57655182-1fa9-4dca-aafc-f16e69319ef6
- **Description**: Assistant returned an error message
- **Error Message**: `400 This model's maximum context length is 65536 tokens. However, your request has 93196 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=93196)`

---

### Issue #11

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**452b6522-ab61-4cb5-9e12-993c22302827
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 16384. This model's maximum context length is 65536 tokens and your request has 53947 input tokens (16384 > 65536 - 53947). (parameter=max_tokens, value=16384)`

---

### Issue #12

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**8ef546cf-18a4-43a7-baec-ed0207c28996
- **Description**: Assistant returned an error message
- **Error Message**: `Request was aborted`

---

### Issue #13

- **Type**: TOOL_ERROR
- **Severity**: medium
- **Session**8ef546cf-18a4-43a7-baec-ed0207c28996
- **Description**: Tool execution failed: gateway
- **Error Message**: `Validation failed for tool "gateway":
  - action: must be equal to one of the allowed values

Received arguments:
{
  "action": "status"
}`

---

### Issue #14

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**9ccfae6c-1ba2-4215-b07c-f16eebaee938
- **Description**: Assistant returned an error message
- **Error Message**: `400 This model's maximum context length is 65536 tokens. However, your request has 92360 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=92360)`

---

### Issue #15

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**c124a8ac-1e3d-4b27-a6e6-e558938ce159
- **Description**: Assistant returned an error message
- **Error Message**: `Request was aborted`

---

### Issue #16

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**8a7a7bbf-e23c-4c9c-bee2-e3b0d0f793dd
- **Description**: Assistant returned an error message
- **Error Message**: `400 This model's maximum context length is 65536 tokens. However, your request has 93398 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=93398)`

---

### Issue #17

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**f15427eb-5cbe-4649-b5e5-ff97dbf69934
- **Description**: Assistant returned an error message
- **Error Message**: `400 This model's maximum context length is 65536 tokens. However, your request has 92483 input tokens. Please reduce the length of the input messages. (parameter=input_tokens, value=92483)`

---

### Issue #18

- **Type**: ASSISTANT_ERROR
- **Severity**: high
- **Session**5a7e6f9d-4c43-4a9a-820e-5ba304317da6
- **Description**: Assistant returned an error message
- **Error Message**: `400 'max_tokens' or 'max_completion_tokens' is too large: 8192. This model's maximum context length is 65536 tokens and your request has 57883 input tokens (8192 > 65536 - 57883). (parameter=max_tokens, value=8192)`

---

### Issue #19

- **Type**: TOOL_ERROR
- **Severity**: medium
- **Session**b5018140-32f9-4102-879a-7853821a47d1
- **Description**: Tool execution failed: read
- **Error Message**: `Aborted`

---

