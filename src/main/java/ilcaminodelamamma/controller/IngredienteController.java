package ilcaminodelamamma.controller;

import java.util.List;
import java.util.Optional;

import ilcaminodelamamma.DAO.IngredienteDAO;
import ilcaminodelamamma.model.Ingrediente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;


public class IngredienteController {
    @FXML
    private TableView<Ingrediente> ingredienteTable;
    
    @FXML
    private TableColumn<Ingrediente, Integer> colId;
    
    @FXML
    private TableColumn<Ingrediente, String> colNombre;

    @FXML
    private TableColumn<Ingrediente, String> colUnidadMedida;
    
    @FXML
    private TableColumn<Ingrediente, String> colStock;
    
    @FXML
    private TableColumn<Ingrediente, String> colAcciones;
    
    @FXML
    private TextField tfNombre;

    @FXML
    private TextField tfUnidadMedida;
    
    @FXML
    private TextField tfStock;
    
    @FXML
    private TextField tfBuscar;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnListarTodos;
    
    @FXML
    private Button btnAtras;
    
    @FXML
    private Button btnFin;
    
    @FXML
    private Button btnNuevaReceta;
    
    @FXML
    private Button btnLibros;
    
    @FXML
    private Button btnComandas;
    
    @FXML
    private Button btnIngredientesSidebar;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnCerrarSesion;
    
    @FXML
    private TextField searchField;
    
    private IngredienteDAO ingredienteDAO;
    private ObservableList<Ingrediente> ingredientesObservable;
    private Ingrediente ingredienteActual;

    @FXML
    public void initialize() {
        ingredienteDAO = new IngredienteDAO();
        setupTableColumns();
        loadAllIngredientes();
        setupButtonHandlers();
        setupSidebarButtons();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id_ingrediente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUnidadMedida.setCellValueFactory(new PropertyValueFactory<>("unidad_medida"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("cantidad_stock"));
        configureTablaAcciones();
    }
    
    private void configureTablaAcciones() {
        colAcciones.setCellFactory(column -> new TableCell<Ingrediente, String>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            
            {
                btnEditar.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-radius: 3; -fx-background-radius: 3;");
                btnEliminar.setStyle("-fx-background-color: #DC5757; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-radius: 3; -fx-background-radius: 3;");
                
                btnEditar.setOnAction(e -> {
                    Ingrediente ingrediente = getTableView().getItems().get(getIndex());
                    cargarIngredienteParaEditar(ingrediente);
                });
                
                btnEliminar.setOnAction(e -> {
                    Ingrediente ingrediente = getTableView().getItems().get(getIndex());
                    eliminarIngredienteDirecto(ingrediente);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    hbox.setStyle("-fx-alignment: center;");
                    hbox.getChildren().addAll(btnEditar, btnEliminar);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void loadAllIngredientes() {
        List<Ingrediente> ingredientes = ingredienteDAO.findAll();
        ingredientesObservable = FXCollections.observableArrayList(ingredientes);
        ingredienteTable.setItems(ingredientesObservable);
    }

    private void setupButtonHandlers() {
        btnGuardar.setOnAction(e -> guardarIngrediente());
        btnBuscar.setOnAction(e -> buscarIngrediente());
        btnListarTodos.setOnAction(e -> loadAllIngredientes());
        btnAtras.setOnAction(e -> volverAVistaChef());
        btnFin.setOnAction(e -> volverAVistaChef());
    }

    private void guardarIngrediente() {
        String nombre = tfNombre.getText().trim();
        String unidadMedida = tfUnidadMedida.getText().trim();
        String stock = tfStock.getText().trim();

        if (nombre.isEmpty() || unidadMedida.isEmpty() || stock.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            
            // Si es edición (ingredienteActual != null)
            if (ingredienteActual != null) {
                ingredienteActual.setNombre(nombre);
                ingredienteActual.setUnidad_medida(unidadMedida);
                ingredienteActual.setCantidad_stock(stockValue);
                ingredienteDAO.update(ingredienteActual);
                mostrarAlerta("Éxito", "Ingrediente actualizado correctamente", Alert.AlertType.INFORMATION);
            } else {
                // Si es creación (ingredienteActual == null)
                Ingrediente nuevoIngrediente = new Ingrediente(nombre, unidadMedida, stockValue);
                ingredienteDAO.create(nuevoIngrediente);
                mostrarAlerta("Éxito", "Ingrediente agregado correctamente", Alert.AlertType.INFORMATION);
            }
            
            limpiarFormulario();
            ingredienteActual = null;
            loadAllIngredientes();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El stock debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el ingrediente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarIngrediente() {
        if (ingredienteActual == null) {
            mostrarAlerta("Error", "Por favor selecciona un ingrediente de la tabla", Alert.AlertType.ERROR);
            return;
        }

        eliminarIngredienteDirecto(ingredienteActual);
    }
    
    private void eliminarIngredienteDirecto(Ingrediente ingrediente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("🗑️ Eliminar Ingrediente");
        confirmacion.setHeaderText("¿Eliminar '" + ingrediente.getNombre() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer. El ingrediente se eliminará permanentemente.");
        
        // Estilizar diálogo
        confirmacion.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
        
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) confirmacion.getDialogPane().lookupButton(ButtonType.OK);
            if (okButton != null) {
                okButton.setText("✓ Sí, eliminar");
                okButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
            }
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) confirmacion.getDialogPane().lookupButton(ButtonType.CANCEL);
            if (cancelButton != null) {
                cancelButton.setText("✗ Cancelar");
                cancelButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
            }
        });

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                ingredienteDAO.deleteById(ingrediente.getId_ingrediente());
                mostrarAlerta("Éxito", "Ingrediente eliminado correctamente", Alert.AlertType.INFORMATION);
                if (ingredienteActual != null && ingrediente.getId_ingrediente().equals(ingredienteActual.getId_ingrediente())) {
                    limpiarFormulario();
                }
                loadAllIngredientes();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el ingrediente: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void buscarIngrediente() {
        String nombreBusqueda = tfBuscar.getText().trim();

        if (nombreBusqueda.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa un nombre para buscar", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<Ingrediente> ingredientes = ingredienteDAO.findByNombre(nombreBusqueda);
            if (ingredientes.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron ingredientes con ese nombre", Alert.AlertType.INFORMATION);
            } else {
                ingredientesObservable = FXCollections.observableArrayList(ingredientes);
                ingredienteTable.setItems(ingredientesObservable);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario() {
        tfNombre.clear();
        tfUnidadMedida.clear();
        tfStock.clear();
        tfBuscar.clear();
        ingredienteTable.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar");
        ingredienteActual = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        
        // Emojis y colores según el tipo
        final String emoji;
        final String colorHeader;
        
        switch (tipo) {
            case INFORMATION:
                emoji = "✓ ";
                colorHeader = "#4CAF50";
                break;
            case WARNING:
                emoji = "⚠ ";
                colorHeader = "#FF9800";
                break;
            case ERROR:
                emoji = "✗ ";
                colorHeader = "#D32F2F";
                break;
            case CONFIRMATION:
                emoji = "❓ ";
                colorHeader = "#2196F3";
                break;
            default:
                emoji = "";
                colorHeader = "#8B7355";
                break;
        }
        
        alerta.setTitle(emoji + titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        
        // Estilizar el diálogo
        alerta.getDialogPane().setStyle(
            "-fx-background-color: #FAF8F5; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 13px;"
        );
        
        // Estilizar header y botones
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Button okButton = (javafx.scene.control.Button) alerta.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
            if (okButton != null) {
                okButton.setStyle(
                    "-fx-background-color: " + colorHeader + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 10px 20px; " +
                    "-fx-background-radius: 5px;"
                );
            }
            
            javafx.scene.control.Button cancelButton = (javafx.scene.control.Button) alerta.getDialogPane().lookupButton(javafx.scene.control.ButtonType.CANCEL);
            if (cancelButton != null) {
                cancelButton.setStyle(
                    "-fx-background-color: #cccccc; " +
                    "-fx-text-fill: #333333; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 10px 20px; " +
                    "-fx-background-radius: 5px;"
                );
            }
        });
        
        alerta.showAndWait();
    }

    private void cargarIngredienteParaEditar(Ingrediente ingrediente) {
        ingredienteActual = ingrediente;
        tfNombre.setText(ingrediente.getNombre());
        tfUnidadMedida.setText(ingrediente.getUnidad_medida());
        tfStock.setText(String.valueOf(ingrediente.getCantidad_stock()));
        ingredienteTable.getSelectionModel().select(ingrediente);
        btnGuardar.setText("Actualizar");
    }
    
    @FXML
    private void cargarDatosSeleccionados() {
        Ingrediente ingredienteSeleccionado = ingredienteTable.getSelectionModel().getSelectedItem();
        if (ingredienteSeleccionado != null) {
            cargarIngredienteParaEditar(ingredienteSeleccionado);
        }
    }
    
    /**
     * Configura los botones del sidebar para navegación
     */
    private void setupSidebarButtons() {
        if (btnLibros != null) {
            btnLibros.setOnAction(e -> volverAVistaChef());
        }
        
        if (btnComandas != null) {
            btnComandas.setOnAction(e -> {
                System.out.println("Navegando a comandas...");
                // Por implementar
            });
        }
        
        if (btnNuevaReceta != null) {
            btnNuevaReceta.setOnAction(e -> abrirNuevaReceta());
        }
        
        if (btnConfiguracion != null) {
            btnConfiguracion.setOnAction(e -> {
                System.out.println("Abriendo configuración...");
                // Por implementar
            });
        }
        
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> cerrarSesion());
        }
        
        if (btnIngredientesSidebar != null) {
            btnIngredientesSidebar.setOnAction(e -> limpiarFormulario());
        }
    }
    
    /**
     * Vuelve a la vista principal del Chef
     */
    private void volverAVistaChef() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnAtras.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/chef/chef-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1200, 700);
            stage.setScene(scene);
            stage.setTitle("Il Camino Della Mamma - Chef");
            stage.centerOnScreen();
            
            System.out.println("Volviendo a vista Chef");
        } catch (Exception e) {
            System.err.println("Error al volver a vista Chef: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Abre la vista de crear nueva receta
     */
    private void abrirNuevaReceta() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnNuevaReceta.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/receta.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
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
     * Cierra la sesión actual y vuelve a la pantalla de login
     */
    private void cerrarSesion() {
        try {
            System.out.println("Cerrando sesión...");
            
            javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/login/login.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 700, 550);
            stage.setScene(scene);
            stage.setTitle("Il Camino Della Mamma - Login");
            stage.centerOnScreen();
            
            System.out.println("Sesión cerrada correctamente");
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
