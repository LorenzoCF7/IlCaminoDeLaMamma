package ilcaminodelamamma.DAO;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UsuarioDAO {
    public Usuario create(Usuario autor) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(autor);
        tx.commit();
        session.close();
        return autor;
    }

    public Usuario update(Usuario autor) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(autor);
        tx.commit();
        session.close();
        return autor;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Usuario autor = session.get(Usuario.class, id);
        if (autor != null) {
            session.delete(autor);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Usuario> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Usuario> autores = session.createQuery("from Usuario where nombre = :nombre", Usuario.class)
                .setParameter("nombre", nombre)
                .list();
        session.close();
        return autores;
    }

    public List<Usuario> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Usuario> autores = session.createQuery("from Usuario", Usuario.class).list();
        session.close();
        return autores;
    }

    public Usuario findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Usuario autor = session.get(Usuario.class, id);
        session.close();
        return autor;
    }
}
