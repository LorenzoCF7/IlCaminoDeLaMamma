package ilcaminodelamamma;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.util.XMLRecetaLoader;

public class prueba {

    public static void main(String[] args) {
        System.out.println("=== CARGANDO RECETAS DESDE XML ===\n");
        
        try {
            // Inicializar Hibernate
            HibernateUtil.getSessionFactory();
            
            // Crear el loader y ejecutarlo
            XMLRecetaLoader loader = new XMLRecetaLoader();
            int recetasProcesadas = loader.cargarRecetasDesdeXML();
            
            System.out.println("\n=== PROCESO COMPLETADO ===");
            System.out.println("Total de recetas procesadas: " + recetasProcesadas);
            
            // Cerrar Hibernate
            HibernateUtil.shutdown();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
