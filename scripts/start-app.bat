
@echo off
set JAVA_HOME=D:\Program\JDK\jdk-17.0.18
set PATH=%JAVA_HOME%\bin;%PATH%
java -Dspring.profiles.active=dev -jar target\clawboard-1.0.0-SNAPSHOT.jar
