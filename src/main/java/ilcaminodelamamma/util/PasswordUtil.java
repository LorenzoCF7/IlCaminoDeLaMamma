package ilcaminodelamamma.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String rawPassword) {
        if (rawPassword == null) return null;
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean verify(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isHashed(String password) {
        if (password == null) return false;
        // BCrypt hashes usually start with $2a$, $2b$, $2y$
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$") || password.startsWith("$2$");
    }
}
