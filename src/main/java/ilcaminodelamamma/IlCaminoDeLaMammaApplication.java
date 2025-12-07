package ilcaminodelamamma;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import ilcaminodelamamma.view.LoginApp;
import javafx.application.Application;

/**
 * Aplicación principal que inicia la interfaz gráfica con el login
 * Esta clase actúa como punto de entrada que integra Spring Boot con JavaFX
 */
@SpringBootApplication
public class IlCaminoDeLaMammaApplication {

    /**
     * Método main que inicia la aplicación
     * Primero inicia el contexto de Spring Boot y luego lanza la aplicación JavaFX
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Lanzar la aplicación JavaFX (LoginApp)
        // JavaFX se encargará de inicializar su propio ciclo de vida
        Application.launch(LoginApp.class, args);
    }

}
