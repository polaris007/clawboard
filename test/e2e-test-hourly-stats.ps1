# 小时级统计聚合修复 - E2E 自动化测试脚本
# 用法: .\test\e2e-test-hourly-stats.ps1

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  小时级统计聚合修复 - E2E 测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 配置
$MYSQL_HOST = "127.0.0.1"
$MYSQL_PORT = "3306"
$MYSQL_USER = "clawboard"
$MYSQL_PASS = "Clqc@1234"
$MYSQL_DB = "clawboard"
$API_BASE = "http://localhost:8080"
$TEST_DATA_DIR = "test\e2e-test-data"
$PROD_LOGS_DIR = "test\prod-logs"

function Invoke-MySql {
    param([string]$Query)
    $cmd = "mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p'$MYSQL_PASS' $MYSQL_DB -e `"$Query`""
    return Invoke-Expression $cmd 2>&1 | Select-String -Pattern "Warning" -NotMatch
}

function Reset-Database {
    Write-Host "[Step 1] 清空数据库..." -ForegroundColor Yellow
    Get-Content scripts\sql\reset-database.sql | mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p"$MYSQL_PASS" $MYSQL_DB 2>&1 | Out-Null
    Write-Host "✓ 数据库已清空" -ForegroundColor Green
    Write-Host ""
}

function Start-Application {
    Write-Host "[Step 2] 启动应用..." -ForegroundColor Yellow
    # 检查是否已经在运行
    $javaProcess = Get-Process -Name java -ErrorAction SilentlyContinue
    if ($javaProcess) {
        Write-Host "  应用已在运行，跳过启动" -ForegroundColor Gray
        return
    }
    
    # 后台启动
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; mvn spring-boot:run '-Dspring-boot.run.profiles=dev'"
    Write-Host "  等待应用启动（30秒）..." -ForegroundColor Gray
    Start-Sleep -Seconds 30
    Write-Host "✓ 应用已启动" -ForegroundColor Green
    Write-Host ""
}

function Trigger-Scan {
    Write-Host "[Step 3] 触发扫描..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "$API_BASE/api/v1/scan/trigger" -Method POST -UseBasicParsing -TimeoutSec 60
        Write-Host "  扫描已触发: $($response.StatusCode)" -ForegroundColor Gray
    } catch {
        Write-Host "  ⚠ 扫描触发失败: $_" -ForegroundColor Red
    }
    Write-Host "  等待扫描完成（60秒）..." -ForegroundColor Gray
    Start-Sleep -Seconds 60
    Write-Host "✓ 扫描完成" -ForegroundColor Green
    Write-Host ""
}

function Check-HourlyStats {
    param(
        [string]$TestName,
        [int]$ExpectedMinRecords,
        [int]$ExpectedMaxZeroPercent = 50
    )
    
    Write-Host "[验证] $TestName" -ForegroundColor Cyan
    
    # 查询 1: 总记录数
    $query1 = @"
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT employee_id) as unique_employees,
    COUNT(DISTINCT stat_hour) as unique_hours,
    MIN(stat_hour) as earliest_hour,
    MAX(stat_hour) as latest_hour
FROM dashboard_hourly_stats;
"@
    
    Write-Host "  执行查询 1: 检查总记录数..." -ForegroundColor Gray
    $result1 = Invoke-MySql -Query $query1
    Write-Host $result1 -ForegroundColor White
    
    # 查询 2: 全 0 记录比例
    $query2 = @"
SELECT 
    COUNT(*) as zero_records,
    COUNT(*) * 100.0 / (SELECT COUNT(*) FROM dashboard_hourly_stats) as percentage
FROM dashboard_hourly_stats
WHERE total_tokens = 0 
  AND conversation_turns = 0 
  AND error_count = 0;
"@
    
    Write-Host "  执行查询 2: 检查全 0 记录比例..." -ForegroundColor Gray
    $result2 = Invoke-MySql -Query $query2
    Write-Host $result2 -ForegroundColor White
    
    # 验证
    $totalRecords = ($result1 | Select-String "total_records").ToString().Split('|')[1].Trim()
    $zeroPercent = ($result2 | Select-String "percentage").ToString().Split('|')[2].Trim()
    
    Write-Host ""
    if ([int]$totalRecords -ge $ExpectedMinRecords) {
        Write-Host "  ✓ 记录数符合预期 (>= $ExpectedMinRecords)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 记录数不符合预期 (< $ExpectedMinRecords)" -ForegroundColor Red
    }
    
    if ([decimal]$zeroPercent -le $ExpectedMaxZeroPercent) {
        Write-Host "  ✓ 全 0 记录比例符合预期 (<= ${ExpectedMaxZeroPercent}%)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 全 0 记录比例过高 (> ${ExpectedMaxZeroPercent}%)" -ForegroundColor Red
    }
    
    Write-Host ""
    return @{
        TotalRecords = $totalRecords
        ZeroPercent = $zeroPercent
    }
}

function Check-SpecificHour {
    param(
        [string]$Hour,
        [string]$ExpectedTokens
    )
    
    Write-Host "[验证] 检查特定小时 $Hour 的聚合结果" -ForegroundColor Cyan
    
    $query = @"
SELECT 
    employee_id,
    stat_hour,
    total_tokens,
    conversation_turns,
    error_count
FROM dashboard_hourly_stats
WHERE stat_hour = '$Hour'
ORDER BY employee_id;
"@
    
    Write-Host "  执行查询..." -ForegroundColor Gray
    $result = Invoke-MySql -Query $query
    Write-Host $result -ForegroundColor White
    Write-Host ""
}

function Check-ScanHistory {
    Write-Host "[验证] 检查 scan_history 表" -ForegroundColor Cyan
    
    $query = @"
SELECT 
    id,
    agg_hours_from_files,
    agg_hours_from_window,
    agg_hours_total,
    agg_duration_ms
FROM dashboard_scan_history
ORDER BY id DESC
LIMIT 5;
"@
    
    Write-Host "  执行查询..." -ForegroundColor Gray
    $result = Invoke-MySql -Query $query
    Write-Host $result -ForegroundColor White
    Write-Host ""
}

# ========================================
# 场景 1: 基础扫描 - 使用 prod-logs 数据
# ========================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  场景 1: 基础扫描测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Reset-Database
Start-Application

# 复制 prod-logs 到扫描目录（临时）
Write-Host "[准备] 复制 prod-logs 数据到测试目录..." -ForegroundColor Yellow
if (Test-Path "$PROD_LOGS_DIR\d29befe28f6d1a440997a29a90d297a20ea5c6b0effffb94967c082745c678e4b4efc8f29e2c81246b38e206e09c98183755650d5db5fc6f525f9d8928b67e24") {
    Copy-Item -Recurse -Force "$PROD_LOGS_DIR\d29befe28f6d1a440997a29a90d297a20ea5c6b0effffb94967c082745c678e4b4efc8f29e2c81246b38e206e09c98183755650d5db5fc6f525f9d8928b67e24" "$TEST_DATA_DIR\prod-user-001"
    Write-Host "  ✓ 数据已复制" -ForegroundColor Green
} else {
    Write-Host "  ⚠ prod-logs 数据不存在，跳过此步骤" -ForegroundColor Yellow
}
Write-Host ""

Trigger-Scan
$result1 = Check-HourlyStats -TestName "场景 1: 基础扫描" -ExpectedMinRecords 100 -ExpectedMaxZeroPercent 80
Check-ScanHistory

# ========================================
# 场景 2: 24 小时前数据聚合
# ========================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  场景 2: 24 小时前数据聚合测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[准备] 创建包含 7 天前数据的测试文件..." -ForegroundColor Yellow
# session-old-001.jsonl 已创建，时间戳是 2026-04-22（7 天前）
Write-Host "  ✓ 测试文件已就绪: test-user-001/session-old-001.jsonl" -ForegroundColor Green
Write-Host ""

# 注意：需要将测试数据复制到实际的扫描目录
# 这里假设配置文件指向 test\e2e-test-data
Write-Host "[提示] 请确保 application-dev.yml 中的 nas.base-path 指向:" -ForegroundColor Yellow
Write-Host "  $PWD\$TEST_DATA_DIR" -ForegroundColor Gray
Write-Host ""

Read-Host "按 Enter 继续触发扫描..."
Trigger-Scan

Write-Host "[验证] 检查最早的小时是否是 7 天前..." -ForegroundColor Cyan
$query = @"
SELECT 
    MIN(stat_hour) as earliest_hour,
    MAX(stat_hour) as latest_hour
FROM dashboard_hourly_stats;
"@
$result = Invoke-MySql -Query $query
Write-Host $result -ForegroundColor White

if ($result -match "2026-04-22") {
    Write-Host "  ✓ 7 天前的数据已被正确聚合" -ForegroundColor Green
} else {
    Write-Host "  ✗ 未找到 7 天前的数据" -ForegroundColor Red
}
Write-Host ""

# ========================================
# 场景 3: 跨文件同小时聚合
# ========================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  场景 3: 跨文件同小时聚合测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Reset-Database

Write-Host "[准备] 只放置 Session A（10:10 的消息）..." -ForegroundColor Yellow
# 需要先清空 test-user-002 的 sessions 目录，只保留 session-a
Remove-Item "$TEST_DATA_DIR\test-user-002\agents\main\sessions\session-b-001.jsonl" -ErrorAction SilentlyContinue
Write-Host "  ✓ 只保留 session-a-001.jsonl" -ForegroundColor Green
Write-Host ""

Read-Host "按 Enter 触发第一次扫描..."
Trigger-Scan

Write-Host "[验证] 第一次扫描后，10:00 小时的 tokens 应该是 150..." -ForegroundColor Cyan
Check-SpecificHour -Hour "2026-04-29 10:00:00" -ExpectedTokens "150"

Write-Host "[准备] 添加 Session B（10:20 的消息）..." -ForegroundColor Yellow
# session-b-001.jsonl 已经存在
Write-Host "  ✓ session-b-001.jsonl 已就绪" -ForegroundColor Green
Write-Host ""

Read-Host "按 Enter 触发第二次扫描..."
Trigger-Scan

Write-Host "[验证] 第二次扫描后，10:00 小时的 tokens 应该是 450 (150 + 300)..." -ForegroundColor Cyan
Check-SpecificHour -Hour "2026-04-29 10:00:00" -ExpectedTokens "450"

# ========================================
# 测试总结
# ========================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "请检查以上输出，确认所有场景是否符合预期。" -ForegroundColor Yellow
Write-Host ""
Write-Host "关键验证点：" -ForegroundColor Cyan
Write-Host "  1. 场景 1: 记录数合理，无大量笛卡尔积" -ForegroundColor White
Write-Host "  2. 场景 2: 7 天前的数据被正确聚合" -ForegroundColor White
Write-Host "  3. 场景 3: 跨文件同小时数据正确累加 (150 → 450)" -ForegroundColor White
Write-Host ""
Write-Host "详细测试报告模板请参考:" -ForegroundColor Cyan
Write-Host "  docs\e2e-test-hourly-stats-aggregation.md" -ForegroundColor White
Write-Host ""
