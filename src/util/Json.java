package util;

/** Piccolo aiuto per scrivere JSON a mano senza librerie esterne. */
public final class Json {

    private Json() { }

    /**
     * Trasforma un testo in una stringa JSON tra virgolette, con gli opportuni "escape":
     * un apice doppio o un a-capo nel testo romperebbero il JSON. null diventa il valore JSON null.
     */
    public static String quote(String s) {
        if (s == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(s.length() + 2);
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));   // altri caratteri di controllo
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }
}
