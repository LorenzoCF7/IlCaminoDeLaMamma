package ilcaminodelamamma.model;

/**
 * Enum que representa los estados posibles de una comanda
 */
public enum EstadoComanda {
    POR_HACER("Por hacer"),
    EN_PREPARACION("En preparación"),
    PREPARADO("Preparado");

    private final String descripcion;

    EstadoComanda(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
