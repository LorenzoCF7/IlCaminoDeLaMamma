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
    @FXML private Button btnCerrarSesion;
    @FXML private javafx.scene.control.Label contentTitle;
    @FXML private javafx.scene.layout.HBox breadcrumbBox;
    @FXML private javafx.scene.control.ScrollPane mainScrollPane;
    
    // Estado actual de la vista
    private String currentView = "categories";
    private String currentCategory = null;
    private int currentColumns = 3;

    // Lista de categorías con sus imágenes (mismas que el chef, en modo solo lectura)
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
    
    // Mapa de platos por categoría (mismo que el chef)
    private final java.util.Map<String, List<DishItem>> dishesByCategory = new java.util.HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Vista del Ayudante de Cocina inicializada correctamente");
        initializeDishes();
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
        
        // Configurar búsqueda de recetas
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                buscarRecetas(newValue);
            });
        }
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
        loadDishesForCategory(categoryName);
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
     * Inicializa los platos por categoría (mismos que el chef)
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
            new DishItem("Cordero Asado", "carnes/cordero-asado.jpg", "Cordero al horno con romero", 21.50)
        ));

        // POSTRES
        dishesByCategory.put("Postres", Arrays.asList(
            new DishItem("Tiramisú", "postres/tiramisu.jpg", "Clásico postre italiano con café", 6.50),
            new DishItem("Panna Cotta", "postres/panna-cotta.jpg", "Crema italiana con frutos rojos", 5.90),
            new DishItem("Cannoli Siciliano", "postres/canolis-sicilianos.jpg", "Hojaldre relleno de ricotta", 6.20),
            new DishItem("Tarta de Limón", "postres/tarta-de-limon-sin-horno.jpg", "Tarta refrescante de limón", 5.50),
            new DishItem("Helado Artesanal", "postres/helados.jpg", "Selección de helados caseros", 4.80),
            new DishItem("Brownie con Helado", "postres/brownie-helado.jpg", "Brownie de chocolate caliente", 7.20),
            new DishItem("Cheesecake", "postres/new-york-cheesecake.jpg", "Tarta de queso estilo Nueva York", 6.80),
            new DishItem("Profiteroles", "postres/profiteroles-de-nata.jpg", "Bolitas de hojaldre con crema", 6.90),
            new DishItem("Affogato", "postres/affogato.jpg", "Helado con espresso caliente", 5.50)
        ));

        // VINOS
        dishesByCategory.put("Vinos", Arrays.asList(
            new DishItem("Rioja Crianza", "vino/rioja-vega-crianza.jpg", "Vino tinto con crianza", 18.00),
            new DishItem("Chianti Classico", "vino/chianti.jpg", "Vino italiano clásico", 22.00),
            new DishItem("Prosecco", "vino/prosecco.jpg", "Espumoso italiano", 15.00),
            new DishItem("Lambrusco", "vino/lambrusco.jpg", "Vino tinto espumoso", 12.00),
            new DishItem("Vermentino", "vino/vermentino.jpg", "Vino blanco fresco", 16.00)
        ));

        // MENÚ INFANTIL
        dishesByCategory.put("Menú Infantil", Arrays.asList(
            new DishItem("Macarrones con Tomate", "menu-infantil/macarrones.jpg", "Pasta simple para niños", 7.50),
            new DishItem("Nuggets de Pollo", "menu-infantil/nuggets.jpg", "Nuggets con patatas", 8.00),
            new DishItem("Pizza Pequeña", "menu-infantil/pizza-infantil.jpg", "Pizza margarita individual", 7.00),
            new DishItem("Hamburguesa Mini", "menu-infantil/mini-hamburguesa.jpg", "Hamburguesa con patatas", 8.50)
        ));
    }
    
    /**
     * Configura el layout responsive
     */
    private void setupResponsiveLayout() {
        if (mainScrollPane != null) {
            mainScrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                double width = newVal.doubleValue();
                int newColumns = width > 1200 ? 4 : (width > 800 ? 3 : 2);
                if (newColumns != currentColumns) {
                    currentColumns = newColumns;
                    if (currentView.equals("categories")) {
                        reloadCategoryGrid();
                    } else {
                        reloadDishGrid();
                    }
                }
            });
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
            javafx.scene.layout.VBox dishCard = createDishCard(dish);
            categoryGrid.add(dishCard, column, row);
            
            column++;
            if (column >= currentColumns) {
                column = 0;
                row++;
            }
        }
    }
    
    /**
     * Recarga el grid de categorías
     */
    private void reloadCategoryGrid() {
        categoryGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;

        for (CategoryItem category : categories) {
            javafx.scene.layout.VBox categoryCard = createCategoryCard(category);
            categoryGrid.add(categoryCard, column, row);
            
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
            
            javafx.scene.control.Button backButton = new javafx.scene.control.Button("← Volver a Categorías");
            backButton.getStyleClass().add("breadcrumb-button");
            backButton.setOnAction(e -> goBackToCategories());
            
            javafx.scene.control.Label separator = new javafx.scene.control.Label("  /  ");
            separator.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
            
            javafx.scene.control.Label currentLabel = new javafx.scene.control.Label(categoryName);
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
        
        // Limpiar búsqueda
        if (searchField != null) {
            searchField.clear();
        }
        
        reloadCategoryGrid();
    }
    
    /**
     * Crea una tarjeta de plato (solo lectura)
     */
    private javafx.scene.layout.VBox createDishCard(DishItem dish) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox();
        card.getStyleClass().add("dish-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(260);

        try {
            String imagePath = "/img/" + dish.imageName;
            var inputStream = getClass().getResourceAsStream(imagePath);
            
            if (inputStream != null) {
                ImageView imageView = new ImageView(new Image(inputStream));
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(false);
                imageView.getStyleClass().add("dish-image");
                
                Label nameLabel = new Label(dish.name);
                nameLabel.getStyleClass().add("dish-name");
                nameLabel.setMaxWidth(180);
                nameLabel.setWrapText(true);
                
                Label descLabel = new Label(dish.description);
                descLabel.getStyleClass().add("dish-description");
                descLabel.setMaxWidth(180);
                descLabel.setWrapText(true);
                
                Label priceLabel = new Label(String.format("%.2f€", dish.price));
                priceLabel.getStyleClass().add("dish-price");
                
                card.getChildren().addAll(imageView, nameLabel, descLabel, priceLabel);
            }
        } catch (Exception e) {
            System.err.println("Error cargando plato: " + dish.name);
        }
        
        return card;
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
        for (java.util.Map.Entry<String, List<DishItem>> entry : dishesByCategory.entrySet()) {
            for (DishItem dish : entry.getValue()) {
                if (dish.name.toLowerCase().contains(searchTerm)) {
                    javafx.scene.layout.VBox dishCard = createDishCard(dish);
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
     * Cierra la sesión actual y vuelve a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            System.out.println("Cerrando sesión del Ayudante de Cocina...");
            
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
     * Clase interna para representar un plato
     */
    private static class DishItem {
        String name;
        String imageName;
        String description;
        double price;

        DishItem(String name, String imageName, String description, double price) {
            this.name = name;
            this.imageName = imageName;
            this.description = description;
            this.price = price;
        }
    }
}
