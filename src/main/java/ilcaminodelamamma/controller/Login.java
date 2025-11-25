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

public class Login {

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

        // Aquí iría tu lógica real de autenticación
        if (usuario.equals("admin") && password.equals("1234")) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Bienvenido", "Inicio de sesión exitoso.");
            cargarVistaPrincipal();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
        }
    }

    private void cargarVistaPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/main/main.fxml"));

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista principal.");
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
