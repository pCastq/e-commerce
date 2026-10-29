package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cart;
import model.User;
import model.service.CartService;
import model.service.CartService.Riepilogo;
import model.service.OrdineService;
import model.service.OrdineService.Risultato;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del checkout (protetto da AuthFilter: serve il login).
 *  GET  /checkout -> riepilogo del carrello + indirizzo di spedizione
 *  POST /checkout -> chiede a OrdineService di creare l'ordine (transazione)
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/checkout.jsp";

    private final CartService cartService = new CartService();
    private final OrdineService ordineService = new OrdineService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!mostraRiepilogo(req, resp)) {
            return;
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User utente = SessionUtil.currentUser(req);          // il filtro garantisce che esista
        Cart cart = SessionUtil.cartOrNull(req);
        String indirizzo = req.getParameter("indirizzo");

        if (cart == null) {
            SessionUtil.flashErr(req, CartService.MSG_CARRELLO_VUOTO);
            resp.sendRedirect(req.getContextPath() + "/carrello");
            return;
        }

        Risultato r;
        try {
            r = ordineService.conferma(utente, cart, indirizzo);
        } catch (SQLException e) {
            throw new ServletException("Errore durante la conferma dell'ordine", e);   // -> pagina 500
        }

        if (r.ok()) {
            SessionUtil.flashOk(req, "Ordine effettuato. Grazie per l'acquisto!");
            resp.sendRedirect(req.getContextPath() + "/ordini/dettaglio?id=" + r.idOrdine());
            return;
        }

        switch (r.problema()) {
            case INDIRIZZO_NON_VALIDO -> {
                // si rimostra il form con l'errore e quanto già scritto
                if (!mostraRiepilogo(req, resp)) {
                    return;
                }
                req.setAttribute("errore", r.messaggio());
                req.setAttribute("indirizzo", indirizzo);
                req.getRequestDispatcher(VIEW).forward(req, resp);
            }
            default -> {
                SessionUtil.flashErr(req, r.messaggio());
                resp.sendRedirect(req.getContextPath() + "/carrello");
            }
        }
    }

    /** Carica il riepilogo; se il carrello è vuoto fa redirect al carrello e restituisce false. */
    private boolean mostraRiepilogo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Riepilogo riepilogo;
        try {
            riepilogo = cartService.riepilogo(SessionUtil.cartOrNull(req));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        if (riepilogo.vuoto()) {
            SessionUtil.flashErr(req, CartService.MSG_CARRELLO_VUOTO);
            resp.sendRedirect(req.getContextPath() + "/carrello");
            return false;
        }
        req.setAttribute("righe", riepilogo.righe());
        req.setAttribute("totale", riepilogo.totale());
        req.setAttribute("avviso", riepilogo.avviso());
        return true;
    }
}
