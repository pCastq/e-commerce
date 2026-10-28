package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartLine;
import model.Product;
import model.dao.ProductDAO;
import util.Json;
import util.SessionKeys;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER del carrello (pubblico: non serve il login, il carrello sta nella sessione).
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

    private final ProductDAO productDAO = new ProductDAO();

    /** Righe da mostrare, totale e un eventuale avviso (es. prodotto non più disponibile). */
    private record Riepilogo(List<CartLine> righe, BigDecimal totale, String avviso) { }

    /** Esito di un'operazione sul carrello: messaggio per l'utente e se è un errore/avviso. */
    private record Esito(String messaggio, boolean errore) { }

    // ------------------------------------------------------------------ GET: mostra
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Cart cart = (session == null) ? null : (Cart) session.getAttribute(SessionKeys.CART);

        Riepilogo r;
        try {
            r = riepilogo(cart);
        } catch (SQLException ex) {
            throw new ServletException("Errore di accesso al database", ex);
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

        HttpSession session = req.getSession(true);        // true: qui il carrello va creato se manca
        Cart cart = (Cart) session.getAttribute(SessionKeys.CART);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(SessionKeys.CART, cart);
        }

        try {
            if (azione.equals("aggiorna") && wantsJson(req)) {
                aggiornaAjax(req, resp, cart);
                return;
            }

            switch (azione) {
                case "aggiungi" -> aggiungi(req, session, cart);
                case "aggiorna" -> flash(session, aggiorna(parseIntOrNull(req.getParameter("id")),
                                                           parseQuantita(req.getParameter("quantita")), cart));
                case "rimuovi"  -> rimuovi(req, cart);
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
    private void aggiornaAjax(HttpServletRequest req, HttpServletResponse resp, Cart cart)
            throws SQLException, IOException {

        Integer id = parseIntOrNull(req.getParameter("id"));
        Integer q = parseQuantita(req.getParameter("quantita"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        if (id == null || q == null || cart.getQuantita(id) == 0) {
            // quantità fuori da 1-99, oppure prodotto non nel carrello (es. sessione scaduta)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"messaggio\":"
                    + Json.quote("Impossibile aggiornare la quantità. Ricarica la pagina.") + "}");
            return;
        }

        Esito esito = aggiorna(id, q, cart);
        Riepilogo r = riepilogo(cart);          // rilegge dal database e riallinea il carrello

        CartLine riga = null;
        for (CartLine l : r.righe()) {
            if (l.getProdotto().getId() == id) {
                riga = l;
            }
        }

        String messaggio = esito.errore() ? esito.messaggio() : r.avviso();
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

    private static boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    // ------------------------------------------------------------------ riepilogo (usato da GET e AJAX)
    /**
     * Costruisce le righe del carrello leggendo i prodotti dal database in una sola query, e riallinea
     * il carrello: toglie i prodotti non più disponibili e riduce le quantità oltre la giacenza.
     */
    private Riepilogo riepilogo(Cart cart) throws SQLException {
        List<CartLine> righe = new ArrayList<>();
        BigDecimal totale = BigDecimal.ZERO;
        String avviso = null;

        if (cart != null && !cart.isEmpty()) {
            Map<Integer, Product> prodotti = new HashMap<>();
            for (Product p : productDAO.findActiveByIds(cart.getItems().keySet())) {
                prodotti.put(p.getId(), p);
            }

            for (Map.Entry<Integer, Integer> e : cart.getItems().entrySet()) {
                Product p = prodotti.get(e.getKey());
                int quantita = e.getValue();

                if (p == null || p.getGiacenza() <= 0) {
                    cart.remove(e.getKey());
                    avviso = "Alcuni prodotti non sono più disponibili e sono stati rimossi dal carrello.";
                    continue;
                }
                if (quantita > p.getGiacenza()) {
                    quantita = p.getGiacenza();
                    cart.set(p.getId(), quantita);
                    avviso = "Alcune quantità sono state ridotte in base alla disponibilità.";
                }
                CartLine riga = new CartLine(p, quantita);
                righe.add(riga);
                totale = totale.add(riga.getSubtotale());
            }
        }
        return new Riepilogo(righe, totale, avviso);
    }

    // ------------------------------------------------------------------ azioni
    private void aggiungi(HttpServletRequest req, HttpSession session, Cart cart) throws SQLException {
        Integer id = parseIntOrNull(req.getParameter("id"));
        Integer q = parseQuantita(req.getParameter("quantita"));
        if (id == null || q == null) {
            session.setAttribute(SessionKeys.FLASH_ERR, "Dati non validi.");
            return;
        }
        Product p = productDAO.findActiveById(id);
        if (p == null) {
            session.setAttribute(SessionKeys.FLASH_ERR, "Prodotto non disponibile.");
            return;
        }
        if (p.getGiacenza() <= 0) {
            session.setAttribute(SessionKeys.FLASH_ERR, "«" + p.getNome() + "» è esaurito.");
            return;
        }
        int nuova = cart.getQuantita(id) + q;
        nuova = Math.min(nuova, Cart.MAX_QUANTITA);
        if (nuova > p.getGiacenza()) {
            nuova = p.getGiacenza();
            session.setAttribute(SessionKeys.FLASH_ERR,
                    "Disponibili solo " + p.getGiacenza() + " pezzi di «" + p.getNome() + "»: quantità adeguata.");
        } else {
            session.setAttribute(SessionKeys.FLASH_OK, "«" + p.getNome() + "» aggiunto al carrello.");
        }
        cart.set(id, nuova);
    }

    /** Imposta la quantità di un prodotto già nel carrello (valore assoluto, mai "+1"). */
    private Esito aggiorna(Integer id, Integer q, Cart cart) throws SQLException {
        if (id == null || q == null || cart.getQuantita(id) == 0) {
            return new Esito("Dati non validi.", true);
        }
        Product p = productDAO.findActiveById(id);
        if (p == null || p.getGiacenza() <= 0) {
            cart.remove(id);
            return new Esito("Prodotto non più disponibile: rimosso dal carrello.", true);
        }
        if (q > p.getGiacenza()) {
            cart.set(id, p.getGiacenza());
            return new Esito("Disponibili solo " + p.getGiacenza() + " pezzi di «" + p.getNome()
                    + "»: quantità adeguata.", true);
        }
        cart.set(id, q);
        return new Esito("Quantità aggiornata.", false);
    }

    private void rimuovi(HttpServletRequest req, Cart cart) {
        Integer id = parseIntOrNull(req.getParameter("id"));
        if (id != null) {
            cart.remove(id);
        }
    }

    // ------------------------------------------------------------------ utilità
    /** Salva l'esito come messaggio flash (mostrato una volta dopo il redirect). */
    private static void flash(HttpSession session, Esito esito) {
        session.setAttribute(esito.errore() ? SessionKeys.FLASH_ERR : SessionKeys.FLASH_OK, esito.messaggio());
    }

    private static Integer parseIntOrNull(String s) {
        try {
            return Integer.valueOf(s);              // null o "abc" -> NumberFormatException
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Quantità valida: intero da 1 a Cart.MAX_QUANTITA; altrimenti null. */
    private static Integer parseQuantita(String s) {
        Integer q = parseIntOrNull(s);
        return (q == null || q < 1 || q > Cart.MAX_QUANTITA) ? null : q;
    }
}
