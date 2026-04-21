@echo off
cd /d d:\workplace\github\clawboard
java TestPost > test_output.txt 2>&1
type test_output.txt
