package ilcaminodelamamma.controller;

import java.util.Optional;

import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.DAO.IngredienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;


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
    
    private IngredienteDAO ingredienteDAO;
    private ObservableList<Ingrediente> ingredientesObservable;
    private Ingrediente ingredienteActual;

    @FXML
    public void initialize() {
        ingredienteDAO = new IngredienteDAO();
        setupTableColumns();
        loadAllIngredientes();
        setupButtonHandlers();
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
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este ingrediente?");
        confirmacion.setContentText("Se eliminará: " + ingrediente.getNombre());

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
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
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
}
