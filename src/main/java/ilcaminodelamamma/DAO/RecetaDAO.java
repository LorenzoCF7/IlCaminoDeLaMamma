package ilcaminodelamamma.DAO;

import ilcaminodelamamma.config.HibernateUtil;
import ilcaminodelamamma.model.Ingrediente;
import ilcaminodelamamma.model.Receta;
import ilcaminodelamamma.model.RecetaIngrediente;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class RecetaDAO {
    public List<Receta> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Receta> recetas = session.createQuery("select distinct r from Receta r left join fetch r.recetaIngredientes", Receta.class)
                .list();
        session.close();
        return recetas;
    }

    public Receta findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Receta receta = session.createQuery(
            "select r from Receta r left join fetch r.recetaIngredientes ri left join fetch ri.ingrediente where r.id_receta = :id", 
            Receta.class)
            .setParameter("id", id)
            .uniqueResult();
        session.close();
        return receta;
    }

    public List<Receta> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Receta> recetas = session.createQuery("select distinct r from Receta r left join fetch r.recetaIngredientes where r.nombre like :nombre", Receta.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        session.close();
        return recetas;
    }

    public List<Receta> findCategoria(String categoria) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Receta> recetas = session.createQuery("from Receta where categoria = :categoria", Receta.class)
                .setParameter("categoria", categoria)
                .list();
        session.close();
        return recetas;
    }

    public Receta create(Receta receta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.persist(receta);
        tx.commit();
        session.close();
        return receta;
    }

    public Receta update(Receta receta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.merge(receta);
        tx.commit();
        session.close();
        return receta;
    }

    public boolean deleteById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Receta receta = session.get(Receta.class, id);
        if (receta != null) {
            session.remove(receta);
            tx.commit();
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public void darIngrediente(Receta receta, Ingrediente ingrediente, Integer cantidad_usada) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        receta = session.merge(receta);
        ingrediente = session.merge(ingrediente);

        RecetaIngrediente recetaIngrediente = new RecetaIngrediente(receta, ingrediente, cantidad_usada);
        receta.getRecetaIngredientes().add(recetaIngrediente);
        
        session.persist(recetaIngrediente);
        tx.commit();
        session.close();
    }
}
