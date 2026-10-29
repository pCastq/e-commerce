package util;

import java.util.List;

/** Piccolo aiuto per scrivere JSON a mano senza librerie esterne. */
public final class Json {

    private Json() { }

    /** Un frammento di JSON già pronto (es. un oggetto annidato): non viene racchiuso di nuovo tra virgolette. */
    public record Raw(String json) { }

    /**
     * Costruisce un oggetto JSON da coppie chiave, valore: Json.object("ok", true, "id", 3, "nome", "Mela").
     * String -> tra virgolette (con escape); Number e Boolean -> così come sono; null -> null; Raw -> senza modifiche.
     */
    public static String object(Object... coppie) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < coppie.length; i += 2) {
            if (i > 0) sb.append(',');
            sb.append(quote((String) coppie[i])).append(':').append(valore(coppie[i + 1]));
        }
        return sb.append('}').toString();
    }

    /** Un array JSON da una lista di valori (con le stesse regole di object). */
    public static String array(List<?> valori) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < valori.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(valore(valori.get(i)));
        }
        return sb.append(']').toString();
    }

    private static String valore(Object v) {
        if (v == null) return "null";
        if (v instanceof Raw r) return r.json();
        if (v instanceof Number || v instanceof Boolean) return v.toString();
        return quote(v.toString());
    }

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
