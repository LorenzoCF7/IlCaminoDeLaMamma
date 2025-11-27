package ilcaminodelamamma;

import ilcaminodelamamma.DAO.UsuarioDAO;
import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

           Usuario usuario = new Usuario("Mario", "Hormigas", "osohormiguero@hormiga.a", "1234", ilcaminodelamamma.model.Rol.ADMIN);
           usuarioDAO.create(usuario);

    }
}
