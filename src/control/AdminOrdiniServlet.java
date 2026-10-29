package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.OrdineService;
import model.service.OrdineService.Ricerca;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER dell'elenco ordini per l'amministratore (AdminFilter protegge /admin/*).
 *  GET /admin/ordini?dal=2026-10-01&al=2026-10-31&cliente=rossi
 * I filtri sono nell'URL (GET, non POST): una ricerca si può salvare tra i preferiti, ricaricare e condividere.
 */
@WebServlet("/admin/ordini")
public class AdminOrdiniServlet extends HttpServlet {

    private final OrdineService ordineService = new OrdineService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String dal = req.getParameter("dal");
        String al = req.getParameter("al");
        String cliente = req.getParameter("cliente");

        Ricerca r;
        try {
            r = ordineService.cerca(dal, al, cliente);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        req.setAttribute("ordini", r.ordini());
        req.setAttribute("errori", r.errori());
        req.setAttribute("troncato", r.troncato());
        req.setAttribute("totale", r.totale());
        req.setAttribute("limite", OrdineService.LIMITE_RICERCA);
        // i filtri così come scritti, per rimostrarli nel form
        req.setAttribute("dal", dal == null ? "" : dal);
        req.setAttribute("al", al == null ? "" : al);
        req.setAttribute("cliente", cliente == null ? "" : cliente);
        req.getRequestDispatcher("/WEB-INF/views/admin/ordini.jsp").forward(req, resp);
    }
}
