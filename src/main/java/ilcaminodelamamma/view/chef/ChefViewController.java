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
import ilcaminodelamamma.model.DetalleComanda;
import ilcaminodelamamma.model.EstadoComanda;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    
    // Elementos de la sección de comandas
    @FXML private VBox comandasSection;
    @FXML private ComboBox<String> estadoComboBox;
    @FXML private VBox comandasListPanel;
    @FXML private VBox platosPanel;
    @FXML private Label mesaLabel;
    @FXML private Label horaLabel;
    @FXML private Label totalLabel;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnVerDetalles;

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
        
        // Inicializar ComboBox de estados
        if (estadoComboBox != null) {
            estadoComboBox.getItems().addAll("Todas", "Por hacer", "En preparación", "Preparado");
            estadoComboBox.getSelectionModel().selectFirst();
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
        byte[] imageBytes;

        DishItem(String name, String imageName, String description, double price) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.imageBytes = null;
        }
        
        DishItem(String name, String imageName, String description, double price, byte[] imageBytes) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
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
            currentView = "comandas";
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
                                 ", Estado=" + (c.getEstadoComanda() != null ? c.getEstadoComanda().getDescripcion() : "null") +
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
     * Crea una tarjeta visual para mostrar una comanda con estado
     */
    private VBox createComandaCard(Comanda comanda) {
        VBox card = new VBox(10);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMinWidth(180);
        card.setPrefWidth(200);
        card.setMaxWidth(250);
        card.setPrefHeight(200);
        card.setPadding(new Insets(15));
        
        // Determinar color de fondo según el estado
        final String bgColor;
        final String borderColor;
        if (comanda.getEstadoComanda() != null) {
            switch (comanda.getEstadoComanda()) {
                case POR_HACER:
                    bgColor = "#ffe6e6";
                    borderColor = "#dc3545";
                    break;
                case EN_PREPARACION:
                    bgColor = "#fff3cd";
                    borderColor = "#ffc107";
                    break;
                case PREPARADO:
                    bgColor = "#e6ffe6";
                    borderColor = "#28a745";
                    break;
                default:
                    bgColor = "#ffffff";
                    borderColor = "#D4A574";
            }
        } else {
            bgColor = "#ffffff";
            borderColor = "#D4A574";
        }
        
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        
        // Número de mesa
        Label lblMesa = new Label("Mesa " + (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));
        lblMesa.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        // Estado
        String estadoTexto = comanda.getEstadoComanda() != null ? 
            comanda.getEstadoComanda().getDescripcion() : "Sin estado";
        Label lblEstado = new Label("Estado: " + estadoTexto);
        lblEstado.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        
        // Número de platos
        int numPlatos = comanda.getDetalleComandas() != null ? comanda.getDetalleComandas().size() : 0;
        Label lblPlatos = new Label(numPlatos + " plato(s)");
        lblPlatos.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        
        // ID de comanda
        Label lblId = new Label("ID: " + comanda.getId_comanda());
        lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        
        // Hora
        String horaTexto = comanda.getFecha_hora() != null ? 
            comanda.getFecha_hora().toLocalTime().toString() : "N/A";
        Label lblHora = new Label("Hora: " + horaTexto);
        lblHora.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        card.getChildren().addAll(lblMesa, lblEstado, lblPlatos, lblHora, lblId);
        
        card.setOnMouseClicked(event -> {
            System.out.println("Comanda seleccionada: ID " + comanda.getId_comanda() + ", Estado: " + 
                (comanda.getEstadoComanda() != null ? comanda.getEstadoComanda().getDescripcion() : "Sin estado"));
            mostrarOpcionesComanda(comanda);
        });
        
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: #8B7355; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 2, 2);");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        });
        
        return card;
    }
    
    /**
     * Muestra detalles de la comanda en el área central
     */
    private void mostrarOpcionesComanda(Comanda comanda) {
        try {
            System.out.println("Mostrando detalles de comanda ID: " + comanda.getId_comanda());
            
            // Limpiar el grid y mostrar los detalles
            categoryGrid.getChildren().clear();
            
            // Cambiar título
            if (contentTitle != null) {
                contentTitle.setText("Comanda #" + comanda.getId_comanda() + " - Mesa " + 
                    (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));
            }
            
            // Crear un contenedor vertical para todos los detalles
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(600);
            
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: #f5f5f5;");
            
            // ============ SECCIÓN 1: INFORMACIÓN GENERAL ============
            VBox infoSection = createInfoSection(comanda);
            mainContent.getChildren().add(infoSection);
            
            // ============ SECCIÓN 2: CAMBIAR ESTADO ============
            VBox estadoSection = createEstadoSection(comanda);
            mainContent.getChildren().add(estadoSection);
            
            // ============ SECCIÓN 3: PLATOS DE LA COMANDA ============
            VBox platosSection = createPlatosSection(comanda);
            mainContent.getChildren().add(platosSection);
            
            // ============ BOTÓN VOLVER ============
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);
            botonesBox.setPadding(new Insets(10, 0, 0, 0));
            
            Button btnVolver = new Button("← Volver a Comandas");
            btnVolver.setStyle("-fx-font-size: 14px; -fx-padding: 10px 30px; -fx-cursor: hand;");
            btnVolver.setOnAction(e -> mostrarComandas());
            
            botonesBox.getChildren().add(btnVolver);
            mainContent.getChildren().add(botonesBox);
            
            scrollPane.setContent(mainContent);
            
            // Agregar el scroll al grid (en posición 0,0 con tamaño grande)
            GridPane.setColumnSpan(scrollPane, 5);
            GridPane.setRowSpan(scrollPane, 5);
            categoryGrid.add(scrollPane, 0, 0);
            
        } catch (Exception e) {
            System.err.println("Error al mostrar opciones: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea la sección de información general de la comanda
     */
    private VBox createInfoSection(Comanda comanda) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-padding: 20; " +
                        "-fx-border-color: #D4A574; -fx-border-width: 1;");
        
        Label titulo = new Label("📋 INFORMACIÓN DE LA COMANDA");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        HBox infoRow1 = new HBox(30);
        infoRow1.setPadding(new Insets(15, 0, 0, 0));
        
        VBox mesaBox = new VBox(5);
        Label mesaTitulo = new Label("Mesa");
        mesaTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label mesaValor = new Label(comanda.getMesa() != null ? 
            String.valueOf(comanda.getMesa().getId_mesa()) : "N/A");
        mesaValor.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        mesaBox.getChildren().addAll(mesaTitulo, mesaValor);
        
        VBox horaBox = new VBox(5);
        Label horaTitulo = new Label("Hora de Creación");
        horaTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        String horaTexto = comanda.getFecha_hora() != null ? 
            comanda.getFecha_hora().toString() : "N/A";
        Label horaValor = new Label(horaTexto);
        horaValor.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C1810;");
        horaBox.getChildren().addAll(horaTitulo, horaValor);
        
        VBox totalBox = new VBox(5);
        Label totalTitulo = new Label("Total");
        totalTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        String totalTexto = comanda.getTotal() != null ? 
            String.format("€%.2f", comanda.getTotal()) : "€0.00";
        Label totalValor = new Label(totalTexto);
        totalValor.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        totalBox.getChildren().addAll(totalTitulo, totalValor);
        
        VBox idBox = new VBox(5);
        Label idTitulo = new Label("ID");
        idTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label idValor = new Label(String.valueOf(comanda.getId_comanda()));
        idValor.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
        idBox.getChildren().addAll(idTitulo, idValor);
        
        infoRow1.getChildren().addAll(mesaBox, horaBox, totalBox, idBox);
        
        section.getChildren().addAll(titulo, infoRow1);
        
        return section;
    }
    
    /**
     * Crea la sección para cambiar el estado
     */
    private VBox createEstadoSection(Comanda comanda) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-padding: 20; " +
                        "-fx-border-color: #D4A574; -fx-border-width: 1;");
        
        Label titulo = new Label("🔄 CAMBIAR ESTADO DE LA COMANDA");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        // Estado actual
        String estadoActual = comanda.getEstadoComanda() != null ? 
            comanda.getEstadoComanda().getDescripcion() : "Sin estado";
        String colorEstado = getColorEstado(comanda.getEstadoComanda());
        
        HBox estadoActualBox = new HBox(10);
        estadoActualBox.setAlignment(Pos.CENTER_LEFT);
        estadoActualBox.setPadding(new Insets(15, 0, 0, 0));
        
        Label estadoLabel = new Label("Estado Actual: ");
        estadoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        Label estadoValor = new Label(estadoActual);
        estadoValor.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8 12; " +
                            "-fx-background-color: " + colorEstado + "; -fx-text-fill: white; " +
                            "-fx-border-radius: 5;");
        
        estadoActualBox.getChildren().addAll(estadoLabel, estadoValor);
        
        // Próximo estado
        String proximoEstado = calcularProximoEstado(comanda.getEstadoComanda());
        String colorProximo = getColorProximoEstado(comanda.getEstadoComanda());
        
        HBox proximoEstadoBox = new HBox(10);
        proximoEstadoBox.setAlignment(Pos.CENTER_LEFT);
        proximoEstadoBox.setPadding(new Insets(10, 0, 0, 0));
        
        Label proximoLabel = new Label("Cambiar a: ");
        proximoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        Label proximoValor = new Label(proximoEstado);
        proximoValor.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8 12; " +
                             "-fx-background-color: " + colorProximo + "; -fx-text-fill: white; " +
                             "-fx-border-radius: 5;");
        
        proximoEstadoBox.getChildren().addAll(proximoLabel, proximoValor);
        
        // Botón para cambiar estado
        Button btnCambiarEstado = new Button("✓ CAMBIAR ESTADO");
        btnCambiarEstado.setStyle("-fx-font-size: 14px; -fx-padding: 12px 30px; -fx-cursor: hand; " +
                                 "-fx-background-color: #2C1810; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCambiarEstado.setPrefWidth(300);
        
        btnCambiarEstado.setOnAction(e -> {
            cambiarEstadoComanda(comanda);
        });
        
        HBox botonesBox = new HBox(10);
        botonesBox.setAlignment(Pos.CENTER);
        botonesBox.setPadding(new Insets(15, 0, 0, 0));
        botonesBox.getChildren().add(btnCambiarEstado);
        
        section.getChildren().addAll(titulo, estadoActualBox, proximoEstadoBox, botonesBox);
        
        return section;
    }
    
    /**
     * Crea la sección de platos de la comanda
     */
    private VBox createPlatosSection(Comanda comanda) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-padding: 20; " +
                        "-fx-border-color: #D4A574; -fx-border-width: 1;");
        
        Label titulo = new Label("🍽️ PLATOS A PREPARAR");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        VBox platosContainer = new VBox(5);
        platosContainer.setPadding(new Insets(15, 0, 0, 0));
        
        if (comanda.getDetalleComandas() != null && !comanda.getDetalleComandas().isEmpty()) {
            int contador = 1;
            for (DetalleComanda detalle : comanda.getDetalleComandas()) {
                HBox platoItem = createPlatoItem(detalle, contador);
                platosContainer.getChildren().add(platoItem);
                contador++;
            }
        } else {
            Label noPlatos = new Label("No hay platos en esta comanda");
            noPlatos.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
            platosContainer.getChildren().add(noPlatos);
        }
        
        section.getChildren().addAll(titulo, platosContainer);
        
        return section;
    }
    
    /**
     * Crea un item visual para un plato
     */
    private HBox createPlatoItem(DetalleComanda detalle, int numero) {
        HBox item = new HBox(15);
        item.setStyle("-fx-padding: 12; -fx-border-color: #E8D4B8; -fx-border-width: 0 0 1 0; " +
                     "-fx-alignment: CENTER_LEFT;");
        
        Label numeroLabel = new Label(numero + ".");
        numeroLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D4A574; " +
                            "-fx-min-width: 30;");
        
        Receta receta = detalle.getReceta();
        String nombrePlato = receta != null ? receta.getNombre() : "Plato desconocido";
        
        VBox detallesBox = new VBox(3);
        
        Label nombreLabel = new Label(nombrePlato);
        nombreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        String descripcion = receta != null ? receta.getDescripcion() : "Sin descripción";
        Label descLabel = new Label(descripcion);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        detallesBox.getChildren().addAll(nombreLabel, descLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox cantidadBox = new VBox(3);
        cantidadBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label cantidadTitulo = new Label("Cantidad");
        cantidadTitulo.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        
        Label cantidadValor = new Label("x" + detalle.getCantidad());
        cantidadValor.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        
        cantidadBox.getChildren().addAll(cantidadTitulo, cantidadValor);
        
        item.getChildren().addAll(numeroLabel, detallesBox, spacer, cantidadBox);
        
        return item;
    }
    
    /**
     * Obtiene el color para el estado actual
     */
    private String getColorEstado(EstadoComanda estado) {
        if (estado == null) return "#cccccc";
        
        switch (estado) {
            case POR_HACER:
                return "#dc3545";
            case EN_PREPARACION:
                return "#ffc107";
            case PREPARADO:
                return "#28a745";
            default:
                return "#cccccc";
        }
    }
    
    /**
     * Obtiene el color para el próximo estado
     */
    private String getColorProximoEstado(EstadoComanda estado) {
        if (estado == null) return "#ffc107";
        
        switch (estado) {
            case POR_HACER:
                return "#ffc107";
            case EN_PREPARACION:
                return "#28a745";
            case PREPARADO:
                return "#dc3545";
            default:
                return "#ffc107";
        }
    }
    
    /**
     * Cambia el estado de la comanda al siguiente en el ciclo
     */
    private void cambiarEstadoComanda(Comanda comanda) {
        try {
            EstadoComanda estadoActual = comanda.getEstadoComanda();
            EstadoComanda nuevoEstado;
            
            switch (estadoActual) {
                case POR_HACER:
                    nuevoEstado = EstadoComanda.EN_PREPARACION;
                    break;
                case EN_PREPARACION:
                    nuevoEstado = EstadoComanda.PREPARADO;
                    break;
                case PREPARADO:
                    nuevoEstado = EstadoComanda.POR_HACER;
                    break;
                default:
                    nuevoEstado = EstadoComanda.POR_HACER;
            }
            
            comanda.setEstadoComanda(nuevoEstado);
            
            // Guardar en BD
            ComandaDAO comandaDAO = new ComandaDAO();
            comandaDAO.update(comanda);
            
            System.out.println("Estado de comanda cambiado a: " + nuevoEstado.getDescripcion());
            
            // Recargar la lista
            mostrarComandas();
            
        } catch (Exception e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calcula el próximo estado en el ciclo
     */
    private String calcularProximoEstado(EstadoComanda estadoActual) {
        if (estadoActual == null) return EstadoComanda.EN_PREPARACION.getDescripcion();
        
        switch (estadoActual) {
            case POR_HACER:
                return EstadoComanda.EN_PREPARACION.getDescripcion();
            case EN_PREPARACION:
                return EstadoComanda.PREPARADO.getDescripcion();
            case PREPARADO:
                return EstadoComanda.POR_HACER.getDescripcion();
            default:
                return EstadoComanda.POR_HACER.getDescripcion();
        }
    }
}

