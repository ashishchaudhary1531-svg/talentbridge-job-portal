@echo off
setlocal
cd /d "%~dp0"
call mvnw.cmd -DskipTests clean package exec:java -Dexec.mainClass=com.talentbridge.StandaloneServer -Dexec.classpathScope=provided
