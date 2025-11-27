package ilcaminodelamamma.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ingrediente;
    private String nombre;
    private String unidad_medida;
    private Integer cantidad_stock;

    // RELACIÓN ONE-TO-MANY con la tabla intermedia
    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecetaIngrediente> recetaIngredientes = new HashSet<>();

    public Ingrediente() {}

    public Ingrediente(String nombre, String unidad_medida, Integer cantidad_stock) {
        this.nombre = nombre;
        this.unidad_medida = unidad_medida;
        this.cantidad_stock = cantidad_stock;
    }

    public Integer getId_ingrediente() {
        return id_ingrediente;
    }

    public void setId_ingrediente(Integer id_ingrediente) {
        this.id_ingrediente = id_ingrediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

    public Integer getCantidad_stock() {
        return cantidad_stock;
    }

    public void setCantidad_stock(Integer cantidad_stock) {
        this.cantidad_stock = cantidad_stock;
    }

    public Set<RecetaIngrediente> getRecetaIngredientes() {
        return recetaIngredientes;
    }

    public void setRecetaIngredientes(Set<RecetaIngrediente> recetaIngredientes) {
        this.recetaIngredientes = recetaIngredientes;
    }
}
