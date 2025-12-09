package ilcaminodelamamma.model;

import jakarta.persistence.*;

@Entity
@Table(name="detallecomandas")
public class DetalleComanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_detalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;
    
    private Integer cantidad;
    private Float precio_unitario;
    private Float subtotal;
    
    public DetalleComanda() {}
    
    public DetalleComanda(Comanda comanda, Receta receta, Integer cantidad, Float precio_unitario, Float subtotal) {
        this.comanda = comanda;
        this.receta = receta;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
        this.subtotal = subtotal;
    }
    
    public Integer getId_detalle() {
        return id_detalle;
    }
    
    public void setId_detalle(Integer id_detalle) {
        this.id_detalle = id_detalle;
    }
    
    public Comanda getComanda() {
        return comanda;
    }
    
    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }
    
    public Receta getReceta() {
        return receta;
    }
    
    public void setReceta(Receta receta) {
        this.receta = receta;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public Float getPrecio_unitario() {
        return precio_unitario;
    }
    
    public void setPrecio_unitario(Float precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
    
    public Float getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }
}
