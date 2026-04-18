# ClawBoard - OpenClaw Monitoring Dashboard

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-blue.svg)](https://mybatis.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-35%20cases-green.svg)](docs/VERIFICATION-CHECKLIST.md)

ClawBoard 是一个用于监控和分析 OpenClaw AI Agent 会话的仪表板系统。它扫描 JSONL 日志文件，提取对话轮次、技能调用、错误信息等数据，并提供 REST API 和可视化界面。

## 📋 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [测试](#测试)
- [项目结构](#项目结构)
- [API 文档](#api-文档)
- [开发指南](#开发指南)
- [贡献](#贡献)
- [许可证](#许可证)

## ✨ 功能特性

- 📊 **实时监控**: 自动扫描 OpenClaw 会话日志
- 🔍 **错误检测**: 智能识别和分类对话中的问题
- 📈 **统计分析**: Token 消耗、技能调用、活跃用户等指标
- 🎯 **技能追踪**: 监控 AI Agent 的技能使用情况
- 🔎 **全文搜索**: 强大的对话和错误搜索功能
- 📱 **REST API**: 完整的 API 接口供前端使用
- 💾 **数据持久化**: MySQL 数据库存储历史数据
- ⚡ **高性能**: 异步扫描和批量处理

## 🛠️ 技术栈

### 后端
- **Java 17** - 编程语言
- **Spring Boot 3.2.5** - 应用框架
- **MyBatis 3.0.3** - ORM 框架
- **MySQL 5.7+** - 数据库
- **Maven** - 构建工具

### 测试
- **JUnit 5** - 测试框架
- **Mockito** - Mock 框架
- **AssertJ** - 断言库
- **H2 Database** - 内存数据库（测试用）
- **Testcontainers** - 容器化测试

### 工具
- **Lombok** - 代码简化
- **Jackson** - JSON 处理
- **Flyway** - 数据库迁移（已禁用，手动管理）

## 🚀 快速开始

### 前置要求

- Java 17+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+
- Git

### 安装步骤

1. **克隆仓库**
```bash
git clone https://github.com/yourusername/clawboard.git
cd clawboard
```

2. **配置数据库**

编辑 `src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/clawboard?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: your_username
    password: your_password
```

3. **初始化数据库**

运行初始化脚本：
```powershell
# Windows PowerShell
.\init-database.ps1
```

或者手动执行 SQL：
```bash
mysql -u username -p clawboard < src/main/resources/db/migration/V1__init_schema_clean.sql
```

4. **构建项目**
```bash
mvn clean package -DskipTests
```

5. **运行应用**
```bash
# 开发模式
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或运行 JAR 包
java -jar target/clawboard-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

6. **访问应用**

打开浏览器访问: http://localhost:8080

API 端点: http://localhost:8080/api/dashboard/summary

## 🧪 测试

ClawBoard 拥有完善的测试套件，包含 35 个测试用例。

### 运行测试

```bash
# 运行所有测试
mvn test

# 只运行单元测试
mvn test -Dtest="*Test"

# 只运行集成测试
mvn test -Dtest="*IntegrationTest"

# 运行特定测试类
mvn test -Dtest=DashboardServiceTest

# 使用交互式脚本 (Windows)
.\run-tests.ps1
```

### 测试覆盖

- ✅ **Controllers**: 100% (3/3)
- ✅ **Services**: 100% (2/2)
- ⚠️ **Mappers**: 12.5% (1/8) - 待补充
- ❌ **Parsers**: 0% - 待添加
- ❌ **Scanners**: 0% - 待添加

详细测试文档:
- 📖 [完整测试指南](TESTING.md)
- 📊 [测试摘要](docs/TEST-SUMMARY.md)
- ✅ [验证清单](docs/VERIFICATION-CHECKLIST.md)
- 📝 [完成报告](docs/COMPLETION-REPORT.md)
- 🚀 [快速参考](docs/QUICK-REFERENCE.md)

## 📁 项目结构

```
clawboard/
├── src/
│   ├── main/
│   │   ├── java/com/company/clawboard/
│   │   │   ├── controller/      # REST 控制器
│   │   │   ├── service/         # 业务逻辑层
│   │   │   ├── mapper/          # MyBatis Mapper
│   │   │   ├── model/           # 数据模型
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── parser/          # JSONL 解析器
│   │   │   ├── scanner/         # 扫描器
│   │   │   ├── config/          # 配置类
│   │   │   └── ClawboardApplication.java
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML
│   │       ├── db/migration/    # 数据库脚本
│   │       ├── application.yml  # 主配置
│   │       └── application-dev.yml
│   └── test/                    # 测试代码
│       ├── java/                # 测试类
│       └── resources/           # 测试资源
├── docs/                        # 文档
├── pom.xml                      # Maven 配置
├── TESTING.md                   # 测试指南
├── init-database.ps1            # 数据库初始化脚本
└── run-tests.ps1                # 测试运行脚本
```

## 📡 API 文档

### Dashboard API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/dashboard/summary` | 获取仪表板摘要 |
| GET | `/api/dashboard/global-stats` | 获取全局统计 |
| GET | `/api/dashboard/users` | 获取用户列表 |
| GET | `/api/dashboard/skills` | 获取技能选项 |

### Turn & Error API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/turns/search` | 搜索对话轮次 |
| GET | `/api/turns/{turnId}/trace` | 获取追踪信息 |
| GET | `/api/errors/summary` | 获取错误摘要 |
| POST | `/api/errors/search` | 搜索错误 |

### Scan API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/scan/trigger` | 触发扫描 |
| GET | `/api/scan/status` | 获取扫描状态 |
| GET | `/api/scan/history` | 获取扫描历史 |

完整 API 文档: [API接口文档.md](docs/API接口文档.md)

## 💻 开发指南

### 添加新功能

1. 创建分支
```bash
git checkout -b feature/your-feature
```

2. 编写代码和测试
```bash
# 确保测试通过
mvn test
```

3. 提交代码
```bash
git add .
git commit -m "feat: add your feature"
git push origin feature/your-feature
```

4. 创建 Pull Request

### 编码规范

- 遵循 Java 命名约定
- 使用 Lombok 简化代码
- 添加完整的 JavaDoc 注释
- 为新功能编写测试
- 保持代码简洁清晰

### 调试技巧

启用详细日志：
```yaml
# application-dev.yml
logging:
  level:
    com.company.clawboard: DEBUG
    org.mybatis: DEBUG
```

## 🤝 贡献

欢迎贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 贡献指南

- 确保代码通过所有测试
- 添加适当的测试用例
- 更新相关文档
- 遵循现有代码风格

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [OpenClaw](https://github.com/openclaw/openclaw) - AI Agent 框架
- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [MyBatis](https://mybatis.org/) - ORM 框架

## 📞 联系方式

- 项目主页: [GitHub](https://github.com/yourusername/clawboard)
- 问题反馈: [Issues](https://github.com/yourusername/clawboard/issues)
- 邮件: your.email@example.com

---

⭐ 如果这个项目对你有帮助，请给它一个星标！