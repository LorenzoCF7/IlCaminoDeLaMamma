package ilcaminodelamamma.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Mesa;

public class MesaDAO {
 public Mesa create(Mesa mesa) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(mesa);
        tx.commit();
        session.close();
        return mesa;
    }

    public Mesa update(Mesa mesa) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(mesa);
        tx.commit();
        session.close();
        return mesa;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Mesa mesa = session.get(Mesa.class, id);
        if (mesa != null) {
            session.delete(mesa);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public List<Mesa> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Mesa> mesas = session.createQuery("from Mesa", Mesa.class).list();
        session.close();
        return mesas;
    }

    public Mesa findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Mesa mesa = session.get(Mesa.class, id);
        session.close();
        return mesa;
    }
    
    /**
     * Crea una mesa con un ID específico usando SQL nativo
     * Esto bypasea la generación automática de IDs de Hibernate
     */
    public Mesa createWithSpecificId(Integer id, ilcaminodelamamma.model.EstadoMesa estado) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        // Usar SQL nativo para insertar con ID específico
        session.createNativeQuery(
            "INSERT INTO mesas (id_mesa, estado) VALUES (:id, :estado)")
            .setParameter("id", id)
            .setParameter("estado", estado.name())
            .executeUpdate();
        
        tx.commit();
        session.close();
        
        // Recuperar la mesa recién creada
        return findById(id);
    }   
}
