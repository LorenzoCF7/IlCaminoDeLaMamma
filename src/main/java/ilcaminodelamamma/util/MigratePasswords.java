package ilcaminodelamamma.util;

import ilcaminodelamamma.DAO.UsuarioDAO;
import ilcaminodelamamma.model.Usuario;

import java.util.List;

public class MigratePasswords {
    public static void main(String[] args) {
        System.out.println("Iniciando migración de contraseñas...");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.findAll();
        int total = 0;
        int updated = 0;
        for (Usuario u : usuarios) {
            total++;
            String pass = u.getContrasena();
            if (pass == null) continue;
            if (!PasswordUtil.isHashed(pass)) {
                String hashed = PasswordUtil.hash(pass);
                u.setContrasena(hashed);
                usuarioDAO.update(u);
                updated++;
                System.out.println("Hasheada contraseña de usuario: " + u.getNombre());
            }
        }
        System.out.println("Migración finalizada. Total usuarios: " + total + ", actualizados: " + updated);
    }
}
