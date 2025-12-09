package ilcaminodelamamma.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Componente de pie de página reutilizable para todas las vistas
 */
public class Footer extends HBox {
    
    private Label infoLabel;
    
    public Footer() {
        this("Sistema de gestión - Il Camino Della Mamma");
    }
    
    public Footer(String text) {
        initializeComponent(text);
    }
    
    private void initializeComponent(String text) {
        // Configurar el HBox
        this.getStyleClass().add("bottom-bar");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(15, 15, 15, 15));
        
        // Label de información
        infoLabel = new Label(text);
        infoLabel.getStyleClass().add("bottom-text");
        this.getChildren().add(infoLabel);
    }
    
    public void setText(String text) {
        infoLabel.setText(text);
    }
    
    public Label getInfoLabel() {
        return infoLabel;
    }
}
