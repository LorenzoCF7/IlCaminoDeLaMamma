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

import java.net.URL;

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
            Scene scene = new Scene(root, 1200, 700);
            stage.setScene(scene);
            stage.setTitle("Il Camino Della Mamma");
            stage.setMaximized(true);
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
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
