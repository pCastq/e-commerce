package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Aiuti per la navigazione (redirect): usati da LoginServlet e dai filtri di accesso. */
public final class Navigation {

    private Navigation() { }

    /**
     * Accetta come "pagina di ritorno" solo un percorso interno all'applicazione (es. /profilo).
     * Rifiuta indirizzi esterni (http://..., //sito.com): sarebbe un "open redirect".
     * Restituisce il percorso, oppure null se non è accettabile.
     */
    public static String safeNext(String next) {
        if (next == null || next.isEmpty()) return null;
        if (!next.startsWith("/") || next.startsWith("//") || next.contains("\\")
                || next.contains(":") || next.contains("\r") || next.contains("\n")) {
            return null;
        }
        return next;
    }

    /** Manda al login ricordando la pagina richiesta (?next=...), per tornarci dopo l'accesso. */
    public static void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (req.getQueryString() != null) {
            path += "?" + req.getQueryString();
        }
        resp.sendRedirect(req.getContextPath() + "/login?next=" + URLEncoder.encode(path, StandardCharsets.UTF_8));
    }
}
