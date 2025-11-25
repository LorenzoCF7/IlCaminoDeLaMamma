package ilcaminodelamamma.model;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    // RELACIÓN MANY-TO-ONE (Muchos libros -> un autor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = true)
    private Rol rol;

    public Usuario() {}

}
