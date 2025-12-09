package ilcaminodelamamma.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RecetaIngredienteId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer receta_id;
    private Integer ingrediente_id;
    
    public RecetaIngredienteId() {}
    
    public RecetaIngredienteId(Integer receta_id, Integer ingrediente_id) {
        this.receta_id = receta_id;
        this.ingrediente_id = ingrediente_id;
    }
    
    public Integer getReceta_id() {
        return receta_id;
    }
    
    public void setReceta_id(Integer receta_id) {
        this.receta_id = receta_id;
    }
    
    public Integer getIngrediente_id() {
        return ingrediente_id;
    }
    
    public void setIngrediente_id(Integer ingrediente_id) {
        this.ingrediente_id = ingrediente_id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecetaIngredienteId that = (RecetaIngredienteId) o;
        return Objects.equals(receta_id, that.receta_id) &&
               Objects.equals(ingrediente_id, that.ingrediente_id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(receta_id, ingrediente_id);
    }
}
