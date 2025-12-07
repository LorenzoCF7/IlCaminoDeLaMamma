package ilcaminodelamamma.view.waiter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

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

    // Datos
    private final Map<String, List<PlatoItem>> platosPorCategoria = new LinkedHashMap<>();
    private final Map<PlatoItem, Integer> platosEnComanda = new HashMap<>();
    private String categoriaActual = "Todos";
    private int mesaNumber = 5;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    
    private static final double IVA = 0.10; // 10% IVA

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Comanda Detail inicializada");
        
        // Inicializar platos con precios de la carta
        inicializarPlatos();
        
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
     * Inicializa todos los platos con sus precios según la carta
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
        // ComboBox de mesas (1-20)
        for (int i = 1; i <= 20; i++) {
            comboMesa.getItems().add(String.valueOf(i));
        }
        comboMesa.setValue(String.valueOf(mesaNumber));
        comboMesa.setOnAction(e -> {
            mesaNumber = Integer.parseInt(comboMesa.getValue());
            lblComandaTitle.setText("Comanda - Mesa #" + mesaNumber);
        });

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
     * Añade un plato a la comanda
     */
    private void anadirPlato(PlatoItem plato) {
        int cantidad = platosEnComanda.getOrDefault(plato, 0);
        platosEnComanda.put(plato, cantidad + 1);
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
            for (Map.Entry<PlatoItem, Integer> entry : platosEnComanda.entrySet()) {
                HBox platoComandaBox = crearPlatoComandaBox(entry.getKey(), entry.getValue());
                platosComandaContainer.getChildren().add(platoComandaBox);
            }
        }
        
        actualizarTotales();
    }

    /**
     * Crea un box para un plato en la comanda
     */
    private HBox crearPlatoComandaBox(PlatoItem plato, int cantidad) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(10);
        box.setPadding(new Insets(8, 10, 8, 10));
        box.getStyleClass().add("plato-comanda-box");
        
        // Nombre del plato
        VBox infoBox = new VBox(2);
        Label nombreLabel = new Label(plato.nombre);
        nombreLabel.getStyleClass().add("plato-comanda-nombre");
        nombreLabel.setWrapText(true);
        
        Label precioUnitLabel = new Label(df.format(plato.precio) + " € c/u");
        precioUnitLabel.getStyleClass().add("plato-comanda-precio-unit");
        
        infoBox.getChildren().addAll(nombreLabel, precioUnitLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        // Controles de cantidad
        HBox cantidadBox = new HBox(5);
        cantidadBox.setAlignment(Pos.CENTER);
        
        Button btnMenos = new Button("-");
        btnMenos.getStyleClass().add("btn-quantity");
        btnMenos.setOnAction(e -> {
            if (cantidad > 1) {
                platosEnComanda.put(plato, cantidad - 1);
            } else {
                platosEnComanda.remove(plato);
            }
            actualizarComanda();
        });
        
        Label cantidadLabel = new Label("x" + cantidad);
        cantidadLabel.getStyleClass().add("cantidad-label");
        cantidadLabel.setMinWidth(35);
        cantidadLabel.setAlignment(Pos.CENTER);
        
        Button btnMas = new Button("+");
        btnMas.getStyleClass().add("btn-quantity");
        btnMas.setOnAction(e -> {
            platosEnComanda.put(plato, cantidad + 1);
            actualizarComanda();
        });
        
        cantidadBox.getChildren().addAll(btnMenos, cantidadLabel, btnMas);
        
        // Precio total del plato
        double precioTotal = plato.precio * cantidad;
        Label precioTotalLabel = new Label(df.format(precioTotal) + " €");
        precioTotalLabel.getStyleClass().add("plato-comanda-precio-total");
        precioTotalLabel.setMinWidth(70);
        precioTotalLabel.setAlignment(Pos.CENTER_RIGHT);
        
        box.getChildren().addAll(infoBox, cantidadBox, precioTotalLabel);
        
        return box;
    }

    /**
     * Actualiza los totales (subtotal, IVA, total)
     */
    private void actualizarTotales() {
        double subtotal = 0.0;
        
        for (Map.Entry<PlatoItem, Integer> entry : platosEnComanda.entrySet()) {
            subtotal += entry.getKey().precio * entry.getValue();
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
            alert.setTitle("Comanda vacía");
            alert.setHeaderText(null);
            alert.setContentText("No se puede guardar una comanda sin platos.");
            alert.showAndWait();
            return;
        }
        
        System.out.println("=== COMANDA GUARDADA ===");
        System.out.println("Mesa: " + mesaNumber);
        System.out.println("Estado: " + comboEstado.getValue());
        System.out.println("Platos:");
        for (Map.Entry<PlatoItem, Integer> entry : platosEnComanda.entrySet()) {
            System.out.println("  - " + entry.getKey().nombre + " x" + entry.getValue() + 
                             " = " + df.format(entry.getKey().precio * entry.getValue()) + " €");
        }
        System.out.println("Subtotal: " + lblSubtotal.getText());
        System.out.println("IVA: " + lblIVA.getText());
        System.out.println("TOTAL: " + lblTotal.getText());
        System.out.println("=======================");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comanda guardada");
        alert.setHeaderText(null);
        alert.setContentText("La comanda se ha guardado correctamente.");
        alert.showAndWait();
        
        volverALista();
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
            Scene scene = new Scene(root);
            stage.setScene(scene);
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
}
