# 国寿龙虾监控平台 API 接口文档

> **Base URL**: `http://<host>/api/v1`
> **Content-Type**: `application/json`

---

## 通用响应结构

所有接口统一返回以下结构：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `code` | Integer | 200 表示成功，其他值表示异常 |
| `message` | String | 响应描述 |
| `data` | Object / Array | 业务数据 |

---

## 数据字典

### 布尔状态字段

| 值 | 含义 |
| :--- | :--- |
| `true` | 成功 / 正常 / Active / 已完成 |
| `false` | 失败 / 异常 / Inactive / 未完成 |

### qualityStatus（质量评价）

| 值 | 含义 |
| :--- | :--- |
| `0` | 未评价（默认） |
| `1` | 正确 |
| `2` | 错误 |
| `3` | 待优化 |

### nodeType（执行链路节点类型）

| 值 | 含义 |
| :--- | :--- |
| `user_input` | 用户输入 |
| `skill_call` | 技能调用 |
| `tool_call` | 工具调用 |
| `reply` | 回复用户 |

---

## 模块一：基础数据字典

### 1. 获取技能列表

获取"请选择技能"下拉框的枚举数据。

- **路径**: `GET /skills/options`
- **请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "skillId": "official-doc-writer", "skillName": "公文写作" },
    { "skillId": "pptx", "skillName": "ppt生成" }
  ]
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `skillId` | String | 技能唯一标识，用于接口筛选参数 |
| `skillName` | String | 技能展示名称 |

---

## 模块二：运营大盘

### 通用筛选参数（适用于接口 3、4、5）

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `teamName` | String | 否 | **机构号筛选**（如 `"18100000"`），**注意：这是机构代码/编号，不是机构名称**，不传则查全部 |
| `userId` | String | 否 | **工号精确匹配**（对应 employee_id） |
| `startTime` | String | 否 | 开始时间，格式 `YYYY-MM-DD HH:mm:ss`，默认当日 00:00:00 |
| `endTime` | String | 否 | 结束时间，格式 `YYYY-MM-DD HH:mm:ss`，默认当日 23:59:59 |

---

### 2. 获取平台全局累计数据

获取平台从上线至今的总体累计统计，**不受筛选条件影响**。

- **路径**: `GET /dashboard/global-stats`
- **请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalTokens": 1250000,
    "totalTurns": 52000,
    "totalSkillCalls": 25000,
    "totalUsers": 872
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `totalTokens` | Integer | 累计消耗 Token 总量，前端自行换算 k/M |
| `totalTurns` | Integer | 累计对话轮次 |
| `totalSkillCalls` | Integer | 累计技能调用次数 |
| `totalUsers` | Integer | 累计用户总数 |

---

### 3. 获取大盘统计卡片数据

获取受筛选条件联动的五个核心指标卡片数据。

- **路径**: `POST /dashboard/summary`
- **请求体**: 见通用筛选参数

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "consumedTokens": 1250000,
    "conversationTurns": 45200,
    "skillCalls": 12300,
    "activeUsers": 150,
    "taskSuccessRate": 82.5
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `consumedTokens` | Integer | 时间范围内消耗 Token 总量 |
| `conversationTurns` | Integer | 时间范围内对话轮次 |
| `skillCalls` | Integer | 时间范围内技能调用次数 |
| `activeUsers` | Integer | 时间范围内活跃用户数 |
| `taskSuccessRate` | Float | 任务成功率，百分比数值，如 `82.5` 表示 82.5% |

---

### 4. 获取热度趋势图表数据

获取折线图数据，后端**始终以小时为最小粒度**返回，前端根据时间跨度决定是否聚合为天粒度（≤48小时展示小时粒度，>48小时前端聚合为天粒度）。

- **路径**: `POST /dashboard/trend`
- **请求体**: 见通用筛选参数

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "timeLabel": "2026-04-21 09:00:00",
      "tokens": 4500,
      "turns": 120,
      "skills": 85
    },
    {
      "timeLabel": "2026-04-21 10:00:00",
      "tokens": 5200,
      "turns": 140,
      "skills": 95
    }
  ]
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `timeLabel` | String | 时间标签，格式 `YYYY-MM-DD HH:00:00`，以小时为粒度 |
| `tokens` | Integer | 该小时内消耗 Token 量 |
| `turns` | Integer | 该小时内对话轮次 |
| `skills` | Integer | 该小时内技能调用次数 |

> **说明**：返回数据条数 = 起止时间内的小时数，最多返回 720 条（30天）。

---

### 5. 分页查询用户消耗明细

获取大盘下方的用户明细表格数据。

- **路径**: `POST /dashboard/usersummary`
- **请求体**: 继承通用筛选参数，并增加分页参数

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `teamName` | String | 否 | **机构号筛选**（如 `"18100000"`），**注意：这是机构代码/编号，不是机构名称** |
| `userId` | String | 否 | **工号精确匹配**（对应 employee_id） |
| `startTime` | String | 否 | 开始时间，格式 `YYYY-MM-DD HH:mm:ss` |
| `endTime` | String | 否 | 结束时间，格式 `YYYY-MM-DD HH:mm:ss` |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 150,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "userId": "18101142",
        "userName": "王颜",
        "orgCode": "18100000",
        "status": true,
        "lastHeartbeat": 1680000000000,
        "tokens": {
          "total": 15400,
          "input": 5400,
          "output": 10000
        },
        "turns": { "success": 3, "total": 3 },
        "skillCalls": { "success": 2, "total": 2 },
        "toolCalls": { "success": 1, "total": 1 },
        "topSkills": ["公文写作", "ppt生成"]
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `userId` | String | 工号 |
| `userName` | String | 姓名 |
| `orgCode` | String | 机构号，如 `"18100000"` |
| `status` | Boolean | 龙虾是否在线：`true` active / `false` inactive |
| `lastHeartbeat` | Long | 最后心跳时间戳（毫秒） |
| `tokens.total` | Integer | 消耗 Token 总量 |
| `tokens.input` | Integer | 输入 Token 量 |
| `tokens.output` | Integer | 输出 Token 量 |
| `turns.success` | Integer | 成功对话轮次 |
| `turns.total` | Integer | 总对话轮次 |
| `skillCalls.success` | Integer | 成功技能调用次数 |
| `skillCalls.total` | Integer | 总技能调用次数 |
| `toolCalls.success` | Integer | 成功工具调用次数 |
| `toolCalls.total` | Integer | 总工具调用次数 |
| `topSkills` | String[] | 常用技能名称列表 |

---

## 模块三：对话记录检索

### 6. 分页查询对话记录

查询对话记录表格数据，支持多条件筛选与分页。"对话"以每次用户侧输入为单位进行分割。

- **路径**: `POST /turns/search`
- **请求体**:

```json
{
  "userId": "18101142",
  "startTime": "2026-04-21 00:00:00",
  "endTime": "2026-04-21 23:59:59",
  "skillId": "pptx",
  "page": 1,
  "pageSize": 10
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `userId` | String | 否 | **工号精确匹配**（对应 employee_id） |
| `startTime` | String | 否 | **开始时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `endTime` | String | 否 | **结束时间，格式 `YYYY-MM-DD HH:mm:ss`** |
| `skillId` | String | 否 | 技能 ID 精确匹配，取值来自接口 1 |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 125,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "turnId": "d6gte29d",
        "timeStamp": 1680000000000,
        "userName": "王颜",
        "userInput": "帮我生成一份二十届三中全会学习PPT",
        "durationMs": 86000,
        "resultStatus": false,
        "qualityStatus": 2,
        "tokens": {
          "total": 15400,
          "input": 5400,
          "output": 10000
        },
        "skills": ["公文写作"],
        "tools": [],
        "logFileName": "session_001.jsonl"
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `turnId` | String | 对话唯一 ID |
| `timeStamp` | Long | 对话发生时间戳（毫秒） |
| `userName` | String | 用户姓名 |
| `userInput` | String | 用户输入内容 |
| `durationMs` | Integer | 本次对话总耗时（毫秒） |
| `resultStatus` | Boolean | 执行结果：`true` 成功 / `false` 失败 |
| `qualityStatus` | Integer | 质量评价，见数据字典 |
| `tokens.total` | Integer | 本次对话消耗 Token 总量 |
| `tokens.input` | Integer | 输入 Token 量 |
| `tokens.output` | Integer | 输出 Token 量 |
| `skills` | String[] | 本次调用的技能名称列表 |
| `tools` | String[] | 本次调用的工具名称列表，无则为空数组 |
| `logFileName` | String | 日志文件名，仅文件名，不含路径 |

---

### 7. 获取执行链路详情

点击对话记录行展开时懒加载调用，获取该次对话的完整执行链路追踪。

- **路径**: `GET /turns/{turnId}/trace`
- **路径参数**: `turnId` — 对话唯一 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "turnId": "d6gte29d",
    "nodes": [
      {
        "stepOrder": 0,
        "nodeType": "user_input",
        "nodeName": "用户输入",
        "status": true,
        "timeStamp": 1680000000000
      },
      {
        "stepOrder": 1,
        "nodeType": "skill_call",
        "nodeName": "公文写作技能调用",
        "status": true,
        "timeStamp": 1680000000200
      },
      {
        "stepOrder": 2,
        "nodeType": "tool_call",
        "nodeName": "数据抽取工具调用",
        "status": true,
        "timeStamp": 1680000000400
      },
      {
        "stepOrder": 3,
        "nodeType": "reply",
        "nodeName": "回复用户",
        "status": false,
        "timeStamp": 1680000000600
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `turnId` | String | 对话唯一 ID |
| `nodes` | Array | 执行节点列表，按 `stepOrder` 升序排列 |
| `nodes[].stepOrder` | Integer | 节点顺序，从 0 开始 |
| `nodes[].nodeType` | String | 节点类型，见数据字典 |
| `nodes[].nodeName` | String | 节点展示名称 |
| `nodes[].status` | Boolean | 节点执行结果：`true` 成功 / `false` 失败 |
| `nodes[].timeStamp` | Long | 节点执行时间戳（毫秒），相邻节点时间差即为该步骤耗时 |
