package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ordine;
import model.User;
import model.dao.OrdineDAO;
import util.SessionKeys;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Pagina di conferma: GET /ordini/confermato?id=N (protetta da AuthFilter tramite /ordini/*).
 * Mostra l'ordine solo al suo proprietario; altrimenti 404 (non si rivela nemmeno che l'ordine esiste).
 */
@WebServlet("/ordini/confermato")
public class OrdineConfermatoServlet extends HttpServlet {

    private final OrdineDAO ordineDAO = new OrdineDAO();

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
            ordine = ordineDAO.findByIdAndUser(id, utente.getId());
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        if (ordine == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("ordine", ordine);
        req.getRequestDispatcher("/WEB-INF/views/ordine-confermato.jsp").forward(req, resp);
    }
}
