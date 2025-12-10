package ilcaminodelamamma;

import ilcaminodelamamma.util.DataInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación principal que inicia la interfaz gráfica con el login
 * Esta clase extiende Application de JavaFX para funcionar correctamente
 * como punto de entrada de la aplicación
 */
public class IlCaminoDeLaMammaApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        
        // Inicializar datos de la aplicación (cargar desde XML)
        System.out.println("\n========================================");
        System.out.println("  INICIALIZANDO APLICACIÓN");
        System.out.println("========================================\n");
        
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.inicializarDatos();
        
        System.out.println("\n========================================");
        System.out.println("  APLICACIÓN LISTA");
        System.out.println("========================================\n");
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login/login.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 700, 550);

            // Configurar la ventana principal
            primaryStage.setTitle("Il Camino Della Mamma - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);
            primaryStage.setResizable(true);
            primaryStage.show();

            System.out.println("Pantalla de login iniciada correctamente");

        } catch (Exception e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método main que inicia la aplicación JavaFX
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}
