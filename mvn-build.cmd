@echo off
%~d0
cd %~dp0

call %~dp0/uros-parent/mvn-build.cmd 1
call %~dp0/uros/mvn-build.cmd 1
call %~dp0/uros-showcase/mvn-build.cmd 1

if '%1' equ '' pause