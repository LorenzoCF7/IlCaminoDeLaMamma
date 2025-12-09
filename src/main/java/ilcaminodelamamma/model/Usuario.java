package ilcaminodelamamma.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    private String nombre;
    private String contrasena;
    
    @Enumerated(EnumType.STRING)
    private RolEnum rol;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comanda> comandas = new HashSet<>();

    public Usuario() {}
    
    public Usuario(String nombre, String contrasena, RolEnum rol) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }
    
    public Integer getId_usuario() {
        return id_usuario;
    }
    
    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public RolEnum getRol() {
        return rol;
    }
    
    public void setRol(RolEnum rol) {
        this.rol = rol;
    }
    
    public Set<Comanda> getComandas() {
        return comandas;
    }
    
    public void setComandas(Set<Comanda> comandas) {
        this.comandas = comandas;
    }
}
