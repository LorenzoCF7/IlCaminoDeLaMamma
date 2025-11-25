package ilcaminodelamamma.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX para el Jefe de Cocina
 */
public class ChefApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef-view.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 1200, 700);

            // Configurar la ventana principal
            primaryStage.setTitle("Il Camino Della Mamma - Jefe de Cocina");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            System.out.println("Aplicación iniciada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
