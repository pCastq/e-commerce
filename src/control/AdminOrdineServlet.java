package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ordine;
import model.service.OrdineService;

import java.io.IOException;
import java.sql.SQLException;

/** GET /admin/ordini/dettaglio?id=N: un ordine qualunque, con i dati del cliente (solo admin). */
@WebServlet("/admin/ordini/dettaglio")
public class AdminOrdineServlet extends HttpServlet {

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

        Ordine ordine;
        try {
            ordine = ordineService.trovaPerAdmin(id);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        if (ordine == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("ordine", ordine);
        req.getRequestDispatcher("/WEB-INF/views/admin/ordine.jsp").forward(req, resp);
    }
}
