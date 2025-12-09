package ilcaminodelamamma.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Comanda;
import ilcaminodelamamma.model.EstadoComanda;

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
            session.remove(comanda);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Comanda> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Comanda> comandas = session.createQuery(
            "SELECT DISTINCT c FROM Comanda c " +
            "LEFT JOIN FETCH c.mesa " +
            "LEFT JOIN FETCH c.detalleComandas " +
            "ORDER BY c.fecha_hora DESC", 
            Comanda.class
        ).list();
        session.close();
        return comandas;
    }

    public Comanda findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Comanda comanda = session.get(Comanda.class, id);
        session.close();
        return comanda;
    }
    
    /**
     * Encuentra todas las comandas con un estado específico
     * @param estado el estado de la comanda a buscar
     * @return lista de comandas con ese estado
     */
    public List<Comanda> findByEstado(EstadoComanda estado) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Comanda> comandas = session.createQuery(
            "SELECT DISTINCT c FROM Comanda c " +
            "LEFT JOIN FETCH c.mesa " +
            "LEFT JOIN FETCH c.detalleComandas " +
            "WHERE c.estadoComanda = :estado " +
            "ORDER BY c.fecha_hora DESC",
            Comanda.class
        ).setParameter("estado", estado).list();
        session.close();
        return comandas;
    }
}
