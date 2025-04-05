@echo off
echo Repairing Flyway migrations...
call mvn flyway:repair
echo Done.
pause
