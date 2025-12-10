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
        List<Comanda> comandas = session.createQuery(
            "SELECT DISTINCT c FROM Comanda c " +
            "LEFT JOIN FETCH c.mesa " +
            "LEFT JOIN FETCH c.detalleComandas", 
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
     * Carga una comanda por id junto con sus detalles y recetas (JOIN FETCH)
     */
    public Comanda findByIdWithDetails(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Comanda> list = session.createQuery(
                "SELECT DISTINCT c FROM Comanda c " +
                "LEFT JOIN FETCH c.mesa m " +
                "LEFT JOIN FETCH c.usuario u " +
                "LEFT JOIN FETCH c.detalleComandas d " +
                "LEFT JOIN FETCH d.receta r " +
                "LEFT JOIN FETCH r.recetaIngredientes ri " +
                "LEFT JOIN FETCH ri.ingrediente ingr " +
                "WHERE c.id_comanda = :id", Comanda.class)
            .setParameter("id", id)
            .list();

            return list.isEmpty() ? null : list.get(0);
        } finally {
            session.close();
        }
    }

    /**
     * Busca la comanda más reciente de una mesa específica
     * @param mesa La mesa a buscar
     * @return La comanda más reciente o null si no hay comandas
     */
    public Comanda findByMesa(ilcaminodelamamma.model.Mesa mesa) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Comanda> comandas = session.createQuery(
                "SELECT DISTINCT c FROM Comanda c " +
                "LEFT JOIN FETCH c.mesa m " +
                "LEFT JOIN FETCH c.usuario u " +
                "LEFT JOIN FETCH c.detalleComandas d " +
                "LEFT JOIN FETCH d.receta r " +
                "LEFT JOIN FETCH r.recetaIngredientes ri " +
                "LEFT JOIN FETCH ri.ingrediente ingr " +
                "WHERE m.id_mesa = :mesaId " +
                "ORDER BY c.fecha_hora DESC", 
                Comanda.class
            )
            .setParameter("mesaId", mesa.getId_mesa())
            .setMaxResults(1)
            .list();
            
            return comandas.isEmpty() ? null : comandas.get(0);
        } finally {
            session.close();
        }
    }
}
