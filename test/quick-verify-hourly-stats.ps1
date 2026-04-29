# 小时级统计聚合修复 - 快速验证脚本
# 用途：快速验证修复是否生效，无需完整 E2E 测试

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  小时级统计聚合修复 - 快速验证" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 配置
$MYSQL_HOST = "127.0.0.1"
$MYSQL_PORT = "3306"
$MYSQL_USER = "clawboard"
$MYSQL_PASS = "Clqc@1234"
$MYSQL_DB = "clawboard"

function Invoke-MySql {
    param([string]$Query)
    $cmd = "mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p'$MYSQL_PASS' $MYSQL_DB -e `"$Query`""
    return Invoke-Expression $cmd 2>&1 | Select-String -Pattern "Warning" -NotMatch
}

# 检查应用是否在运行
Write-Host "[检查] 应用是否正在运行..." -ForegroundColor Yellow
$javaProcess = Get-Process -Name java -ErrorAction SilentlyContinue
if (-not $javaProcess) {
    Write-Host "✗ 应用未运行，请先启动应用" -ForegroundColor Red
    Write-Host "  命令: mvn spring-boot:run '-Dspring-boot.run.profiles=dev'" -ForegroundColor Gray
    exit 1
}
Write-Host "✓ 应用正在运行" -ForegroundColor Green
Write-Host ""

# 查询数据库状态
Write-Host "[检查] 当前数据库状态..." -ForegroundColor Yellow
$query = @"
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT employee_id) as unique_employees,
    COUNT(DISTINCT stat_hour) as unique_hours,
    MIN(stat_hour) as earliest_hour,
    MAX(stat_hour) as latest_hour
FROM dashboard_hourly_stats;
"@

$result = Invoke-MySql -Query $query
Write-Host $result -ForegroundColor White
Write-Host ""

# 提取关键指标
$totalRecords = ($result | Select-String "total_records").ToString().Split('|')[1].Trim()
$uniqueEmployees = ($result | Select-String "unique_employees").ToString().Split('|')[2].Trim()
$uniqueHours = ($result | Select-String "unique_hours").ToString().Split('|')[3].Trim()

Write-Host "[分析] 关键指标:" -ForegroundColor Cyan
Write-Host "  总记录数: $totalRecords" -ForegroundColor White
Write-Host "  唯一员工数: $uniqueEmployees" -ForegroundColor White
Write-Host "  唯一小时数: $uniqueHours" -ForegroundColor White
Write-Host ""

# 检查笛卡尔积问题
if ([int]$totalRecords -gt 0 -and [int]$uniqueHours -gt 0) {
    $expectedMaxRecords = [int]$uniqueEmployees * [int]$uniqueHours
    $ratio = [int]$totalRecords / $expectedMaxRecords
    
    Write-Host "[验证] 笛卡尔积检查:" -ForegroundColor Cyan
    Write-Host "  预期最大记录数（员工 × 小时）: $expectedMaxRecords" -ForegroundColor Gray
    Write-Host "  实际记录数: $totalRecords" -ForegroundColor Gray
    Write-Host "  比率: $([math]::Round($ratio * 100, 2))%" -ForegroundColor Gray
    
    if ($ratio -le 1.0) {
        Write-Host "  ✓ 无笛卡尔积问题（比率 <= 100%）" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 可能存在笛卡尔积问题（比率 > 100%）" -ForegroundColor Red
    }
    Write-Host ""
}

# 检查全 0 记录比例
Write-Host "[验证] 全 0 记录检查:" -ForegroundColor Cyan
$query2 = @"
SELECT 
    COUNT(*) as zero_records,
    COUNT(*) * 100.0 / NULLIF((SELECT COUNT(*) FROM dashboard_hourly_stats), 0) as percentage
FROM dashboard_hourly_stats
WHERE total_tokens = 0 
  AND conversation_turns = 0 
  AND error_count = 0;
"@

$result2 = Invoke-MySql -Query $query2
Write-Host $result2 -ForegroundColor White

$zeroPercent = ($result2 | Select-String "percentage").ToString().Split('|')[2].Trim()
if ([decimal]$zeroPercent -le 50) {
    Write-Host "  ✓ 全 0 记录比例合理 (<= 50%)" -ForegroundColor Green
} else {
    Write-Host "  ⚠ 全 0 记录比例较高 (> 50%)，可能需要优化" -ForegroundColor Yellow
}
Write-Host ""

# 检查 scan_history
Write-Host "[验证] Scan History 检查:" -ForegroundColor Cyan
$query3 = @"
SELECT 
    id,
    agg_hours_from_files,
    agg_hours_from_window,
    agg_hours_total,
    agg_duration_ms
FROM dashboard_scan_history
ORDER BY id DESC
LIMIT 1;
"@

$result3 = Invoke-MySql -Query $query3
Write-Host $result3 -ForegroundColor White

$hoursFromFiles = ($result3 | Select-String "agg_hours_from_files").ToString().Split('|')[1].Trim()
if ([int]$hoursFromFiles -gt 0) {
    Write-Host "  ✓ 从文件提取的小时数 > 0（说明收集功能正常工作）" -ForegroundColor Green
} else {
    Write-Host "  ⚠ 从文件提取的小时数 = 0（可能是全新数据库或没有扫描数据）" -ForegroundColor Yellow
}
Write-Host ""

# 总结
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  验证总结" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$allPassed = $true

if ([int]$totalRecords -eq 0) {
    Write-Host "⚠ 数据库为空，请先执行扫描" -ForegroundColor Yellow
    Write-Host "  命令: Invoke-WebRequest -Uri http://localhost:8080/api/v1/scan/trigger -Method POST -UseBasicParsing" -ForegroundColor Gray
    $allPassed = $false
} elseif ($ratio -le 1.0 -and [decimal]$zeroPercent -le 50 -and [int]$hoursFromFiles -gt 0) {
    Write-Host "✅ 所有检查通过！修复已生效。" -ForegroundColor Green
} else {
    Write-Host "❌ 部分检查未通过，请查看上方详细输出。" -ForegroundColor Red
    $allPassed = $false
}

Write-Host ""
Write-Host "如需完整 E2E 测试，请运行:" -ForegroundColor Cyan
Write-Host "  .\test\e2e-test-hourly-stats.ps1" -ForegroundColor White
Write-Host ""
Write-Host "详细测试文档:" -ForegroundColor Cyan
Write-Host "  docs\e2e-test-hourly-stats-aggregation.md" -ForegroundColor White
Write-Host ""

if ($allPassed) {
    exit 0
} else {
    exit 1
}
