package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartLine;
import model.service.CartService;
import model.service.CartService.Esito;
import model.service.CartService.Riepilogo;
import model.service.CartService.Stato;
import util.Json;
import util.SessionKeys;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del carrello (pubblico: non serve il login, il carrello sta nella sessione).
 * Le regole (quantità, giacenza, disponibilità) stanno in CartService: qui si legge la richiesta,
 * si chiama il service e si sceglie la risposta.
 *
 *  GET  /carrello  -> mostra il carrello
 *  POST /carrello  -> modifica il carrello. Il parametro "azione" dice cosa fare:
 *                     aggiungi | aggiorna | rimuovi | svuota
 *
 * Due modi di rispondere a un POST:
 *  - normale (form HTML): redirect a /carrello (Post/Redirect/Get);
 *  - AJAX (solo per "aggiorna", quando il browser chiede application/json): niente redirect,
 *    risposta JSON con quantità reale, subtotale e totali, così la pagina si aggiorna senza ricaricarsi.
 */
@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    // ------------------------------------------------------------------ GET: mostra
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Cart cart = (session == null) ? null : (Cart) session.getAttribute(SessionKeys.CART);

        Riepilogo r;
        try {
            r = cartService.riepilogo(cart);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        req.setAttribute("righe", r.righe());
        req.setAttribute("totale", r.totale());
        req.setAttribute("avviso", r.avviso());
        req.getRequestDispatcher("/WEB-INF/views/carrello.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------ POST: modifica
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");
        if (azione == null) {
            azione = "";
        }
        Integer id = parseIntOrNull(req.getParameter("id"));            // null = non numerico
        Integer quantita = parseIntOrNull(req.getParameter("quantita"));

        HttpSession session = req.getSession(true);        // true: qui il carrello va creato se manca
        Cart cart = (Cart) session.getAttribute(SessionKeys.CART);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(SessionKeys.CART, cart);
        }

        try {
            if (azione.equals("aggiorna") && wantsJson(req)) {
                aggiornaAjax(resp, cart, id, quantita);
                return;
            }

            switch (azione) {
                case "aggiungi" -> flash(session, cartService.aggiungi(cart, id, quantita));
                case "aggiorna" -> flash(session, cartService.aggiorna(cart, id, quantita));
                case "rimuovi"  -> { if (id != null) cart.remove(id); }
                case "svuota"   -> cart.clear();
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

    // ------------------------------------------------------------------ aggiornamento via AJAX
    private void aggiornaAjax(HttpServletResponse resp, Cart cart, Integer id, Integer quantita)
            throws SQLException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        Esito esito = cartService.aggiorna(cart, id, quantita);
        if (esito.stato() == Stato.ERRORE) {
            // quantità fuori da 1-99, oppure prodotto non nel carrello (es. sessione scaduta)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"messaggio\":"
                    + Json.quote("Impossibile aggiornare la quantità. Ricarica la pagina.") + "}");
            return;
        }

        Riepilogo r = cartService.riepilogo(cart);   // rilegge dal database e riallinea il carrello
        CartLine riga = null;
        for (CartLine l : r.righe()) {
            if (l.getProdotto().getId() == id) {
                riga = l;
            }
        }

        String messaggio = (esito.stato() == Stato.AVVISO) ? esito.messaggio() : r.avviso();
        StringBuilder json = new StringBuilder("{\"ok\":true");
        json.append(",\"id\":").append(id);
        json.append(",\"quantita\":").append(riga == null ? 0 : riga.getQuantita());
        json.append(",\"rimosso\":").append(riga == null);
        json.append(",\"messaggio\":").append(Json.quote(messaggio));
        json.append(",\"subtotale\":").append(Json.quote(riga == null ? null : riga.getSubtotale().toPlainString()));
        json.append(",\"totale\":").append(Json.quote(r.totale().toPlainString()));
        json.append(",\"totaleArticoli\":").append(cart.getTotalItems());
        json.append('}');
        resp.getWriter().write(json.toString());
    }

    // ------------------------------------------------------------------ utilità HTTP
    private static boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    /** Salva l'esito come messaggio flash (mostrato una volta dopo il redirect). */
    private static void flash(HttpSession session, Esito esito) {
        String chiave = (esito.stato() == Stato.OK) ? SessionKeys.FLASH_OK : SessionKeys.FLASH_ERR;
        session.setAttribute(chiave, esito.messaggio());
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return Integer.valueOf(s);              // null o "abc" -> NumberFormatException
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
