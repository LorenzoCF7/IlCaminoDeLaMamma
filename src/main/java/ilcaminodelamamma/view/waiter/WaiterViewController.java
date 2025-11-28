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
        
        // Mostrar comandas
        displayComandas();
    }

    /**
     * Carga comandas de ejemplo
     */
    private void loadSampleComandas() {
        comandas.add(new ComandaItem(15, "Pasta.png"));
        comandas.add(new ComandaItem(10, "Pasta.png"));
        comandas.add(new ComandaItem(2, "Pasta.png"));
        comandas.add(new ComandaItem(8, "Pasta.png"));
        comandas.add(new ComandaItem(20, "Pasta.png"));
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
     * Crea un box de comanda
     */
    private HBox createComandaBox(ComandaItem comanda) {
        HBox box = new HBox();
        box.getStyleClass().add("comanda-box");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(20);
        box.setPadding(new Insets(15, 20, 15, 20));
        box.setPrefHeight(80);
        VBox.setMargin(box, new Insets(0, 0, 10, 0));
        
        // Imagen del plato
        try {
            var imageStream = getClass().getResourceAsStream("/img/" + comanda.imageName);
            if (imageStream != null) {
                ImageView imageView = new ImageView(new Image(imageStream));
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("comanda-image");
                box.getChildren().add(imageView);
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
        
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
        System.out.println("Abriendo detalle de comanda para Mesa " + mesaNumber);
        // TODO: Implementar navegación al detalle
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
}
