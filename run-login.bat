@echo off
REM Script para ejecutar LoginApp 
REM Compilar y ejecutar con Maven

title Il Camino Della Mamma - Login
echo.
echo ================================================
echo   IL CAMINO DELLA MAMMA - SISTEMA DE LOGIN
echo ================================================
echo.

REM Configurar rutas
set "MAVEN_HOME=C:\maven\maven-3.9.11"
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Compilando proyecto...
echo.

REM Compilar (sin clean para evitar bloqueos de archivos)
call mvn package -DskipTests

if errorlevel 1 (
    echo Error durante la compilacion
    pause
    exit /b 1
)

REM Ejecutar con javafx:run
echo.
echo Lanzando aplicacion...
call mvn javafx:run

pause


    setlocal enabledelayedexpansion

    REM Detectar Maven (opcional) para usar javafx:run
    set "MAVEN_CMD="
    if exist "%USERPROFILE%\.maven\maven-3.9.11\bin\mvn.cmd" (
        set "MAVEN_CMD=%USERPROFILE%\.maven\maven-3.9.11\bin\mvn.cmd"
    ) else if exist "C:\Maven\bin\mvn.cmd" (
        set "MAVEN_CMD=C:\Maven\bin\mvn.cmd"
    ) else if exist "C:\Program Files\maven\bin\mvn.cmd" (
        set "MAVEN_CMD=C:\Program Files\maven\bin\mvn.cmd"
    )

    echo Compilando proyecto...
    if not "%MAVEN_CMD%"=="" (
        "%MAVEN_CMD%" clean compile -DskipTests -q
        if errorlevel 1 (
            echo Error durante la compilacion del proyecto
            "%MAVEN_CMD%" clean compile -DskipTests
            pause
            exit /b 1
        )
        echo Proyecto compilado correctamente (Maven)
    ) else (
        REM Intentar compilacion con javac via mvn no disponible (fall back)
        echo Maven no encontrado. Asumiendo que "target\classes" esta disponible.
    )

    REM Si Maven existe, preferimos usar javafx:run (maneja natives correctamente)
    if not "%MAVEN_CMD%"=="" (
        echo Ejecutando mediante Maven (javafx:run -P login)...
        "%MAVEN_CMD%" javafx:run -P login
        if errorlevel 1 (
            echo Error al ejecutar con javafx:run. Se intentara fallback manual.
        ) else (
            echo Aplicacion finalizada.
            pause
            exit /b 0
        )
    )

    echo Ejecutando fallback manual (java -cp ...)

    REM Construir classpath manual (excluir javadoc/sources; usar -win jars para JavaFX)
    set "M2_REPO=%USERPROFILE%\.m2\repository"
    set "CLASSPATH=target\classes"

    set "CP_ADD=%M2_REPO%\org\openjfx\javafx-controls\21\javafx-controls-21-win.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    set "CP_ADD=%M2_REPO%\org\openjfx\javafx-fxml\21\javafx-fxml-21-win.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    set "CP_ADD=%M2_REPO%\org\openjfx\javafx-graphics\21\javafx-graphics-21-win.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    set "CP_ADD=%M2_REPO%\org\openjfx\javafx-base\21\javafx-base-21-win.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    REM Agregar otras dependencias que suelen ser necesarias (mysql, hibernate)
    set "CP_ADD=%M2_REPO%\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    set "CP_ADD=%M2_REPO%\org\hibernate\orm\hibernate-core\6.6.3.Final\hibernate-core-6.6.3.Final.jar"
    if exist "%CP_ADD%" set "CLASSPATH=%CLASSPATH%;%CP_ADD%"

    echo Classpath construido: %CLASSPATH%

    echo Iniciando aplicacion (fallback)...
    java -Xmx512m -cp "%CLASSPATH%" ilcaminodelamamma.IlCaminoDeLaMammaApplication

    if errorlevel 1 (
        echo.
        echo ERROR durante la ejecucion de la aplicacion
        pause
        exit /b 1
    )

    pause

