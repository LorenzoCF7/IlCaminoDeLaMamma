@echo off
setlocal enabledelayedexpansion
echo Preparando ejecución de IlCaminoDeLaMamma...

set M2=%USERPROFILE%\.m2\repository
set LIB_DIR=target\lib
set RES_SRC=src\main\resources
set RES_DST=target\classes

echo Creando carpeta %LIB_DIR% si no existe...
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

echo Copiando recursos (si faltan)...
if exist "%RES_SRC%" (
     xcopy "%RES_SRC%\*" "%RES_DST%\" /E /Y >nul
)

echo Copiando dependencias necesarias desde el repositorio Maven local (operación puede tardar)...
set PREFIXES=org\openjfx org\hibernate com\fasterxml com\mysql org\springframework org\jboss javax jakarta org\slf4j ch\qos org\apache
for %%P in (%PREFIXES%) do (
    if exist "%M2%\%%P" (
        for /r "%M2%\%%P" %%J in (*.jar) do (
            copy "%%~fJ" "%LIB_DIR%\" >nul
        )
    )
)

REM Eliminar fuentes y javadoc si se copiaron
del "%LIB_DIR%\*-sources.jar" "%LIB_DIR%\*-javadoc.jar" 2>nul

echo Localizando jars de JavaFX para module-path...
set MODULE_PATH=
for %%A in (javafx-controls javafx-fxml javafx-graphics javafx-base) do (
     if exist "%M2%\org\openjfx\%%A\21" (
          for %%F in ("%M2%\org\openjfx\%%A\21\*win*.jar" "%M2%\org\openjfx\%%A\21\*.jar") do (
               if exist %%F (
                    if defined MODULE_PATH (set MODULE_PATH=!MODULE_PATH!;%%~fF) else (set MODULE_PATH=%%~fF)
               )
          )
     )
)

if not defined MODULE_PATH (
     echo No se encontraron jars de JavaFX en %M2%\org\openjfx\*. Asegurate de haber descargado JavaFX via Maven.
     echo Intentando ejecutar con classpath solo (puede fallar)...
)

echo Ejecutando aplicación...
set CP=target\classes;%LIB_DIR%\*

if defined MODULE_PATH (
     java --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED --add-opens javafx.base/com.sun.javafx.binding=ALL-UNNAMED --add-opens javafx.base/com.sun.javafx.event=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED -cp "%CP%" ilcaminodelamamma.view.chef.ChefApp
) else (
     java -cp "%CP%" ilcaminodelamamma.view.chef.ChefApp
)

pause
