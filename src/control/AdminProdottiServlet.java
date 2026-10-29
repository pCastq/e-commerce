package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.ProductService;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER dell'elenco prodotti per l'amministratore. L'accesso è controllato da AdminFilter (/admin/*).
 *  GET  /admin            -> redirect all'elenco
 *  GET  /admin/prodotti   -> elenco di TUTTI i prodotti (anche eliminati)
 *  POST /admin/prodotti   -> azione = elimina | ripristina (eliminazione logica), poi redirect all'elenco
 */
@WebServlet({"/admin", "/admin/prodotti"})
public class AdminProdottiServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getServletPath().equals("/admin")) {
            resp.sendRedirect(req.getContextPath() + "/admin/prodotti");
            return;
        }
        try {
            req.setAttribute("prodotti", productService.tutti());
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/prodotti.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if ("elimina".equals(azione)) {
                if (productService.elimina(id)) {
                    SessionUtil.flashOk(req, "Prodotto eliminato dal catalogo (resta nello storico degli ordini). Puoi ripristinarlo dall'elenco.");
                } else {
                    SessionUtil.flashErr(req, "Prodotto non trovato.");
                }
            } else if ("ripristina".equals(azione)) {
                if (productService.ripristina(id)) {
                    SessionUtil.flashOk(req, "Prodotto ripristinato: è di nuovo a catalogo.");
                } else {
                    SessionUtil.flashErr(req, "Prodotto non trovato.");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/prodotti");
    }
}
