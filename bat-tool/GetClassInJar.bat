@echo off 
setlocal enabledelayedexpansion 
for /f  %%i in ('dir /b /s "*.jar"') do ( 
echo %%i
set str=%%i 
set str=!str:\=\\!
echo\ >> classList.txt
echo START: !str! >> classList.txt
jar -tf !str! >> classList.txt
echo END: !str! >> classList.txt
echo. >> classList.txt) 
@echo on 