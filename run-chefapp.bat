@echo off
echo Ejecutando ChefApp con JavaFX...

set JAVAFX_PATH=C:\Users\marcocc090506\.m2\repository\org\openjfx

java --module-path "%JAVAFX_PATH%\javafx-controls\21;%JAVAFX_PATH%\javafx-fxml\21;%JAVAFX_PATH%\javafx-graphics\21;%JAVAFX_PATH%\javafx-base\21" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
     --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.binding=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.event=ALL-UNNAMED ^
     --add-opens javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED ^
     -cp "target\classes;%JAVAFX_PATH%\javafx-controls\21\javafx-controls-21.jar;%JAVAFX_PATH%\javafx-fxml\21\javafx-fxml-21.jar;%JAVAFX_PATH%\javafx-graphics\21\javafx-graphics-21.jar;%JAVAFX_PATH%\javafx-base\21\javafx-base-21.jar" ^
     ilcaminodelamamma.view.ChefApp

pause
