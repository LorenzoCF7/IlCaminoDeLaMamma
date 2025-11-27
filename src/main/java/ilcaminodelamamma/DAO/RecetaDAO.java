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
        List<Receta> recetas = session.createQuery("from Receta", Receta.class).list();
        session.close();
        return recetas;
    }

    public Receta findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Receta receta = session.get(Receta.class, id);
        session.close();
        return receta;
    }

    public List<Receta> findByNombre(String nombre) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Receta> recetas = session.createQuery("from Receta where nombre = :nombre", Receta.class)
                .setParameter("nombre", nombre)
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
            session.delete(receta);
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
