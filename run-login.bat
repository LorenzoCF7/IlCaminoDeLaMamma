@echo off
title Il Camino Della Mamma - Login
echo.
echo ================================================
echo   IL CAMINO DELLA MAMMA - SISTEMA DE LOGIN
echo ================================================
echo.
echo Compilando proyecto...

REM Ruta a Maven (ajustar según tu instalación)
set MAVEN_HOME=C:\Users\marcocc090506\.maven\maven-3.9.11
set MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd

REM Si no encuentra Maven en esa ruta, buscar en PATH
if not exist "%MAVEN_CMD%" (
    set MAVEN_CMD=mvn
)

REM Compilar el proyecto
%MAVEN_CMD% clean compile -DskipTests -q
if errorlevel 1 (
    echo Error durante la compilacion del proyecto
    pause
    exit /b 1
)

echo Proyecto compilado correctamente!
echo.
echo Ejecutando aplicacion...
echo.

REM Rutas de JavaFX
set JAVAFX_PATH=C:\Users\marcocc090506\.m2\repository\org\openjfx

REM Ejecutar la aplicacion
java --module-path "%JAVAFX_PATH%\javafx-controls\21;%JAVAFX_PATH%\javafx-fxml\21;%JAVAFX_PATH%\javafx-graphics\21;%JAVAFX_PATH%\javafx-base\21" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
     --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.binding=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.event=ALL-UNNAMED ^
     --add-opens javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED ^
     -cp "target\classes;%JAVAFX_PATH%\javafx-controls\21\javafx-controls-21.jar;%JAVAFX_PATH%\javafx-fxml\21\javafx-fxml-21.jar;%JAVAFX_PATH%\javafx-graphics\21\javafx-graphics-21.jar;%JAVAFX_PATH%\javafx-base\21\javafx-base-21.jar" ^
     ilcaminodelamamma.IlCaminoDeLaMammaApplication

if errorlevel 1 (
    echo.
    echo Error durante la ejecucion de la aplicacion
    pause
    exit /b 1
)

pause

