package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cart;
import model.service.CartService;
import model.service.CartService.Aggiornamento;
import model.service.CartService.Esito;
import model.service.CartService.Riepilogo;
import model.service.CartService.Stato;
import util.Json;
import util.JsonResponse;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del carrello (pubblico: non serve il login, il carrello sta nella sessione).
 * Qui c'è solo HTTP: si leggono i parametri, si chiama CartService (che applica le regole) e si sceglie la risposta.
 *
 *  GET  /carrello  -> mostra il carrello
 *  POST /carrello  -> modifica il carrello. Il parametro "azione" dice cosa fare:
 *                     aggiungi | aggiorna | rimuovi | svuota
 *
 * Due modi di rispondere a un POST:
 *  - normale (form HTML): redirect a /carrello (Post/Redirect/Get);
 *  - AJAX (per "aggiorna" e per "aggiungi" dal catalogo, quando il browser chiede application/json): niente redirect,
 *    risposta JSON con quantità reale, subtotale e totali.
 */
@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Riepilogo r;
        try {
            r = cartService.riepilogo(SessionUtil.cartOrNull(req));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        req.setAttribute("righe", r.righe());
        req.setAttribute("totale", r.totale());
        req.setAttribute("avviso", r.avviso());
        req.getRequestDispatcher("/WEB-INF/views/carrello.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione") == null ? "" : req.getParameter("azione");
        Integer id = parseIntOrNull(req.getParameter("id"));            // null = non numerico
        Integer quantita = parseIntOrNull(req.getParameter("quantita"));
        Cart cart = SessionUtil.cartOrCreate(req);

        try {
            if (azione.equals("aggiorna") && wantsJson(req)) {
                rispondiJson(resp, cartService.aggiornaERiepiloga(cart, id, quantita), id);
                return;
            }
            if (azione.equals("aggiungi") && wantsJson(req)) {
                // AJAX dal catalogo: si resta sulla pagina, il browser aggiorna badge e messaggio
                Esito e = cartService.aggiungi(cart, id, quantita);
                JsonResponse.send(resp, Json.object(
                        "ok", e.stato() != Stato.ERRORE,
                        "stato", e.stato().name(),
                        "messaggio", e.messaggio(),
                        "totaleArticoli", cart.getTotalItems()));
                return;
            }
            switch (azione) {
                case "aggiungi" -> flash(req, cartService.aggiungi(cart, id, quantita));
                case "aggiorna" -> flash(req, cartService.aggiorna(cart, id, quantita));
                case "rimuovi"  -> cartService.rimuovi(cart, id);
                case "svuota"   -> cartService.svuota(cart);
                default -> {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        resp.sendRedirect(req.getContextPath() + "/carrello");
    }

    /** Traduce il risultato del service in JSON (la forma della risposta è una scelta della servlet/view). */
    private static void rispondiJson(HttpServletResponse resp, Aggiornamento a, Integer id) throws IOException {
        if (a.esito().stato() == Stato.ERRORE) {
            // quantità fuori da 1-99, oppure prodotto non nel carrello (es. sessione scaduta)
            JsonResponse.send(resp, HttpServletResponse.SC_BAD_REQUEST, Json.object(
                    "ok", false,
                    "messaggio", "Impossibile aggiornare la quantità. Ricarica la pagina."));
            return;
        }
        JsonResponse.send(resp, Json.object(
                "ok", true,
                "id", id,
                "quantita", a.riga() == null ? 0 : a.riga().getQuantita(),
                "rimosso", a.riga() == null,
                "messaggio", a.messaggio(),
                "subtotale", a.riga() == null ? null : a.riga().getSubtotale().toPlainString(),
                "totale", a.riepilogo().totale().toPlainString(),
                "totaleArticoli", a.totaleArticoli()));
    }

    private static boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    /** Salva l'esito come messaggio flash: verde se tutto ok, rosso altrimenti. */
    private static void flash(HttpServletRequest req, Esito esito) {
        if (esito.stato() == Stato.OK) {
            SessionUtil.flashOk(req, esito.messaggio());
        } else {
            SessionUtil.flashErr(req, esito.messaggio());
        }
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return Integer.valueOf(s);              // null o "abc" -> NumberFormatException
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
