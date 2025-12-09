package ilcaminodelamamma;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Aplicación de prueba simple para verificar que JavaFX funciona
 */
public class TestApp extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println(">>> TestApp iniciado <<<");
        
        Label label = new Label("✅ JavaFX funciona correctamente!");
        label.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C1810;");
        
        Label label2 = new Label("Si ves este mensaje, JavaFX está OK");
        label2.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 30;");
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, label2);
        
        Scene scene = new Scene(root, 600, 400);
        
        stage.setTitle("Test JavaFX");
        stage.setScene(scene);
        stage.show();
        
        System.out.println("TestApp ventana mostrada");
    }

    public static void main(String[] args) {
        System.out.println("=== LANZANDO TEST APP ===");
        launch(args);
    }
}
