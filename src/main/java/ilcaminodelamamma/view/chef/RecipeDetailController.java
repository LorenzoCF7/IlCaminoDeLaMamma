package ilcaminodelamamma.view.chef;

import java.io.ByteArrayInputStream;

import ilcaminodelamamma.model.Receta;
import ilcaminodelamamma.model.RecetaIngrediente;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para la vista de detalles de una receta
 * Muestra información completa de forma visual y atractiva
 */
public class RecipeDetailController {

    @FXML private ImageView recipeImage;
    @FXML private Label recipeName;
    @FXML private Label categoryLabel;
    @FXML private Label timeLabel;
    @FXML private Label priceLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label availabilityLabel;
    @FXML private VBox ingredientsBox;
    @FXML private VBox stepsBox;
    @FXML private VBox descriptionBox;
    @FXML private HBox priceBox;
    @FXML private Button closeButton;

    private Receta receta;

    @FXML
    public void initialize() {
        // Configurar botón de cerrar
        if (closeButton != null) {
            closeButton.setOnAction(e -> cerrarVentana());
            closeButton.setOnMouseEntered(e -> 
                closeButton.setStyle("-fx-background-color: rgba(255,0,0,0.7); -fx-text-fill: white; -fx-font-size: 20px; -fx-background-radius: 25; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;")
            );
            closeButton.setOnMouseExited(e -> 
                closeButton.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-size: 20px; -fx-background-radius: 25; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;")
            );
        }
    }

    /**
     * Carga los datos de la receta en la vista
     */
    public void setReceta(Receta receta) {
        this.receta = receta;
        
        if (receta == null) {
            return;
        }

        // Nombre
        if (recipeName != null) {
            recipeName.setText(receta.getNombre());
        }

        // Categoría
        if (categoryLabel != null) {
            categoryLabel.setText(receta.getCategoria() != null ? receta.getCategoria() : "Sin categoría");
        }

        // Tiempo de preparación
        if (timeLabel != null) {
            if (receta.getTiempo_preparacion() != null && receta.getTiempo_preparacion() > 0) {
                timeLabel.setText(receta.getTiempo_preparacion() + " min");
            } else {
                timeLabel.setText("No especificado");
            }
        }

        // Precio
        if (priceLabel != null && priceBox != null) {
            if (receta.getPrecio() != null && receta.getPrecio() > 0) {
                priceLabel.setText(String.format("%.2f €", receta.getPrecio() / 100.0));
            } else {
                priceBox.setVisible(false);
                priceBox.setManaged(false);
            }
        }

        // Descripción
        if (descriptionLabel != null && descriptionBox != null) {
            if (receta.getDescripcion() != null && !receta.getDescripcion().trim().isEmpty()) {
                descriptionLabel.setText(receta.getDescripcion());
            } else {
                descriptionBox.setVisible(false);
                descriptionBox.setManaged(false);
            }
        }

        // Disponibilidad
        if (availabilityLabel != null) {
            if (receta.getDisponible() != null && receta.getDisponible()) {
                availabilityLabel.setText("✅ Disponible para servir");
                availabilityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            } else {
                availabilityLabel.setText("❌ No disponible actualmente");
                availabilityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #C62828; -fx-font-weight: bold;");
            }
        }

        // Imagen
        if (recipeImage != null) {
            if (receta.getImagen() != null && receta.getImagen().length > 0) {
                try {
                    Image img = new Image(new ByteArrayInputStream(receta.getImagen()));
                    recipeImage.setImage(img);
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen de receta: " + e.getMessage());
                    cargarImagenPorDefecto();
                }
            } else {
                cargarImagenPorDefecto();
            }
        }

        // Ingredientes
        cargarIngredientes();

        // Pasos de preparación
        cargarPasos();
    }

    /**
     * Carga los ingredientes de la receta
     */
    private void cargarIngredientes() {
        if (ingredientsBox == null) return;
        
        ingredientsBox.getChildren().clear();

        if (receta.getRecetaIngredientes() == null || receta.getRecetaIngredientes().isEmpty()) {
            Label noIngredientes = new Label("No se han especificado ingredientes");
            noIngredientes.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; -fx-font-style: italic;");
            ingredientsBox.getChildren().add(noIngredientes);
            return;
        }

        for (RecetaIngrediente ri : receta.getRecetaIngredientes()) {
            if (ri.getIngrediente() == null) continue;
            
            HBox ingredienteItem = new HBox(10);
            ingredienteItem.setStyle("-fx-alignment: center-left; -fx-padding: 8 0;");
            
            // Bullet point
            Label bullet = new Label("•");
            bullet.setStyle("-fx-font-size: 16px; -fx-text-fill: #8B7355; -fx-font-weight: bold;");
            
            // Nombre del ingrediente
            Label nombreLabel = new Label(ri.getIngrediente().getNombre());
            nombreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C1810;");
            nombreLabel.setWrapText(true);
            
            // Cantidad
            if (ri.getCantidad_usada() != null && ri.getCantidad_usada() > 0) {
                String unidad = ri.getIngrediente().getUnidad_medida() != null ? 
                    ri.getIngrediente().getUnidad_medida() : "";
                Label cantidadLabel = new Label("(" + ri.getCantidad_usada() + " " + unidad + ")");
                cantidadLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");
                
                ingredienteItem.getChildren().addAll(bullet, nombreLabel, cantidadLabel);
            } else {
                ingredienteItem.getChildren().addAll(bullet, nombreLabel);
            }
            
            ingredientsBox.getChildren().add(ingredienteItem);
        }
    }

    /**
     * Carga los pasos de preparación
     */
    private void cargarPasos() {
        if (stepsBox == null) return;
        
        stepsBox.getChildren().clear();

        String pasos = receta.getPasos();
        
        if (pasos == null || pasos.trim().isEmpty()) {
            Label noPasos = new Label("No se han especificado pasos de preparación");
            noPasos.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; -fx-font-style: italic;");
            stepsBox.getChildren().add(noPasos);
            return;
        }

        // Dividir por líneas y procesar cada paso
        String[] lineas = pasos.split("\n");
        int numeroStep = 1;
        
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            
            // Crear contenedor para cada paso
            HBox pasoContainer = new HBox(12);
            pasoContainer.setStyle("-fx-alignment: top-left; -fx-padding: 10; -fx-background-color: #F8F6F3; -fx-background-radius: 8;");
            
            // Número del paso
            Label numeroLabel = new Label(String.valueOf(numeroStep));
            numeroLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; " +
                               "-fx-background-color: #8B7355; -fx-min-width: 32; -fx-min-height: 32; " +
                               "-fx-alignment: center; -fx-background-radius: 16;");
            numeroLabel.setPadding(new Insets(5));
            
            // Texto del paso (eliminar número si ya viene en el texto)
            String textoLimpio = linea.replaceFirst("^\\d+\\.?\\s*", "");
            Label textoLabel = new Label(textoLimpio);
            textoLabel.setWrapText(true);
            textoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C1810; -fx-line-spacing: 2px;");
            textoLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(textoLabel, javafx.scene.layout.Priority.ALWAYS);
            
            pasoContainer.getChildren().addAll(numeroLabel, textoLabel);
            stepsBox.getChildren().add(pasoContainer);
            
            numeroStep++;
        }
    }

    /**
     * Carga imagen por defecto según categoría
     */
    private void cargarImagenPorDefecto() {
        try {
            String imagePath = "/img/placeholder.png";
            
            // Intentar cargar imagen según categoría
            if (receta.getCategoria() != null) {
                String categoryImagePath = "/img/categorias/" + receta.getCategoria() + ".jpg";
                
                try {
                    Image categoryImage = new Image(getClass().getResourceAsStream(categoryImagePath));
                    if (!categoryImage.isError()) {
                        recipeImage.setImage(categoryImage);
                        return;
                    }
                } catch (Exception e) {
                    // Continuar con placeholder
                }
            }
            
            // Cargar placeholder por defecto
            Image placeholder = new Image(getClass().getResourceAsStream(imagePath));
            recipeImage.setImage(placeholder);
        } catch (Exception e) {
            System.err.println("Error al cargar imagen por defecto: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana modal
     */
    private void cerrarVentana() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
