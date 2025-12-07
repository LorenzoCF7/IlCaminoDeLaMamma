package ilcaminodelamamma.DAO;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Usuario;
import ilcaminodelamamma.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UsuarioDAO {
    public Usuario create(Usuario usuario) {
        // Hash password before persisting if not already hashed
        if (usuario.getContrasena() != null && !PasswordUtil.isHashed(usuario.getContrasena())) {
            usuario.setContrasena(PasswordUtil.hash(usuario.getContrasena()));
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(usuario);
        tx.commit();
        session.close();
        return usuario;
    }

    public Usuario update(Usuario usuario) {
        // Hash password before updating if not already hashed
        if (usuario.getContrasena() != null && !PasswordUtil.isHashed(usuario.getContrasena())) {
            usuario.setContrasena(PasswordUtil.hash(usuario.getContrasena()));
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(usuario);
        tx.commit();
        session.close();
        return usuario;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Usuario usuario = session.get(Usuario.class, id);
        if (usuario != null) {
            session.delete(usuario);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Usuario> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        // Buscar por nombre de forma insensible a mayúsculas
        List<Usuario> usuarios = session.createQuery("from Usuario where lower(nombre) = :nombre", Usuario.class)
            .setParameter("nombre", nombre.toLowerCase())
            .list();
        session.close();
        return usuarios;
    }

    public List<Usuario> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Usuario> usuarios = session.createQuery("from Usuario", Usuario.class).list();
        session.close();
        return usuarios;
    }

    public Usuario findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Usuario usuario = session.get(Usuario.class, id);
        session.close();
        return usuario;
    }
}
