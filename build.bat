@echo off
echo ========================================
echo HoyoZeroDeploy 一键打包脚本
echo ========================================

echo.
echo [1/3] 正在构建前端项目...
cd web
call npm install
call npm run build
if errorlevel 1 (
    echo 前端构建失败！
    pause
    exit /b 1
)

echo.
echo [2/3] 前端构建成功！
cd ..

echo.
echo [3/3] 正在打包后端项目...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo 后端打包失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo 打包成功！
echo JAR 文件位置: target\hoyozero-deploy-1.0.0.jar
echo 运行命令: java -jar target\hoyozero-deploy-1.0.0.jar
echo ========================================
pause
