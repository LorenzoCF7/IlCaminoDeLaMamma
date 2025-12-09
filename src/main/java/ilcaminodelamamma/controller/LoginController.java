package ilcaminodelamamma.controller;

import java.net.URL;
import java.util.List;

import ilcaminodelamamma.DAO.UsuarioDAO;
import ilcaminodelamamma.model.Usuario;
import ilcaminodelamamma.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private void initialize() {
        // Puedes agregar lógica inicial si lo necesitas
        btnLogin.setOnAction(event -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        // Validación básica
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor completa todos los campos.");
            return;
        }

        // Primero intentar credenciales demo (más rápido para desarrollo)
        String usuarioLower = usuario.toLowerCase();
        Usuario u = null;
        
        if ((usuarioLower.equals("chef") || usuarioLower.equals("assistant") || 
             usuarioLower.equals("admin") || usuarioLower.equals("waiter") || 
             usuarioLower.equals("camarero")) && password.equals("1234")) {
            // Crear usuario temporal con rol según el nombre
            u = new Usuario();
            u.setNombre(usuarioLower);
            if (usuarioLower.equals("chef")) {
                u.setRol(ilcaminodelamamma.model.RolEnum.JEFE);
            } else if (usuarioLower.equals("assistant") || usuarioLower.equals("admin")) {
                u.setRol(ilcaminodelamamma.model.RolEnum.ADMIN);
            } else { // waiter o camarero
                u.setRol(ilcaminodelamamma.model.RolEnum.CAMARERO);
            }
            
            System.out.println("✓ Login demo exitoso: " + usuarioLower + " → " + u.getRol());
        } else {
            // Si no es demo, buscar en la base de datos
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            try {
                List<Usuario> usuarios = usuarioDAO.findByNombre(usuario);
                if (!usuarios.isEmpty()) {
                    u = usuarios.get(0);
                    if (!PasswordUtil.verify(password, u.getContrasena())) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
                        return;
                    }
                    System.out.println("✓ Login BD exitoso: " + u.getNombre() + " → " + u.getRol());
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error al buscar usuario en BD: " + e.getMessage());
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación", "No se pudo verificar el usuario: " + e.getMessage());
                return;
            }
        }

        if (u == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo autenticar el usuario.");
            return;
        }

        try {
            // Guardar usuario en la sesión
            ilcaminodelamamma.config.SessionManager.setUsuarioActual(u);
            
            // cargar vista según rol
            String fxmlPath;
            switch (u.getRol()) {
                case JEFE:
                    fxmlPath = "/fxml/chef/chef-view.fxml";
                    break;
                case ADMIN:
                    fxmlPath = "/fxml/assistant/assistant-view.fxml";
                    break;
                case CAMARERO:
                default:
                    fxmlPath = "/fxml/waiter/waiter-view.fxml";
                    break;
            }
            
            System.out.println("Cargando vista: " + fxmlPath + " para rol: " + u.getRol());
            cargarVistaPorRol(fxmlPath);
            
        } catch (Exception e) {
            System.err.println("Error al cargar vista: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar vista", e.getMessage());
        }
    }

    private void cargarVistaPorRol(String fxmlPath) {
        try {
            System.out.println("Intentando cargar vista: " + fxmlPath);

            // Verificar que el recurso existe
            URL recurso = getClass().getResource(fxmlPath);
            if (recurso == null) {
                throw new Exception("Archivo FXML no encontrado: " + fxmlPath);
            }

            System.out.println("Recurso encontrado en: " + recurso);

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            System.out.println("FXML cargado exitosamente");
            System.out.println("Controlador cargado: " + (loader.getController() != null ? loader.getController().getClass().getName() : "ninguno"));

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Il Camino Della Mamma");
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            System.out.println("Vista cargada y mostrada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al cargar la vista: " + fxmlPath);
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga de Vista",
                "No se pudo cargar la vista.\n\n" +
                "Ruta intentada: " + fxmlPath + "\n" +
                "Error: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        
        // Emojis y colores según el tipo
        final String emoji;
        final String colorHeader;
        
        switch (tipo) {
            case INFORMATION:
                emoji = "✓ ";
                colorHeader = "#4CAF50";
                break;
            case WARNING:
                emoji = "⚠ ";
                colorHeader = "#FF9800";
                break;
            case ERROR:
                emoji = "✗ ";
                colorHeader = "#D32F2F";
                break;
            case CONFIRMATION:
                emoji = "❓ ";
                colorHeader = "#2196F3";
                break;
            default:
                emoji = "";
                colorHeader = "#8B7355";
                break;
        }
        
        alert.setTitle(emoji + titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        
        // Estilizar el diálogo
        alert.getDialogPane().setStyle(
            "-fx-background-color: #FAF8F5; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 13px;"
        );
        
        // Estilizar botones
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) alert.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
            if (okButton != null) {
                okButton.setStyle(
                    "-fx-background-color: " + colorHeader + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 10px 20px; " +
                    "-fx-background-radius: 5px;"
                );
            }
        });
        
        alert.showAndWait();
    }
}
