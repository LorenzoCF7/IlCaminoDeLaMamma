package ilcaminodelamamma.view.chef;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.DetalleComanda;
import ilcaminodelamamma.model.Receta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para detalle de comanda en vista Chef
 */
public class ComandaDetailController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private Button btnCerrar;
    @FXML private Label lblComandaTitle;
    @FXML private ComboBox<String> comboEstado;
    @FXML private VBox platosContainer;
    @FXML private Label lblMesa;
    @FXML private Label lblIdComanda;
    @FXML private Label lblFecha;
    @FXML private Button btnMarcarPreparacion;
    @FXML private Button btnMarcarTerminado;

    private Comanda comanda;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboEstado.getItems().addAll("Por hacer", "En preparación", "Terminada");
        comboEstado.setValue("Por hacer");

        btnVolver.setOnAction(e -> cerrar(null));
        btnCerrar.setOnAction(e -> cerrar(null));

        btnMarcarPreparacion.setOnAction(e -> {
            comboEstado.setValue("En preparación");
        });

        btnMarcarTerminado.setOnAction(e -> {
            comboEstado.setValue("Terminada");
        });
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
        if (comanda == null) return;

        lblIdComanda.setText("ID: " + comanda.getId_comanda());
        lblMesa.setText("Mesa: " + (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));
        lblComandaTitle.setText("Comanda - Mesa " + (comanda.getMesa() != null ? comanda.getMesa().getId_mesa() : "N/A"));

        if (comanda.getFecha_hora() != null) {
            lblFecha.setText("Fecha: " + comanda.getFecha_hora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            lblFecha.setText("Fecha: --");
        }

        // Cargar detalles (platos)
        platosContainer.getChildren().clear();
        Set<DetalleComanda> detalles = comanda.getDetalleComandas();
        if (detalles == null || detalles.isEmpty()) {
            Label empty = new Label("No hay platos en esta comanda");
            platosContainer.getChildren().add(empty);
        } else {
            for (DetalleComanda det : detalles) {
                HBox row = new HBox(10);
                row.setStyle("-fx-padding:8; -fx-alignment:center-left;");
                Receta receta = det.getReceta();
                String nombre = receta != null ? receta.getNombre() : "Plato desconocido";
                Label nameLabel = new Label(nombre + "  x" + (det.getCantidad() != null ? det.getCantidad() : 1));
                nameLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#2C1810; -fx-underline:true; -fx-cursor:hand;");
                nameLabel.setOnMouseClicked((MouseEvent me) -> {
                    // Abrir detalles de la receta
                    abrirDetallesReceta(receta);
                });

                Label qty = new Label("  Precio: " + (det.getPrecio_unitario() != null ? String.format("%.2f €", det.getPrecio_unitario()) : "--"));
                row.getChildren().addAll(nameLabel, qty);
                platosContainer.getChildren().add(row);
            }
        }
    }

    private void abrirDetallesReceta(Receta receta) {
        if (receta == null) return;
        System.out.println("DEBUG: Abrir detalles - receta=" + (receta.getNombre() != null ? receta.getNombre() : "<sin-nombre>"));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef/recipe-detail.fxml"));
            Parent root = loader.load();
            ilcaminodelamamma.view.chef.RecipeDetailController controller = loader.getController();
            System.out.println("DEBUG: Controller recipe-detail cargado: " + controller);
            controller.setReceta(receta);
            System.out.println("DEBUG: Receta pasada al controlador");

            // Obtener root de la escena
            if (btnCerrar.getScene() == null) {
                System.err.println("DEBUG: btnCerrar.getScene() es nulo, no se puede colocar el centro ahora");
                return;
            }
            Parent sceneRoot = btnCerrar.getScene().getRoot();
            if (!(sceneRoot instanceof javafx.scene.layout.BorderPane)) {
                System.err.println("DEBUG: sceneRoot no es BorderPane: " + sceneRoot);
                return;
            }

            javafx.scene.layout.BorderPane mainRoot = (javafx.scene.layout.BorderPane) sceneRoot;

            // Ejecutar en hilo de JavaFX y forzar re-layout; aplicar fallback si no se renderiza
            Platform.runLater(() -> {
                try {
                    mainRoot.setCenter(root);
                    try { root.applyCss(); root.autosize(); root.layout(); } catch (Exception ex) { /* ignore */ }
                    mainRoot.requestLayout();
                    System.out.println("DEBUG: setCenter ejecutado");

                    // Fallback: a los 150ms comprobar si el centro se ha establecido correctamente
                    Platform.runLater(() -> {
                        try {
                            boolean needFallback = false;
                            javafx.scene.Node currentCenter = mainRoot.getCenter();
                            if (currentCenter == null) needFallback = true;
                            else if (currentCenter instanceof javafx.scene.Parent) {
                                javafx.scene.Parent p = (javafx.scene.Parent) currentCenter;
                                if (p.getChildrenUnmodifiable().isEmpty()) needFallback = true;
                            }
                            if (needFallback) {
                                StackPane wrapper = new StackPane();
                                wrapper.getChildren().add(root);
                                mainRoot.setCenter(wrapper);
                                try { wrapper.applyCss(); wrapper.layout(); } catch (Exception ex) {}
                                System.out.println("DEBUG: Fallback aplicado: root envuelto en StackPane");
                            } else {
                                System.out.println("DEBUG: Centro correcto tras setCenter");
                            }
                        } catch (Exception ex) {
                            System.err.println("DEBUG: Error en comprobacion fallback: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    System.err.println("Error colocando detalle de receta: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.err.println("Error abriendo detalles receta desde comanda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar(javafx.event.ActionEvent ev) {
        try {
            // Cargar el FXML del Chef y colocar su centro en el BorderPane principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef/chef-view.fxml"));
            Parent chefRoot = loader.load();
            if (btnCerrar.getScene() != null) {
                javafx.scene.Parent sceneRoot = btnCerrar.getScene().getRoot();
                if (sceneRoot instanceof javafx.scene.layout.BorderPane && chefRoot instanceof javafx.scene.layout.BorderPane) {
                    javafx.scene.layout.BorderPane mainRoot = (javafx.scene.layout.BorderPane) sceneRoot;
                    javafx.scene.layout.BorderPane newChefRoot = (javafx.scene.layout.BorderPane) chefRoot;
                    mainRoot.setCenter(newChefRoot.getCenter());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al volver a la vista Chef: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
