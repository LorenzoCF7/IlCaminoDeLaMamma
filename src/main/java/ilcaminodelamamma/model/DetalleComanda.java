package ilcaminodelamamma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Detalle_Comanda")
public class DetalleComanda {
    
    // Enum para estados de plato individual
    public enum EstadoPlato {
        POR_HACER("Por hacer"),
        EN_COCINA("En cocina"),
        PREPARADO("Preparado");
        
        private final String descripcion;
        
        EstadoPlato(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        @Override
        public String toString() {
            return descripcion;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_detalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comanda", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;
    
    private Integer cantidad;
    private Float precio_unitario;
    private Float subtotal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_plato")
    private EstadoPlato estadoPlato = EstadoPlato.POR_HACER;
    
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
    
    public EstadoPlato getEstadoPlato() {
        return estadoPlato != null ? estadoPlato : EstadoPlato.POR_HACER;
    }
    
    public void setEstadoPlato(EstadoPlato estadoPlato) {
        this.estadoPlato = estadoPlato;
    }
}
