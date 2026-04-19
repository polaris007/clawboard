# ============================================
# ClawBoard Database Reset Script
# Usage: .\reset-database.ps1
# ============================================

$DB_HOST = "localhost"
$DB_USER = "clawboard"
$DB_PASS = "Clqc@1234"
$DB_NAME = "clawboard"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ClawBoard Database Reset Tool" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Confirm before proceeding
$confirm = Read-Host "This will DELETE ALL DATA from database '$DB_NAME'. Continue? (yes/no)"
if ($confirm -ne "yes") {
    Write-Host "Operation cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Truncating tables..." -ForegroundColor Green

# Build MySQL command
$mysqlCmd = "mysql -h $DB_HOST -u $DB_USER -p'$DB_PASS' $DB_NAME"

# Truncate tables in correct order (respecting foreign key constraints)
$tables = @(
    "dashboard_transcript_issue",
    "dashboard_skill_invocation",
    "dashboard_conversation_turn",
    "dashboard_message",
    "dashboard_session_summary",
    "dashboard_scan_progress",
    "dashboard_scan_history",
    "dashboard_hourly_stats",
    "dashboard_employee"
)

foreach ($table in $tables) {
    try {
        Invoke-Expression "$mysqlCmd -e `"TRUNCATE TABLE $table;`"" 2>&1 | Out-Null
        Write-Host "  ✓ Truncated: $table" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ Failed to truncate: $table" -ForegroundColor Red
        Write-Host "    Error: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Verifying..." -ForegroundColor Green

# Check table counts
Invoke-Expression "$mysqlCmd -e `"SELECT 'dashboard_scan_history' as tbl, COUNT(*) as cnt FROM dashboard_scan_history UNION ALL SELECT 'dashboard_message', COUNT(*) FROM dashboard_message UNION ALL SELECT 'dashboard_session_summary', COUNT(*) FROM dashboard_session_summary UNION ALL SELECT 'dashboard_transcript_issue', COUNT(*) FROM dashboard_transcript_issue;`""

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Database reset completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can now run a fresh scan." -ForegroundColor Yellow
