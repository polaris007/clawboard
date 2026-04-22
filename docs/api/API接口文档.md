# 国寿龙虾监控平台 API 接口文档

---

## 核心约定：数据字典与布尔值映射

为了方便前后端解耦与灵活的 UI 展示，接口中的状态类字段统一遵循以下规范：

1. **状态类字段 (Boolean)**:  
   * `true`: 代表前端显示的“成功”、“正常”、“Active”或“已完成”。  
   * `false`: 代表前端显示的“失败”、“异常”、“Inactive”或“未完成”。  
2. **质量评价 `qualityStatus` (Integer)**:  
   * `0`: 未评价 (默认)  
   * `1`: 正确  
   * `2`: 错误  
   * `3`: 待优化

---

## 模块一：下拉列表数据字典接口

### 1\. 获取技能列表

用于获取“请选择技能”下拉列表的数据字典。

* **路径**: `/api/v1/skills/options`  
* **方法**: `GET`  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": \[

    { "skillId": "official-doc-writer", "skillName": "公文写作" },

    { "skillId": "pptx", "skillName": "ppt生成" }

  \]

}

---

## 模块二：龙虾运营大盘接口

本模块包含大盘页面的全局指标、过滤指标、趋势图表及用户明细列表。

### 2\. 获取平台全局累计数据

获取整个平台所有 openclaw 实例从上线至今的总体累计统计数据。此接口不受页面筛选条件影响。

* **路径**: `/api/v1/dashboard/global-stats`  
* **方法**: `GET`  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": {

    "totalTokens": 1250000,  // 即1.25M token，具体k/M由前端进行换算

    "totalTurns": 52000,

    "totalSkillCalls": 25000,

    "totalUsers": 872

  }

}

**通用请求体参数说明（大盘筛选参数，包括 团队、姓名、时间范围，将应用于 3、4、5 接口）：** | 字段名 | 类型 | 必填 | 说明 | | :--- | :--- | :--- | :--- | | `teamName` | String | 否 | 团队筛选 | | `userName` | String | 否 | 姓名筛选 | | `startTime` | Long/String | 否 | 开始时间 (默认为当日0点) | | `endTime` | Long/String | 否 | 结束时间 (默认为当前时间) |

### 3\. 获取大盘统计卡片数据

获取受筛选条件联动的四个核心指标卡片数据。

* **路径**: `/api/v1/dashboard/summary`  
* **方法**: `POST`  
* **请求体**: 见通用请求体参数  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": {

    "consumedTokens": 1250000,

    "conversationTurns": 45200,

    "skillCalls": 12300,

    "activeUsers": 150

  }

}

### 4\. 获取热度趋势图表数据

* **路径**: `/api/v1/dashboard/trend`  
* **方法**: `POST`  
* **请求体**: 见通用请求体参数  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": \[

    {

      "timeLabel": 1680000000000,

      "tokens": 4500,

      "turns": 120,

      "skills": 85

    },

    {

      "timeLabel": 1680000000000,

      "tokens": 5200,

      "turns": 140,

      "skills": 95

    }

  \]

}

### 5\. 分页查询用户状态与消耗明细

获取页面下方的用户明细表格数据。

* **路径**: `/api/v1/dashboard/usersummary`  
* **方法**: `POST`  
* **请求体**: 继承通用请求体参数，并增加分页参数 `page` (默认1) 和 `pageSize` (默认10)  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": {

    "total": 150,

    "page": 1,

    "pageSize": 10,

    "list": \[

      {

        "userId": "18101142",

        "userName": "王颜",

        "teamName": "新技术",

        "status": true,

        "lastHeartbeat": 1680000000000,

        "tokens": {

          "total": 15400,

          "input": 5400,

          "output": 10000

        },

        "turns": {

          "success": 3,

          "total": 3

        },

        "skillCalls": {

          "success": 2,

          "total": 2

        },

        "toolCalls": {

          "success": 1,

          "total": 1

        },

        "topSkills": \["公文写作", "ppt生成"\]

      }

    \]

  }

}

---

## 模块三：龙虾对话记录检索接口

本模块用于具体的对话记录查询及链路追踪。
“对话”以每一次用户侧输入为界限从整个会话中进行分割。

### 6\. 分页查询对话记录

用于获取对话表格的基础信息层级。

* **路径**: `/api/v1/turns/search`  
* **方法**: `POST`  
* **请求体**:

{

  "userName": "王颜",

  "startTime": 1680000000000,

  "endTime": 1680000000000,

  "skillId": "pptx",

  "page": 1,

  "pageSize": 10

}

* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": {

    "total": 125,

    "page": 1,

    "pageSize": 10,

    "list": \[

      {

        "turnId": "d6gte29d",

        "timeStamp": 1680000000000,

        "userName": "王颜",

        "userInput": "帮我生成一份二十届三中全会学习PPT",

        "durationMs": 86000,   // 单位：毫秒

        "resultStatus": false,

        "qualityStatus": 2,

        "tokens": {

          "total": 15400,

          "input": 5400,

          "output": 10000

        },

        "skills": \["公文写作"\],

        "tools": \[\],

        "logFileName": "/datafs/openclaw/agents/main/sessions/xxx.jsonl"

      }

    \]

  }

}

### 7\. 获取执行链路详情

用户点击表格某一行展开时懒加载调用，获取本次对话的执行链路追踪。

* **路径**: `/api/v1/turns/{turnId}/trace`  
* **方法**: `GET`  
* **响应示例**:

{

  "code": 200,

  "message": "success",

  "data": {

    "turnId": "d6gte29d",

    "nodes": \[

      {

        "stepOrder": 0,

        "nodeType": "user\_input",

        "nodeName": "用户输入",

        "status": true,

        "timeStamp": 1680000000000

      },

      {

        "stepOrder": 1,

        "nodeType": "skill\_call",

        "nodeName": "xxxx技能调用",

        "status": true,

        "timeStamp": 1680000000000

      },

      {

        "stepOrder": 2,

        "nodeType": "tool\_call",

        "nodeName": "xxx工具调用",

        "status": true,

        "timeStamp": 1680000000000

      },

      {

        "stepOrder": 3,

        "nodeType": "reply",

        "nodeName": "回复用户",

        "status": false,

        "timeStamp": 1680000000000

      }

    \]

  }

}  
