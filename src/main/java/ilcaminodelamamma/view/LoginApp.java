package ilcaminodelamamma.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

/**
 * Aplicación JavaFX principal que muestra la pantalla de login
 */
public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== INICIANDO APLICACIÓN LOGIN ===");
        System.out.println("Thread: " + Thread.currentThread().getName());
        
        try {
            System.out.println("1. Buscando recurso FXML...");
            var fxmlUrl = getClass().getResource("/fxml/login/login.fxml");
            
            if (fxmlUrl == null) {
                System.err.println("❌ ERROR: No se encontró login.fxml en /fxml/login/");
                System.err.println("   Recursos disponibles: " + getClass().getResource("/fxml/"));
                mostrarErrorDialog(primaryStage, "Error de Recurso", "No se encontró el archivo login.fxml");
                return;
            }
            
            System.out.println("2. Cargando FXML desde: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            System.out.println("3. FXML cargado correctamente");

            System.out.println("4. Creando escena (700x550)...");
            Scene scene = new Scene(root, 700, 550);

            System.out.println("5. Configurando ventana principal...");
            primaryStage.setTitle("Il Camino Della Mamma - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);
            primaryStage.setResizable(true);
            
            // Configurar manejador de cierre para impedir que la app se cierre
            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Impedir el cierre
                System.out.println("⚠️ Intento de cierre bloqueado - La aplicación debe ejecutarse siempre");
            });
            
            System.out.println("6. Mostrando ventana...");
            primaryStage.show();

            System.out.println("✅ Pantalla de login iniciada correctamente");
            System.out.println("=== FIN INICIALIZACIÓN ===\n");

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO al cargar la pantalla de login:");
            System.err.println("   Tipo: " + e.getClass().getName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            // Intentar mostrar diálogo de error
            try {
                mostrarErrorDialog(primaryStage, "Error de Inicialización", 
                    "Error: " + e.getClass().getSimpleName() + "\n" + e.getMessage());
            } catch (Exception ex) {
                System.err.println("No se pudo mostrar diálogo de error: " + ex.getMessage());
            }
        }
    }

    private void mostrarErrorDialog(Stage owner, String titulo, String mensaje) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.initOwner(owner);
            alert.setTitle(titulo);
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("No se pudo mostrar alerta: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println(">>> LANZANDO LOGINAPP <<<");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("❌ EXCEPCIÓN en launch():");
            e.printStackTrace();
        }
    }
}

