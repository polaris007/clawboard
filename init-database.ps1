# MySQL Database Initialization Script for ClawBoard
# This script initializes the clawboard database using the Flyway migration SQL

$mysqlPath = "F:\Program\Database\mysql-5.7.44-winx64\bin\mysql.exe"
$sqlFile = "src\main\resources\db\migration\V1__init_schema_clean.sql"
$dbUser = "clawboard"
$dbPassword = "Clqc@1234"
$dbName = "clawboard"
$dbHost = "127.0.0.1"
$dbPort = "3306"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ClawBoard Database Initialization" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if MySQL executable exists
if (-not (Test-Path $mysqlPath)) {
    Write-Host "ERROR: MySQL executable not found at: $mysqlPath" -ForegroundColor Red
    exit 1
}

# Check if SQL file exists
if (-not (Test-Path $sqlFile)) {
    Write-Host "ERROR: SQL migration file not found at: $sqlFile" -ForegroundColor Red
    exit 1
}

Write-Host "MySQL Path: $mysqlPath" -ForegroundColor Gray
Write-Host "SQL File: $sqlFile" -ForegroundColor Gray
Write-Host "Database: ${dbName}@${dbHost}:${dbPort}" -ForegroundColor Gray
Write-Host ""

# Execute SQL file
Write-Host "Executing SQL migration..." -ForegroundColor Yellow
try {
    # Read SQL content
    $sqlContent = Get-Content $sqlFile -Raw
    
    # Start mysql process with stdin
    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = $mysqlPath
    $psi.Arguments = "-h${dbHost} -P${dbPort} -u${dbUser} -p${dbPassword} --default-character-set=utf8mb4 ${dbName}"
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardError = $true
    $psi.RedirectStandardOutput = $true
    $psi.UseShellExecute = $false
    $psi.CreateNoWindow = $true
    
    $process = [System.Diagnostics.Process]::Start($psi)
    $process.StandardInput.WriteLine($sqlContent)
    $process.StandardInput.Close()
    $stdout = $process.StandardOutput.ReadToEnd()
    $stderr = $process.StandardError.ReadToEnd()
    $process.WaitForExit()
    
    if ($stdout) {
        Write-Host $stdout -ForegroundColor Gray
    }
    if ($stderr) {
        Write-Host $stderr -ForegroundColor Red
    }
    
    if ($process.ExitCode -eq 0) {
        Write-Host ""
        Write-Host "SUCCESS: Database initialized successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "You can now run the application with:" -ForegroundColor Cyan
        Write-Host "mvn spring-boot:run -Dspring-boot.run.profiles=dev" -ForegroundColor White
    } else {
        Write-Host ""
        Write-Host "ERROR: MySQL execution failed with exit code: $($process.ExitCode)" -ForegroundColor Red
        Write-Host "Please check the error messages above." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Failed to execute MySQL command" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
