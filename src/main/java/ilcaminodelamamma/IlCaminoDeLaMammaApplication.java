package ilcaminodelamamma;

import ilcaminodelamamma.view.LoginApp;
import javafx.application.Application;

/**
 * Aplicación principal que inicia la interfaz gráfica con el login
 * Esta clase actúa como punto de entrada que integra Spring Boot con JavaFX
 */
public class IlCaminoDeLaMammaApplication {

    /**
     * Método main que inicia la aplicación
     * Lanza directamente la aplicación JavaFX con LoginApp
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Lanzar la aplicación con LoginApp
        Application.launch(LoginApp.class, args);
    }
}
