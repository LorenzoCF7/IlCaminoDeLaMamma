package ilcaminodelamamma.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.DetalleComanda;

public class DetalleComandaDAO {
    public DetalleComanda create(DetalleComanda detalleComanda) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(detalleComanda);
        tx.commit();
        session.close();
        return detalleComanda;
    }

    public DetalleComanda update(DetalleComanda detalleComanda) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(detalleComanda);
        tx.commit();
        session.close();
        return detalleComanda;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        DetalleComanda detalleComanda = session.get(DetalleComanda.class, id);
        if (detalleComanda != null) {
            session.delete(detalleComanda);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }



    public List<DetalleComanda> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<DetalleComanda> detalleComandas = session.createQuery("from DetalleComanda", DetalleComanda.class).list();
        session.close();
        return detalleComandas;
    }

    public DetalleComanda findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        DetalleComanda detalleComanda = session.get(DetalleComanda.class, id);
        session.close();
        return detalleComanda;
    }
}
