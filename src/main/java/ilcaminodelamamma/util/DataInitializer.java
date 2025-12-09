package ilcaminodelamamma.util;

import java.util.List;

import ilcaminodelamamma.DAO.IngredienteDAO;
import ilcaminodelamamma.DAO.RecetaDAO;
import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.model.Receta;

/**
 * Servicio de inicialización de datos de la aplicación
 * Se ejecuta al inicio para cargar datos necesarios desde fuentes externas
 */
public class DataInitializer {
    
    private final XMLRecetaLoader xmlRecetaLoader;
    private final ImagenLoader imagenLoader;
    private final RecetaDAO recetaDAO;
    private final IngredienteDAO ingredienteDAO;
    
    public DataInitializer() {
        this.xmlRecetaLoader = new XMLRecetaLoader();
        this.imagenLoader = new ImagenLoader();
        this.recetaDAO = new RecetaDAO();
        this.ingredienteDAO = new IngredienteDAO();
    }
    
    /**
     * Inicializa los datos de la aplicación
     * Carga recetas desde XML si ya existen recetas en la BD
     */
    public void inicializarDatos() {
        System.out.println("🚀 Iniciando carga de datos...");
        
        try {
            // Verificar datos existentes
            List<Receta> recetasExistentes = recetaDAO.findAll();
            List<Ingrediente> ingredientesExistentes = ingredienteDAO.findAll();
            
            System.out.println("\n📊 DATOS ACTUALES EN LA BASE DE DATOS:");
            System.out.println("   🍝 Recetas: " + recetasExistentes.size());
            System.out.println("   🥕 Ingredientes: " + ingredientesExistentes.size());
            
            if (!recetasExistentes.isEmpty()) {
                System.out.println("\n📚 Recetas existentes:");
                for (Receta r : recetasExistentes) {
                    System.out.println("   - " + r.getNombre() + " (" + r.getCategoria() + ")");
                }
            }
            
            System.out.println("\n🔄 Cargando recetas e ingredientes desde XML...");
            
            // Cargar datos del XML (crea nuevas o actualiza existentes)
            int procesadas = xmlRecetaLoader.cargarRecetasDesdeXML();
            
            if (procesadas > 0) {
                System.out.println("\n✅ Inicialización completada exitosamente");
                System.out.println("   " + procesadas + " recetas procesadas desde el XML");
            } else {
                System.out.println("\n⚠️  No se procesaron recetas. Verifica que el archivo Recetas.xml exista en resources/");
            }
            
            // Cargar imágenes de ejemplo
            cargarImagenesEjemplo();
            
        } catch (Exception e) {
            System.err.println("❌ Error durante la inicialización de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Forzar recarga de datos desde XML
     * Útil para actualizar después de cambios en el XML
     */
    public void recargarDatosXML() {
        System.out.println("🔄 Forzando recarga de datos desde XML...");
        int actualizadas = xmlRecetaLoader.cargarRecetasDesdeXML();
        System.out.println("✅ Recarga completada: " + actualizadas + " recetas actualizadas");
    }
    
    /**
     * Carga una imagen para una receta específica
     * Delega la operación a ImagenLoader
     * 
     * @param rutaImagen Ruta de la imagen
     * @param idReceta ID de la receta
     * @return true si se cargó correctamente
     */
    public boolean cargarImagenReceta(String rutaImagen, Integer idReceta) {
        return imagenLoader.cargarImagenReceta(rutaImagen, idReceta);
    }
    
    /**
     * Carga imágenes de ejemplo para las primeras 5 recetas
     * Delega la operación a ImagenLoader
     */
    public void cargarImagenesEjemplo() {
        imagenLoader.cargarImagenesEjemplo();
    }
}
