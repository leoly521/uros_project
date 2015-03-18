@echo off
%~d0
cd %~dp0

rem call mvn clean
ping -n 2 127.0.0.1 > nul
@echo on
call mvn install -Dmaven.test.skip

if '%1' equ '' pause