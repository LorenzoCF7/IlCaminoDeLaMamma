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
        // Lanzar la aplicación con LoginApp (requiere MySQL configurado)
        // Para desarrollo sin BD, cambiar a: ilcaminodelamamma.view.chef.ChefApp.class
        Application.launch(LoginApp.class, args);
    }

}
