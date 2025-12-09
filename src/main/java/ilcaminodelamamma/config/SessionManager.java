package ilcaminodelamamma.config;

import ilcaminodelamamma.model.Usuario;

/**
 * Gestiona la sesión del usuario actual en la aplicación
 */
public class SessionManager {
    private static Usuario usuarioActual;
    
    /**
     * Establece el usuario que ha iniciado sesión
     */
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
        if (usuario != null) {
            System.out.println("✅ Sesión iniciada: " + usuario.getNombre() + " (Rol: " + usuario.getRol() + ")");
        }
    }
    
    /**
     * Obtiene el usuario actual logueado
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public static void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("❌ Sesión cerrada: " + usuarioActual.getNombre());
        }
        usuarioActual = null;
    }
    
    /**
     * Verifica si hay un usuario logueado
     */
    public static boolean hayUsuarioLogueado() {
        return usuarioActual != null;
    }
}
