@echo off
REM 优化版ClawBoard应用启动脚本
REM 解决Java模块系统兼容性和内存问题

echo ============================================
echo ClawBoard Spring Boot应用启动脚本
echo ============================================
echo.

REM 设置Java路径
set JAVA_HOME="D:\Program Files\Java\jdk-17.0.18"
set PATH=%JAVA_HOME%\bin;%PATH%

REM 设置Maven路径
set MAVEN_HOME="F:\Program\Maven\apache-maven-3.9.15"
set PATH=%MAVEN_HOME%\bin;%PATH%

REM 设置Maven本地仓库路径
set MAVEN_OPTS=-Dmaven.repo.local=I:\m2

REM 确保脚本以管理员权限运行（解决文件访问权限问题）
echo 检查管理员权限...
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo 请以管理员权限运行此脚本！
    echo 右键点击脚本 -> 以管理员身份运行
    pause
    exit /b 1
)

REM 确保目标目录存在并有写入权限
if not exist target mkdir target
if not exist scripts\reports mkdir scripts\reports

REM 设置Java内存参数和模块系统选项
set JAVA_OPTS=^
    -Xmx1024m ^           // 最大内存限制为1GB，避免内存溢出
    -Xms256m ^            // 初始堆内存256MB
    -XX:MaxMetaspaceSize=512m ^  // Metaspace内存限制
    -XX:ReservedCodeCacheSize=256m ^  // 代码缓存大小
    -XX:+UseG1GC ^        // 使用G1垃圾回收器
    -XX:+UseStringDeduplication ^  // 字符串去重
    -XX:+HeapDumpOnOutOfMemoryError ^  // 内存溢出时生成dump文件
    -XX:HeapDumpPath=./target/heapdump.hprof ^
    -XX:ErrorFile=./target/hs_err_pid%p.log ^
    -Djava.security.egd=file:/dev/./urandom ^  // 更快的随机数生成
    -Dfile.encoding=UTF-8

REM 解决Java模块系统兼容性问题
set MODULE_OPTS=^
    --add-opens java.base/java.lang=ALL-UNNAMED ^
    --add-opens java.base/java.lang.reflect=ALL-UNNAMED ^
    --add-opens java.base/java.util=ALL-UNNAMED ^
    --add-opens java.base/java.util.concurrent=ALL-UNNAMED ^
    --add-opens java.base/java.net=ALL-UNNAMED ^
    --add-opens java.base/java.nio=ALL-UNNAMED ^
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED ^
    --add-opens java.base/sun.nio.fs=ALL-UNNAMED ^
    --add-opens java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED

REM 应用日志配置
set LOG_OPTS=^
    -Dlogging.file.path=./logs ^
    -Dlogging.file.name=clawboard.log ^
    -Dlogging.level.root=INFO ^
    -Dlogging.level.com.company.clawboard=DEBUG

REM 数据库配置（根据CLAUDE.md中的配置）
set DB_OPTS=^
    -Dspring.datasource.url=jdbc:mysql://127.0.0.1:3306/clawboard ^
    -Dspring.datasource.username=clawboard ^
    -Dspring.datasource.password=Clqc@1234 ^
    -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver ^
    -Dspring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect

REM 应用特定配置
set APP_OPTS=^
    -Dspring.main.allow-circular-references=true ^
    -Dspring.jpa.open-in-view=false ^
    -Dspring.flyway.enabled=true ^
    -Dserver.port=8080 ^
    -Dserver.tomcat.max-threads=200 ^
    -Dserver.tomcat.min-spare-threads=50

echo 正在编译应用...
call mvn clean compile -DskipTests

if %errorlevel% neq 0 (
    echo 编译失败，请检查错误信息
    pause
    exit /b 1
)

echo 编译成功，准备启动应用...

REM 启动Spring Boot应用
echo 启动命令：java %JAVA_OPTS% %MODULE_OPTS% %LOG_OPTS% %DB_OPTS% %APP_OPTS% -cp "target/classes;target/dependency/*" com.company.clawboard.ClawboardApplication

java %JAVA_OPTS% %MODULE_OPTS% %LOG_OPTS% %DB_OPTS% %APP_OPTS% -cp "target/classes;target/dependency/*" com.company.clawboard.ClawboardApplication

if %errorlevel% equ 0 (
    echo 应用启动成功！
    echo 访问地址：http://localhost:8080
    echo 按Ctrl+C停止应用
) else (
    echo 应用启动失败，错误代码：%errorlevel%
    echo 请检查：
    echo 1. 数据库是否已启动且配置正确
    echo 2. 端口8080是否被占用
    echo 3. 检查日志文件：./logs/clawboard.log
    pause
    exit /b %errorlevel%
)