@echo off
echo Starting Machine Shop API...
echo.
echo Make sure PostgreSQL is running (docker-compose up -d)
echo.
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
.\mvnw.cmd spring-boot:run
