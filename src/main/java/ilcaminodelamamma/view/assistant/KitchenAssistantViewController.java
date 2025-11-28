package ilcaminodelamamma.view.assistant;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Controlador para la vista del Ayudante de Cocina
 * Vista de solo lectura de recetas y categorías
 */
public class KitchenAssistantViewController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button tabPlatos;
    @FXML private Button tabCategorias;
    @FXML private Button tabFavoritos;
    @FXML private GridPane categoryGrid;
    @FXML private VBox recentRecipesPanel;

    // Lista de categorías con sus imágenes
    private final List<CategoryItem> categories = Arrays.asList(
        new CategoryItem("Entrantes", "Entrantes.jpg"),
        new CategoryItem("Postres", "Postres.jpg"),
        new CategoryItem("Pasta", "Pasta.png"),
        new CategoryItem("Pizza", "Pizza.jpg"),
        new CategoryItem("Menú Infantil", "Menu_Infantil.png"),
        new CategoryItem("Pescados", "Pescados.png"),
        new CategoryItem("Carnes", "Carnes.jpg")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista del Ayudante de Cocina inicializada correctamente");
        loadCategoryGrid();
        loadRecentRecipes();
        setupTabButtons();
    }

    /**
     * Carga el grid de categorías con las imágenes
     */
    private void loadCategoryGrid() {
        int column = 0;
        int row = 0;
        final int COLUMNS = 3; // 3 columnas

        for (CategoryItem category : categories) {
            VBox categoryCard = createCategoryCard(category);
            categoryGrid.add(categoryCard, column, row);
            
            column++;
            if (column >= COLUMNS) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Crea una tarjeta de categoría (solo lectura)
     */
    private VBox createCategoryCard(CategoryItem category) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(180);
        card.setPrefHeight(160);

        try {
            // Cargar imagen
            String imagePath = "/img/" + category.imageName;
            var inputStream = getClass().getResourceAsStream(imagePath);
            
            if (inputStream == null) {
                System.err.println("No se pudo cargar la imagen: " + imagePath);
                throw new RuntimeException("Imagen no encontrada: " + imagePath);
            }
            
            Image image = new Image(inputStream);
            
            if (image.isError()) {
                System.err.println("Error al cargar imagen: " + imagePath);
                throw new RuntimeException("Error en la imagen: " + imagePath);
            }
            
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(180);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(false);
            imageView.getStyleClass().add("category-image");
            
            System.out.println("Imagen cargada correctamente: " + imagePath);
            
            // Etiqueta con el nombre de la categoría
            Label label = new Label(category.name);
            label.getStyleClass().add("category-label");
            label.setMaxWidth(Double.MAX_VALUE);
            
            card.getChildren().addAll(imageView, label);
            
            // Evento de clic para ver recetas de la categoría
            card.setOnMouseClicked(event -> viewRecipesInCategory(category.name));
            
        } catch (Exception e) {
            System.err.println("Error cargando " + category.name + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return card;
    }

    /**
     * Ver recetas de una categoría (solo lectura)
     */
    private void viewRecipesInCategory(String categoryName) {
        System.out.println("Viendo recetas de categoría: " + categoryName);
        // TODO: Implementar vista de recetas de la categoría
    }

    /**
     * Carga las recetas vistas recientemente
     */
    private void loadRecentRecipes() {
        // TODO: Implementar carga de recetas recientes
        System.out.println("Cargando recetas recientes...");
    }

    /**
     * Configura los botones de pestañas
     */
    private void setupTabButtons() {
        // Marcar "Platos" como activo por defecto
        tabPlatos.getStyleClass().add("active");
        
        // Configurar eventos
        tabPlatos.setOnAction(e -> switchTab(tabPlatos));
        tabCategorias.setOnAction(e -> switchTab(tabCategorias));
        tabFavoritos.setOnAction(e -> switchTab(tabFavoritos));
    }

    /**
     * Cambia entre pestañas
     */
    private void switchTab(Button activeTab) {
        // Remover clase activa de todos
        tabPlatos.getStyleClass().remove("active");
        tabCategorias.getStyleClass().remove("active");
        tabFavoritos.getStyleClass().remove("active");
        
        // Agregar clase activa al seleccionado
        activeTab.getStyleClass().add("active");
        
        System.out.println("Pestaña cambiada: " + activeTab.getText());
    }

    /**
     * Clase interna para representar una categoría
     */
    private static class CategoryItem {
        String name;
        String imageName;
        
        CategoryItem(String name, String imageName) {
            this.name = name;
            this.imageName = imageName;
        }
    }
}
