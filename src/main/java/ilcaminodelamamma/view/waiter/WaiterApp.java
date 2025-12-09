package ilcaminodelamamma.view.waiter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX para Camareros
 */
public class WaiterApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/waiter/waiter-view.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 1200, 700);

            // Configurar la ventana principal
            primaryStage.setTitle("Il Camino Della Mamma - Camarero");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            System.out.println("Aplicación de Camarero iniciada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al cargar la vista del camarero: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
