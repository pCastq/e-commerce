package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Hash delle password con SHA-256 (scelta didattica: hash semplice, senza sale).
 * Nel database NON si salva mai la password in chiaro, ma solo questa impronta
 * di 64 caratteri esadecimali. Un hash è a senso unico: dall'impronta non si
 * "decifra" la password; al login si rifà l'hash di quella digitata e si confrontano.
 */
public final class PasswordUtil {

    private PasswordUtil() { }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);        // es. "a3f5..." (64 caratteri)
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponibile", e);   // non succede in Java
        }
    }

    /** Vero se la password digitata corrisponde all'hash salvato. */
    public static boolean matches(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }
}
