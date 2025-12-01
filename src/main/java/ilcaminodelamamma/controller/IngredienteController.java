package ilcaminodelamamma.controller;

import java.util.Optional;

import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.DAO.IngredienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TextField tfNombre;

    @FXML
    private TextField tfUnidadMedida;
    
    @FXML
    private TextField tfStock;
    
    @FXML
    private TextField tfBuscar;
    
    @FXML
    private Button btnAgregar;
    
    @FXML
    private Button btnModificar;
    
    @FXML
    private Button btnEliminar;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpiar;
    
    @FXML
    private Button btnListarTodos;
    
    private IngredienteDAO ingredienteDAO;
    private ObservableList<Ingrediente> ingredientesObservable;

    @FXML
    public void initialize() {
        ingredienteDAO = new IngredienteDAO();
        setupTableColumns();
        loadAllIngredientes();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUnidadMedida.setCellValueFactory(new PropertyValueFactory<>("unidad_medida"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // Agregar listener para cargar datos cuando se selecciona un ingrediente
        ingredienteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void loadAllIngredientes() {
        List<Ingrediente> ingredientes = ingredienteDAO.findAll();
        ingredientesObservable = FXCollections.observableArrayList(ingredientes);
        ingredienteTable.setItems(ingredientesObservable);
    }

    private void setupButtonHandlers() {
        btnAgregar.setOnAction(e -> agregarIngrediente());
        btnModificar.setOnAction(e -> modificarIngrediente());
        btnEliminar.setOnAction(e -> eliminarIngrediente());
        btnBuscar.setOnAction(e -> buscarIngrediente());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnListarTodos.setOnAction(e -> loadAllIngredientes());
    }

    @FXML
    private void agregarIngrediente() {
        String nombre = tfNombre.getText().trim();
        String unidadMedida = tfUnidadMedida.getText().trim();
        String stock = tfStock.getText().trim();

        if (nombre.isEmpty() || unidadMedida.isEmpty() || stock.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            Ingrediente nuevoIngrediente = new Ingrediente(nombre, unidadMedida, stockValue);
            ingredienteDAO.create(nuevoIngrediente);
            mostrarAlerta("Éxito", "Ingrediente agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllIngredientes();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el ingrediente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarIngrediente() {
        Ingrediente ingredienteSeleccionado = ingredienteTable.getSelectionModel().getSelectedItem();

        if (ingredienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un ingrediente de la tabla", Alert.AlertType.ERROR);
            return;
        }

        String nombre = tfNombre.getText().trim();
        String unidadMedida = tfUnidadMedida.getText().trim();
        String stock = tfStock.getText().trim();

        if (nombre.isEmpty() || unidadMedida.isEmpty() || stock.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            ingredienteSeleccionado.setNombre(nombre);
            ingredienteSeleccionado.setUnidad_medida(unidadMedida);
            ingredienteSeleccionado.setCantidad_stock(Integer.parseInt(stock));
            ingredienteDAO.update(ingredienteSeleccionado);
            mostrarAlerta("Éxito", "Ingrediente modificado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllIngredientes();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo modificar el ingrediente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarIngrediente() {
        Ingrediente ingredienteSeleccionado = ingredienteTable.getSelectionModel().getSelectedItem();

        if (ingredienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un ingrediente de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este ingrediente?");
        confirmacion.setContentText("Se eliminará: " + ingredienteSeleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                ingredienteDAO.deleteById(ingredienteSeleccionado.getId_ingrediente());
                mostrarAlerta("Éxito", "Ingrediente eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
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
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cargarDatosSeleccionados() {
        Ingrediente ingredienteSeleccionado = ingredienteTable.getSelectionModel().getSelectedItem();
        if (ingredienteSeleccionado != null) {
            tfNombre.setText(ingredienteSeleccionado.getNombre());
            tfUnidadMedida.setText(ingredienteSeleccionado.getUnidad_medida());
            tfStock.setText(String.valueOf(ingredienteSeleccionado.getCantidad_stock()));
        }
    }
}
