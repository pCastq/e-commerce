package util;

/** Piccoli aiuti per costruire query SQL in sicurezza. */
public final class SqlUtil {

    private SqlUtil() { }

    /**
     * Neutralizza % _ e \ che in LIKE/ILIKE hanno un significato speciale, così il testo scritto
     * dall'utente è cercato "alla lettera". Da usare con: ... ILIKE ? ESCAPE '\'
     */
    public static String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
