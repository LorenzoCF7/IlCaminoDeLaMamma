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

    // Instancia estática para acceso desde otros controladores
    private static ChefViewController instance;
    
    public static ChefViewController getInstance() {
        return instance;
    }

    @FXML private StackPane rootStackPane;
    @FXML private javafx.scene.layout.BorderPane mainBorderPane;
    @FXML private VBox recipeDetailOverlay;
    @FXML private StackPane recipeDetailContainer;
    
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
    @FXML private Button btnComandasTerminadas;

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
        
        // Guardar instancia para acceso estático
        instance = this;
        
        recetaDAO = new RecetaDAO();
        initializeImageMapping(); // Cargar mapeo de imágenes
        loadDynamicDishesFromDatabase(); // Cargar todos los platos desde BD
        loadCategoryGrid();
        loadRecentRecipes();
        setupTabButtons();
        setupResponsiveLayout();
        
        // Configurar overlay de detalles de receta
        setupRecipeDetailOverlay();
        
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
        
        // Configurar botón de comandas terminadas
        if (btnComandasTerminadas != null) {
            btnComandasTerminadas.setOnAction(e -> mostrarComandasTerminadas());
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
    public void goBackToCategories() {
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
     * Configura el overlay para mostrar detalles de recetas
     */
    private void setupRecipeDetailOverlay() {
        if (recipeDetailOverlay != null) {
            // Cerrar overlay al hacer clic fuera del contenedor
            recipeDetailOverlay.setOnMouseClicked(event -> {
                if (event.getTarget() == recipeDetailOverlay) {
                    cerrarDetallesReceta();
                }
            });
        }
    }
    
    /**
     * Abre el overlay con los detalles de la receta
     * Método público estático para ser llamado desde cualquier controlador
     */
    public void mostrarDetallesReceta(Receta receta) {
        abrirDetallesReceta(receta);
    }
    
    /**
     * Abre el overlay con los detalles de la receta por ID
     * Método público para ser llamado desde otros controladores
     */
    public void mostrarDetallesRecetaPorId(Integer recetaId) {
        if (recetaId == null) {
            System.err.println("❌ ID de receta es null");
            return;
        }
        Receta receta = recetaDAO.findById(recetaId);
        if (receta != null) {
            abrirDetallesReceta(receta);
        } else {
            System.err.println("❌ No se encontró receta con ID: " + recetaId);
        }
    }
    
    /**
     * Cierra el overlay de detalles de receta
     */
    public void cerrarDetallesReceta() {
        if (recipeDetailOverlay != null) {
            recipeDetailOverlay.setVisible(false);
            recipeDetailOverlay.setManaged(false);
            if (recipeDetailContainer != null) {
                recipeDetailContainer.getChildren().clear();
            }
            System.out.println("✓ Overlay de detalles cerrado");
        }
    }
    
    /**
     * Abre una ventana modal con los detalles completos de la receta
     */
    private void abrirDetallesReceta(Receta receta) {
        if (receta == null) {
            System.err.println("❌ No se puede abrir detalles: receta es null");
            return;
        }
        
        System.out.println("🔍 Abriendo detalles de receta: " + receta.getNombre() + " (ID: " + receta.getId_receta() + ")");
        
        try {
            // Cargar la receta completa con ingredientes desde la BD
            Receta recetaCompleta = recetaDAO.findById(receta.getId_receta());
            
            if (recetaCompleta == null) {
                System.err.println("❌ No se encontró la receta en la BD con ID: " + receta.getId_receta());
                return;
            }
            
            System.out.println("✓ Receta cargada de BD: " + recetaCompleta.getNombre());
            
            // Crear vista de detalle programáticamente (más robusto que FXML)
            VBox detalleView = crearVistaDetalleReceta(recetaCompleta);
            
            // Mostrar en el overlay
            if (recipeDetailContainer != null && recipeDetailOverlay != null) {
                recipeDetailContainer.getChildren().clear();
                recipeDetailContainer.getChildren().add(detalleView);
                recipeDetailOverlay.setVisible(true);
                recipeDetailOverlay.setManaged(true);
                System.out.println("✓ Detalles mostrados en overlay");
            } else {
                System.err.println("⚠️ Overlay no disponible, intentando método alternativo...");
                // Método alternativo: abrir en ventana separada
                abrirDetallesEnVentana(recetaCompleta);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al abrir detalles de receta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea la vista de detalle de receta programáticamente
     */
    private VBox crearVistaDetalleReceta(Receta receta) {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #F5F1ED; -fx-background-radius: 15;");
        container.setMaxWidth(850);
        container.setMaxHeight(650);
        
        // === HEADER con imagen ===
        StackPane header = new StackPane();
        header.setMinHeight(200);
        header.setMaxHeight(200);
        header.setStyle("-fx-background-color: #2C1810; -fx-background-radius: 15 15 0 0;");
        
        // Imagen de fondo
        ImageView imgView = new ImageView();
        imgView.setFitWidth(850);
        imgView.setFitHeight(200);
        imgView.setPreserveRatio(false);
        if (receta.getImagen() != null && receta.getImagen().length > 0) {
            try {
                imgView.setImage(new Image(new ByteArrayInputStream(receta.getImagen())));
            } catch (Exception e) {
                cargarImagenCategoria(imgView, receta.getCategoria());
            }
        } else {
            cargarImagenCategoria(imgView, receta.getCategoria());
        }
        
        // Overlay oscuro para mejor legibilidad
        VBox overlay = new VBox(5);
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 100%);");
        overlay.setPadding(new Insets(20, 25, 20, 25));
        
        Label nombreLabel = new Label(receta.getNombre());
        nombreLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        HBox infoBox = new HBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        if (receta.getCategoria() != null) {
            Label catLabel = new Label(receta.getCategoria());
            catLabel.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-text-fill: #2C1810; -fx-padding: 5 12; -fx-background-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;");
            infoBox.getChildren().add(catLabel);
        }
        
        if (receta.getTiempo_preparacion() != null && receta.getTiempo_preparacion() > 0) {
            Label tiempoLabel = new Label("⏱ " + receta.getTiempo_preparacion() + " min");
            tiempoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            infoBox.getChildren().add(tiempoLabel);
        }
        
        if (receta.getPrecio() != null && receta.getPrecio() > 0) {
            Label precioLabel = new Label("💰 " + String.format("%.2f €", receta.getPrecio() / 100.0));
            precioLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            infoBox.getChildren().add(precioLabel);
        }
        
        overlay.getChildren().addAll(nombreLabel, infoBox);
        
        // Botón cerrar
        Button btnCerrar = new Button("✕");
        btnCerrar.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> cerrarDetallesReceta());
        btnCerrar.setOnMouseEntered(e -> btnCerrar.setStyle("-fx-background-color: rgba(200,0,0,0.8); -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;"));
        btnCerrar.setOnMouseExited(e -> btnCerrar.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;"));
        StackPane.setAlignment(btnCerrar, Pos.TOP_RIGHT);
        StackPane.setMargin(btnCerrar, new Insets(10, 10, 0, 0));
        
        header.getChildren().addAll(imgView, overlay, btnCerrar);
        
        // === CONTENIDO con scroll ===
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: #F5F1ED;");
        
        // Descripción
        if (receta.getDescripcion() != null && !receta.getDescripcion().trim().isEmpty()) {
            VBox descBox = new VBox(8);
            Label descTitulo = new Label("📝 Descripción");
            descTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
            Label descTexto = new Label(receta.getDescripcion());
            descTexto.setWrapText(true);
            descTexto.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            descBox.getChildren().addAll(descTitulo, descTexto);
            contenido.getChildren().add(descBox);
        }
        
        // Grid con ingredientes y pasos
        HBox gridBox = new HBox(20);
        
        // Ingredientes
        VBox ingredientesBox = new VBox(10);
        ingredientesBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        ingredientesBox.setMinWidth(250);
        ingredientesBox.setPrefWidth(300);
        
        Label ingTitulo = new Label("🥕 Ingredientes");
        ingTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        ingredientesBox.getChildren().add(ingTitulo);
        
        if (receta.getRecetaIngredientes() != null && !receta.getRecetaIngredientes().isEmpty()) {
            for (var ri : receta.getRecetaIngredientes()) {
                if (ri.getIngrediente() != null) {
                    String texto = "• " + ri.getIngrediente().getNombre();
                    if (ri.getCantidad_usada() != null && ri.getCantidad_usada() > 0) {
                        String unidad = ri.getIngrediente().getUnidad_medida() != null ? ri.getIngrediente().getUnidad_medida() : "";
                        texto += " (" + ri.getCantidad_usada() + " " + unidad + ")";
                    }
                    Label ingLabel = new Label(texto);
                    ingLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
                    ingLabel.setWrapText(true);
                    ingredientesBox.getChildren().add(ingLabel);
                }
            }
        } else {
            Label noIng = new Label("Sin ingredientes especificados");
            noIng.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; -fx-font-style: italic;");
            ingredientesBox.getChildren().add(noIng);
        }
        
        // Pasos
        VBox pasosBox = new VBox(10);
        pasosBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        HBox.setHgrow(pasosBox, javafx.scene.layout.Priority.ALWAYS);
        
        Label pasosTitulo = new Label("👨‍🍳 Preparación");
        pasosTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        pasosBox.getChildren().add(pasosTitulo);
        
        if (receta.getPasos() != null && !receta.getPasos().trim().isEmpty()) {
            String[] lineas = receta.getPasos().split("\n");
            int numPaso = 1;
            for (String linea : lineas) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                HBox pasoItem = new HBox(10);
                pasoItem.setAlignment(Pos.TOP_LEFT);
                
                Label numLabel = new Label(String.valueOf(numPaso));
                numLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #8B7355; -fx-min-width: 28; -fx-min-height: 28; -fx-alignment: center; -fx-background-radius: 14;");
                numLabel.setMinWidth(28);
                numLabel.setAlignment(Pos.CENTER);
                
                String textoLimpio = linea.replaceFirst("^\\d+\\.?\\s*", "");
                Label pasoLabel = new Label(textoLimpio);
                pasoLabel.setWrapText(true);
                pasoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
                HBox.setHgrow(pasoLabel, javafx.scene.layout.Priority.ALWAYS);
                
                pasoItem.getChildren().addAll(numLabel, pasoLabel);
                pasosBox.getChildren().add(pasoItem);
                numPaso++;
            }
        } else {
            Label noPasos = new Label("Sin pasos de preparación especificados");
            noPasos.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; -fx-font-style: italic;");
            pasosBox.getChildren().add(noPasos);
        }
        
        gridBox.getChildren().addAll(ingredientesBox, pasosBox);
        contenido.getChildren().add(gridBox);
        
        // Disponibilidad
        Label dispLabel = new Label(receta.getDisponible() != null && receta.getDisponible() 
            ? "✅ Disponible para servir" 
            : "❌ No disponible actualmente");
        dispLabel.setStyle(receta.getDisponible() != null && receta.getDisponible() 
            ? "-fx-font-size: 14px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;" 
            : "-fx-font-size: 14px; -fx-text-fill: #C62828; -fx-font-weight: bold;");
        contenido.getChildren().add(dispLabel);
        
        scrollPane.setContent(contenido);
        container.getChildren().addAll(header, scrollPane);
        
        return container;
    }
    
    /**
     * Carga imagen de categoría por defecto
     */
    private void cargarImagenCategoria(ImageView imgView, String categoria) {
        try {
            String path = "/img/categorias/" + (categoria != null ? categoria : "default") + ".jpg";
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                imgView.setImage(new Image(stream));
            }
        } catch (Exception e) {
            // Ignorar
        }
    }
    
    /**
     * Abre detalles en ventana separada como fallback
     */
    private void abrirDetallesEnVentana(Receta receta) {
        try {
            Stage stage = new Stage();
            VBox detalleView = crearVistaDetalleReceta(receta);
            Scene scene = new Scene(detalleView, 850, 650);
            stage.setScene(scene);
            stage.setTitle("Detalles: " + receta.getNombre());
            stage.show();
        } catch (Exception e) {
            System.err.println("Error abriendo ventana de detalles: " + e.getMessage());
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
     * Muestra la lista de comandas activas (no finalizadas)
     */
    private void mostrarComandas() {
        try {
            System.out.println("=== MOSTRANDO COMANDAS ACTIVAS ===");
            
            // Limpiar el grid actual
            categoryGrid.getChildren().clear();
            System.out.println("Grid limpiado");
            
            // Cambiar el título
            if (contentTitle != null) {
                contentTitle.setText("📋 Comandas Activas");
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
            List<Comanda> todasComandas = comandaDAO.findAll();
            System.out.println("Comandas recuperadas de BD: " + todasComandas.size());
            
            // Filtrar solo las activas (NO finalizadas)
            List<Comanda> comandasActivas = new java.util.ArrayList<>();
            for (Comanda c : todasComandas) {
                if (c.getEstadoComanda() != Comanda.EstadoComanda.FINALIZADA) {
                    comandasActivas.add(c);
                }
            }
            System.out.println("Comandas activas (no finalizadas): " + comandasActivas.size());
            
            int column = 0;
            int row = 0;
            
            for (Comanda comanda : comandasActivas) {
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
            
            if (comandasActivas.isEmpty()) {
                System.out.println("⚠️ NO HAY COMANDAS ACTIVAS");
                Label noData = new Label("📭 No hay comandas activas en este momento");
                noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-style: italic;");
                categoryGrid.add(noData, 0, 0);
            }
            
            System.out.println("=== COMANDAS ACTIVAS CARGADAS EXITOSAMENTE: " + comandasActivas.size() + " ===");
            
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
     * Crea una tarjeta visual para mostrar una comanda - MEJORADA
     */
    private VBox createComandaCard(Comanda comanda) {
        VBox card = new VBox(8);
        card.getStyleClass().add("comanda-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMinWidth(200);
        card.setPrefWidth(230);
        card.setMaxWidth(280);
        card.setPrefHeight(200);
        card.setPadding(new Insets(15));
        
        // Estado de la comanda para el color
        Comanda.EstadoComanda estadoComanda = comanda.getEstadoComanda();
        String bgColor = "white";
        String borderColor = "#D4A574";
        String estadoTexto = estadoComanda != null ? estadoComanda.getDescripcion() : "Por hacer";
        String estadoColor = "#E65100"; // Naranja por defecto
        
        if (estadoComanda == Comanda.EstadoComanda.EN_PROCESO) {
            borderColor = "#2196F3";
            estadoColor = "#1565C0";
        } else if (estadoComanda == Comanda.EstadoComanda.FINALIZADA) {
            bgColor = "#E8F5E9";
            borderColor = "#4CAF50";
            estadoColor = "#2E7D32";
        }
        
        final String finalBgColor = bgColor;
        final String finalBorderColor = borderColor;
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12; -fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-cursor: hand;");
        
        // Header: Mesa + Estado badge
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label lblMesa = new Label("🍽️ Mesa " + (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));
        lblMesa.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
        
        Label estadoBadge = new Label(estadoTexto);
        estadoBadge.setStyle("-fx-background-color: " + estadoColor + "22; -fx-text-fill: " + estadoColor + "; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(lblMesa, estadoBadge);
        
        // Separador
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.setStyle("-fx-background-color: #E0D5C7;");
        
        // Número de platos
        int numPlatos = comanda.getDetalleComandas() != null ? comanda.getDetalleComandas().size() : 0;
        Label lblPlatos = new Label("📋 " + numPlatos + " plato(s)");
        lblPlatos.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        
        // Total de la comanda
        float total = 0;
        if (comanda.getDetalleComandas() != null) {
            for (ilcaminodelamamma.model.DetalleComanda det : comanda.getDetalleComandas()) {
                if (det.getSubtotal() != null) {
                    total += det.getSubtotal();
                } else if (det.getPrecio_unitario() != null && det.getCantidad() != null) {
                    total += det.getPrecio_unitario() * det.getCantidad();
                }
            }
        }
        // Convertir de céntimos a euros para mostrar
        Label lblTotal = new Label("💰 " + String.format("%.2f €", total / 100.0));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        
        // Fecha/hora
        String fechaTexto = "--";
        if (comanda.getFecha_hora() != null) {
            fechaTexto = comanda.getFecha_hora().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        Label lblFecha = new Label("🕐 " + fechaTexto);
        lblFecha.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        
        // ID de comanda
        Label lblId = new Label("#" + comanda.getId_comanda());
        lblId.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
        
        card.getChildren().addAll(header, sep, lblPlatos, lblTotal, lblFecha, lblId);
        
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
            card.setStyle("-fx-background-color: #FDF8F3; -fx-background-radius: 12; -fx-border-color: #8B7355; -fx-border-width: 2; -fx-border-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + finalBgColor + "; -fx-background-radius: 12; -fx-border-color: " + finalBorderColor + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-cursor: hand;");
        });
        
        return card;
    }
    
    /**
     * Muestra las comandas finalizadas con el total de caja
     */
    private void mostrarComandasTerminadas() {
        try {
            System.out.println("=== MOSTRANDO COMANDAS TERMINADAS ===");
            
            // Limpiar el grid actual
            categoryGrid.getChildren().clear();
            
            // Cambiar el título
            if (contentTitle != null) {
                contentTitle.setText("✅ Comandas Terminadas");
            }
            
            // Ocultar breadcrumb
            if (breadcrumbBox != null) {
                breadcrumbBox.setVisible(false);
                breadcrumbBox.setManaged(false);
            }
            
            // Cargar comandas finalizadas desde la base de datos
            ComandaDAO comandaDAO = new ComandaDAO();
            List<Comanda> todasComandas = comandaDAO.findAll();
            
            // Filtrar solo las finalizadas
            List<Comanda> comandasTerminadas = new java.util.ArrayList<>();
            float totalCaja = 0;
            
            for (Comanda c : todasComandas) {
                if (c.getEstadoComanda() == Comanda.EstadoComanda.FINALIZADA) {
                    comandasTerminadas.add(c);
                    // Calcular total de esta comanda
                    if (c.getDetalleComandas() != null) {
                        for (ilcaminodelamamma.model.DetalleComanda det : c.getDetalleComandas()) {
                            if (det.getSubtotal() != null) {
                                totalCaja += det.getSubtotal();
                            } else if (det.getPrecio_unitario() != null && det.getCantidad() != null) {
                                totalCaja += det.getPrecio_unitario() * det.getCantidad();
                            }
                        }
                    }
                }
            }
            
            // Crear panel de resumen de caja
            VBox panelCaja = new VBox(12);
            panelCaja.setAlignment(Pos.CENTER);
            panelCaja.setPadding(new Insets(20));
            panelCaja.setStyle("-fx-background-color: linear-gradient(to bottom, #E8F5E9 0%, #C8E6C9 100%); -fx-background-radius: 15; -fx-border-color: #81C784; -fx-border-radius: 15; -fx-border-width: 2;");
            panelCaja.setMinWidth(250);
            panelCaja.setPrefWidth(280);
            panelCaja.setMaxWidth(320);
            
            Label lblIconCaja = new Label("💰");
            lblIconCaja.setStyle("-fx-font-size: 40px;");
            
            Label lblTituloCaja = new Label("Total en Caja");
            lblTituloCaja.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
            
            // Convertir de céntimos a euros para mostrar
            final float totalCajaFinal = totalCaja;
            Label lblTotalCaja = new Label(String.format("%.2f €", totalCaja / 100.0));
            lblTotalCaja.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1B5E20;");
            
            Label lblNumComandas = new Label(comandasTerminadas.size() + " comandas completadas");
            lblNumComandas.setStyle("-fx-font-size: 12px; -fx-text-fill: #558B2F;");
            
            // Botón de vaciar caja
            Button btnVaciarCaja = new Button("🧾 Vaciar Caja");
            btnVaciarCaja.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
            btnVaciarCaja.setOnMouseEntered(e -> btnVaciarCaja.setStyle("-fx-background-color: #B71C1C; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;"));
            btnVaciarCaja.setOnMouseExited(e -> btnVaciarCaja.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;"));
            
            // Deshabilitar si no hay comandas
            if (comandasTerminadas.isEmpty()) {
                btnVaciarCaja.setDisable(true);
                btnVaciarCaja.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
            }
            
            // Guardar lista de comandas para el closure
            final List<Comanda> comandasParaExportar = new java.util.ArrayList<>(comandasTerminadas);
            
            btnVaciarCaja.setOnAction(e -> vaciarCaja(comandasParaExportar, totalCajaFinal));
            
            panelCaja.getChildren().addAll(lblIconCaja, lblTituloCaja, lblTotalCaja, lblNumComandas, btnVaciarCaja);
            
            // Añadir panel de caja en la primera posición
            categoryGrid.add(panelCaja, 0, 0);
            
            // Mostrar comandas terminadas
            int column = 1;
            int row = 0;
            
            for (Comanda comanda : comandasTerminadas) {
                VBox comandaCard = createComandaCard(comanda);
                categoryGrid.add(comandaCard, column, row);
                
                column++;
                if (column >= currentColumns) {
                    column = 0;
                    row++;
                }
            }
            
            if (comandasTerminadas.isEmpty()) {
                Label noData = new Label("📭 No hay comandas terminadas aún");
                noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-style: italic;");
                categoryGrid.add(noData, 1, 0);
            }
            
            System.out.println("=== COMANDAS TERMINADAS CARGADAS: " + comandasTerminadas.size() + " - Total: " + totalCaja + " € ===");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR AL CARGAR COMANDAS TERMINADAS: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Vacía la caja: exporta un JSON con las comandas terminadas y el total, 
     * luego elimina las comandas finalizadas de la BD
     */
    private void vaciarCaja(List<Comanda> comandasTerminadas, float totalCaja) {
        // Confirmar acción
        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Vaciar Caja");
        confirmacion.setHeaderText("¿Está seguro de vaciar la caja?");
        confirmacion.setContentText("Se exportará un archivo JSON con el resumen de " + comandasTerminadas.size() + 
                                   " comandas y un total de " + String.format("%.2f €", totalCaja / 100.0) + 
                                   ".\n\nLas comandas finalizadas serán eliminadas de la base de datos.");
        
        java.util.Optional<javafx.scene.control.ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
            try {
                // Recargar comandas con sesión activa para evitar LazyInitializationException
                ComandaDAO comandaDAO = new ComandaDAO();
                List<Integer> idsComandas = new java.util.ArrayList<>();
                for (Comanda c : comandasTerminadas) {
                    idsComandas.add(c.getId_comanda());
                }
                
                // Crear el JSON
                StringBuilder json = new StringBuilder();
                json.append("{\n");
                json.append("  \"fecha_cierre\": \"").append(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\",\n");
                json.append("  \"total_caja_euros\": ").append(String.format(java.util.Locale.US, "%.2f", totalCaja / 100.0)).append(",\n");
                json.append("  \"numero_comandas\": ").append(idsComandas.size()).append(",\n");
                json.append("  \"comandas\": [\n");
                
                for (int i = 0; i < idsComandas.size(); i++) {
                    Integer idComanda = idsComandas.get(i);
                    
                    // Cargar comanda con detalles (sesión activa)
                    Comanda c = comandaDAO.findByIdWithDetails(idComanda);
                    if (c == null) continue;
                    
                    // Calcular total de esta comanda
                    float totalComanda = 0;
                    int numPlatos = 0;
                    
                    // Extraer datos mientras la sesión está activa
                    Integer mesaId = c.getMesa() != null ? c.getMesa().getId_mesa() : null;
                    String camarero = "N/A";
                    try {
                        if (c.getUsuario() != null) {
                            camarero = c.getUsuario().getNombre();
                        }
                    } catch (Exception ex) {
                        camarero = "N/A";
                    }
                    String fechaHora = c.getFecha_hora() != null ? 
                        c.getFecha_hora().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";
                    
                    json.append("    {\n");
                    json.append("      \"id_comanda\": ").append(idComanda).append(",\n");
                    json.append("      \"mesa\": ").append(mesaId != null ? mesaId : "null").append(",\n");
                    json.append("      \"camarero\": \"").append(camarero).append("\",\n");
                    json.append("      \"fecha_hora\": \"").append(fechaHora).append("\",\n");
                    
                    // Procesar platos
                    StringBuilder platosJson = new StringBuilder();
                    platosJson.append("      \"platos\": [\n");
                    
                    if (c.getDetalleComandas() != null && !c.getDetalleComandas().isEmpty()) {
                        numPlatos = c.getDetalleComandas().size();
                        int j = 0;
                        for (ilcaminodelamamma.model.DetalleComanda det : c.getDetalleComandas()) {
                            // Calcular subtotal
                            float subtotal = 0;
                            if (det.getSubtotal() != null) {
                                subtotal = det.getSubtotal();
                                totalComanda += subtotal;
                            } else if (det.getPrecio_unitario() != null && det.getCantidad() != null) {
                                subtotal = det.getPrecio_unitario() * det.getCantidad();
                                totalComanda += subtotal;
                            }
                            
                            String nombrePlato = "N/A";
                            try {
                                if (det.getReceta() != null) {
                                    nombrePlato = det.getReceta().getNombre();
                                }
                            } catch (Exception ex) {
                                nombrePlato = "N/A";
                            }
                            
                            platosJson.append("        {\n");
                            platosJson.append("          \"nombre\": \"").append(nombrePlato).append("\",\n");
                            platosJson.append("          \"cantidad\": ").append(det.getCantidad() != null ? det.getCantidad() : 0).append(",\n");
                            platosJson.append("          \"precio_unitario_euros\": ").append(String.format(java.util.Locale.US, "%.2f", 
                                det.getPrecio_unitario() != null ? det.getPrecio_unitario() / 100.0 : 0)).append(",\n");
                            platosJson.append("          \"subtotal_euros\": ").append(String.format(java.util.Locale.US, "%.2f", subtotal / 100.0)).append("\n");
                            platosJson.append("        }");
                            if (j < numPlatos - 1) platosJson.append(",");
                            platosJson.append("\n");
                            j++;
                        }
                    }
                    platosJson.append("      ]\n");
                    
                    json.append("      \"num_platos\": ").append(numPlatos).append(",\n");
                    json.append("      \"total_euros\": ").append(String.format(java.util.Locale.US, "%.2f", totalComanda / 100.0)).append(",\n");
                    json.append(platosJson);
                    
                    json.append("    }");
                    if (i < idsComandas.size() - 1) json.append(",");
                    json.append("\n");
                }
                
                json.append("  ]\n");
                json.append("}\n");
                
                // Guardar archivo JSON
                String nombreArchivo = "cierre_caja_" + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
                
                // Usar FileChooser para que el usuario elija dónde guardar
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Guardar Cierre de Caja");
                fileChooser.setInitialFileName(nombreArchivo);
                fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Archivo JSON", "*.json"));
                
                // Obtener la ventana actual
                javafx.stage.Window window = categoryGrid.getScene().getWindow();
                java.io.File archivo = fileChooser.showSaveDialog(window);
                
                if (archivo != null) {
                    // Escribir el JSON
                    try (java.io.FileWriter writer = new java.io.FileWriter(archivo)) {
                        writer.write(json.toString());
                    }
                    
                    System.out.println("✅ JSON exportado a: " + archivo.getAbsolutePath());
                    
                    // Eliminar las comandas finalizadas de la BD
                    int eliminadas = 0;
                    for (Integer id : idsComandas) {
                        try {
                            comandaDAO.deleteById(id);
                            eliminadas++;
                        } catch (Exception ex) {
                            System.err.println("Error eliminando comanda " + id + ": " + ex.getMessage());
                        }
                    }
                    
                    System.out.println("✅ " + eliminadas + " comandas eliminadas de la BD");
                    
                    // Mostrar mensaje de éxito
                    javafx.scene.control.Alert exito = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    exito.setTitle("Caja Vaciada");
                    exito.setHeaderText("✅ Cierre de caja completado");
                    exito.setContentText("Se ha exportado el archivo:\n" + archivo.getName() + 
                                        "\n\nTotal facturado: " + String.format("%.2f €", totalCaja / 100.0) +
                                        "\nComandas procesadas: " + eliminadas);
                    exito.showAndWait();
                    
                    // Recargar la vista de comandas terminadas
                    mostrarComandasTerminadas();
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error al vaciar caja: " + e.getMessage());
                e.printStackTrace();
                
                javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error al vaciar la caja");
                error.setContentText("No se pudo completar el cierre de caja:\n" + e.getMessage());
                error.showAndWait();
            }
        }
    }

    /**
     * Helper: coloca el contenido dado en el centro del BorderPane principal
     * envolviéndolo en un `StackPane` con padding para mantener el header/sidebar/footer.
     */
    private void setCenterWithPadding(javafx.scene.Parent content) {
        System.out.println("📍 setCenterWithPadding llamado");
        
        // Método 1: Usar directamente el mainBorderPane si está disponible
        if (mainBorderPane != null) {
            mainBorderPane.setCenter(content);
            System.out.println("✓ Contenido establecido en mainBorderPane directamente");
            return;
        }
        
        // Método 2: Buscar el BorderPane en la estructura de la escena
        if (centerArea == null) {
            System.err.println("❌ centerArea es null");
            return;
        }
        if (centerArea.getScene() == null) {
            System.err.println("❌ centerArea.getScene() es null");
            return;
        }
        
        javafx.scene.Parent sceneRoot = centerArea.getScene().getRoot();
        System.out.println("📍 sceneRoot: " + sceneRoot.getClass().getName());
        
        // Si la raíz es un StackPane, buscar el BorderPane dentro
        if (sceneRoot instanceof javafx.scene.layout.StackPane) {
            javafx.scene.layout.StackPane rootStack = (javafx.scene.layout.StackPane) sceneRoot;
            for (javafx.scene.Node child : rootStack.getChildren()) {
                if (child instanceof javafx.scene.layout.BorderPane) {
                    javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) child;
                    borderPane.setCenter(content);
                    System.out.println("✓ Contenido establecido en BorderPane (dentro de StackPane)");
                    return;
                }
            }
            System.err.println("❌ No se encontró BorderPane dentro del StackPane");
            return;
        }
        
        // Si la raíz es directamente un BorderPane
        if (sceneRoot instanceof javafx.scene.layout.BorderPane) {
            javafx.scene.layout.BorderPane mainRoot = (javafx.scene.layout.BorderPane) sceneRoot;
            mainRoot.setCenter(content);
            System.out.println("✓ Contenido establecido en el centro del BorderPane");
            return;
        }
        
        System.err.println("❌ sceneRoot no es BorderPane ni StackPane, es: " + sceneRoot.getClass().getName());
    }
}

