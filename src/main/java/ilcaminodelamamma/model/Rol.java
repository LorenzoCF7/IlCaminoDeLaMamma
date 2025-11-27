package ilcaminodelamamma.model;

public enum Rol {
    ADMIN("Administrador"),
    JEFECOCINA("Jefe de Cocina"),
    CAMARERO("Camarero");
    
    private final String descripcion;
    
    Rol(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
