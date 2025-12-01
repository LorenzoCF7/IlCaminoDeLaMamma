package ilcaminodelamamma.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX principal que muestra la pantalla de login
 */
public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login/login.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 500, 400);

            // Configurar la ventana principal
            primaryStage.setTitle("Il Camino Della Mamma - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("Pantalla de login iniciada correctamente");

        } catch (Exception e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

