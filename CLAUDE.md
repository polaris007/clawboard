# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:

- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:

- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:

- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:

- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:

```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

***

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

# 开发环境配置

1. 开发环境使用的是MySQL 5.7，服务器地址：127.0.0.1，端口：3306，用户名：clawboard，密码：Clqc\@1234，数据库：clawboard
2. JAVA安装在D:\Program\JDK\jdk-17.0.18
3. Maven安装在D:\Program\Maven\apache-maven-3.9.15 .m2的位置在：D:\m2
4. MySQL在D:\Program\Database\mysql-5.7.44-winx64，数据库已经启动，账号和数据库也已经建了，并配置了权限
5. scrpts\detect-all-transcript-issues.py是Python 2.7的脚本，需要用D:\Python27来执行
6. OpenClaw的源码在..\openclaw目录下
7. 开发过程中需要使用dev profile(application-dev.yml)启动和调试应用
8. 每次启动应用前清空数据库表（参照scripts\reset-database.sql）
9. 所有 PowerShell 命令中的 `Invoke-WebRequest` 或 `irm`，必须加上 `-UseBasicParsing` 参数，避免弹出安全确认框。
10. 设计文档为：docs\design\2026-04-18-openclaw-monitoring-dashboard-design.md，计划文档为：docs\design\2026-04-18-clawboard-implementation-plan.md
11. 日期都要是北京(+8)时区
12. 生成的测试脚本要放到test目录下或者对应的test目录的子目录下，生成的文档要放到docs或者docs的子目录里

