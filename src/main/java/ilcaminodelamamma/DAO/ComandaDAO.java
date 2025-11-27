package ilcaminodelamamma.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Comanda;

public class ComandaDAO {
    public Comanda create(Comanda comanda) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(comanda);
        tx.commit();
        session.close();
        return comanda;
    }

    public Comanda update(Comanda comanda) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(comanda);
        tx.commit();
        session.close();
        return comanda;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Comanda comanda = session.get(Comanda.class, id);
        if (comanda != null) {
            session.delete(comanda);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Comanda> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Comanda> comandas = session.createQuery("from Comanda", Comanda.class).list();
        session.close();
        return comandas;
    }

    public Comanda findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Comanda comanda = session.get(Comanda.class, id);
        session.close();
        return comanda;
    }
}
