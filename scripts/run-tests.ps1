# ClawBoard Test Runner Script
# This script runs different test suites with various options

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ClawBoard Test Runner" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$env:JAVA_HOME = "D:\Program Files\Java\jdk-17.0.18"
$env:M2_REPO = "I:\m2"
$mavenPath = "F:\Program\Maven\apache-maven-3.9.15\bin\mvn.cmd"

function Run-Tests {
    param(
        [string]$TestPattern,
        [string]$Description,
        [switch]$Verbose
    )
    
    Write-Host "" -NoNewline
    Write-Host "Running: $Description" -ForegroundColor Yellow
    Write-Host "Pattern: $TestPattern" -ForegroundColor Gray
    Write-Host ""
    
    $args = @("clean", "test")
    if ($TestPattern) {
        $args += "-Dtest=$TestPattern"
        $args += "-DfailIfNoTests=false"
    }
    if ($Verbose) {
        $args += "-X"
    }
    
    $startTime = Get-Date
    & $mavenPath @args
    $exitCode = $LASTEXITCODE
    $endTime = Get-Date
    $duration = ($endTime - $startTime).TotalSeconds
    
    Write-Host ""
    if ($exitCode -eq 0) {
        Write-Host "✓ Tests passed in $([math]::Round($duration, 2)) seconds" -ForegroundColor Green
    } else {
        Write-Host "✗ Tests failed in $([math]::Round($duration, 2)) seconds" -ForegroundColor Red
    }
    Write-Host ""
    
    return $exitCode
}

# Menu
Write-Host "Select test suite to run:" -ForegroundColor Cyan
Write-Host "1. All Tests" -ForegroundColor White
Write-Host "2. Unit Tests Only" -ForegroundColor White
Write-Host "3. Integration Tests Only" -ForegroundColor White
Write-Host "4. Controller Tests" -ForegroundColor White
Write-Host "5. Service Tests" -ForegroundColor White
Write-Host "6. Mapper Tests" -ForegroundColor White
Write-Host "7. Specific Test Class" -ForegroundColor White
Write-Host "0. Exit" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter choice (0-7)"

switch ($choice) {
    "1" {
        Run-Tests -Description "All Tests" -TestPattern ""
    }
    "2" {
        Run-Tests -Description "Unit Tests" -TestPattern "*Test"
    }
    "3" {
        Run-Tests -Description "Integration Tests" -TestPattern "*IntegrationTest"
    }
    "4" {
        Run-Tests -Description "Controller Tests" -TestPattern "*Controller*Test"
    }
    "5" {
        Run-Tests -Description "Service Tests" -TestPattern "*Service*Test"
    }
    "6" {
        Run-Tests -Description "Mapper Tests" -TestPattern "*Mapper*Test"
    }
    "7" {
        $className = Read-Host "Enter test class name (e.g., DashboardServiceTest)"
        if ($className) {
            Run-Tests -Description "Specific Test: $className" -TestPattern $className
        } else {
            Write-Host "No class name provided." -ForegroundColor Red
        }
    }
    "0" {
        Write-Host "Exiting..." -ForegroundColor Gray
        exit 0
    }
    default {
        Write-Host "Invalid choice." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "Test execution completed!" -ForegroundColor Cyan
Write-Host ""
