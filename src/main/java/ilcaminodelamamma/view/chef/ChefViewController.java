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
    
    // Mapeo de categorías BD → UI (XML usa singular, UI usa plural)
    private final Map<String, String> categoryMapping = new HashMap<String, String>() {{
        put("Entrante", "Entrantes");
        put("Pasta", "Pasta");
        put("Pizza", "Pizza");
        put("Pescado", "Pescados");
        put("Carne", "Carnes");
        put("Postre", "Postres");
        put("Vino", "Vinos");
        put("Menu Infantil", "Menú Infantil");
    }};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista del Chef inicializada correctamente");
        recetaDAO = new RecetaDAO();
        initializeImageMapping(); // Cargar mapeo de imágenes
        loadDynamicDishesFromDatabase(); // Cargar todos los platos desde BD
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

    // Mapa de imágenes por nombre de plato (para asignar imágenes a platos de BD)
    // Usa claves en minúsculas para búsqueda case-insensitive
    private final Map<String, String> imageMapping = new HashMap<>();
    
    /**
     * Inicializa el mapeo de imágenes por nombre de plato
     * Esto permite asignar las imágenes correctas a los platos cargados desde la BD
     * Las claves se almacenan en minúsculas para hacer búsquedas case-insensitive
     * RUTAS EXACTAS desde la carpeta resources/img/
     */
    private void initializeImageMapping() {
        // ENTRANTES (claves en minúsculas, rutas EXACTAS con prefijo /img/)
        imageMapping.put("bruschetta clásica", "/img/entrantes/bruschetta-clasica.jpg.jpg");
        imageMapping.put("ensalada caprese", "/img/entrantes/ensalada-caprese-receta-original-italiana.jpg");
        imageMapping.put("carpaccio de ternera", "/img/entrantes/carpaccio-de-ternera.jpg");
        imageMapping.put("tabla de quesos italianos", "/img/entrantes/quesos-italianos.jpg");
        imageMapping.put("sopa minestrone", "/img/entrantes/sopas-minestrone.jpg");
        imageMapping.put("calamares fritos", "/img/entrantes/calamares-fritos.jpg");
        imageMapping.put("provolone al horno", "/img/entrantes/Provolone-al-horno-1-scaled.jpg");
        imageMapping.put("tartar de salmón", "/img/entrantes/tartar-de-salmon-y-aguacate.jpg");
        imageMapping.put("antipasto mixto", "/img/entrantes/full.Mixed_Antipasto.jpg");
        
        // PASTA
        imageMapping.put("spaghetti carbonara", "/img/pasta/espaguetis-a-la-carbonara.jpg");
        imageMapping.put("penne arrabbiata", "/img/pasta/Penne-all-Arrabbiata_EXPS_TOHD24_277252_KristinaVanni_6.jpg");
        imageMapping.put("tagliatelle al pesto", "/img/pasta/tagliatelle-al-pesto.jpg");
        imageMapping.put("lasagna boloñesa", "/img/pasta/lasagna-boloñesa.jpg");
        imageMapping.put("ravioli ricotta y espinacas", "/img/pasta/ravioli-ricotta-espinacas.jpg");
        imageMapping.put("gnocchi a la sorrentina", "/img/pasta/Noquis-a-la-sorrentina_650x433_wm.jpg");
        imageMapping.put("fettuccine alfredo", "/img/pasta/one-pot-alfredo-recipe.jpg");
        imageMapping.put("tortellini panna e prosciutto", "/img/pasta/tortellini_pannaprosciuttopiselli.jpg");
        imageMapping.put("spaghetti marinara", "/img/pasta/marinara-sauce-18.jpg");
        
        // PIZZA
        imageMapping.put("margherita", "/img/pizza/margherita-1-scaled.jpg");
        imageMapping.put("pepperoni", "/img/pizza/pepperoni.jpg");
        imageMapping.put("cuatro quesos", "/img/pizza/pizza-4-quesos.jpg");
        imageMapping.put("hawaiana", "/img/pizza/hawaiana.jpg");
        imageMapping.put("bbq pollo", "/img/pizza/bbq-pollo.jpg");
        imageMapping.put("prosciutto e funghi", "/img/pizza/pizza-prosciutto-e-funghi-1.jpg");
        imageMapping.put("vegetariana", "/img/pizza/pizza-vegetariana.jpg");
        imageMapping.put("diavola", "/img/pizza/Pizza-alla-diavola_650x433_wm.jpg");
        imageMapping.put("calzone clásico", "/img/pizza/pizza calzone ab.jpg");
        
        // PESCADOS
        imageMapping.put("salmón a la plancha", "/img/pescados/salmon-en-salsa-de-limon.jpg");
        imageMapping.put("lubina al horno", "/img/pescados/lubina-al-horno-con-patatas.jpg");
        imageMapping.put("bacalao con tomate", "/img/pescados/bacalao-con-tomate.jpg");
        imageMapping.put("atún a la parrilla", "/img/pescados/atun_a_la_parrilla_31410_orig.jpg");
        imageMapping.put("merluza en salsa verde", "/img/pescados/merluza-salsa-verde-receta.jpg");
        imageMapping.put("dorada a la espalda", "/img/pescados/dorada-a-la-espalda-receta.jpg");
        imageMapping.put("pulpo a la brasa", "/img/pescados/pulpo-brasa.jpg");
        imageMapping.put("calamares en su tinta", "/img/pescados/calamares-tinta-1-scaled.jpg");
        imageMapping.put("fritura mixta de mar", "/img/pescados/fritura-mixata.jpg");
        
        // CARNES
        imageMapping.put("pollo a la parrilla", "/img/carnes/PechugaParrillaHierbasLimon.jpg");
        imageMapping.put("solomillo de cerdo", "/img/carnes/solomillo-de-cerdo.jpg");
        imageMapping.put("entrecot de ternera", "/img/carnes/Entrecot-de-ternera-con-patatas-al-ajo-y-tomillo-y-espírragos-blancos.jpg");
        imageMapping.put("costillas bbq", "/img/carnes/costillas-bbq.jpg");
        imageMapping.put("carrillera de ternera", "/img/carnes/carrilleras-de-ternera-receta.jpg");
        imageMapping.put("albóndigas en salsa", "/img/carnes/Albondigas-de-carne-picada-en-salsa-de-tomate.jpg");
        imageMapping.put("filete de pollo empanado", "/img/carnes/Pollo-empanado-air-fryer.jpg");
        imageMapping.put("hamburguesa gourmet", "/img/carnes/hamburguesa-con-queso-cabra.jpg.jpg");
        imageMapping.put("cordero asado", "/img/carnes/cordero-asado.jpg");
        
        // POSTRES
        imageMapping.put("tiramisú clásico", "/img/postres/Tiramisu-clasico.jpg");
        imageMapping.put("panna cotta", "/img/postres/PANACOTTA-CON-FRUTOS-ROJOS.jpg");
        imageMapping.put("helado artesanal", "/img/postres/helado-artesanal.jpg");
        imageMapping.put("brownie con helado", "/img/postres/brownie-con-helado-destacada.jpg");
        imageMapping.put("tarta de queso", "/img/postres/tarta-queso-horno-receta.jpg");
        imageMapping.put("coulant de chocolate", "/img/postres/coulant-de-chocolate_515_1.jpg");
        imageMapping.put("fruta fresca", "/img/postres/fruta-fresca.jpg");
        imageMapping.put("cannoli sicilianos", "/img/postres/Cannoli-siciliani_1200x800.jpg");
        imageMapping.put("gelato affogato", "/img/postres/gelato-affogato.jpg");
        
        // VINOS
        imageMapping.put("rioja crianza", "/img/vino/rioja-vega-crianza.jpg");
        imageMapping.put("albariño rías baixas", "/img/vino/albariño rias baixas.jpg");
        imageMapping.put("chianti docg", "/img/vino/chianti-docg.jpg");
        imageMapping.put("ribera del duero crianza", "/img/vino/ribera-duero-crianza.jpg");
        imageMapping.put("godello sobre lías", "/img/vino/valdeorras-o-luar-do-sil-godello-sobre-lias-75-cl.jpg");
        imageMapping.put("barolo joven", "/img/vino/barolo-joven.jpg");
        imageMapping.put("barolo joven (piamonte)", "/img/vino/barolo-joven.jpg");
        imageMapping.put("ribera del duero reserva", "/img/vino/rivera-duero.jpg");
        imageMapping.put("chablis premier cru", "/img/vino/chablis-premier-cru-montmains-simonnet-febvre.jpg");
        imageMapping.put("brunello di montalcino", "/img/vino/brunello.jpg");
        
        // MENÚ INFANTIL
        imageMapping.put("menú pizza", "/img/menu-infantil/MENU-INFANTIL.jpeg");
        imageMapping.put("menú hamburguesa", "/img/menu-infantil/menu-infantil2.jpg");
    }
    
    /**
     * Carga todos los platos dinámicamente desde la base de datos
     * Utiliza el mapeo de imágenes para asignar las imágenes correctas
     * y mapea categorías de BD (singular) a categorías de UI (plural)
     */
    private void loadDynamicDishesFromDatabase() {
        try {
            // Limpiar categorías existentes
            dishesByCategory.clear();
            
            List<Receta> todasLasRecetas = recetaDAO.findAll();
            System.out.println("\n🔄 Cargando " + todasLasRecetas.size() + " recetas desde la base de datos...");
            
            for (Receta receta : todasLasRecetas) {
                String categoriaBD = receta.getCategoria();
                if (categoriaBD == null || categoriaBD.trim().isEmpty()) {
                    System.out.println("⚠️ Receta sin categoría: " + receta.getNombre());
                    continue;
                }
                
                // Convertir categoría de BD a categoría de UI usando el mapeo
                String categoriaUI = categoryMapping.getOrDefault(categoriaBD, categoriaBD);
                
                // Buscar imagen en el mapeo (case-insensitive)
                String nombreNormalizado = receta.getNombre().toLowerCase().trim();
                String imagePath = imageMapping.get(nombreNormalizado);
                
                // Si no hay imagen en el mapeo, usar la imagen de la BD
                if (imagePath == null && receta.getImagen() != null) {
                    imagePath = null; // Se usará la imagen de BD (byte[])
                }
                
                // Calcular precio en euros (BD guarda en centavos)
                double precio = receta.getPrecio() != null ? receta.getPrecio() / 100.0 : 0.0;
                
                // Crear el plato con toda la información
                DishItem dish = new DishItem(
                    receta.getNombre(),
                    imagePath,
                    receta.getDescripcion(),
                    precio,
                    receta.getImagen(),
                    receta
                );
                
                // Añadir a la categoría UI correspondiente
                dishesByCategory.computeIfAbsent(categoriaUI, k -> new ArrayList<>()).add(dish);
                
                String imageInfo = imagePath != null ? "con imagen local" : 
                                  (receta.getImagen() != null ? "con imagen BD" : "sin imagen");
                System.out.println("✓ " + categoriaBD + " → " + categoriaUI + ": " + receta.getNombre() + " (" + imageInfo + ")");
            }
            
            System.out.println("\n📊 Resumen final por categorías UI:");
            dishesByCategory.forEach((cat, dishes) -> {
                System.out.println("  " + cat + ": " + dishes.size() + " platos");
            });
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando recetas desde BD: " + e.getMessage());
            e.printStackTrace();
        }
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
                // imageName ya contiene la ruta completa con /img/
                String imagePath = dish.imageName;
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
            abrirDetallesReceta(dish.receta);
        });

        return card;
    }
    
    /**
     * Abre una ventana modal con los detalles completos de la receta
     */
    private void abrirDetallesReceta(Receta receta) {
        if (receta == null) {
            System.err.println("No se puede abrir detalles: receta es null");
            return;
        }
        
        try {
            // Cargar la receta completa con ingredientes desde la BD
            Receta recetaCompleta = recetaDAO.findById(receta.getId_receta());
            
            if (recetaCompleta == null) {
                System.err.println("No se encontró la receta en la BD");
                return;
            }
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef/recipe-detail.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar la receta
            RecipeDetailController controller = loader.getController();
            controller.setReceta(recetaCompleta);

            // Reemplazar solo el centro del BorderPane principal para mantener header/sidebar/footer
            setCenterWithPadding(root);
            
        } catch (Exception e) {
            System.err.println("Error al abrir detalles de receta: " + e.getMessage());
            e.printStackTrace();
        }
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
        Receta receta; // Referencia a la receta completa

        DishItem(String name, String imageName, String description, double price) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
            this.imageBytes = null;
            this.receta = null;
        }
        
        DishItem(String name, String imageName, String description, double price, byte[] imageBytes) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
            this.imageBytes = imageBytes;
            this.receta = null;
        }
        
        DishItem(String name, String imageName, String description, double price, byte[] imageBytes, Receta receta) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
            this.imageBytes = imageBytes;
            this.receta = receta;
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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef/comanda-detail.fxml"));
                Parent root = loader.load();

                // Obtener el controlador y pasar la comanda (cargar detalles desde DAO para evitar Lazy issues)
                ilcaminodelamamma.DAO.ComandaDAO comandaDAO = new ilcaminodelamamma.DAO.ComandaDAO();
                ilcaminodelamamma.model.Comanda comandaFull = comandaDAO.findByIdWithDetails(comanda.getId_comanda());
                ilcaminodelamamma.view.chef.ComandaDetailController controller = loader.getController();
                controller.setComanda(comandaFull != null ? comandaFull : comanda);
                // Reemplazar solo el centro del BorderPane principal para mantener header/sidebar/footer
                setCenterWithPadding(root);

            } catch (Exception e) {
                System.err.println("Error al abrir detalles de comanda: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #F5E6D3; -fx-background-radius: 10; -fx-border-color: #8B7355; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #D4A574; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;");
        });
        
        return card;
    }

    /**
     * Helper: coloca el contenido dado en el centro del BorderPane principal
     * envolviéndolo en un `StackPane` con padding para mantener el header/sidebar/footer.
     */
    private void setCenterWithPadding(javafx.scene.Parent content) {
        if (centerArea == null || centerArea.getScene() == null) return;
        javafx.scene.Parent sceneRoot = centerArea.getScene().getRoot();
        if (!(sceneRoot instanceof javafx.scene.layout.BorderPane)) return;
        javafx.scene.layout.BorderPane mainRoot = (javafx.scene.layout.BorderPane) sceneRoot;

        // Just set the content directly on the center - let the BorderPane handle the space
        mainRoot.setCenter(content);
    }
}

