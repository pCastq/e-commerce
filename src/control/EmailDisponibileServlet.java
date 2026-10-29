package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.UserService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Endpoint AJAX: GET /api/email-disponibile?email=... -> {"disponibile":true|false}
 * Risponde in JSON, non in HTML: lo chiama il JavaScript con fetch().
 */
@WebServlet("/api/email-disponibile")
public class EmailDisponibileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        if (!userService.isEmailValida(email)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean disponibile;
        try {
            disponibile = userService.emailDisponibile(email);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write("{\"disponibile\":" + disponibile + "}");
    }
}
