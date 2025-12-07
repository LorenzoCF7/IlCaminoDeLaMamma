package ilcaminodelamamma;

import ilcaminodelamamma.view.LoginApp;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal que inicia la interfaz gráfica con el login
 */
@SpringBootApplication
public class IlCaminoDeLaMammaApplication {

    public static void main(String[] args) {
        // Ensure demo users exist in the database (will be hashed by DAO)
        try {
            ilcaminodelamamma.util.DemoDataInitializer.ensureDefaultUsers();
        } catch (Exception e) {
            System.err.println("No se pudieron inicializar usuarios demo: " + e.getMessage());
            e.printStackTrace();
        }

        // Inicia la aplicación JavaFX del login
        LoginApp.main(args);
    }

}
