package ilcaminodelamamma.view.waiter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controlador para la vista de Camarero - Lista de Comandas
 */
public class WaiterViewController implements Initializable {

    @FXML private Button btnNuevaComanda;
    @FXML private Button btnListaComandas;
    @FXML private Button btnAyuda;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;
    
    @FXML private Button tabTodas;
    @FXML private Button tabPreparacion;
    @FXML private Button tabListas;
    
    @FXML private VBox comandasContainer;

    // Sistema de mesas (15 mesas predeterminadas) - TODAS LIBRES INICIALMENTE
    public static final List<MesaInfo> MESAS = new ArrayList<>();
    static {
        // Inicializar 15 mesas - todas libres
        for (int i = 1; i <= 15; i++) {
            MESAS.add(new MesaInfo(i, "Libre", null));
        }
    }
    
    // Almacenamiento temporal de comandas (por mesa)
    public static final Map<Integer, ComandaData> COMANDAS_ACTIVAS = new HashMap<>();
    
    private List<MesaInfo> mesasFiltradas = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista de Camarero inicializada correctamente");
        
        // Configurar botones de pestañas
        setupTabButtons();
        
        // Habilitar botón Nueva Comanda
        btnNuevaComanda.setOnAction(e -> abrirSelectorMesa());
        
        // Mostrar todas las mesas ocupadas
        filtrarYMostrarMesas("Todas");
        
        // Configurar botón de cerrar sesión
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> cerrarSesion());
        }
    }

    /**
     * Filtra y muestra las mesas según el estado
     */
    private void filtrarYMostrarMesas(String filtro) {
        mesasFiltradas.clear();
        
        for (MesaInfo mesa : MESAS) {
            // Solo mostrar mesas ocupadas
            if (mesa.estado.equals("Ocupada")) {
                mesasFiltradas.add(mesa);
            }
        }
        
        displayMesas();
    }

    /**
     * Configura los botones de las pestañas
     */
    private void setupTabButtons() {
        // Marcar "Todas" como activa por defecto
        tabTodas.getStyleClass().add("active");
        
        // Configurar eventos de clic
        tabTodas.setOnAction(e -> switchTab(tabTodas));
        tabPreparacion.setOnAction(e -> switchTab(tabPreparacion));
        tabListas.setOnAction(e -> switchTab(tabListas));
    }

    /**
     * Cambia la pestaña activa
     */
    private void switchTab(Button activeTab) {
        // Remover clase activa de todos
        tabTodas.getStyleClass().remove("active");
        tabPreparacion.getStyleClass().remove("active");
        tabListas.getStyleClass().remove("active");
        
        // Agregar clase activa al seleccionado
        activeTab.getStyleClass().add("active");
    }

    /**
     * Muestra las mesas en el contenedor
     */
    private void displayMesas() {
        comandasContainer.getChildren().clear();
        
        for (MesaInfo mesa : mesasFiltradas) {
            HBox mesaBox = createMesaBox(mesa);
            comandasContainer.getChildren().add(mesaBox);
        }
    }

    /**
     * Crea un box de mesa con tamaños uniformes
     */
    private HBox createMesaBox(MesaInfo mesa) {
        HBox box = new HBox();
        box.getStyleClass().add("comanda-box");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(25);
        box.setPadding(new Insets(12, 20, 12, 20));
        box.setPrefHeight(90);
        box.setMinHeight(90);
        box.setMaxHeight(90);
        VBox.setMargin(box, new Insets(0, 0, 12, 0));
        
        // Contenedor de imagen con tamaño fijo
        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setMinWidth(70);
        imageContainer.setPrefWidth(70);
        imageContainer.setMaxWidth(70);
        imageContainer.setMinHeight(70);
        imageContainer.setPrefHeight(70);
        imageContainer.setMaxHeight(70);
        imageContainer.getStyleClass().add("image-container");
        
        // Imagen del plato con tamaño fijo
        if (mesa.imageName != null) {
            try {
                var imageStream = getClass().getResourceAsStream("/img/" + mesa.imageName);
                if (imageStream != null) {
                    ImageView imageView = new ImageView(new Image(imageStream));
                    imageView.setFitWidth(70);
                    imageView.setFitHeight(70);
                    imageView.setPreserveRatio(false); // Forzar tamaño exacto
                    imageView.setSmooth(true); // Suavizar la imagen
                    imageView.getStyleClass().add("comanda-image");
                    imageContainer.getChildren().add(imageView);
                }
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + e.getMessage());
            }
        }
        
        box.getChildren().add(imageContainer);
    
        
        // Espacio
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);
        
        // Label de mesa
        Label mesaLabel = new Label("Mesa " + mesa.numero);
        mesaLabel.getStyleClass().add("mesa-label");
        mesaLabel.setPrefWidth(200);
        mesaLabel.setAlignment(Pos.CENTER);
        box.getChildren().add(mesaLabel);
        
        // Espacio
        HBox spacer2 = new HBox();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        box.getChildren().add(spacer2);
        
        // Botón de flecha
        Button arrowButton = new Button("→");
        arrowButton.getStyleClass().add("arrow-button");
        arrowButton.setOnAction(e -> abrirComandaMesa(mesa.numero));
        box.getChildren().add(arrowButton);
        
        return box;
    }

    /**
     * Abre selector de mesa para crear nueva comanda
     */
    private void abrirSelectorMesa() {
        // Crear diálogo de selección de mesa
        javafx.scene.control.Dialog<Integer> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("✨ Nueva Comanda");
        
        // Crear ComboBox con mesas libres
        javafx.scene.control.ComboBox<String> comboMesas = new javafx.scene.control.ComboBox<>();
        for (MesaInfo mesa : MESAS) {
            if (mesa.estado.equals("Libre")) {
                comboMesas.getItems().add("🪑 Mesa " + mesa.numero);
            }
        }
        comboMesas.setPromptText("Selecciona una mesa...");
        comboMesas.setPrefWidth(250);
        comboMesas.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        
        Label titulo = new Label("Selecciona una mesa libre");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B7355;");
        
        Label subtitulo = new Label("Elige la mesa donde se sentarán los clientes");
        subtitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: #FAF8F5; -fx-background-radius: 10px;");
        content.getChildren().addAll(
            titulo,
            subtitulo,
            comboMesas
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: #FAF8F5;");
        dialog.getDialogPane().getButtonTypes().addAll(
            javafx.scene.control.ButtonType.OK,
            javafx.scene.control.ButtonType.CANCEL
        );
        
        // Personalizar botones
        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
        okButton.setText("✓ Crear Comanda");
        okButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        
        javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(javafx.scene.control.ButtonType.CANCEL);
        cancelButton.setText("✗ Cancelar");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #333333; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK && comboMesas.getValue() != null) {
                String selected = comboMesas.getValue();
                return Integer.parseInt(selected.replace("🪑 Mesa ", ""));
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(mesaNumber -> {
            abrirComandaMesa(mesaNumber);
        });
    }
    
    /**
     * Abre la comanda de una mesa específica
     */
    private void abrirComandaMesa(int mesaNumber) {
        try {
            System.out.println("Abriendo comanda de Mesa " + mesaNumber);
            
            // Pasar el número de mesa al controlador de detalle
            javafx.stage.Stage stage = (javafx.stage.Stage) comandasContainer.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/waiter/comanda-detail.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Pasar número de mesa al controlador
            ComandaDetailController controller = loader.getController();
            controller.setMesaNumber(mesaNumber);
            
            // Crear escena con tamaño fijo 1024x768
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1024, 768);
            stage.setScene(scene);
            stage.setMinWidth(1024);
            stage.setMinHeight(768);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("Error al abrir comanda de mesa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clase interna para representar información de una mesa
     */
    public static class MesaInfo {
        int numero;
        String estado; // Libre, Ocupada, Reservada
        String imageName; // Imagen del plato principal (null si está libre)
        
        MesaInfo(int numero, String estado, String imageName) {
            this.numero = numero;
            this.estado = estado;
            this.imageName = imageName;
        }
    }
    
    /**
     * Clase para almacenar datos de una comanda
     */
    public static class ComandaData {
        public Map<String, PlatoComanda> platos = new HashMap<>();
        public String estado = "Ocupada";
        
        public static class PlatoComanda {
            public String nombre;
            public double precio;
            public int cantidad;
            public String nota;
            public String categoria;
            
            public PlatoComanda(String nombre, double precio, int cantidad, String nota, String categoria) {
                this.nombre = nombre;
                this.precio = precio;
                this.cantidad = cantidad;
                this.nota = nota;
                this.categoria = categoria;
            }
        }
    }
    
    /**
     * Cierra la sesión actual y vuelve a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            System.out.println("Cerrando sesión del Camarero...");
            
            // Limpiar la sesión
            ilcaminodelamamma.config.SessionManager.cerrarSesion();
            
            // Obtener el Stage actual
            javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
            
            // Cargar la vista de login
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/login/login.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Crear nueva escena con la vista de login
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 700, 550);
            
            // Cambiar la escena
            stage.setScene(scene);
            stage.setTitle("Il Camino Della Mamma - Login");
            stage.centerOnScreen();
            
            System.out.println("Sesión cerrada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
