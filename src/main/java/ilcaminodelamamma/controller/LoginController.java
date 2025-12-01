package ilcaminodelamamma.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
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

        // Lógica de autenticación de ejemplo (reemplazar por autenticación real)
        // Mapeamos usuarios de ejemplo a vistas por rol:
        // - chef  -> chef-view.fxml
        // - assistant -> assistant-view.fxml
        // - waiter -> waiter-view.fxml
        // Si quieres integrar con la base de datos, aquí debes consultar al servicio/DAO correspondiente.
        String usuarioLower = usuario.toLowerCase();

        try {
            switch (usuarioLower) {
                case "chef":
                    if (!password.equals("1234")) {
                        throw new Exception("Contraseña incorrecta");
                    }
                    cargarVistaPorRol("/fxml/chef/chef-view.fxml");
                    break;
                case "assistant":
                case "admin": // opcional: permitir admin para vista assistant
                    if (!password.equals("1234")) {
                        throw new Exception("Contraseña incorrecta");
                    }
                    cargarVistaPorRol("/fxml/assistant/assistant-view.fxml");
                    break;
                case "waiter":
                case "camarero":
                    if (!password.equals("1234")) {
                        throw new Exception("Contraseña incorrecta");
                    }
                    cargarVistaPorRol("/fxml/waiter/waiter-view.fxml");
                    break;
                default:
                    mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación", e.getMessage());
        }
    }

    private void cargarVistaPorRol(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
