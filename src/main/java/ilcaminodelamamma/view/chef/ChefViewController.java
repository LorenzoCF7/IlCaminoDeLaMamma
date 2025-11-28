package ilcaminodelamamma.view.chef;

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
        System.out.println("Vista del Chef inicializada correctamente");
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
            
            card.getChildren().add(imageView);
            System.out.println("Imagen cargada correctamente: " + imagePath);
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar placeholder
            System.err.println("Error cargando " + category.imageName + ": " + e.getMessage());
            Label placeholder = new Label("📷\n" + category.name);
            placeholder.setStyle("-fx-font-size: 30px; -fx-padding: 40; -fx-text-alignment: center;");
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
     * Carga las recetas recientes de ejemplo
     */
    private void loadRecentRecipes() {
        String[] recentRecipes = {
            "🍝 Carbonara Clásica",
            "🍕 Pizza Margherita",
            "🥗 Ensalada César",
            "🍰 Tiramisú Italiano",
            "🦐 Gambas al Ajillo"
        };

        for (String recipe : recentRecipes) {
            VBox recipeCard = createRecentRecipeCard(recipe);
            recentRecipesPanel.getChildren().add(recipeCard);
        }
    }

    /**
     * Crea una tarjeta de receta reciente
     */
    private VBox createRecentRecipeCard(String recipeName) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 8; " +
                     "-fx-padding: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2); " +
                     "-fx-cursor: hand;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(recipeName);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2C1810; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        Label timeLabel = new Label("⏱ 15 min");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

        card.getChildren().addAll(nameLabel, timeLabel);

        // Efecto hover
        card.setOnMouseEntered(e -> 
            card.setStyle(card.getStyle() + "-fx-background-color: #F5E6D3;"));
        card.setOnMouseExited(e -> 
            card.setStyle(card.getStyle().replace("-fx-background-color: #F5E6D3;", 
                                                 "-fx-background-color: white;")));
        
        card.setOnMouseClicked(e -> 
            System.out.println("Receta seleccionada: " + recipeName));

        return card;
    }

    /**
     * Configura los botones de pestañas
     */
    private void setupTabButtons() {
        tabPlatos.setOnAction(e -> {
            System.out.println("Pestaña Platos seleccionada");
            setActiveTab(tabPlatos);
        });

        tabCategorias.setOnAction(e -> {
            System.out.println("Pestaña Categorías seleccionada");
            setActiveTab(tabCategorias);
        });

        tabFavoritos.setOnAction(e -> {
            System.out.println("Pestaña Favoritos seleccionada");
            setActiveTab(tabFavoritos);
        });
    }

    /**
     * Marca una pestaña como activa
     */
    private void setActiveTab(Button activeButton) {
        tabPlatos.getStyleClass().remove("active");
        tabCategorias.getStyleClass().remove("active");
        tabFavoritos.getStyleClass().remove("active");
        
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
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

