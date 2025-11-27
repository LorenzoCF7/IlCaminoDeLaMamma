package ilcaminodelamamma.model;

import jakarta.persistence.*;

@Entity
@Table(name="roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_rol;
    private String nombre_Rol;
    private String descripcion;

    public Rol() {}
    public Rol(String nombre_Rol, String descripcion) {
        this.nombre_Rol = nombre_Rol;
        this.descripcion = descripcion;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre_Rol() {
        return nombre_Rol;
    }

    public void setNombre_Rol(String nombre_Rol) {
        this.nombre_Rol = nombre_Rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
