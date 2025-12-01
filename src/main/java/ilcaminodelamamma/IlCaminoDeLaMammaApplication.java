package ilcaminodelamamma;

import ilcaminodelamamma.view.LoginApp;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal que inicia la interfaz gráfica con el login
 */
@SpringBootApplication
public class IlCaminoDeLaMammaApplication {

    public static void main(String[] args) {
        // Inicia la aplicación JavaFX del login
        LoginApp.main(args);
    }

}
