package ilcaminodelamamma.view.chef;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ilcaminodelamamma.DAO.ComandaDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.Receta;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para la vista principal del Jefe de Cocina
 * Vista responsive con navegación entre categorías y platos
 */
public class ChefViewController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button tabPlatos;
    @FXML private Button tabCategorias;
    @FXML private Button tabFavoritos;
    @FXML private GridPane categoryGrid;
    @FXML private VBox recentRecipesPanel;
    @FXML private ScrollPane mainScrollPane;
    @FXML private Label contentTitle;
    @FXML private HBox breadcrumbBox;
    @FXML private VBox centerArea;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnNuevaReceta;
    @FXML private Button btnIngredientes;
    @FXML private Button btnLibros;
    @FXML private Button btnComandas;

    // Estado actual de la vista
    private String currentView = "categories";
    private String currentCategory = null;
    private int currentColumns = 3;
    
    // DAO para acceder a recetas de la base de datos
    private RecetaDAO recetaDAO;

    // Lista de categorías con sus imágenes (en carpeta categorias/)
    private final List<CategoryItem> categories = Arrays.asList(
        new CategoryItem("Entrantes", "categorias/Entrantes.jpg"),
        new CategoryItem("Pasta", "categorias/Pasta.png"),
        new CategoryItem("Pizza", "categorias/Pizza.jpg"),
        new CategoryItem("Pescados", "categorias/Pescados.png"),
        new CategoryItem("Carnes", "categorias/Carnes.jpg"),
        new CategoryItem("Postres", "categorias/Postres.jpg"),
        new CategoryItem("Vinos", "vino/rioja-vega-crianza.jpg"),
        new CategoryItem("Menú Infantil", "categorias/Menu_Infantil.png")
    );

    // Mapa de platos por categoría
    private final Map<String, List<DishItem>> dishesByCategory = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista del Chef inicializada correctamente");
        recetaDAO = new RecetaDAO();
        initializeDishes();
        loadDynamicDishesFromDatabase();
        loadCategoryGrid();
        loadRecentRecipes();
        setupTabButtons();
        setupResponsiveLayout();
        
        // Ocultar breadcrumb inicialmente
        if (breadcrumbBox != null) {
            breadcrumbBox.setVisible(false);
            breadcrumbBox.setManaged(false);
        }
        
        // Configurar botón de cerrar sesión
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> cerrarSesion());
        }
        
        // Configurar botón de nueva receta
        if (btnNuevaReceta != null) {
            btnNuevaReceta.setOnAction(e -> abrirNuevaReceta());
        }
        
        // Configurar botón de ingredientes
        if (btnIngredientes != null) {
            btnIngredientes.setOnAction(e -> abrirIngredientes());
        }
        
        // Configurar botón de libros de cocina
        if (btnLibros != null) {
            btnLibros.setOnAction(e -> {
                System.out.println("Mostrando libros de cocina (categorías)");
                goBackToCategories();
            });
        }
        
        // Configurar botón de comandas
        if (btnComandas != null) {
            btnComandas.setOnAction(e -> mostrarComandas());
        }
        
        // Configurar búsqueda de recetas
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                buscarRecetas(newValue);
            });
        }
    }

    /**
     * Carga recetas dinámicamente desde la base de datos
     */
    private void loadDynamicDishesFromDatabase() {
        try {
            List<Receta> todasLasRecetas = recetaDAO.findAll();
            System.out.println("Recetas cargadas de la BD: " + todasLasRecetas.size());
            
            // Agrupar recetas por categoría
            for (Receta receta : todasLasRecetas) {
                String categoria = receta.getCategoria();
                if (categoria == null || categoria.trim().isEmpty()) {
                    continue; // Saltar recetas sin categoría
                }
                
                // Crear DishItem desde la receta
                DishItem dish = new DishItem(
                    receta.getNombre(),
                    null, // La imagen se cargará desde los bytes
                    receta.getDescripcion(),
                    0.0,  // Precio no disponible en BD
                    receta.getImagen()
                );
                
                // Agregar a la categoría correspondiente
                dishesByCategory.computeIfAbsent(categoria, k -> new ArrayList<>()).add(dish);
            }
            
            System.out.println("Categorías con recetas dinámicas:");
            dishesByCategory.forEach((cat, dishes) -> 
                System.out.println("  " + cat + ": " + dishes.size() + " recetas")
            );
            
        } catch (Exception e) {
            System.err.println("Error cargando recetas desde BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa los platos por categoría (platos estáticos de ejemplo)
     */
    private void initializeDishes() {
        // ENTRANTES
        dishesByCategory.put("Entrantes", Arrays.asList(
            new DishItem("Bruschetta Clásica", "entrantes/bruschetta-clasica.jpg.jpg", "Tostadas con tomate fresco y albahaca", 6.50),
            new DishItem("Ensalada Caprese", "entrantes/ensalada-caprese-receta-original-italiana.jpg", "Tomate, mozzarella y albahaca", 8.90),
            new DishItem("Carpaccio de Ternera", "entrantes/carpaccio-de-ternera.jpg", "Finas láminas de ternera con rúcula", 12.00),
            new DishItem("Tabla de Quesos Italianos", "entrantes/quesos-italianos.jpg", "Selección de quesos artesanos", 14.50),
            new DishItem("Sopa Minestrone", "entrantes/sopas-minestrone.jpg", "Sopa tradicional italiana de verduras", 7.20),
            new DishItem("Calamares Fritos", "entrantes/calamares-fritos.jpg", "Calamares crujientes con limón", 11.80),
            new DishItem("Provolone al Horno", "entrantes/Provolone-al-horno-1-scaled.jpg", "Queso provolone gratinado con orégano", 9.50),
            new DishItem("Tartar de Salmón", "entrantes/tartar-de-salmon-y-aguacate.jpg", "Salmón fresco con aguacate y sésamo", 13.90),
            new DishItem("Antipasto Mixto", "entrantes/full.Mixed_Antipasto.jpg", "Selección de embutidos y quesos italianos", 15.00)
        ));

        // PASTA
        dishesByCategory.put("Pasta", Arrays.asList(
            new DishItem("Spaghetti Carbonara", "pasta/espaguetis-a-la-carbonara.jpg", "Pasta con huevo, pecorino y guanciale", 12.90),
            new DishItem("Penne Arrabbiata", "pasta/Penne-all-Arrabbiata_EXPS_TOHD24_277252_KristinaVanni_6.jpg", "Pasta con salsa de tomate picante", 11.50),
            new DishItem("Tagliatelle al Pesto", "pasta/tagliatelle-al-pesto.jpg", "Pasta fresca con pesto genovés", 13.20),
            new DishItem("Lasagna Boloñesa", "pasta/lasagna-boloñesa.jpg", "Capas de pasta con ragú y bechamel", 14.50),
            new DishItem("Ravioli Ricotta y Espinacas", "pasta/ravioli-ricotta-espinacas.jpg", "Pasta rellena con salsa de mantequilla", 13.80),
            new DishItem("Gnocchi a la Sorrentina", "pasta/Noquis-a-la-sorrentina_650x433_wm.jpg", "Ñoquis con tomate y mozzarella", 12.00),
            new DishItem("Fettuccine Alfredo", "pasta/one-pot-alfredo-recipe.jpg", "Pasta con crema de parmesano", 13.90),
            new DishItem("Tortellini Panna e Prosciutto", "pasta/tortellini_pannaprosciuttopiselli.jpg", "Tortellini con nata y jamón", 14.00),
            new DishItem("Spaghetti Marinara", "pasta/marinara-sauce-18.jpg", "Pasta con salsa de tomate y albahaca", 15.30)
        ));

        // PIZZA
        dishesByCategory.put("Pizza", Arrays.asList(
            new DishItem("Margherita", "pizza/margherita-1-scaled.jpg", "Tomate, mozzarella y albahaca fresca", 9.00),
            new DishItem("Pepperoni", "pizza/pepperoni.jpg", "Con pepperoni picante", 11.50),
            new DishItem("Cuatro Quesos", "pizza/pizza-4-quesos.jpg", "Mozzarella, gorgonzola, parmesano y fontina", 12.50),
            new DishItem("Hawaiana", "pizza/hawaiana.jpg", "Jamón y piña", 11.00),
            new DishItem("BBQ Pollo", "pizza/bbq-pollo.jpg", "Pollo con salsa barbacoa", 13.20),
            new DishItem("Prosciutto e Funghi", "pizza/pizza-prosciutto-e-funghi-1.jpg", "Jamón y champiñones", 12.80),
            new DishItem("Vegetariana", "pizza/pizza-vegetariana.jpg", "Verduras variadas de temporada", 11.90),
            new DishItem("Diavola", "pizza/Pizza-alla-diavola_650x433_wm.jpg", "Con salame picante calabrés", 12.20),
            new DishItem("Calzone Clásico", "pizza/pizza calzone ab.jpg", "Pizza cerrada rellena", 13.50)
        ));

        // PESCADOS
        dishesByCategory.put("Pescados", Arrays.asList(
            new DishItem("Salmón a la Plancha", "pescados/salmon-en-salsa-de-limon.jpg", "Salmón con limón y hierbas", 17.90),
            new DishItem("Lubina al Horno", "pescados/lubina-al-horno-con-patatas.jpg", "Lubina con patatas y verduras", 19.50),
            new DishItem("Bacalao con Tomate", "pescados/bacalao-con-tomate.jpg", "Bacalao en salsa de tomate casera", 16.80),
            new DishItem("Atún a la Parrilla", "pescados/atun_a_la_parrilla_31410_orig.jpg", "Atún sellado con especias", 21.00),
            new DishItem("Merluza en Salsa Verde", "pescados/merluza-salsa-verde-receta.jpg", "Merluza con perejil y almejas", 15.90),
            new DishItem("Dorada a la Espalda", "pescados/dorada-a-la-espalda-receta.jpg", "Dorada abierta al horno", 18.20),
            new DishItem("Pulpo a la Brasa", "pescados/pulpo-brasa.jpg", "Pulpo con pimentón y aceite", 22.50),
            new DishItem("Calamares en su Tinta", "pescados/calamares-tinta-1-scaled.jpg", "Calamares en salsa de tinta", 15.80),
            new DishItem("Fritura Mixta de Mar", "pescados/fritura-mixata.jpg", "Variedad de pescados y mariscos fritos", 17.50)
        ));

        // CARNES
        dishesByCategory.put("Carnes", Arrays.asList(
            new DishItem("Pollo a la Parrilla", "carnes/PechugaParrillaHierbasLimon.jpg", "Pollo con hierbas mediterráneas", 14.50),
            new DishItem("Solomillo de Cerdo", "carnes/solomillo-de-cerdo.jpg", "Solomillo con salsa de mostaza", 16.90),
            new DishItem("Entrecot de Ternera", "carnes/Entrecot-de-ternera-con-patatas-al-ajo-y-tomillo-y-espírragos-blancos.jpg", "Entrecot con guarnición", 22.00),
            new DishItem("Costillas BBQ", "carnes/costillas-bbq.jpg", "Costillas con salsa barbacoa", 18.50),
            new DishItem("Carrillera de Ternera", "carnes/carrilleras-de-ternera-receta.jpg", "Carrillera estofada al vino", 19.20),
            new DishItem("Albóndigas en Salsa", "carnes/Albondigas-de-carne-picada-en-salsa-de-tomate.jpg", "Albóndigas caseras con tomate", 13.50),
            new DishItem("Filete de Pollo Empanado", "carnes/Pollo-empanado-air-fryer.jpg", "Pollo crujiente empanado", 12.80),
            new DishItem("Hamburguesa Gourmet", "carnes/hamburguesa-con-queso-cabra.jpg.jpg", "Hamburguesa con queso de cabra", 15.90),
            new DishItem("Cordero Asado", "carnes/cordero-asado.jpg", "Cordero al horno con romero", 23.00)
        ));

        // POSTRES
        dishesByCategory.put("Postres", Arrays.asList(
            new DishItem("Tiramisú Clásico", "postres/Tiramisu-clasico.jpg", "Postre italiano con café y mascarpone", 6.50),
            new DishItem("Panna Cotta", "postres/PANACOTTA-CON-FRUTOS-ROJOS.jpg", "Crema italiana con frutos rojos", 6.80),
            new DishItem("Helado Artesanal", "postres/helado-artesanal.jpg", "2 bolas de helado a elegir", 4.80),
            new DishItem("Brownie con Helado", "postres/brownie-con-helado-destacada.jpg", "Brownie caliente con helado", 6.90),
            new DishItem("Tarta de Queso", "postres/tarta-queso-horno-receta.jpg", "Tarta de queso al horno", 6.70),
            new DishItem("Coulant de Chocolate", "postres/coulant-de-chocolate_515_1.jpg", "Bizcocho con corazón fundido", 7.20),
            new DishItem("Fruta Fresca", "postres/fruta-fresca.jpg", "Fruta fresca de temporada", 4.50),
            new DishItem("Cannoli Sicilianos", "postres/Cannoli-siciliani_1200x800.jpg", "Cannoli rellenos de ricotta", 5.80),
            new DishItem("Gelato Affogato", "postres/gelato-affogato.jpg", "Helado con espresso caliente", 5.90)
        ));

        // VINOS
        dishesByCategory.put("Vinos", Arrays.asList(
            new DishItem("Rioja Crianza", "vino/rioja-vega-crianza.jpg", "Vino tinto español con crianza en barrica", 18.00),
            new DishItem("Albariño Rías Baixas", "vino/albariño rias baixas.jpg", "Vino blanco gallego fresco y afrutado", 17.80),
            new DishItem("Chianti DOCG", "vino/chianti-docg.jpg", "Vino tinto italiano de la Toscana", 18.90),
            new DishItem("Ribera del Duero Crianza", "vino/ribera-duero-crianza.jpg", "Vino tinto castellano con cuerpo", 26.00),
            new DishItem("Godello sobre Lías", "vino/valdeorras-o-luar-do-sil-godello-sobre-lias-75-cl.jpg", "Vino blanco de Valdeorras con crianza", 24.50),
            new DishItem("Barolo Joven", "vino/barolo-joven.jpg", "Vino tinto italiano del Piamonte", 32.00),
            new DishItem("Ribera del Duero Reserva", "vino/rivera-duero.jpg", "Vino tinto reserva de alta calidad", 45.00),
            new DishItem("Chablis Premier Cru", "vino/chablis-premier-cru-montmains-simonnet-febvre.jpg", "Vino blanco francés de Borgoña", 48.00),
            new DishItem("Brunello di Montalcino", "vino/brunello.jpg", "Vino tinto italiano premium de la Toscana", 62.00)
        ));

        // MENÚ INFANTIL
        dishesByCategory.put("Menú Infantil", Arrays.asList(
            new DishItem("Menú Pizza", "menu-infantil/MENU-INFANTIL.jpeg", "Pizza infantil con bebida y postre", 8.50),
            new DishItem("Menú Hamburguesa", "menu-infantil/menu-infantil2.jpg", "Hamburguesa infantil con patatas y bebida", 8.50)
        ));
    }

    /**
     * Configura el layout responsive
     */
    private void setupResponsiveLayout() {
        if (mainScrollPane != null && categoryGrid != null) {
            ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
                adjustGridColumns();
            };
            
            mainScrollPane.widthProperty().addListener(sizeListener);
            
            // Ajuste inicial después de que se renderice
            javafx.application.Platform.runLater(this::adjustGridColumns);
        }
    }

    /**
     * Ajusta el número de columnas según el ancho disponible
     */
    private void adjustGridColumns() {
        if (mainScrollPane == null || categoryGrid == null) return;
        
        double availableWidth = mainScrollPane.getWidth() - 40;
        if (availableWidth <= 0) availableWidth = 600;
        
        int cardWidth = 200;
        int newColumns = Math.max(2, Math.min(5, (int) (availableWidth / cardWidth)));
        
        if (newColumns != currentColumns) {
            currentColumns = newColumns;
            if (currentView.equals("categories")) {
                reloadCategoryGrid();
            } else {
                reloadDishGrid();
            }
        }
    }

    /**
     * Carga el grid de categorías con las imágenes
     */
    private void loadCategoryGrid() {
        currentView = "categories";
        currentCategory = null;
        reloadCategoryGrid();
    }

    /**
     * Recarga el grid de categorías
     */
    private void reloadCategoryGrid() {
        categoryGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;

        for (CategoryItem category : categories) {
            VBox categoryCard = createCategoryCard(category);
            categoryGrid.add(categoryCard, column, row);
            
            column++;
            if (column >= currentColumns) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Carga los platos de una categoría específica
     */
    private void loadDishesForCategory(String categoryName) {
        currentView = "dishes";
        currentCategory = categoryName;
        
        // Actualizar título
        if (contentTitle != null) {
            contentTitle.setText(categoryName + " (" + 
                (dishesByCategory.get(categoryName) != null ? 
                 dishesByCategory.get(categoryName).size() : 0) + " platos)");
        }
        
        // Mostrar breadcrumb
        showBreadcrumb(categoryName);
        
        // Cargar platos
        reloadDishGrid();
    }

    /**
     * Recarga el grid de platos
     */
    private void reloadDishGrid() {
        if (currentCategory == null) return;
        
        categoryGrid.getChildren().clear();
        
        List<DishItem> dishes = dishesByCategory.get(currentCategory);
        if (dishes == null) return;
        
        int column = 0;
        int row = 0;

        for (DishItem dish : dishes) {
            VBox dishCard = createDishCard(dish);
            categoryGrid.add(dishCard, column, row);
            
            column++;
            if (column >= currentColumns) {
                column = 0;
                row++;
            }
        }
    }

    /**
     * Muestra el breadcrumb de navegación
     */
    private void showBreadcrumb(String categoryName) {
        if (breadcrumbBox != null) {
            breadcrumbBox.getChildren().clear();
            breadcrumbBox.setVisible(true);
            breadcrumbBox.setManaged(true);
            
            Button backButton = new Button("← Volver a Categorías");
            backButton.getStyleClass().add("breadcrumb-button");
            backButton.setOnAction(e -> goBackToCategories());
            
            Label separator = new Label("  /  ");
            separator.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
            
            Label currentLabel = new Label(categoryName);
            currentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C1810; -fx-font-size: 14px;");
            
            breadcrumbBox.getChildren().addAll(backButton, separator, currentLabel);
        }
    }

    /**
     * Vuelve a la vista de categorías
     */
    private void goBackToCategories() {
        currentView = "categories";
        currentCategory = null;
        
        if (contentTitle != null) {
            contentTitle.setText("Todas las recetas (300)");
        }
        
        if (breadcrumbBox != null) {
            breadcrumbBox.setVisible(false);
            breadcrumbBox.setManaged(false);
        }
        
        reloadCategoryGrid();
    }

    /**
     * Crea una tarjeta de categoría
     */
    private VBox createCategoryCard(CategoryItem category) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMinWidth(160);
        card.setPrefWidth(180);
        card.setMaxWidth(220);
        card.setPrefHeight(180);

        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(120);
        imageContainer.setPrefHeight(130);
        imageContainer.setStyle("-fx-background-radius: 10 10 0 0;");

        try {
            String imagePath = "/img/" + category.imageName;
            var inputStream = getClass().getResourceAsStream(imagePath);
            
            if (inputStream == null) {
                throw new RuntimeException("Imagen no encontrada: " + imagePath);
            }
            
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(180);
            imageView.setFitHeight(130);
            imageView.setPreserveRatio(false);
            imageView.getStyleClass().add("category-image");
            
            imageContainer.getChildren().add(imageView);
        } catch (Exception e) {
            Label placeholder = new Label("📷");
            placeholder.setStyle("-fx-font-size: 40px;");
            imageContainer.getChildren().add(placeholder);
        }

        Label label = new Label(category.name);
        label.getStyleClass().add("category-label");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        
        card.getChildren().addAll(imageContainer, label);

        card.setOnMouseClicked(event -> {
            System.out.println("Categoría seleccionada: " + category.name);
            loadDishesForCategory(category.name);
        });

        return card;
    }

    /**
     * Crea una tarjeta de plato
     */
    private VBox createDishCard(DishItem dish) {
        VBox card = new VBox(5);
        card.getStyleClass().add("dish-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMinWidth(160);
        card.setPrefWidth(180);
        card.setMaxWidth(220);
        card.setPrefHeight(260);
        card.setPadding(new Insets(0, 0, 10, 0));

        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(130);
        imageContainer.setPrefHeight(140);
        imageContainer.setStyle("-fx-background-radius: 10 10 0 0;");

        try {
            Image image = null;
            
            // Primero intentar cargar desde bytes (recetas de BD)
            if (dish.imageBytes != null && dish.imageBytes.length > 0) {
                image = new Image(new ByteArrayInputStream(dish.imageBytes));
            }
            // Si no hay bytes, cargar desde archivo de recursos
            else if (dish.imageName != null && !dish.imageName.isEmpty()) {
                String imagePath = "/img/" + dish.imageName;
                var inputStream = getClass().getResourceAsStream(imagePath);
                
                if (inputStream == null) {
                    throw new RuntimeException("Imagen no encontrada: " + imagePath);
                }
                
                image = new Image(inputStream);
            }
            
            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(180);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(false);
                imageView.getStyleClass().add("dish-image");
                
                imageContainer.getChildren().add(imageView);
            } else {
                throw new RuntimeException("No hay imagen disponible");
            }
        } catch (Exception e) {
            Label placeholder = new Label("🍽️");
            placeholder.setStyle("-fx-font-size: 50px;");
            imageContainer.getChildren().add(placeholder);
        }

        Label nameLabel = new Label(dish.name);
        nameLabel.getStyleClass().add("dish-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);
        VBox.setMargin(nameLabel, new Insets(8, 8, 0, 8));

        Label descLabel = new Label(dish.description);
        descLabel.getStyleClass().add("dish-description");
        descLabel.setMaxWidth(Double.MAX_VALUE);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setWrapText(true);
        VBox.setMargin(descLabel, new Insets(0, 8, 8, 8));

        card.getChildren().addAll(imageContainer, nameLabel, descLabel);

        card.setOnMouseClicked(event -> {
            System.out.println("Plato seleccionado: " + dish.name);
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
        VBox card = new VBox(3);
        card.getStyleClass().add("recent-recipe-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));

        Label nameLabel = new Label(recipeName);
        nameLabel.getStyleClass().add("recent-recipe-name");
        nameLabel.setWrapText(true);

        Label timeLabel = new Label("⏱ 15 min");
        timeLabel.getStyleClass().add("recent-recipe-time");

        card.getChildren().addAll(nameLabel, timeLabel);

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
            goBackToCategories();
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

    /**
     * Clase interna para representar un plato
     */
    private static class DishItem {
        String name;
        String imageName;
        String description;
        double price;
        byte[] imageBytes;

        DishItem(String name, String imageName, String description, double price) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
            this.imageBytes = null;
        }
        
        DishItem(String name, String imageName, String description, double price, byte[] imageBytes) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
            this.imageBytes = imageBytes;
        }
    }
    
    /**
     * Busca recetas por nombre en todas las categorías
     */
    private void buscarRecetas(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Si no hay búsqueda, mostrar vista actual
            if (currentView.equals("categories")) {
                reloadCategoryGrid();
            } else {
                reloadDishGrid();
            }
            return;
        }
        
        String searchTerm = query.toLowerCase().trim();
        categoryGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;
        int foundCount = 0;
        
        // Buscar en todas las categorías
        for (Map.Entry<String, List<DishItem>> entry : dishesByCategory.entrySet()) {
            for (DishItem dish : entry.getValue()) {
                if (dish.name.toLowerCase().contains(searchTerm)) {
                    VBox dishCard = createDishCard(dish);
                    categoryGrid.add(dishCard, column, row);
                    
                    column++;
                    foundCount++;
                    if (column >= currentColumns) {
                        column = 0;
                        row++;
                    }
                }
            }
        }
        
        // Actualizar título con resultados
        if (contentTitle != null) {
            contentTitle.setText("Resultados de búsqueda: \"" + query + "\" (" + foundCount + " recetas)");
        }
        
        // Ocultar breadcrumb en búsqueda
        if (breadcrumbBox != null) {
            breadcrumbBox.setVisible(false);
            breadcrumbBox.setManaged(false);
        }
    }
    
    /**
     * Cierra la sesión actual y vuelve a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            System.out.println("Cerrando sesión del Chef...");
            
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
    
    /**
     * Carga la vista de crear nueva receta en el área central
     */
    private void abrirNuevaReceta() {
        try {
            System.out.println("Cargando vista de nueva receta en el área central...");
            
            // Cambiar a la ventana principal completa para mostrar el formulario
            Stage stage = (Stage) btnNuevaReceta.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/receta.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Nueva Receta - Il Camino Della Mamma");
            stage.centerOnScreen();
            
            System.out.println("Vista de nueva receta cargada");
            
        } catch (Exception e) {
            System.err.println("Error al cargar vista de nueva receta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga la vista de gestión de ingredientes en el área central
     */
    private void abrirIngredientes() {
        try {
            System.out.println("Cargando vista de ingredientes en el área central...");
            
            // Cambiar a la ventana principal completa para mostrar el formulario
            Stage stage = (Stage) btnIngredientes.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingrediente.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Gestión de Ingredientes - Il Camino Della Mamma");
            stage.centerOnScreen();
            
            System.out.println("Vista de ingredientes cargada");
            
        } catch (Exception e) {
            System.err.println("Error al cargar vista de ingredientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la lista de comandas activas
     */
    private void mostrarComandas() {
        try {
            System.out.println("=== MOSTRANDO COMANDAS ACTIVAS ===");
            
            // Limpiar el grid actual
            categoryGrid.getChildren().clear();
            System.out.println("Grid limpiado");
            
            // Cambiar el título
            if (contentTitle != null) {
                contentTitle.setText("Comandas Activas");
                System.out.println("Título cambiado a: Comandas Activas");
            }
            
            // Ocultar breadcrumb
            if (breadcrumbBox != null) {
                breadcrumbBox.setVisible(false);
                breadcrumbBox.setManaged(false);
            }
            
            // Cargar comandas desde la base de datos
            System.out.println("Creando ComandaDAO...");
            ComandaDAO comandaDAO = new ComandaDAO();
            System.out.println("ComandaDAO creado. Cargando comandas...");
            List<Comanda> comandas = comandaDAO.findAll();
            System.out.println("Comandas recuperadas de BD: " + comandas.size());
            
            // Imprimir detalles de cada comanda
            for (int i = 0; i < comandas.size(); i++) {
                Comanda c = comandas.get(i);
                System.out.println("  Comanda " + (i+1) + ": ID=" + c.getId_comanda() + 
                                 ", Mesa=" + (c.getMesa() != null ? c.getMesa().getId_mesa() : "null") +
                                 ", Detalles=" + (c.getDetalleComandas() != null ? c.getDetalleComandas().size() : "null"));
            }
            
            int column = 0;
            int row = 0;
            
            for (Comanda comanda : comandas) {
                System.out.println("Creando tarjeta para comanda ID: " + comanda.getId_comanda());
                VBox comandaCard = createComandaCard(comanda);
                categoryGrid.add(comandaCard, column, row);
                System.out.println("Tarjeta añadida al grid en posición (" + column + ", " + row + ")");
                
                column++;
                if (column >= currentColumns) {
                    column = 0;
                    row++;
                }
            }
            
            if (comandas.isEmpty()) {
                System.out.println("⚠️ NO HAY COMANDAS EN LA BASE DE DATOS");
                Label noData = new Label("No hay comandas activas");
                noData.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
                categoryGrid.add(noData, 0, 0);
            }
            
            System.out.println("=== COMANDAS CARGADAS EXITOSAMENTE: " + comandas.size() + " ===");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR AL CARGAR COMANDAS: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar el error en la interfaz
            Label errorLabel = new Label("Error al cargar comandas: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            categoryGrid.add(errorLabel, 0, 0);
        }
    }
    
    /**
     * Crea una tarjeta visual para mostrar una comanda
     */
    private VBox createComandaCard(Comanda comanda) {
        VBox card = new VBox(10);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMinWidth(180);
        card.setPrefWidth(200);
        card.setMaxWidth(250);
        card.setPrefHeight(180);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #D4A574; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        
        // Número de mesa
        Label lblMesa = new Label("Mesa " + (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));
        lblMesa.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        // Estado
        String estadoTexto = comanda.getMesa() != null ? comanda.getMesa().getEstado().toString() : "SIN MESA";
        Label lblEstado = new Label("Estado: " + estadoTexto);
        lblEstado.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        // Número de platos
        int numPlatos = comanda.getDetalleComandas() != null ? comanda.getDetalleComandas().size() : 0;
        Label lblPlatos = new Label(numPlatos + " plato(s)");
        lblPlatos.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        
        // ID de comanda
        Label lblId = new Label("ID: " + comanda.getId_comanda());
        lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        
        card.getChildren().addAll(lblMesa, lblEstado, lblPlatos, lblId);
        
        card.setOnMouseClicked(event -> {
            System.out.println("Comanda seleccionada: ID " + comanda.getId_comanda());
            // Aquí puedes agregar lógica para ver detalles de la comanda
        });
        
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #F5E6D3; -fx-background-radius: 10; -fx-border-color: #8B7355; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #D4A574; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        });
        
        return card;
    }
}

