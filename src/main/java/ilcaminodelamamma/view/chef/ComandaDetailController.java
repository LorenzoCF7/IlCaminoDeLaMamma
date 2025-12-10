package ilcaminodelamamma.view.chef;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Set;

import ilcaminodelamamma.DAO.ComandaDAO;
import ilcaminodelamamma.DAO.DetalleComandaDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.Comanda.EstadoComanda;
import ilcaminodelamamma.model.DetalleComanda;
import ilcaminodelamamma.model.DetalleComanda.EstadoPlato;
import ilcaminodelamamma.model.Receta;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controlador para detalle de comanda en vista Chef - MEJORADO
 */
public class ComandaDetailController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private Button btnCerrar;
    @FXML private Label lblComandaTitle;
    @FXML private Label lblFechaHeader;
    @FXML private ComboBox<String> comboEstado;
    @FXML private VBox platosContainer;
    @FXML private Label lblMesa;
    @FXML private Label lblIdComanda;
    @FXML private Label lblFecha;
    @FXML private Label lblCamarero;
    @FXML private Label lblPorHacer;
    @FXML private Label lblEnCocina;
    @FXML private Label lblPreparados;
    @FXML private Label lblTotal;
    @FXML private Button btnEnProceso;
    @FXML private Button btnFinalizar;

    private Comanda comanda;
    private ComandaDAO comandaDAO;
    private DetalleComandaDAO detalleComandaDAO;
    private RecetaDAO recetaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comandaDAO = new ComandaDAO();
        detalleComandaDAO = new DetalleComandaDAO();
        recetaDAO = new RecetaDAO();
        
        // Configurar estados de comanda
        comboEstado.getItems().addAll("Por hacer", "En proceso", "Finalizada");
        comboEstado.setValue("Por hacer");
        
        // Listener para cambio de estado
        comboEstado.setOnAction(e -> {
            if (comanda != null) {
                String nuevoEstado = comboEstado.getValue();
                switch (nuevoEstado) {
                    case "Por hacer":
                        comanda.setEstadoComanda(EstadoComanda.POR_HACER);
                        break;
                    case "En proceso":
                        comanda.setEstadoComanda(EstadoComanda.EN_PROCESO);
                        break;
                    case "Finalizada":
                        comanda.setEstadoComanda(EstadoComanda.FINALIZADA);
                        break;
                }
                comandaDAO.update(comanda);
            }
        });

        btnVolver.setOnAction(e -> cerrar(null));
        btnCerrar.setOnAction(e -> cerrar(null));

        // Botón En Proceso
        if (btnEnProceso != null) {
            btnEnProceso.setOnAction(e -> {
                comboEstado.setValue("En proceso");
                if (comanda != null) {
                    comanda.setEstadoComanda(EstadoComanda.EN_PROCESO);
                    comandaDAO.update(comanda);
                }
            });
        }

        // Botón Finalizar
        if (btnFinalizar != null) {
            btnFinalizar.setOnAction(e -> {
                comboEstado.setValue("Finalizada");
                if (comanda != null) {
                    comanda.setEstadoComanda(EstadoComanda.FINALIZADA);
                    comandaDAO.update(comanda);
                    
                    // Mostrar popup de confirmación
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Comanda Finalizada");
                    alert.setHeaderText(null);
                    alert.setContentText("¡Comanda Finalizada!");
                    alert.showAndWait();
                    
                    // Redirigir al Libro de cocina
                    ChefViewController controller = ChefViewController.getInstance();
                    if (controller != null) {
                        controller.goBackToCategories();
                    }
                }
            });
        }
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
        if (comanda == null) return;

        // Info básica
        lblIdComanda.setText("#" + comanda.getId_comanda());
        String mesaNum = comanda.getMesa() != null ? String.valueOf(comanda.getMesa().getId_mesa()) : "N/A";
        lblMesa.setText("Mesa " + mesaNum);
        lblComandaTitle.setText("Comanda - Mesa " + mesaNum);

        if (comanda.getFecha_hora() != null) {
            String fechaFormateada = comanda.getFecha_hora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            lblFecha.setText(fechaFormateada);
            if (lblFechaHeader != null) {
                lblFechaHeader.setText(fechaFormateada);
            }
        } else {
            lblFecha.setText("--");
            if (lblFechaHeader != null) {
                lblFechaHeader.setText("--");
            }
        }
        
        // Camarero
        if (lblCamarero != null && comanda.getUsuario() != null) {
            lblCamarero.setText(comanda.getUsuario().getNombre());
        }
        
        // Estado de la comanda
        if (comanda.getEstadoComanda() != null) {
            comboEstado.setValue(comanda.getEstadoComanda().getDescripcion());
        }

        // Cargar detalles (platos) con estilo mejorado
        cargarPlatos();
        
        // Calcular totales
        calcularTotales();
    }
    
    private void cargarPlatos() {
        platosContainer.getChildren().clear();
        Set<DetalleComanda> detalles = comanda.getDetalleComandas();
        
        if (detalles == null || detalles.isEmpty()) {
            Label empty = new Label("📭 No hay platos en esta comanda");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #999; -fx-font-style: italic;");
            platosContainer.getChildren().add(empty);
            return;
        }
        
        for (DetalleComanda det : detalles) {
            VBox platoCard = crearTarjetaPlato(det);
            platosContainer.getChildren().add(platoCard);
        }
    }
    
    private VBox crearTarjetaPlato(DetalleComanda detalle) {
        VBox card = new VBox(8);
        card.getStyleClass().add("plato-item-card");
        card.setPadding(new Insets(12, 15, 12, 15));
        
        Receta receta = detalle.getReceta();
        String nombrePlato = receta != null ? receta.getNombre() : "Plato desconocido";
        int cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : 1;
        
        // Fila superior: nombre + precio
        HBox filaSuper = new HBox(10);
        filaSuper.setAlignment(Pos.CENTER_LEFT);
        
        // Nombre del plato (clickeable)
        Label nameLabel = new Label(nombrePlato);
        nameLabel.getStyleClass().add("plato-nombre");
        nameLabel.setOnMouseClicked((MouseEvent me) -> {
            if (receta != null) {
                abrirDetallesReceta(receta);
            }
        });
        
        // Cantidad
        Label cantidadLabel = new Label("x" + cantidad);
        cantidadLabel.getStyleClass().add("plato-cantidad");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Precio (convertir de céntimos a euros)
        Label precioLabel = new Label(detalle.getPrecio_unitario() != null ? 
            String.format("%.2f €", detalle.getPrecio_unitario() / 100.0) : "--");
        precioLabel.getStyleClass().add("plato-precio");
        
        filaSuper.getChildren().addAll(nameLabel, cantidadLabel, spacer, precioLabel);
        
        // Fila inferior: estado + botones de estado
        HBox filaEstado = new HBox(8);
        filaEstado.setAlignment(Pos.CENTER_LEFT);
        
        // Badge de estado actual
        EstadoPlato estadoActual = detalle.getEstadoPlato();
        Label estadoBadge = crearBadgeEstado(estadoActual);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        // Botones para cambiar estado
        Button btnPorHacer = new Button("Por hacer");
        btnPorHacer.getStyleClass().add("btn-estado-plato");
        if (estadoActual == EstadoPlato.POR_HACER) {
            btnPorHacer.getStyleClass().add("btn-estado-plato-activo");
        }
        
        Button btnEnCocina = new Button("En cocina");
        btnEnCocina.getStyleClass().add("btn-estado-plato");
        if (estadoActual == EstadoPlato.EN_COCINA) {
            btnEnCocina.getStyleClass().add("btn-estado-plato-activo");
        }
        
        Button btnPreparado = new Button("Preparado");
        btnPreparado.getStyleClass().add("btn-estado-plato");
        if (estadoActual == EstadoPlato.PREPARADO) {
            btnPreparado.getStyleClass().add("btn-estado-plato-activo");
        }
        
        // Acciones de botones
        btnPorHacer.setOnAction(e -> {
            detalle.setEstadoPlato(EstadoPlato.POR_HACER);
            detalleComandaDAO.update(detalle);
            cargarPlatos();
            calcularTotales();
        });
        
        btnEnCocina.setOnAction(e -> {
            detalle.setEstadoPlato(EstadoPlato.EN_COCINA);
            detalleComandaDAO.update(detalle);
            cargarPlatos();
            calcularTotales();
        });
        
        btnPreparado.setOnAction(e -> {
            detalle.setEstadoPlato(EstadoPlato.PREPARADO);
            detalleComandaDAO.update(detalle);
            cargarPlatos();
            calcularTotales();
        });
        
        filaEstado.getChildren().addAll(estadoBadge, spacer2, btnPorHacer, btnEnCocina, btnPreparado);
        
        card.getChildren().addAll(filaSuper, filaEstado);
        
        return card;
    }
    
    private Label crearBadgeEstado(EstadoPlato estado) {
        Label badge = new Label(estado.getDescripcion());
        badge.getStyleClass().add("estado-badge");
        
        switch (estado) {
            case POR_HACER:
                badge.getStyleClass().add("estado-por-hacer");
                break;
            case EN_COCINA:
                badge.getStyleClass().add("estado-en-cocina");
                break;
            case PREPARADO:
                badge.getStyleClass().add("estado-preparado");
                break;
        }
        
        return badge;
    }
    
    private void calcularTotales() {
        if (comanda == null || comanda.getDetalleComandas() == null) return;
        
        int porHacer = 0;
        int enCocina = 0;
        int preparados = 0;
        float total = 0;
        
        for (DetalleComanda det : comanda.getDetalleComandas()) {
            switch (det.getEstadoPlato()) {
                case POR_HACER:
                    porHacer++;
                    break;
                case EN_COCINA:
                    enCocina++;
                    break;
                case PREPARADO:
                    preparados++;
                    break;
            }
            
            if (det.getSubtotal() != null) {
                total += det.getSubtotal();
            } else if (det.getPrecio_unitario() != null && det.getCantidad() != null) {
                total += det.getPrecio_unitario() * det.getCantidad();
            }
        }
        
        if (lblPorHacer != null) lblPorHacer.setText(String.valueOf(porHacer));
        if (lblEnCocina != null) lblEnCocina.setText(String.valueOf(enCocina));
        if (lblPreparados != null) lblPreparados.setText(String.valueOf(preparados));
        // Convertir de céntimos a euros para mostrar
        if (lblTotal != null) lblTotal.setText(String.format("%.2f €", total / 100.0));
    }

    private void abrirDetallesReceta(Receta receta) {
        if (receta == null) {
            System.err.println("DEBUG: receta es null");
            return;
        }
        
        try {
            // Obtener el ID de forma segura (puede ser un proxy de Hibernate)
            Integer recetaId = null;
            String nombreReceta = null;
            
            try {
                recetaId = receta.getId_receta();
                nombreReceta = receta.getNombre();
            } catch (Exception e) {
                System.err.println("DEBUG: Error accediendo a la receta (posible LazyInitializationException): " + e.getMessage());
                return;
            }
            
            System.out.println("DEBUG: Abrir detalles - receta=" + (nombreReceta != null ? nombreReceta : "<sin-nombre>") + " id=" + recetaId);
            
            if (recetaId == null) {
                System.err.println("DEBUG: recetaId es null, no se puede cargar la receta");
                return;
            }
            
            // Usar el ChefViewController para mostrar detalles en el overlay
            ChefViewController chefController = ChefViewController.getInstance();
            if (chefController != null) {
                chefController.mostrarDetallesRecetaPorId(recetaId);
                System.out.println("DEBUG: Detalles mostrados via ChefViewController");
            } else {
                // Fallback: abrir en ventana separada
                System.out.println("DEBUG: ChefViewController no disponible, abriendo ventana...");
                abrirDetallesEnVentana(recetaId);
            }

        } catch (Exception e) {
            System.err.println("Error abriendo detalles receta desde comanda: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Abre detalles en ventana separada como fallback
     */
    private void abrirDetallesEnVentana(Integer recetaId) {
        try {
            Receta recetaCompleta = recetaDAO.findById(recetaId);
            if (recetaCompleta == null) {
                System.err.println("No se encontró la receta con ID: " + recetaId);
                return;
            }
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Crear vista simple de detalles
            VBox container = new VBox(15);
            container.setPadding(new javafx.geometry.Insets(20));
            container.setStyle("-fx-background-color: #F5F1ED;");
            
            Label titulo = new Label(recetaCompleta.getNombre());
            titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C1810;");
            
            Label categoria = new Label("Categoría: " + (recetaCompleta.getCategoria() != null ? recetaCompleta.getCategoria() : "N/A"));
            categoria.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            
            Label precio = new Label("Precio: " + (recetaCompleta.getPrecio() != null ? String.format("%.2f €", recetaCompleta.getPrecio() / 100.0) : "N/A"));
            precio.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            
            Label tiempo = new Label("Tiempo: " + (recetaCompleta.getTiempo_preparacion() != null ? recetaCompleta.getTiempo_preparacion() + " min" : "N/A"));
            tiempo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            
            Label desc = new Label(recetaCompleta.getDescripcion() != null ? recetaCompleta.getDescripcion() : "Sin descripción");
            desc.setWrapText(true);
            desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
            
            javafx.scene.control.Button btnCerrarVentana = new javafx.scene.control.Button("Cerrar");
            btnCerrarVentana.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 5;");
            btnCerrarVentana.setOnAction(e -> stage.close());
            
            container.getChildren().addAll(titulo, categoria, precio, tiempo, desc, btnCerrarVentana);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(container, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Detalles: " + recetaCompleta.getNombre());
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error abriendo ventana de detalles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar(javafx.event.ActionEvent ev) {
        try {
            // Volver a la vista principal del chef usando el ChefViewController
            ChefViewController chefController = ChefViewController.getInstance();
            if (chefController != null) {
                // Cerrar el overlay de detalles si está abierto
                chefController.cerrarDetallesReceta();
            }
            
            // Cargar el FXML del Chef y colocar su centro en el BorderPane principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chef/chef-view.fxml"));
            Parent chefRoot = loader.load();
            if (btnCerrar.getScene() != null) {
                javafx.scene.Parent sceneRoot = btnCerrar.getScene().getRoot();
                // Ahora el root es un StackPane que contiene un BorderPane
                if (sceneRoot instanceof javafx.scene.layout.StackPane) {
                    javafx.scene.layout.StackPane rootStack = (javafx.scene.layout.StackPane) sceneRoot;
                    // Buscar el BorderPane dentro del StackPane
                    for (javafx.scene.Node child : rootStack.getChildren()) {
                        if (child instanceof javafx.scene.layout.BorderPane) {
                            javafx.scene.layout.BorderPane mainRoot = (javafx.scene.layout.BorderPane) child;
                            if (chefRoot instanceof javafx.scene.layout.StackPane) {
                                // El nuevo chef-view también tiene StackPane como root
                                javafx.scene.layout.StackPane newStack = (javafx.scene.layout.StackPane) chefRoot;
                                for (javafx.scene.Node newChild : newStack.getChildren()) {
                                    if (newChild instanceof javafx.scene.layout.BorderPane) {
                                        javafx.scene.layout.BorderPane newChefBorder = (javafx.scene.layout.BorderPane) newChild;
                                        mainRoot.setCenter(newChefBorder.getCenter());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al volver a la vista Chef: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
