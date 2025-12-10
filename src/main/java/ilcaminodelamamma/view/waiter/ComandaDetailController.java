package ilcaminodelamamma.view.waiter;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import ilcaminodelamamma.DAO.ComandaDAO;
import ilcaminodelamamma.DAO.MesaDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.DetalleComanda;
import ilcaminodelamamma.model.EstadoMesa;
import ilcaminodelamamma.model.Mesa;
import ilcaminodelamamma.model.Receta;
import ilcaminodelamamma.service.TicketPdfService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para el detalle de una comanda
 */
public class ComandaDetailController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private Label lblComandaTitle;
    @FXML private Label lblEstadoMesa;
    @FXML private TextField searchField;
    @FXML private HBox categoriesBox;
    @FXML private VBox platosContainer;
    @FXML private ComboBox<String> comboMesa;
    @FXML private ComboBox<String> comboEstado;
    @FXML private VBox platosComandaContainer;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotal;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGenerarTicket;

    // Datos
    private final Map<String, List<PlatoItem>> platosPorCategoria = new LinkedHashMap<>();
    private final Map<String, PlatoEnComanda> platosEnComanda = new HashMap<>(); // Key: nombre del plato
    private String categoriaActual = "Todos";
    private int mesaNumber = 5;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private RecetaDAO recetaDAO;
    
    private static final double IVA = 0.10; // 10% IVA
    
    // Mapeo de categorías BD → UI
    private final Map<String, String> categoryMapping = new HashMap<String, String>() {{
        put("Entrante", "Entrantes");
        put("Pasta", "Pasta");
        put("Pizza", "Pizza");
        put("Pescado", "Pescados");
        put("Carne", "Carnes");
        put("Postre", "Postres");
        put("Vino", "Vinos");
        put("Menu Infantil", "Menú Infantil");
    }};
    
    /**
     * Clase para representar un plato en la comanda con cantidad y nota
     */
    private static class PlatoEnComanda {
        PlatoItem plato;
        int cantidad;
        String nota;
        
        PlatoEnComanda(PlatoItem plato, int cantidad, String nota) {
            this.plato = plato;
            this.cantidad = cantidad;
            this.nota = nota != null ? nota : "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Comanda Detail inicializada");
        
        // Inicializar DAO
        recetaDAO = new RecetaDAO();
        
        // Inicializar platos cargándolos dinámicamente desde la BD
        inicializarPlatosDesdeDB();
        
        // Configurar combos
        configurarCombos();
        
        // Configurar categorías
        configurarCategorias();
        
        // Mostrar todos los platos inicialmente
        mostrarPlatos("Todos");
        
        // Configurar búsqueda
        searchField.textProperty().addListener((obs, oldVal, newVal) -> buscarPlatos(newVal));
        
        // Actualizar título
        lblComandaTitle.setText("Comanda - Mesa #" + mesaNumber);
    }
    
    /**
     * Establece el número de mesa (llamado desde WaiterViewController)
     */
    public void setMesaNumber(int numero) {
        this.mesaNumber = numero;
        lblComandaTitle.setText("Comanda - Mesa #" + mesaNumber);
        comboMesa.setValue(String.valueOf(mesaNumber));
        
        // Deshabilitar el combo de mesa (no se puede cambiar la mesa)
        comboMesa.setDisable(true);
        
        // Cargar comanda existente si la hay
        cargarComandaExistente();
    }
    
    /**
     * Carga una comanda existente desde el almacenamiento
     */
    private void cargarComandaExistente() {
        WaiterViewController.ComandaData comandaData = 
            WaiterViewController.COMANDAS_ACTIVAS.get(mesaNumber);
        
        if (comandaData != null) {
            System.out.println("Cargando comanda existente de Mesa " + mesaNumber);
            
            // Cargar platos de la comanda
            for (WaiterViewController.ComandaData.PlatoComanda platoData : comandaData.platos.values()) {
                // Buscar el plato en nuestro catálogo
                PlatoItem plato = buscarPlatoPorNombre(platoData.nombre);
                if (plato != null) {
                    platosEnComanda.put(platoData.nombre, 
                        new PlatoEnComanda(plato, platoData.cantidad, platoData.nota));
                }
            }
            
            // Actualizar estado
            comboEstado.setValue(comandaData.estado);
            actualizarEstadoVisual();
            
            // Actualizar visualización
            actualizarComanda();
        }
    }
    
    /**
     * Busca un plato por nombre en todas las categorías
     */
    private PlatoItem buscarPlatoPorNombre(String nombre) {
        for (List<PlatoItem> platos : platosPorCategoria.values()) {
            for (PlatoItem plato : platos) {
                if (plato.nombre.equals(nombre)) {
                    return plato;
                }
            }
        }
        return null;
    }

    /**
     * Inicializa los platos cargándolos dinámicamente desde la base de datos
     */
    private void inicializarPlatosDesdeDB() {
        try {
            platosPorCategoria.clear();
            
            List<Receta> todasLasRecetas = recetaDAO.findAll();
            System.out.println("🍽️ Cargando " + todasLasRecetas.size() + " recetas para comandas...");
            
            for (Receta receta : todasLasRecetas) {
                String categoriaBD = receta.getCategoria();
                if (categoriaBD == null || categoriaBD.trim().isEmpty()) {
                    continue;
                }
                
                // Convertir categoría de BD a categoría de UI
                String categoriaUI = categoryMapping.getOrDefault(categoriaBD, categoriaBD);
                
                // El precio ya viene en euros desde la BD (DECIMAL)
                double precio = receta.getPrecio() != null ? receta.getPrecio() / 100.0 : 0.0;
                
                // Crear el plato
                PlatoItem plato = new PlatoItem(receta.getNombre(), precio, categoriaUI);
                
                // Añadir a la categoría correspondiente
                platosPorCategoria.computeIfAbsent(categoriaUI, k -> new ArrayList<>()).add(plato);
            }
            
            System.out.println("\n📊 Comandas: " + platosPorCategoria.size() + " categorías cargadas");
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando recetas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa todos los platos con sus precios según la carta (método antiguo de respaldo)
     */
    private void inicializarPlatos() {
        // ENTRANTES
        List<PlatoItem> entrantes = Arrays.asList(
            new PlatoItem("Bruschetta clásica", 6.50, "Entrantes"),
            new PlatoItem("Ensalada caprese", 8.90, "Entrantes"),
            new PlatoItem("Carpaccio de ternera", 12.00, "Entrantes"),
            new PlatoItem("Tabla de quesos italianos", 14.50, "Entrantes"),
            new PlatoItem("Sopa minestrone", 7.20, "Entrantes"),
            new PlatoItem("Calamares fritos", 11.80, "Entrantes"),
            new PlatoItem("Provolone al horno", 9.50, "Entrantes"),
            new PlatoItem("Tartar de salmón", 13.90, "Entrantes"),
            new PlatoItem("Antipasto mixto", 15.00, "Entrantes")
        );
        platosPorCategoria.put("Entrantes", entrantes);

        // PASTA
        List<PlatoItem> pasta = Arrays.asList(
            new PlatoItem("Spaghetti Carbonara", 12.90, "Pasta"),
            new PlatoItem("Penne Arrabbiata", 11.50, "Pasta"),
            new PlatoItem("Tagliatelle al pesto", 13.20, "Pasta"),
            new PlatoItem("Lasagna boloñesa", 14.50, "Pasta"),
            new PlatoItem("Ravioli de ricotta y espinacas", 13.80, "Pasta"),
            new PlatoItem("Gnocchi a la sorrentina", 12.00, "Pasta"),
            new PlatoItem("Fettuccine Alfredo", 13.90, "Pasta"),
            new PlatoItem("Tortellini panna e prosciutto", 14.00, "Pasta"),
            new PlatoItem("Spaghetti marinara", 15.30, "Pasta")
        );
        platosPorCategoria.put("Pasta", pasta);

        // PIZZA
        List<PlatoItem> pizza = Arrays.asList(
            new PlatoItem("Margherita", 9.00, "Pizza"),
            new PlatoItem("Pepperoni", 11.50, "Pizza"),
            new PlatoItem("Cuatro quesos", 12.50, "Pizza"),
            new PlatoItem("Hawaiana", 11.00, "Pizza"),
            new PlatoItem("BBQ Pollo", 13.20, "Pizza"),
            new PlatoItem("Prosciutto e funghi", 12.80, "Pizza"),
            new PlatoItem("Vegetariana", 11.90, "Pizza"),
            new PlatoItem("Diavola", 12.20, "Pizza"),
            new PlatoItem("Calzone clásico", 13.50, "Pizza")
        );
        platosPorCategoria.put("Pizza", pizza);

        // PESCADO
        List<PlatoItem> pescado = Arrays.asList(
            new PlatoItem("Salmón a la plancha con limón", 17.90, "Pescado"),
            new PlatoItem("Lubina al horno", 19.50, "Pescado"),
            new PlatoItem("Bacalao con tomate", 16.80, "Pescado"),
            new PlatoItem("Atún a la parrilla", 21.00, "Pescado"),
            new PlatoItem("Merluza en salsa verde", 15.90, "Pescado"),
            new PlatoItem("Dorada a la espalda", 18.20, "Pescado"),
            new PlatoItem("Pulpo a la brasa", 22.50, "Pescado"),
            new PlatoItem("Calamares en su tinta", 15.80, "Pescado"),
            new PlatoItem("Fritura mixta de mar", 17.50, "Pescado")
        );
        platosPorCategoria.put("Pescado", pescado);

        // CARNE
        List<PlatoItem> carne = Arrays.asList(
            new PlatoItem("Pollo a la parrilla con hierbas", 14.50, "Carne"),
            new PlatoItem("Solomillo de cerdo a la mostaza", 16.90, "Carne"),
            new PlatoItem("Entrecot de ternera", 22.00, "Carne"),
            new PlatoItem("Costillas BBQ", 18.50, "Carne"),
            new PlatoItem("Carrillera de ternera", 19.20, "Carne"),
            new PlatoItem("Albóndigas en salsa casera", 13.50, "Carne"),
            new PlatoItem("Filete de pollo empanado", 12.80, "Carne"),
            new PlatoItem("Hamburguesa gourmet", 15.90, "Carne"),
            new PlatoItem("Cordero asado", 23.00, "Carne")
        );
        platosPorCategoria.put("Carne", carne);

        // POSTRES
        List<PlatoItem> postres = Arrays.asList(
            new PlatoItem("Tiramisú clásico", 6.50, "Postres"),
            new PlatoItem("Panna cotta con frutos rojos", 6.80, "Postres"),
            new PlatoItem("Helado artesanal (2 bolas)", 4.80, "Postres"),
            new PlatoItem("Brownie con helado", 6.90, "Postres"),
            new PlatoItem("Tarta de queso al horno", 6.70, "Postres"),
            new PlatoItem("Coulant de chocolate", 7.20, "Postres"),
            new PlatoItem("Fruta fresca de temporada", 4.50, "Postres"),
            new PlatoItem("Cannoli sicilianos", 5.80, "Postres"),
            new PlatoItem("Gelato affogato", 5.90, "Postres")
        );
        platosPorCategoria.put("Postres", postres);

        // VINOS
        List<PlatoItem> vinos = Arrays.asList(
            new PlatoItem("Rioja Crianza", 18.00, "Vinos"),
            new PlatoItem("Albariño Rías Baixas", 17.80, "Vinos"),
            new PlatoItem("Chianti DOCG", 18.90, "Vinos"),
            new PlatoItem("Ribera del Duero Crianza", 26.00, "Vinos"),
            new PlatoItem("Godello sobre lías (Valdeorras)", 24.50, "Vinos"),
            new PlatoItem("Barolo joven (Piamonte)", 32.00, "Vinos"),
            new PlatoItem("Ribera del Duero Reserva", 45.00, "Vinos"),
            new PlatoItem("Chablis Premier Cru", 48.00, "Vinos"),
            new PlatoItem("Brunello di Montalcino", 62.00, "Vinos")
        );
        platosPorCategoria.put("Vinos", vinos);

        // MENÚ INFANTIL
        List<PlatoItem> infantil = Arrays.asList(
            new PlatoItem("Mini hamburguesa + patatas + bebida + helado", 9.90, "Menú Infantil"),
            new PlatoItem("Pasta corta con tomate + bebida + fruta", 8.50, "Menú Infantil")
        );
        platosPorCategoria.put("Menú Infantil", infantil);
    }

    /**
     * Configura los ComboBox
     */
    private void configurarCombos() {
        // ComboBox de mesas (1-15) - sistema de 15 mesas predeterminadas
        for (int i = 1; i <= 15; i++) {
            comboMesa.getItems().add(String.valueOf(i));
        }
        comboMesa.setValue(String.valueOf(mesaNumber));

        // ComboBox de estados
        comboEstado.getItems().addAll("Ocupada", "Libre", "Reservada");
        comboEstado.setValue("Ocupada");
        comboEstado.setOnAction(e -> actualizarEstadoVisual());
    }

    /**
     * Actualiza el color del estado visual
     */
    private void actualizarEstadoVisual() {
        lblEstadoMesa.getStyleClass().clear();
        lblEstadoMesa.getStyleClass().add("estado-label");
        
        switch (comboEstado.getValue()) {
            case "Ocupada":
                lblEstadoMesa.getStyleClass().add("estado-ocupada");
                break;
            case "Libre":
                lblEstadoMesa.getStyleClass().add("estado-libre");
                break;
            case "Reservada":
                lblEstadoMesa.getStyleClass().add("estado-reservada");
                break;
        }
    }

    /**
     * Configura los botones de categorías
     */
    private void configurarCategorias() {
        String[] categorias = {"Todos", "Entrantes", "Pasta", "Pizza", "Pescado", 
                               "Carne", "Postres", "Vinos", "Menú Infantil"};
        
        for (String categoria : categorias) {
            Button btnCategoria = new Button(categoria);
            btnCategoria.getStyleClass().add("category-button");
            
            if (categoria.equals("Todos")) {
                btnCategoria.getStyleClass().add("active");
            }
            
            btnCategoria.setOnAction(e -> {
                // Remover active de todos
                categoriesBox.getChildren().forEach(node -> 
                    node.getStyleClass().remove("active"));
                
                // Añadir active al seleccionado
                btnCategoria.getStyleClass().add("active");
                categoriaActual = categoria;
                mostrarPlatos(categoria);
            });
            
            categoriesBox.getChildren().add(btnCategoria);
        }
    }

    /**
     * Muestra los platos de una categoría
     */
    private void mostrarPlatos(String categoria) {
        platosContainer.getChildren().clear();
        
        List<PlatoItem> platosAMostrar = new ArrayList<>();
        
        if (categoria.equals("Todos")) {
            platosPorCategoria.values().forEach(platosAMostrar::addAll);
        } else {
            platosAMostrar.addAll(platosPorCategoria.getOrDefault(categoria, new ArrayList<>()));
        }
        
        for (PlatoItem plato : platosAMostrar) {
            HBox platoBox = crearPlatoBox(plato);
            platosContainer.getChildren().add(platoBox);
        }
    }

    /**
     * Crea un box para un plato
     */
    private HBox crearPlatoBox(PlatoItem plato) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(15);
        box.setPadding(new Insets(10, 15, 10, 15));
        box.getStyleClass().add("plato-box");
        
        // Nombre del plato
        VBox infoBox = new VBox(2);
        Label nombreLabel = new Label(plato.nombre);
        nombreLabel.getStyleClass().add("plato-nombre");
        Label categoriaLabel = new Label(plato.categoria);
        categoriaLabel.getStyleClass().add("plato-categoria");
        infoBox.getChildren().addAll(nombreLabel, categoriaLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        // Precio
        Label precioLabel = new Label(df.format(plato.precio) + " €");
        precioLabel.getStyleClass().add("plato-precio");
        precioLabel.setMinWidth(80);
        precioLabel.setAlignment(Pos.CENTER_RIGHT);
        
        // Botón añadir
        Button btnAnadir = new Button("+");
        btnAnadir.getStyleClass().add("btn-add");
        btnAnadir.setOnAction(e -> anadirPlato(plato));
        
        box.getChildren().addAll(infoBox, precioLabel, btnAnadir);
        
        return box;
    }

    /**
     * Añade un plato a la comanda con opción de nota
     */
    private void anadirPlato(PlatoItem plato) {
        PlatoEnComanda existing = platosEnComanda.get(plato.nombre);
        
        if (existing != null) {
            // Si ya existe, solo aumentar cantidad
            existing.cantidad++;
        } else {
            // Nuevo plato - preguntar si quiere añadir nota
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Añadir plato");
            dialog.setHeaderText(plato.nombre);
            
            // Crear campos
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            Label label = new Label("¿Deseas añadir alguna nota? (opcional)");
            TextField notaField = new TextField();
            notaField.setPromptText("Ej: Sin cebolla, poco hecho, etc.");
            notaField.setPrefWidth(300);
            
            content.getChildren().addAll(label, notaField);
            dialog.getDialogPane().setContent(content);
            
            // Botones
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return notaField.getText();
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(nota -> {
                platosEnComanda.put(plato.nombre, new PlatoEnComanda(plato, 1, nota));
                actualizarComanda();
            });
            
            return; // No actualizar aquí, se hace en el callback
        }
        
        actualizarComanda();
    }

    /**
     * Actualiza la visualización de la comanda
     */
    private void actualizarComanda() {
        platosComandaContainer.getChildren().clear();
        
        if (platosEnComanda.isEmpty()) {
            Label emptyLabel = new Label("No hay platos añadidos");
            emptyLabel.getStyleClass().add("empty-label");
            platosComandaContainer.getChildren().add(emptyLabel);
        } else {
            for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
                HBox platoComandaBox = crearPlatoComandaBox(platoComanda);
                platosComandaContainer.getChildren().add(platoComandaBox);
            }
        }
        
        actualizarTotales();
    }

    /**
     * Crea un box para un plato en la comanda con nota
     */
    private HBox crearPlatoComandaBox(PlatoEnComanda platoComanda) {
        VBox mainBox = new VBox(5);
        mainBox.getStyleClass().add("plato-comanda-box");
        mainBox.setPadding(new Insets(8, 10, 8, 10));
        
        // Fila principal con info del plato
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        // Nombre del plato y precio
        VBox infoBox = new VBox(2);
        Label nombreLabel = new Label(platoComanda.plato.nombre);
        nombreLabel.getStyleClass().add("plato-comanda-nombre");
        nombreLabel.setWrapText(true);
        
        Label precioUnitLabel = new Label(df.format(platoComanda.plato.precio) + " € c/u");
        precioUnitLabel.getStyleClass().add("plato-comanda-precio-unit");
        
        infoBox.getChildren().addAll(nombreLabel, precioUnitLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        // Controles de cantidad
        HBox cantidadBox = new HBox(5);
        cantidadBox.setAlignment(Pos.CENTER);
        
        Button btnMenos = new Button("-");
        btnMenos.getStyleClass().add("btn-quantity");
        btnMenos.setOnAction(e -> {
            if (platoComanda.cantidad > 1) {
                platoComanda.cantidad--;
            } else {
                platosEnComanda.remove(platoComanda.plato.nombre);
            }
            actualizarComanda();
        });
        
        Label cantidadLabel = new Label("x" + platoComanda.cantidad);
        cantidadLabel.getStyleClass().add("cantidad-label");
        cantidadLabel.setMinWidth(35);
        cantidadLabel.setAlignment(Pos.CENTER);
        
        Button btnMas = new Button("+");
        btnMas.getStyleClass().add("btn-quantity");
        btnMas.setOnAction(e -> {
            platoComanda.cantidad++;
            actualizarComanda();
        });
        
        cantidadBox.getChildren().addAll(btnMenos, cantidadLabel, btnMas);
        
        // Precio total del plato
        double precioTotal = platoComanda.plato.precio * platoComanda.cantidad;
        Label precioTotalLabel = new Label(df.format(precioTotal) + " €");
        precioTotalLabel.getStyleClass().add("plato-comanda-precio-total");
        precioTotalLabel.setMinWidth(70);
        precioTotalLabel.setAlignment(Pos.CENTER_RIGHT);
        
        topRow.getChildren().addAll(infoBox, cantidadBox, precioTotalLabel);
        mainBox.getChildren().add(topRow);
        
        // Si hay nota, mostrarla en segunda fila
        if (platoComanda.nota != null && !platoComanda.nota.trim().isEmpty()) {
            HBox notaRow = new HBox(5);
            notaRow.setPadding(new Insets(0, 0, 0, 10));
            
            Label notaIcon = new Label("📝");
            Label notaLabel = new Label(platoComanda.nota);
            notaLabel.getStyleClass().add("plato-nota");
            notaLabel.setWrapText(true);
            notaLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d; -fx-font-size: 12px;");
            
            notaRow.getChildren().addAll(notaIcon, notaLabel);
            mainBox.getChildren().add(notaRow);
        }
        
        // Convertir VBox a HBox para retornar
        HBox wrapper = new HBox();
        wrapper.getChildren().add(mainBox);
        HBox.setHgrow(mainBox, Priority.ALWAYS);
        
        return wrapper;
    }

    /**
     * Actualiza los totales (subtotal, IVA, total)
     */
    private void actualizarTotales() {
        double subtotal = 0.0;
        
        for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
            subtotal += platoComanda.plato.precio * platoComanda.cantidad;
        }
        
        double iva = subtotal * IVA;
        double total = subtotal + iva;
        
        lblSubtotal.setText(df.format(subtotal) + " €");
        lblIVA.setText(df.format(iva) + " €");
        lblTotal.setText(df.format(total) + " €");
    }

    /**
     * Busca platos por nombre
     */
    private void buscarPlatos(String query) {
        if (query == null || query.trim().isEmpty()) {
            mostrarPlatos(categoriaActual);
            return;
        }
        
        platosContainer.getChildren().clear();
        String queryLower = query.toLowerCase().trim();
        
        List<PlatoItem> platosEncontrados = new ArrayList<>();
        
        for (List<PlatoItem> platos : platosPorCategoria.values()) {
            for (PlatoItem plato : platos) {
                if (plato.nombre.toLowerCase().contains(queryLower)) {
                    platosEncontrados.add(plato);
                }
            }
        }
        
        if (platosEncontrados.isEmpty()) {
            Label noResultLabel = new Label("No se encontraron platos");
            noResultLabel.getStyleClass().add("no-result-label");
            platosContainer.getChildren().add(noResultLabel);
        } else {
            for (PlatoItem plato : platosEncontrados) {
                HBox platoBox = crearPlatoBox(plato);
                platosContainer.getChildren().add(platoBox);
            }
        }
    }

    /**
     * Guarda la comanda
     */
    @FXML
    private void guardarComanda() {
        if (platosEnComanda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("⚠ Comanda vacía");
            alert.setHeaderText("La comanda no tiene platos");
            alert.setContentText("Debes añadir al menos un plato antes de guardar la comanda.");
            
            // Estilizar
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
            return;
        }
        
        // Calcular totales
        double subtotal = 0.0;
        for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
            subtotal += platoComanda.plato.precio * platoComanda.cantidad;
        }
        double iva = subtotal * IVA;
        double total = subtotal + iva;
        
        // Guardar en el almacenamiento temporal
        WaiterViewController.ComandaData comandaData = new WaiterViewController.ComandaData();
        comandaData.estado = comboEstado.getValue();
        
        for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
            WaiterViewController.ComandaData.PlatoComanda platoData = 
                new WaiterViewController.ComandaData.PlatoComanda(
                    platoComanda.plato.nombre,
                    platoComanda.plato.precio,
                    platoComanda.cantidad,
                    platoComanda.nota,
                    platoComanda.plato.categoria
                );
            comandaData.platos.put(platoComanda.plato.nombre, platoData);
        }
        
        WaiterViewController.COMANDAS_ACTIVAS.put(mesaNumber, comandaData);
        
        // Actualizar estado de la mesa
        for (WaiterViewController.MesaInfo mesa : WaiterViewController.MESAS) {
            if (mesa.numero == mesaNumber) {
                mesa.estado = comboEstado.getValue();
                // Todas las mesas con comanda usan la imagen de mesa
                mesa.imageName = "img/mesa.jpg";
                break;
            }
        }
        
        System.out.println("=== COMANDA GUARDADA ===");
        System.out.println("Mesa: " + mesaNumber);
        System.out.println("Estado: " + comboEstado.getValue());
        System.out.println("Número de platos: " + platosEnComanda.size());
        System.out.println("Platos:");
        for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
            System.out.println("  - " + platoComanda.plato.nombre + " x" + platoComanda.cantidad + 
                             " = " + df.format(platoComanda.plato.precio * platoComanda.cantidad) + " €" +
                             (platoComanda.nota != null && !platoComanda.nota.isEmpty() ? " [" + platoComanda.nota + "]" : ""));
        }
        System.out.println("Subtotal: " + df.format(subtotal) + " €");
        System.out.println("IVA (10%): " + df.format(iva) + " €");
        System.out.println("TOTAL: " + df.format(total) + " €");
        System.out.println("=======================");
        
        // Guardar en la base de datos
        try {
            System.out.println("Guardando comanda en base de datos...");
            
            // Obtener o crear la mesa en la BD
            MesaDAO mesaDAO = new MesaDAO();
            Mesa mesa = mesaDAO.findById(mesaNumber);
            if (mesa == null) {
                System.out.println("⚠️ Mesa " + mesaNumber + " no existe en BD. Creando...");
                mesa = mesaDAO.createWithSpecificId(mesaNumber, EstadoMesa.LIBRE);
                System.out.println("✅ Mesa " + mesaNumber + " creada en BD");
            }
            
            // Actualizar estado de la mesa
            String estadoSeleccionado = comboEstado.getValue();
            if ("Ocupada".equals(estadoSeleccionado)) {
                mesa.setEstado(EstadoMesa.OCUPADA);
            } else if ("Reservada".equals(estadoSeleccionado)) {
                mesa.setEstado(EstadoMesa.RESERVADA);
            }
            mesaDAO.update(mesa);
            
            // Obtener el usuario actual de la sesión
            ilcaminodelamamma.model.Usuario usuarioSesion = ilcaminodelamamma.config.SessionManager.getUsuarioActual();
            if (usuarioSesion == null) {
                throw new Exception("No hay usuario logueado. Por favor, inicia sesión nuevamente.");
            }
            
            // IMPORTANTE: Obtener el usuario desde la BD (no usar el de sesión directamente)
            // porque el de sesión es un objeto transient (no gestionado por Hibernate)
            ilcaminodelamamma.DAO.UsuarioDAO usuarioDAO = new ilcaminodelamamma.DAO.UsuarioDAO();
            ilcaminodelamamma.model.Usuario usuarioActual = null;
            
            if (usuarioSesion.getId_usuario() != null) {
                // Si tiene ID, buscarlo por ID
                usuarioActual = usuarioDAO.findById(usuarioSesion.getId_usuario());
            } else {
                // Si no tiene ID (usuario demo), buscarlo por nombre
                List<ilcaminodelamamma.model.Usuario> usuarios = usuarioDAO.findByNombre(usuarioSesion.getNombre());
                if (!usuarios.isEmpty()) {
                    usuarioActual = usuarios.get(0);
                }
            }
            
            // Si aún no tenemos usuario, es porque es un usuario demo que no existe en BD
            // En ese caso, lo creamos
            if (usuarioActual == null) {
                System.out.println("⚠️ Usuario no existe en BD. Creando usuario: " + usuarioSesion.getNombre());
                usuarioActual = new ilcaminodelamamma.model.Usuario();
                usuarioActual.setNombre(usuarioSesion.getNombre());
                usuarioActual.setRol(usuarioSesion.getRol());
                usuarioActual.setContrasena("$2a$10$dummyhash"); // Hash dummy
                usuarioActual = usuarioDAO.create(usuarioActual);
                System.out.println("✅ Usuario creado en BD con ID: " + usuarioActual.getId_usuario());
            }
            
            // Crear la comanda
            Comanda comanda = new Comanda();
            comanda.setUsuario(usuarioActual);
            comanda.setMesa(mesa);
            comanda.setFecha_hora(LocalDateTime.now());
            comanda.setTotal((float) total);
            
            // Crear los detalles de la comanda
            RecetaDAO recetaDAO = new RecetaDAO();
            List<DetalleComanda> detalles = new ArrayList<>();
            
            for (PlatoEnComanda platoComanda : platosEnComanda.values()) {
                // Buscar la receta por nombre
                List<Receta> recetas = recetaDAO.findByNombre(platoComanda.plato.nombre);
                if (!recetas.isEmpty()) {
                    Receta receta = recetas.get(0);
                    
                    DetalleComanda detalle = new DetalleComanda();
                    detalle.setComanda(comanda);
                    detalle.setReceta(receta);
                    detalle.setCantidad(platoComanda.cantidad);
                    // Convertir de euros a céntimos para guardar en BD
                    detalle.setPrecio_unitario((float) (platoComanda.plato.precio * 100));
                    detalle.setSubtotal((float) (platoComanda.plato.precio * platoComanda.cantidad * 100));
                    
                    detalles.add(detalle);
                } else {
                    System.err.println("⚠️ Receta no encontrada: " + platoComanda.plato.nombre);
                }
            }
            
            comanda.setDetalleComandas(new java.util.HashSet<>(detalles));
            
            // Guardar en la BD
            ComandaDAO comandaDAO = new ComandaDAO();
            comandaDAO.create(comanda);
            
            System.out.println("✅ Comanda guardada en BD con ID: " + comanda.getId_comanda());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✓ Comanda guardada");
            alert.setHeaderText("Comanda guardada correctamente");
            alert.setContentText("Mesa #" + mesaNumber + "\n\n" +
                               "Total: " + df.format(total) + " €\n" +
                               "Platos: " + platosEnComanda.size() + " tipos\n\n" +
                               "La comanda ha sido guardada en la base de datos.");
            
            // Estilizar
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setText("✓ Entendido");
                    okBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
            
            volverALista();
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar comanda en BD: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("✗ Error al guardar");
            alert.setHeaderText("No se pudo guardar la comanda");
            alert.setContentText("Ha ocurrido un error al guardar en la base de datos:\n\n" + e.getMessage());
            
            // Estilizar
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
        }
    }
    
    /**
     * Elimina la comanda actual
     */
    @FXML
    private void eliminarComanda() {
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("🗑️ Eliminar comanda");
        confirmacion.setHeaderText("¿Eliminar comanda de Mesa #" + mesaNumber + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer. Todos los platos de esta comanda se perderán.");
        
        // Estilizar el diálogo de confirmación
        confirmacion.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
        
        // Personalizar botones
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
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Eliminar del almacenamiento
                WaiterViewController.COMANDAS_ACTIVAS.remove(mesaNumber);
                
                // Actualizar estado de la mesa a Libre
                for (WaiterViewController.MesaInfo mesa : WaiterViewController.MESAS) {
                    if (mesa.numero == mesaNumber) {
                        mesa.estado = "Libre";
                        mesa.imageName = "img/mesa.jpg";
                        break;
                    }
                }
                
                System.out.println("Comanda de Mesa " + mesaNumber + " eliminada");
                
                // Mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("✓ Comanda eliminada");
                alert.setHeaderText("Comanda eliminada correctamente");
                alert.setContentText("La Mesa #" + mesaNumber + " está ahora libre y disponible para nuevos clientes.");
                
                // Estilizar mensaje de éxito
                alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
                
                javafx.application.Platform.runLater(() -> {
                    Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                    if (okBtn != null) {
                        okBtn.setText("✓ Entendido");
                        okBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                    }
                });
                
                alert.showAndWait();
                
                volverALista();
            }
        });
    }

    /**
     * Genera un ticket PDF de la comanda actual
     */
    @FXML
    private void generarTicketPdf() {
        if (platosEnComanda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("⚠ Comanda vacía");
            alert.setHeaderText("No se puede generar el ticket");
            alert.setContentText("La comanda no tiene platos. Añade al menos un plato antes de generar el ticket.");
            
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
            return;
        }
        
        try {
            // Primero verificar si la comanda ya está guardada en la BD
            ComandaDAO comandaDAO = new ComandaDAO();
            MesaDAO mesaDAO = new MesaDAO();
            Mesa mesa = mesaDAO.findById(mesaNumber);
            
            if (mesa == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("❌ Error");
                alert.setHeaderText("Mesa no encontrada");
                alert.setContentText("La mesa " + mesaNumber + " no existe en la base de datos. Guarda la comanda primero.");
                alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
                alert.showAndWait();
                return;
            }
            
            // Buscar la comanda más reciente de esta mesa
            Comanda comanda = comandaDAO.findByMesa(mesa);
            
            if (comanda == null) {
                // Si no hay comanda en BD, mostrar mensaje
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠ Comanda no guardada");
                alert.setHeaderText("Debes guardar la comanda primero");
                alert.setContentText("Para generar el ticket PDF, primero debes guardar la comanda en el sistema.");
                
                alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
                javafx.application.Platform.runLater(() -> {
                    Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                    if (okBtn != null) {
                        okBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                    }
                });
                
                alert.showAndWait();
                return;
            }
            
            // Generar el ticket
            TicketPdfService ticketService = new TicketPdfService();
            String rutaArchivo = ticketService.generarTicket(comanda);
            
            // Mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✓ Ticket generado");
            alert.setHeaderText("Ticket PDF creado correctamente");
            alert.setContentText("El ticket se ha guardado en: " + rutaArchivo);
            
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setText("✓ Entendido");
                    okBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
            
            // Abrir el archivo PDF automáticamente
            try {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                java.io.File archivo = new java.io.File(rutaArchivo);
                if (archivo.exists()) {
                    desktop.open(archivo);
                }
            } catch (Exception e) {
                System.err.println("No se pudo abrir el PDF automáticamente: " + e.getMessage());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("❌ Error");
            alert.setHeaderText("Error al generar el ticket");
            alert.setContentText("Ocurrió un error al generar el PDF: " + e.getMessage());
            
            alert.getDialogPane().setStyle("-fx-background-color: #FAF8F5; -fx-font-family: 'Segoe UI';");
            javafx.application.Platform.runLater(() -> {
                Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
                }
            });
            
            alert.showAndWait();
        }
    }

    /**
     * Vuelve a la lista de comandas
     */
    @FXML
    private void volverALista() {
        try {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/waiter/waiter-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1024, 768);
            stage.setScene(scene);
            stage.setMinWidth(1024);
            stage.setMinHeight(768);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al volver a lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clase interna para representar un plato
     */
    private static class PlatoItem {
        String nombre;
        double precio;
        String categoria;
        
        PlatoItem(String nombre, double precio, String categoria) {
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlatoItem platoItem = (PlatoItem) o;
            return Objects.equals(nombre, platoItem.nombre);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(nombre);
        }
    }
    
    /**
     * Obtiene la imagen representativa de una categoría de plato
     */
    private String obtenerImagenCategoria(String categoria) {
        return switch (categoria.toLowerCase()) {
            case "entrantes" -> "img/entrantes/ensalada-caprese.jpg";
            case "pasta" -> "img/pasta/espaguetis-a-la-carbonara.jpg";
            case "pizza" -> "img/pizza/margherita.jpg";
            case "carnes" -> "img/carnes/escalopa-a-la-milanesa.jpg";
            case "pescados" -> "img/pescados/salmon-a-la-plancha.jpg";
            case "postres" -> "img/postres/tiramisu.jpg";
            case "vino" -> "img/vino/chianti-classico.jpg";
            case "menu-infantil" -> "img/menu-infantil/macarrones-con-tomate.jpg";
            default -> "img/default.jpg";
        };
    }
}
