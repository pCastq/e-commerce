package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.service.OrdineService;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;

/** GET /ordini: lo storico degli ordini dell'utente loggato (protetto da AuthFilter). */
@WebServlet("/ordini")
public class OrdiniServlet extends HttpServlet {

    private final OrdineService ordineService = new OrdineService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User utente = SessionUtil.currentUser(req);
        try {
            req.setAttribute("ordini", ordineService.ordiniDi(utente));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/ordini.jsp").forward(req, resp);
    }
}
