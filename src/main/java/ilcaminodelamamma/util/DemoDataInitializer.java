package ilcaminodelamamma.util;

import ilcaminodelamamma.DAO.UsuarioDAO;
import ilcaminodelamamma.model.RolEnum;
import ilcaminodelamamma.model.Usuario;

import java.util.List;

public class DemoDataInitializer {

    public static void ensureDefaultUsers() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        createIfMissing(usuarioDAO, "chef", "1234", RolEnum.JEFE);
        createIfMissing(usuarioDAO, "assistant", "1234", RolEnum.ADMIN);
        createIfMissing(usuarioDAO, "waiter", "1234", RolEnum.CAMARERO);
    }

    private static void createIfMissing(UsuarioDAO dao, String nombre, String password, RolEnum rol) {
        List<ilcaminodelamamma.model.Usuario> found = dao.findByNombre(nombre);
        if (found == null || found.isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setContrasena(password); // UsuarioDAO.create() will hash if needed
            u.setRol(rol);
            dao.create(u);
            System.out.println("Usuario creado: " + nombre + " (rol=" + rol + ")");
        } else {
            System.out.println("Usuario ya existe: " + nombre);
        }
    }
}
