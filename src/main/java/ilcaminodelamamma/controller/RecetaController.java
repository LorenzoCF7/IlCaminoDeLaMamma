package ilcaminodelamamma.controller;

import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.DAO.IngredienteDAO;
import ilcaminodelamamma.model.Receta;
import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.model.RecetaIngrediente;
import ilcaminodelamamma.model.RecetaIngredienteId;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecetaController {
    
    @FXML
    private StackPane stackImagen;
    
    @FXML
    private ImageView imgReceta;
    
    @FXML
    private Label lblIconoImagen;
    
    @FXML
    private ListView<String> lvIngredientes;
    
    @FXML
    private TextField tfNombre;
    
    @FXML
    private TextArea taDescripcion;
    
    @FXML
    private TextField tfTiempo;
    
    @FXML
    private ComboBox<String> cbCategoria;
    
    @FXML
    private TextArea taPasos;
    
    @FXML
    private Button btnAtras;
    
    @FXML
    private Button btnFin;
    
    @FXML
    private Button btnAgregar;
    
    @FXML
    private TableView<Receta> tvRecetas;
    
    @FXML
    private TableColumn<Receta, Integer> colId;
    
    @FXML
    private TableColumn<Receta, String> colNombre;
    
    @FXML
    private TableColumn<Receta, String> colDescripcion;
    
    @FXML
    private TableColumn<Receta, String> colCategoria;
    
    @FXML
    private TableColumn<Receta, Integer> colTiempo;
    
    @FXML
    private TableColumn<Receta, String> colAcciones;
    
    @FXML
    private Button btnRecargarRecetas;
    
    @FXML
    private TextField tfBuscarReceta;
    
    @FXML
    private Button btnBuscarReceta;
    
    private RecetaDAO recetaDAO;
    private IngredienteDAO ingredienteDAO;
    private Receta recetaActual;
    private ObservableList<String> ingredientesLista;
    private List<RecetaIngrediente> ingredientesSeleccionados;
    private byte[] imagenBytes;
    
    @FXML
    public void initialize() {
        recetaDAO = new RecetaDAO();
        ingredienteDAO = new IngredienteDAO();
        ingredientesLista = FXCollections.observableArrayList();
        ingredientesSeleccionados = new ArrayList<>();
        lvIngredientes.setItems(ingredientesLista);
        
        // Configurar categorías
        cbCategoria.setItems(FXCollections.observableArrayList(
            "Pasta", "Pizza", "Ensalada", "Postre", "Bebida", "Entrante", "Plato Principal"
        ));
        
        // Configurar tabla de recetas
        configureTablaRecetas();
        cargarRecetas();
        
        setupEventHandlers();
    }
    
    private void configureTablaRecetas() {
        // Configurar columnas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId_receta()).asObject());
        colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        colCategoria.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoria()));
        colTiempo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTiempo_preparacion()).asObject());
        
        // Columna de acciones con botones
        colAcciones.setCellFactory(column -> new TableCell<Receta, String>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            
            {
                btnEditar.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-radius: 3; -fx-background-radius: 3;");
                btnEliminar.setStyle("-fx-background-color: #DC5757; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-border-radius: 3; -fx-background-radius: 3;");
                
                btnEditar.setOnAction(e -> {
                    Receta receta = getTableView().getItems().get(getIndex());
                    cargarRecetaParaEditar(receta);
                });
                
                btnEliminar.setOnAction(e -> {
                    Receta receta = getTableView().getItems().get(getIndex());
                    eliminarReceta(receta);
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
    
    private void cargarRecetas() {
        List<Receta> recetas = recetaDAO.findAll();
        tvRecetas.setItems(FXCollections.observableArrayList(recetas));
    }
    
    private void buscarRecetas() {
        String nombreBusqueda = tfBuscarReceta.getText().trim();
        
        if (nombreBusqueda.isEmpty()) {
            cargarRecetas();
            return;
        }
        
        try {
            List<Receta> recetas = recetaDAO.findByNombre(nombreBusqueda);
            if (recetas.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron recetas con ese nombre", Alert.AlertType.INFORMATION);
                cargarRecetas();
            } else {
                tvRecetas.setItems(FXCollections.observableArrayList(recetas));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void cargarRecetaParaEditar(Receta receta) {
        tfNombre.setText(receta.getNombre());
        taDescripcion.setText(receta.getDescripcion());
        tfTiempo.setText(receta.getTiempo_preparacion().toString());
        cbCategoria.setValue(receta.getCategoria());
        taPasos.setText(receta.getPasos() != null ? receta.getPasos() : "");
        
        // Cargar ingredientes
        ingredientesSeleccionados.clear();
        ingredientesLista.clear();
        
        try {
            // Forzar la carga de los ingredientes si están presentes
            if (receta.getRecetaIngredientes() != null) {
                for (RecetaIngrediente ri : receta.getRecetaIngredientes()) {
                    if (ri != null && ri.getIngrediente() != null) {
                        // Crear una copia temporal para editar
                        RecetaIngrediente riTemp = new RecetaIngrediente();
                        riTemp.setIngrediente(ri.getIngrediente());
                        riTemp.setCantidad_usada(ri.getCantidad_usada());
                        ingredientesSeleccionados.add(riTemp);
                        String item = ri.getIngrediente().getNombre() + " - " + ri.getCantidad_usada() + " " + ri.getIngrediente().getUnidad_medida();
                        ingredientesLista.add(item);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando ingredientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Cargar imagen
        if (receta.getImagen() != null && receta.getImagen().length > 0) {
            imagenBytes = receta.getImagen();
            try {
                Image img = new Image(new ByteArrayInputStream(imagenBytes));
                imgReceta.setImage(img);
                lblIconoImagen.setVisible(false);
            } catch (Exception e) {
                System.out.println("Error cargando imagen: " + e.getMessage());
                lblIconoImagen.setVisible(true);
            }
        } else {
            imagenBytes = null;
            imgReceta.setImage(null);
            lblIconoImagen.setVisible(true);
        }
        
        recetaActual = receta;
        btnAgregar.setText("Actualizar Receta");
    }
    
    private void eliminarReceta(Receta receta) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Receta");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar la receta '" + receta.getNombre() + "'?");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                Integer recetaId = receta.getId_receta();
                recetaDAO.deleteById(recetaId);
                
                // Si la receta eliminada es la que se está editando, limpiar el formulario
                if (recetaActual != null && recetaActual.getId_receta().equals(recetaId)) {
                    limpiarFormulario();
                }
                
                cargarRecetas();
                mostrarAlerta("Éxito", "Receta eliminada correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar la receta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void setupEventHandlers() {
        // Click en el StackPane para cambiar la imagen
        stackImagen.setOnMouseClicked(e -> seleccionarImagen());
        
        // Botón Agregar para guardar
        btnAgregar.setOnAction(e -> guardarReceta());
        
        // Botón Recargar recetas
        btnRecargarRecetas.setOnAction(e -> cargarRecetas());
        
        // Botón Buscar recetas
        btnBuscarReceta.setOnAction(e -> buscarRecetas());
        
        // Botón Fin
        btnFin.setOnAction(e -> {
            // Aquí puedes cerrar la ventana o navegar a otra vista
            limpiarFormulario();
        });
        
        // Botón Atrás
        btnAtras.setOnAction(e -> limpiarFormulario());
    }
    
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de receta");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File archivo = fileChooser.showOpenDialog(imgReceta.getScene().getWindow());
        if (archivo != null) {
            try {
                // Verificar tamaño del archivo (máximo 1MB)
                if (archivo.length() > 1048576) { // 1MB en bytes
                    mostrarAlerta("Advertencia", "La imagen es muy grande. Por favor selecciona una imagen menor a 1MB.", Alert.AlertType.WARNING);
                    return;
                }
                
                Image imagen = new Image(archivo.toURI().toString());
                imgReceta.setImage(imagen);
                lblIconoImagen.setVisible(false); // Ocultar el ícono cuando hay imagen
                
                // Convertir imagen a bytes
                FileInputStream fis = new FileInputStream(archivo);
                imagenBytes = fis.readAllBytes();
                fis.close();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void agregarIngrediente() {
        // Crear diálogo para seleccionar ingrediente
        List<Ingrediente> todosIngredientes = ingredienteDAO.findAll();
        
        if (todosIngredientes.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay ingredientes disponibles. Agrega ingredientes primero.", Alert.AlertType.WARNING);
            return;
        }
        
        ChoiceDialog<Ingrediente> dialog = new ChoiceDialog<>(todosIngredientes.get(0), todosIngredientes);
        dialog.setTitle("Agregar Ingrediente");
        dialog.setHeaderText("Selecciona un ingrediente");
        dialog.setContentText("Ingrediente:");
        
        Optional<Ingrediente> resultado = dialog.showAndWait();
        resultado.ifPresent(ingrediente -> {
            // Pedir cantidad
            TextInputDialog cantidadDialog = new TextInputDialog();
            cantidadDialog.setTitle("Cantidad");
            cantidadDialog.setHeaderText("Ingresa la cantidad a usar");
            cantidadDialog.setContentText("Cantidad:");
            
            Optional<String> cantidadResult = cantidadDialog.showAndWait();
            cantidadResult.ifPresent(cantidad -> {
                try {
                    Integer cantidadInt = Integer.parseInt(cantidad);
                    // Crear RecetaIngrediente temporal (sin receta aún)
                    RecetaIngrediente ri = new RecetaIngrediente();
                    ri.setIngrediente(ingrediente);
                    ri.setCantidad_usada(cantidadInt);
                    ingredientesSeleccionados.add(ri);
                    // Mostrar en la lista visual
                    String item = ingrediente.getNombre() + " - " + cantidadInt + " " + ingrediente.getUnidad_medida();
                    ingredientesLista.add(item);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "Cantidad inválida", Alert.AlertType.ERROR);
                }
            });
        });
    }
    
    private void guardarReceta() {
        String nombre = tfNombre.getText().trim();
        String descripcion = taDescripcion.getText().trim();
        String tiempoStr = tfTiempo.getText().trim();
        String categoria = cbCategoria.getValue();
        String pasos = taPasos.getText().trim();
        
        if (nombre.isEmpty() || descripcion.isEmpty() || tiempoStr.isEmpty() || categoria == null || pasos.isEmpty() || ingredientesSeleccionados.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos obligatorios", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            Integer tiempo = Integer.parseInt(tiempoStr);
            
            // Si es edición (recetaActual != null)
            if (recetaActual != null) {
                byte[] imagenAGuardar = (imagenBytes != null && imagenBytes.length <= 1048576) ? imagenBytes : recetaActual.getImagen();
                
                recetaActual.setNombre(nombre);
                recetaActual.setDescripcion(descripcion);
                recetaActual.setTiempo_preparacion(tiempo);
                recetaActual.setCategoria(categoria);
                recetaActual.setPasos(pasos);
                recetaActual.setImagen(imagenAGuardar);
                
                // Limpiar ingredientes viejos
                recetaActual.getRecetaIngredientes().clear();
                
                // Agregar nuevos ingredientes
                for (RecetaIngrediente riTemp : ingredientesSeleccionados) {
                    RecetaIngredienteId id = new RecetaIngredienteId(
                        recetaActual.getId_receta(), 
                        riTemp.getIngrediente().getId_ingrediente()
                    );
                    
                    RecetaIngrediente ri = new RecetaIngrediente();
                    ri.setId(id);
                    ri.setReceta(recetaActual);
                    ri.setIngrediente(riTemp.getIngrediente());
                    ri.setCantidad_usada(riTemp.getCantidad_usada());
                    
                    recetaActual.getRecetaIngredientes().add(ri);
                }
                
                recetaDAO.update(recetaActual);
                mostrarAlerta("Éxito", "Receta actualizada correctamente", Alert.AlertType.INFORMATION);
                recetaActual = null;
                btnAgregar.setText("Agregar Receta");
            } else {
                // Crear nueva receta
                byte[] imagenAGuardar = (imagenBytes != null && imagenBytes.length <= 1048576) ? imagenBytes : null;
                
                Receta nuevaReceta = new Receta(nombre, descripcion, 0, tiempo, true, imagenAGuardar, categoria);
                nuevaReceta.setPasos(pasos);
                recetaDAO.create(nuevaReceta);
                
                // Guardar las relaciones con ingredientes
                for (RecetaIngrediente riTemp : ingredientesSeleccionados) {
                    RecetaIngredienteId id = new RecetaIngredienteId(
                        nuevaReceta.getId_receta(), 
                        riTemp.getIngrediente().getId_ingrediente()
                    );
                    
                    RecetaIngrediente ri = new RecetaIngrediente();
                    ri.setId(id);
                    ri.setReceta(nuevaReceta);
                    ri.setIngrediente(riTemp.getIngrediente());
                    ri.setCantidad_usada(riTemp.getCantidad_usada());
                    
                    nuevaReceta.getRecetaIngredientes().add(ri);
                }
                
                // Actualizar la receta con las relaciones
                if (!ingredientesSeleccionados.isEmpty()) {
                    recetaDAO.update(nuevaReceta);
                }
                
                mostrarAlerta("Éxito", "Receta guardada correctamente con " + ingredientesSeleccionados.size() + " ingredientes", Alert.AlertType.INFORMATION);
            }
            
            cargarRecetas();
            limpiarFormulario();
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Tiempo de preparación inválido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar la receta: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void eliminarIngrediente() {
        int indiceSeleccionado = lvIngredientes.getSelectionModel().getSelectedIndex();
        
        if (indiceSeleccionado < 0) {
            mostrarAlerta("Advertencia", "Selecciona un ingrediente para eliminar", Alert.AlertType.WARNING);
            return;
        }
        
        // Eliminar de ambas listas
        ingredientesLista.remove(indiceSeleccionado);
        ingredientesSeleccionados.remove(indiceSeleccionado);
    }
    
    private void limpiarFormulario() {
        tfNombre.clear();
        taDescripcion.clear();
        tfTiempo.clear();
        cbCategoria.setValue(null);
        taPasos.clear();
        ingredientesLista.clear();
        ingredientesSeleccionados.clear();
        imgReceta.setImage(null);
        lblIconoImagen.setVisible(true); // Mostrar el ícono de nuevo
        imagenBytes = null;
        recetaActual = null; // Limpiar la receta actual
        btnAgregar.setText("Agregar Receta"); // Resetear el texto del botón
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

