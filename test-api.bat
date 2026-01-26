@echo off
echo Testing Machine Shop API...
echo.
echo 1. Health Check:
curl http://localhost:8080/api/health
echo.
echo.
echo 2. Welcome Endpoint:
curl http://localhost:8080/api/
echo.
echo.
echo 3. Swagger UI:
echo Open browser: http://localhost:8080/swagger-ui.html
echo.
pause
