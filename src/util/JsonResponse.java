package util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Invia una risposta JSON con le intestazioni giuste (prima ripetute in ogni servlet AJAX). */
public final class JsonResponse {

    private JsonResponse() { }

    public static void send(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");     // risposte dinamiche: il browser non le deve riusare
        resp.getWriter().write(json);
    }

    public static void send(HttpServletResponse resp, String json) throws IOException {
        send(resp, HttpServletResponse.SC_OK, json);
    }
}
