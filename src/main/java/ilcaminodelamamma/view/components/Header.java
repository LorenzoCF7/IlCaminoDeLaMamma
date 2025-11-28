package ilcaminodelamamma.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * Componente de cabecera reutilizable para todas las vistas
 */
public class Header extends HBox {
    
    private TextField searchField;
    
    public Header() {
        this(true); // Por defecto, con buscador
    }
    
    public Header(boolean withSearchField) {
        initializeComponent(withSearchField);
    }
    
    private void initializeComponent(boolean withSearchField) {
        // Configurar el HBox
        this.getStyleClass().add("top-bar");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);
        this.setPadding(new Insets(10, 15, 10, 15));
        
        // Logo
        try {
            var logoStream = getClass().getResourceAsStream("/img/logo.png");
            if (logoStream != null) {
                ImageView logoView = new ImageView(new Image(logoStream));
                logoView.setFitWidth(50);
                logoView.setFitHeight(50);
                logoView.setPreserveRatio(true);
                this.getChildren().add(logoView);
            }
        } catch (Exception e) {
            System.err.println("Error cargando logo: " + e.getMessage());
        }
        
        // Espaciador
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        this.getChildren().add(spacer);
        
        // Buscador (opcional)
        if (withSearchField) {
            searchField = new TextField();
            searchField.setPromptText("🔍 Buscar...");
            searchField.setPrefWidth(300);
            searchField.getStyleClass().add("search-field");
            this.getChildren().add(searchField);
        }
    }
    
    public TextField getSearchField() {
        return searchField;
    }
}
