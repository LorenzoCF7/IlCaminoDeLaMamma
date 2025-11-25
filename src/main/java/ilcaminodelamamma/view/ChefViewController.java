package ilcaminodelamamma.view;

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
 * Controlador para la vista principal del Jefe de Cocina
 */
public class ChefViewController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button tabPlatos;
    @FXML private Button tabCategorias;
    @FXML private Button tabFavoritos;
    @FXML private GridPane categoryGrid;

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
        System.out.println("Vista del Chef inicializada correctamente");
        loadCategoryGrid();
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
     * Crea una tarjeta de categoría
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
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(180);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(false);
            imageView.getStyleClass().add("category-image");
            
            card.getChildren().add(imageView);
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar placeholder
            Label placeholder = new Label("📷");
            placeholder.setStyle("-fx-font-size: 40px; -fx-padding: 40;");
            card.getChildren().add(placeholder);
        }

        // Etiqueta de categoría
        Label label = new Label(category.name);
        label.getStyleClass().add("category-label");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        
        card.getChildren().add(label);

        // Evento de clic
        card.setOnMouseClicked(event -> {
            System.out.println("Categoría seleccionada: " + category.name);
        });

        return card;
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
