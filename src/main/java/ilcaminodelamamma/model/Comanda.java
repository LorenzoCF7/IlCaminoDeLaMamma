package ilcaminodelamamma.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="comandas")
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_comanda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;
    
    private LocalDateTime fecha_hora;
    private Float total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_comanda", nullable = false)
    private EstadoComanda estadoComanda;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<DetalleComanda> detalleComandas = new HashSet<>();

    public Comanda() {}

    public Comanda(Usuario usuario, Mesa mesa, LocalDateTime fecha_hora, Float total) {
        this.usuario = usuario;
        this.mesa = mesa;
        this.fecha_hora = fecha_hora;
        this.total = total;
        this.estadoComanda = EstadoComanda.POR_HACER;
    }
    
    public Comanda(Usuario usuario, Mesa mesa, LocalDateTime fecha_hora, Float total, EstadoComanda estadoComanda) {
        this.usuario = usuario;
        this.mesa = mesa;
        this.fecha_hora = fecha_hora;
        this.total = total;
        this.estadoComanda = estadoComanda;
    }
    
    public Integer getId_comanda() {
        return id_comanda;
    }
    
    public void setId_comanda(Integer id_comanda) {
        this.id_comanda = id_comanda;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Mesa getMesa() {
        return mesa;
    }
    
    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
    
    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }
    
    public void setFecha_hora(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
    
    public Float getTotal() {
        return total;
    }
    
    public void setTotal(Float total) {
        this.total = total;
    }
    
    public EstadoComanda getEstadoComanda() {
        return estadoComanda;
    }
    
    public void setEstadoComanda(EstadoComanda estadoComanda) {
        this.estadoComanda = estadoComanda;
    }
    
    public Set<DetalleComanda> getDetalleComandas() {
        return detalleComandas;
    }
    
    public void setDetalleComandas(Set<DetalleComanda> detalleComandas) {
        this.detalleComandas = detalleComandas;
    }
}
