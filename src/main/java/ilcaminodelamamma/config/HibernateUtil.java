package ilcaminodelamamma.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.net.URL;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    static {
        try {
            Configuration configuration = new Configuration();
            
            // Obtener la URL del archivo desde el classpath
            URL hibernateConfigUrl = HibernateUtil.class.getClassLoader()
                    .getResource("hibernate.cfg.xml");
            
            if (hibernateConfigUrl != null) {
                System.out.println("✓ Archivo hibernate.cfg.xml encontrado en: " + hibernateConfigUrl);
                // Usar configure con la URL del recurso
                configuration.configure(hibernateConfigUrl);
            } else {
                System.out.println("⚠ Intentando cargar hibernate.cfg.xml con configure() por defecto");
                configuration.configure();
            }
            
            sessionFactory = configuration.buildSessionFactory();
            System.out.println("✓ SessionFactory creada exitosamente");
        } catch (Throwable ex) {
            System.err.println("❌ Error al crear SessionFactory: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
