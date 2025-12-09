@echo off
REM Script para ejecutar la aplicación principal con Login
REM Utiliza el perfil 'login' que está configurado por defecto en pom.xml

echo ========================================
echo  Il Camino Della Mamma - Aplicación Principal
echo ========================================
echo.
echo Iniciando aplicación con pantalla de Login...
echo.

REM Configurar la ruta de Maven (desde IntelliJ IDEA)
set MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.3\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Ejecutar con Maven usando JavaFX
mvn clean javafx:run

pause
