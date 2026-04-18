@echo off
set JAVA_HOME=D:\Program Files\Java\jdk-17.0.18
set M2_REPO=I:\m2

echo ========================================
echo Running DashboardServiceTest
echo ========================================

F:\Program\Maven\apache-maven-3.9.15\bin\mvn.cmd test -Dtest=DashboardServiceTest

echo.
echo Test execution completed!
pause
