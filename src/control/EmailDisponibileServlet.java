package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UserDAO;
import util.Validator;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Endpoint AJAX: GET /api/email-disponibile?email=... -> {"disponibile":true|false}
 * Risponde in JSON, non in HTML: lo chiama il JavaScript con fetch().
 */
@WebServlet("/api/email-disponibile")
public class EmailDisponibileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        email = (email == null) ? "" : email.trim().toLowerCase();

        if (!Validator.isEmail(email)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean disponibile;
        try {
            disponibile = !userDAO.emailExists(email);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write("{\"disponibile\":" + disponibile + "}");
    }
}
