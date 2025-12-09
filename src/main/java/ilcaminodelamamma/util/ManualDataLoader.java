package ilcaminodelamamma.util;

import ilcaminodelamamma.config.HibernateUtil;

/**
 * Clase utilitaria para ejecutar manualmente la carga de datos desde XML
 * Útil para pruebas y mantenimiento
 */
public class ManualDataLoader {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  CARGA MANUAL DE DATOS DESDE XML");
        System.out.println("========================================\n");
        
        try {
            // Inicializar Hibernate
            HibernateUtil.getSessionFactory();
            
            // Crear el inicializador y ejecutar
            DataInitializer dataInitializer = new DataInitializer();
            dataInitializer.inicializarDatos();
            
            System.out.println("\n========================================");
            System.out.println("  PROCESO COMPLETADO");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("\n❌ Error durante la carga: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar Hibernate
            HibernateUtil.shutdown();
        }
    }
}
