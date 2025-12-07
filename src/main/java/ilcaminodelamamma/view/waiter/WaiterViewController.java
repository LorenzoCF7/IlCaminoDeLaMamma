package ilcaminodelamamma.view.waiter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    // Lista de comandas de ejemplo
    private final List<ComandaItem> comandas = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista de Camarero inicializada correctamente");
        
        // Cargar comandas de ejemplo
        loadSampleComandas();
        
        // Configurar botones de pestañas
        setupTabButtons();
        
        // Configurar botón Nueva Comanda
        btnNuevaComanda.setOnAction(e -> abrirNuevaComanda());
        
        // Mostrar comandas
        displayComandas();
        
        // Configurar botón de cerrar sesión
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> cerrarSesion());
        }
    }

    /**
     * Carga comandas de ejemplo (datos temporales hasta conectar con la base de datos)
     */
    private void loadSampleComandas() {
        // Agregamos 10 comandas de prueba con diferentes mesas y platos de pasta
        comandas.add(new ComandaItem(15, "pasta/espaguetis-a-la-carbonara.jpg"));
        comandas.add(new ComandaItem(10, "pasta/lasagna-boloñesa.jpg"));
        comandas.add(new ComandaItem(2, "pasta/ravioli-ricotta-espinacas.jpg"));
        comandas.add(new ComandaItem(8, "pasta/tagliatelle-al-pesto.jpg"));
        comandas.add(new ComandaItem(20, "pasta/tortellini_pannaprosciuttopiselli.jpg"));
        comandas.add(new ComandaItem(5, "pasta/Penne-all-Arrabbiata_EXPS_TOHD24_277252_KristinaVanni_6.jpg"));
        comandas.add(new ComandaItem(12, "pasta/one-pot-alfredo-recipe.jpg"));
        comandas.add(new ComandaItem(7, "pasta/Noquis-a-la-sorrentina_650x433_wm.jpg"));
        comandas.add(new ComandaItem(18, "pasta/marinara-sauce-18.jpg"));
        comandas.add(new ComandaItem(3, "pasta/espaguetis-a-la-carbonara.jpg"));
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
     * Muestra las comandas en el contenedor
     */
    private void displayComandas() {
        comandasContainer.getChildren().clear();
        
        for (ComandaItem comanda : comandas) {
            HBox comandaBox = createComandaBox(comanda);
            comandasContainer.getChildren().add(comandaBox);
        }
    }

    /**
     * Crea un box de comanda con tamaños uniformes
     */
    private HBox createComandaBox(ComandaItem comanda) {
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
        try {
            var imageStream = getClass().getResourceAsStream("/img/" + comanda.imageName);
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
        
        box.getChildren().add(imageContainer);
    
        
        // Espacio
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);
        
        // Label de mesa
        Label mesaLabel = new Label("Mesa " + comanda.mesaNumber);
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
        arrowButton.setOnAction(e -> openComandaDetail(comanda.mesaNumber));
        box.getChildren().add(arrowButton);
        
        return box;
    }

    /**
     * Abre el detalle de una comanda
     */
    private void openComandaDetail(int mesaNumber) {
        try {
            System.out.println("Abriendo detalle de comanda para Mesa " + mesaNumber);
            
            javafx.stage.Stage stage = (javafx.stage.Stage) comandasContainer.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/waiter/comanda-detail.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("Error al abrir detalle de comanda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clase interna para representar una comanda
     */
    private static class ComandaItem {
        int mesaNumber;
        String imageName;
        
        ComandaItem(int mesaNumber, String imageName) {
            this.mesaNumber = mesaNumber;
            this.imageName = imageName;
        }
    }
    
    /**
     * Abre la vista de nueva comanda
     */
    private void abrirNuevaComanda() {
        openComandaDetail(0); // 0 indica nueva comanda
    }
    
    /**
     * Cierra la sesión actual y vuelve a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            System.out.println("Cerrando sesión del Camarero...");
            
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
