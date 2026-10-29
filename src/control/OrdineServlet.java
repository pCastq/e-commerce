package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ordine;
import model.User;
import model.service.OrdineService;
import util.SessionKeys;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Due pagine dello stesso ordine, scelte dall'URL (entrambe protette da AuthFilter):
 *   GET /ordini/dettaglio?id=N  -> dettaglio dell'ordine
 *   GET /fattura?id=N           -> fattura stampabile
 * Un ordine si vede solo se è dell'utente loggato; altrimenti 404 (non si rivela se esiste).
 */
@WebServlet({"/ordini/dettaglio", "/fattura"})
public class OrdineServlet extends HttpServlet {

    private final OrdineService ordineService = new OrdineService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User utente = (User) req.getSession(false).getAttribute(SessionKeys.USER);
        Ordine ordine;
        try {
            ordine = ordineService.trovaDi(id, utente);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        if (ordine == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String view = req.getServletPath().equals("/fattura") ? "fattura.jsp" : "ordine.jsp";
        req.setAttribute("ordine", ordine);
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }
}
