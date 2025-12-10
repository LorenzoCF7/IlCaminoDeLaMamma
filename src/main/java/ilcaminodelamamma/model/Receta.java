package ilcaminodelamamma.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Receta")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_receta;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Integer tiempo_preparacion;
    private Boolean disponible;
    @Lob
    private byte[] imagen;
    private String categoria;
    @Lob
    private String pasos;

    // RELACIÓN ONE-TO-MANY con la tabla intermedia (que contiene cantidad_usada)
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecetaIngrediente> recetaIngredientes = new HashSet<>();

    // ⭐️ RELACIÓN ONE-TO-MANY (Un autor -> muchos libros)
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DetalleComanda> detalleComandas = new HashSet<>();

    public Receta() {

    }

    public Receta(String nombre, String descripcion, Integer precio, Integer tiempo_preparacion, Boolean disponible, byte[] imagen, String categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tiempo_preparacion = tiempo_preparacion;
        this.disponible = disponible;
        this.imagen = imagen;
        this.categoria = categoria;
        this.pasos = null;
    }

    public Integer getId_receta() {
        return id_receta;
    }

    public void setId_receta(Integer id_receta) {
        this.id_receta = id_receta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public Integer getTiempo_preparacion() {
        return tiempo_preparacion;
    }

    public void setTiempo_preparacion(Integer tiempo_preparacion) {
        this.tiempo_preparacion = tiempo_preparacion;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Set<RecetaIngrediente> getRecetaIngredientes() {
        return recetaIngredientes;
    }

    public void setRecetaIngredientes(Set<RecetaIngrediente> recetaIngredientes) {
        this.recetaIngredientes = recetaIngredientes;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPasos() {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public Set<DetalleComanda> getDetalleComandas() {
        return detalleComandas;
    }

    public void setDetalleComandas(Set<DetalleComanda> detalleComandas) {
        this.detalleComandas = detalleComandas;
    }

    

}
