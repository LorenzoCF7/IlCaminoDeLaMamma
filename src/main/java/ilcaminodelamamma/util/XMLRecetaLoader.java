package ilcaminodelamamma.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ilcaminodelamamma.DAO.IngredienteDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.model.Receta;

public class XMLRecetaLoader {
    
    private final RecetaDAO recetaDAO;
    private final IngredienteDAO ingredienteDAO;
    
    public XMLRecetaLoader() {
        this.recetaDAO = new RecetaDAO();
        this.ingredienteDAO = new IngredienteDAO();
    }
    
    /**
     * Carga las recetas desde el XML y las vincula con los datos existentes en la BD
     * @return número de recetas procesadas (actualizadas + creadas)
     */
    public int cargarRecetasDesdeXML() {
        int recetasActualizadas = 0;
        int recetasCreadas = 0;
        
        try {
            // Cargar el archivo XML desde resources
            InputStream xmlStream = getClass().getClassLoader().getResourceAsStream("Recetas.xml");
            if (xmlStream == null) {
                System.err.println("No se pudo encontrar el archivo Recetas.xml");
                return 0;
            }
            
            // Parsear el XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlStream);
            doc.getDocumentElement().normalize();
            
            // Obtener todas las recetas del XML
            NodeList recetaNodes = doc.getElementsByTagName("receta");
            System.out.println("📖 Encontradas " + recetaNodes.getLength() + " recetas en el XML");
            
            for (int i = 0; i < recetaNodes.getLength(); i++) {
                Node recetaNode = recetaNodes.item(i);
                
                if (recetaNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element recetaElement = (Element) recetaNode;
                    
                    // Extraer datos del XML
                    String nombre = getTextContent(recetaElement, "Nombre_Receta");
                    String descripcion = getTextContent(recetaElement, "Descripcion_corta");
                    String tiempoStr = getTextContent(recetaElement, "Tiempo_preparacion");
                    String categoria = getTextContent(recetaElement, "Categoria");
                    String precioStr = getTextContent(recetaElement, "Precio");
                    
                    // Parsear tiempo de preparación (extraer número de "XX minutos")
                    Integer tiempoPreparacion = parsearTiempo(tiempoStr);
                    
                    // Parsear precio (debe estar en céntimos en el XML)
                    Integer precio = parsearPrecio(precioStr);
                    
                    // Obtener pasos
                    String pasos = obtenerPasos(recetaElement);
                    
                    // Obtener ingredientes
                    List<String> ingredientes = obtenerIngredientes(recetaElement);
                    
                    // Buscar la receta en la BD por nombre
                    List<Receta> recetasEncontradas = recetaDAO.findByNombre(nombre);
                    
                    Receta receta;
                    if (!recetasEncontradas.isEmpty()) {
                        // Actualizar la primera receta encontrada
                        receta = recetasEncontradas.get(0);
                        
                        // Actualizar precio, tiempo de preparación y pasos
                        receta.setPrecio(precio);
                        receta.setTiempo_preparacion(tiempoPreparacion);
                        receta.setPasos(pasos);
                        
                        // Actualizar la receta
                        recetaDAO.update(receta);
                        
                        recetasActualizadas++;
                        System.out.println("✅ Actualizada: " + nombre + " - Precio: " + precio + " céntimos");
                    } else {
                        // Crear nueva receta
                        receta = new Receta(
                            nombre,
                            descripcion,
                            precio, // Precio desde el XML (en céntimos)
                            tiempoPreparacion,
                            Boolean.TRUE, // Disponible por defecto
                            null, // Sin imagen por ahora
                            categoria
                        );
                        receta.setPasos(pasos);
                        
                        receta = recetaDAO.create(receta);
                        recetasCreadas++;
                        System.out.println("✨ Creada nueva receta: " + nombre + " (" + categoria + ") - Precio: " + precio + " céntimos");
                    }
                    
                    // Vincular ingredientes (tanto para actualizadas como creadas)
                    vincularIngredientes(receta, ingredientes);
                }
            }
            
            System.out.println("\n✅ RESUMEN DEL PROCESO:");
            System.out.println("   📝 Recetas actualizadas: " + recetasActualizadas);
            System.out.println("   ✨ Recetas creadas: " + recetasCreadas);
            System.out.println("   📊 Total procesadas: " + (recetasActualizadas + recetasCreadas));
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar recetas desde XML: " + e.getMessage());
            e.printStackTrace();
        }
        
        return recetasActualizadas + recetasCreadas;
    }
    
    /**
     * Vincula los ingredientes con la receta
     */
    private void vincularIngredientes(Receta receta, List<String> nombresIngredientes) {
        for (String nombreIngrediente : nombresIngredientes) {
            // Limpiar el nombre
            nombreIngrediente = nombreIngrediente.trim();
            
            // Buscar el ingrediente en la BD
            Ingrediente ingrediente = buscarOCrearIngrediente(nombreIngrediente);
            
            if (ingrediente != null) {
                // Cantidad por defecto: 100g o 100ml
                Integer cantidadUsada = 100;
                
                try {
                    // Vincular con la receta (solo si no está ya vinculado)
                    boolean yaVinculado = receta.getRecetaIngredientes().stream()
                        .anyMatch(ri -> ri.getIngrediente().getId_ingrediente().equals(ingrediente.getId_ingrediente()));
                    
                    if (!yaVinculado) {
                        recetaDAO.darIngrediente(receta, ingrediente, cantidadUsada);
                        System.out.println("   ➕ Ingrediente vinculado a receta: " + nombreIngrediente);
                    } else {
                        System.out.println("   ⏭️  Ya vinculado: " + nombreIngrediente);
                    }
                } catch (Exception e) {
                    System.err.println("   ⚠️  Error al vincular ingrediente " + nombreIngrediente + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Busca un ingrediente por nombre, si no existe lo crea
     */
    private Ingrediente buscarOCrearIngrediente(String nombre) {
        // Buscar ingrediente existente
        List<Ingrediente> encontrados = ingredienteDAO.findByNombre(nombre);
        
        if (!encontrados.isEmpty()) {
            System.out.println("   🔍 Ingrediente encontrado en BD: " + nombre);
            return encontrados.get(0);
        }
        
        // Si no existe, crear uno nuevo
        String unidadMedida = determinarUnidadMedida(nombre);
        Ingrediente nuevoIngrediente = new Ingrediente(nombre, unidadMedida, 1000); // Stock inicial: 1000
        System.out.println("   ✨ Ingrediente creado: " + nombre + " (" + unidadMedida + ")");
        
        return ingredienteDAO.create(nuevoIngrediente);
    }
    
    /**
     * Determina la unidad de medida según el tipo de ingrediente
     * g = gramos (sólidos), L = litros (líquidos)
     */
    private String determinarUnidadMedida(String nombreIngrediente) {
        String nombreLower = nombreIngrediente.toLowerCase();
        
        // Lista de líquidos comunes
        String[] liquidos = {
            "aceite", "vino", "caldo", "agua", "leche", "nata", "crema",
            "salsa", "vinagre", "zumo", "licor", "cerveza"
        };
        
        // Verificar si es un líquido
        for (String liquido : liquidos) {
            if (nombreLower.contains(liquido)) {
                return "L";
            }
        }
        
        // Por defecto, usar gramos
        return "g";
    }
    
    /**
     * Obtiene el contenido de texto de un elemento hijo
     */
    private String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
    
    /**
     * Parsea el tiempo de preparación desde formato "XX minutos"
     */
    private Integer parsearTiempo(String tiempoStr) {
        try {
            // Extraer el número del string "XX minutos"
            String numero = tiempoStr.replaceAll("[^0-9]", "");
            return Integer.parseInt(numero);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Parsea el precio desde el XML (debe estar en céntimos)
     */
    private Integer parsearPrecio(String precioStr) {
        try {
            if (precioStr == null || precioStr.trim().isEmpty()) {
                return 0;
            }
            return Integer.parseInt(precioStr.trim());
        } catch (Exception e) {
            System.err.println("⚠️  Error al parsear precio: " + precioStr);
            return 0;
        }
    }
    
    /**
     * Obtiene todos los pasos de preparación concatenados
     */
    private String obtenerPasos(Element recetaElement) {
        StringBuilder pasos = new StringBuilder();
        NodeList pasosNode = recetaElement.getElementsByTagName("Pasos");
        
        if (pasosNode.getLength() > 0) {
            Element pasosElement = (Element) pasosNode.item(0);
            NodeList pasoList = pasosElement.getElementsByTagName("Paso");
            
            for (int i = 0; i < pasoList.getLength(); i++) {
                pasos.append(i + 1).append(". ")
                     .append(pasoList.item(i).getTextContent().trim())
                     .append("\n");
            }
        }
        
        return pasos.toString().trim();
    }
    
    /**
     * Obtiene la lista de ingredientes
     */
    private List<String> obtenerIngredientes(Element recetaElement) {
        List<String> ingredientes = new ArrayList<>();
        NodeList ingredientesNode = recetaElement.getElementsByTagName("Ingredientes");
        
        if (ingredientesNode.getLength() > 0) {
            Element ingredientesElement = (Element) ingredientesNode.item(0);
            NodeList ingredienteList = ingredientesElement.getElementsByTagName("Ingrediente");
            
            for (int i = 0; i < ingredienteList.getLength(); i++) {
                String ingrediente = ingredienteList.item(i).getTextContent().trim();
                if (!ingrediente.isEmpty()) {
                    ingredientes.add(ingrediente);
                }
            }
        }
        
        return ingredientes;
    }
}
