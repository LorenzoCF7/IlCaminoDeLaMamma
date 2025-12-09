package ilcaminodelamamma.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ilcaminodelamamma.DAO.IngredienteDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.model.Receta;
import ilcaminodelamamma.model.RecetaIngrediente;
import ilcaminodelamamma.model.RecetaIngredienteId;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

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
    private TextField tfPrecio;
    
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
    
    @FXML
    private Button btnNuevaRecetaSidebar;
    
    @FXML
    private Button btnLibros;
    
    @FXML
    private Button btnComandas;
    
    @FXML
    private Button btnIngredientes;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnCerrarSesion;
    
    @FXML
    private TextField searchField;
    
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
            "Entrantes", "Pasta", "Pizza", "Pescados", "Carnes", "Postres", "Vinos", "Menú Infantil"
        ));
        
        // Configurar tabla de recetas
        configureTablaRecetas();
        cargarRecetas();
        
        setupEventHandlers();
        setupSidebarButtons();
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
        
        // Convertir precio de centimos a euros para mostrar
        double precioEuros = receta.getPrecio() != null ? receta.getPrecio() / 100.0 : 0.0;
        tfPrecio.setText(String.format("%.2f", precioEuros));
        
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
        confirmacion.setTitle("🗑️ Eliminar Receta");
        confirmacion.setHeaderText("¿Eliminar la receta '" + receta.getNombre() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer. Todos los ingredientes y pasos se perderán.");
        
        // Estilizar diálogo de confirmación
        confirmacion.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
        
        javafx.application.Platform.runLater(() -> {
            Button okButton = (Button) confirmacion.getDialogPane().lookupButton(ButtonType.OK);
            if (okButton != null) {
                okButton.setText("✓ Sí, eliminar");
                okButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
            }
            
            Button cancelButton = (Button) confirmacion.getDialogPane().lookupButton(ButtonType.CANCEL);
            if (cancelButton != null) {
                cancelButton.setText("✗ Cancelar");
                cancelButton.setStyle("-fx-background-color: #8B7355; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
            }
        });
        
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
        
        // Botón Fin - Volver a vista Chef
        btnFin.setOnAction(e -> volverAVistaChef());
        
        // Botón Atrás - Volver a vista Chef
        btnAtras.setOnAction(e -> volverAVistaChef());
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
                // Cargar y comprimir la imagen
                Image imagenOriginal = new Image(archivo.toURI().toString());
                imgReceta.setImage(imagenOriginal);
                lblIconoImagen.setVisible(false);
                
                // Comprimir imagen a bytes
                imagenBytes = comprimirImagen(imagenOriginal);
                
                // Verificar tamaño final (máximo 500KB para MySQL)
                if (imagenBytes != null && imagenBytes.length > 512000) {
                    mostrarAlerta("Advertencia", "La imagen es muy grande incluso después de comprimir. Intenta con una imagen más pequeña.", Alert.AlertType.WARNING);
                    imagenBytes = null;
                    return;
                }
                
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    /**
     * Comprime una imagen a formato JPEG con calidad reducida
     */
    private byte[] comprimirImagen(Image imagen) {
        try {
            // Crear BufferedImage redimensionado si es necesario
            int maxWidth = 800;
            int maxHeight = 600;
            
            double width = imagen.getWidth();
            double height = imagen.getHeight();
            
            // Calcular nueva dimensión manteniendo proporción
            if (width > maxWidth || height > maxHeight) {
                double ratio = Math.min(maxWidth / width, maxHeight / height);
                width *= ratio;
                height *= ratio;
            }
            
            // Redimensionar imagen
            javafx.scene.image.ImageView tempView = new javafx.scene.image.ImageView(imagen);
            tempView.setFitWidth(width);
            tempView.setFitHeight(height);
            tempView.setPreserveRatio(true);
            tempView.setSmooth(true);
            
            // Crear snapshot
            javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage((int)width, (int)height);
            javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT);
            tempView.snapshot(params, writableImage);
            
            // Convertir a BufferedImage
            java.awt.image.BufferedImage buffered = javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null);
            
            // Comprimir como JPEG
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(buffered, "jpg", baos);
            baos.flush();
            byte[] comprimido = baos.toByteArray();
            baos.close();
            
            System.out.println("✓ Imagen comprimida: " + comprimido.length + " bytes");
            return comprimido;
            
        } catch (Exception e) {
            System.err.println("Error comprimiendo imagen: " + e.getMessage());
            return null;
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
        String precioStr = tfPrecio.getText().trim();
        String categoria = cbCategoria.getValue();
        String pasos = taPasos.getText().trim();
        
        if (nombre.isEmpty() || descripcion.isEmpty() || tiempoStr.isEmpty() || precioStr.isEmpty() || categoria == null || pasos.isEmpty() || ingredientesSeleccionados.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos obligatorios", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            Integer tiempo = Integer.parseInt(tiempoStr);
            
            // Convertir precio de euros a centimos (multiplicar por 100)
            double precioEuros = Double.parseDouble(precioStr.replace(",", "."));
            Integer precioCentimos = (int) Math.round(precioEuros * 100);
            
            // Si es edición (recetaActual != null)
            if (recetaActual != null) {
                byte[] imagenAGuardar = (imagenBytes != null && imagenBytes.length <= 1048576) ? imagenBytes : recetaActual.getImagen();
                
                recetaActual.setNombre(nombre);
                recetaActual.setDescripcion(descripcion);
                recetaActual.setTiempo_preparacion(tiempo);
                recetaActual.setPrecio(precioCentimos);
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
                
                Receta nuevaReceta = new Receta(nombre, descripcion, precioCentimos, tiempo, true, imagenAGuardar, categoria);
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
            mostrarAlerta("Error", "Tiempo de preparación o precio inválido. El precio debe ser un número (Ej: 12.50)", Alert.AlertType.ERROR);
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
        tfPrecio.clear();
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
        
        // Estilizar header
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node header = alerta.getDialogPane().lookup(".header-panel");
            if (header != null) {
                header.setStyle("-fx-background-color: " + colorHeader + ";");
            }
            
            javafx.scene.Node headerLabel = alerta.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle(
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold;"
                );
            }
            
            // Estilizar botón OK
            Button okButton = (Button) alerta.getDialogPane().lookupButton(ButtonType.OK);
            if (okButton != null) {
                okButton.setStyle(
                    "-fx-background-color: " + colorHeader + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 10px 20px; " +
                    "-fx-background-radius: 5px;"
                );
            }
            
            // Estilizar botón CANCEL si existe
            Button cancelButton = (Button) alerta.getDialogPane().lookupButton(ButtonType.CANCEL);
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
        
        if (btnIngredientes != null) {
            btnIngredientes.setOnAction(e -> abrirIngredientes());
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
        
        if (btnNuevaRecetaSidebar != null) {
            btnNuevaRecetaSidebar.setOnAction(e -> limpiarFormulario());
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
     * Abre la vista de gestión de ingredientes
     */
    private void abrirIngredientes() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) btnIngredientes.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/ingrediente.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Gestión de Ingredientes - Il Camino Della Mamma");
            stage.centerOnScreen();
            
            System.out.println("Vista de ingredientes cargada");
        } catch (Exception e) {
            System.err.println("Error al cargar vista de ingredientes: " + e.getMessage());
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

