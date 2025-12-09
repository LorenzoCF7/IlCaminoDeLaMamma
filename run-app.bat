@echo off
REM Script para ejecutar la aplicación principal con Login
REM Utiliza el perfil 'login' que está configurado por defecto en pom.xml

echo ========================================
echo  Il Camino Della Mamma - Aplicacion Principal
echo ========================================
echo.
echo Iniciando aplicacion con pantalla de Login...
echo.

REM Configurar la ruta de Maven
set MAVEN_HOME=C:\maven\maven-3.9.11
set PATH=%MAVEN_HOME%\bin;%PATH%
set JAVA_HOME=C:\Program Files\Java\jdk-21

REM Ejecutar con Maven usando JavaFX
call mvn javafx:run

pause
