package ilcaminodelamamma.controller;

import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import ilcaminodelamamma.DAO.UsuarioDAO;
import ilcaminodelamamma.model.Usuario;
import ilcaminodelamamma.util.PasswordUtil;

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

        // Autenticación usando la base de datos y contraseñas hasheadas
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            List<Usuario> usuarios = usuarioDAO.findByNombre(usuario);
            Usuario u = null;
            if (!usuarios.isEmpty()) {
                // tomar el primer usuario con ese nombre
                u = usuarios.get(0);
                if (!PasswordUtil.verify(password, u.getContrasena())) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
                    return;
                }
            } else {
                // Si no hay usuario en la BD, permitir credenciales demo para pruebas
                String usuarioLower = usuario.toLowerCase();
                if ((usuarioLower.equals("chef") || usuarioLower.equals("assistant") || usuarioLower.equals("admin") || usuarioLower.equals("waiter") || usuarioLower.equals("camarero"))
                        && password.equals("1234")) {
                    // construir un usuario temporal con rol según el nombre
                    u = new Usuario();
                    u.setNombre(usuarioLower);
                    if (usuarioLower.equals("chef")) {
                        u.setRol(ilcaminodelamamma.model.RolEnum.JEFE);
                    } else if (usuarioLower.equals("assistant") || usuarioLower.equals("admin")) {
                        u.setRol(ilcaminodelamamma.model.RolEnum.ADMIN);
                    } else {
                        u.setRol(ilcaminodelamamma.model.RolEnum.CAMARERO);
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
                    return;
                }
            }

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
            cargarVistaPorRol(fxmlPath);
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
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
