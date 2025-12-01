package ilcaminodelamamma.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Ingrediente;

public class IngredienteDAO {
    public Ingrediente create(Ingrediente ingrediente) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(ingrediente);
        tx.commit();
        session.close();
        return ingrediente;
    }

    public Ingrediente update(Ingrediente ingrediente) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(ingrediente);
        tx.commit();
        session.close();
        return ingrediente;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Ingrediente ingrediente = session.get(Ingrediente.class, id);
        if (ingrediente != null) {
            session.delete(ingrediente);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Ingrediente> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ingrediente> ingredientes = session.createQuery("from Ingrediente where nombre = :nombre", Ingrediente.class)
                .setParameter("nombre", nombre)
                .list();
        session.close();
        return ingredientes;
    }

    public List<Ingrediente> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ingrediente> ingredientes = session.createQuery("from Ingrediente", Ingrediente.class).list();
        session.close();
        return ingredientes;
    }

    public Ingrediente findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Ingrediente ingrediente = session.get(Ingrediente.class, id);
        session.close();
        return ingrediente;
    }
}
