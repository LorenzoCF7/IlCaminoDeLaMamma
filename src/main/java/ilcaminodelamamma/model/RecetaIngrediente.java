package ilcaminodelamamma.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recetas_ingredientes")
public class RecetaIngrediente {
    
    @EmbeddedId
    private RecetaIngredienteId id;
    
    @ManyToOne
    @MapsId("receta_id")
    @JoinColumn(name = "receta_id")
    private Receta receta;
    
    @ManyToOne
    @MapsId("ingrediente_id")
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;
    
    private Integer cantidad_usada;
    
    public RecetaIngrediente() {}
    
    public RecetaIngrediente(Receta receta, Ingrediente ingrediente, Integer cantidad_usada) {
        this.receta = receta;
        this.ingrediente = ingrediente;
        this.cantidad_usada = cantidad_usada;
        this.id = new RecetaIngredienteId(receta.getId_receta(), ingrediente.getId_ingrediente());
    }
    
    public RecetaIngredienteId getId() {
        return id;
    }
    
    public void setId(RecetaIngredienteId id) {
        this.id = id;
    }
    
    public Receta getReceta() {
        return receta;
    }
    
    public void setReceta(Receta receta) {
        this.receta = receta;
    }
    
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
    
    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }
    
    public Integer getCantidad_usada() {
        return cantidad_usada;
    }
    
    public void setCantidad_usada(Integer cantidad_usada) {
        this.cantidad_usada = cantidad_usada;
    }
}
