package ilcaminodelamamma.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Mesa")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mesa;
    
    @Enumerated(EnumType.STRING)
    private EstadoMesa estado;
    
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comanda> comandas = new HashSet<>();
    
    public Mesa() {}
    
    public Mesa(EstadoMesa estado) {
        this.estado = estado;
    }
    
    public Integer getId_mesa() {
        return id_mesa;
    }
    
    public void setId_mesa(Integer id_mesa) {
        this.id_mesa = id_mesa;
    }
    
    public EstadoMesa getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }
    
    public Set<Comanda> getComandas() {
        return comandas;
    }
    
    public void setComandas(Set<Comanda> comandas) {
        this.comandas = comandas;
    }
}
