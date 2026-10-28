package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.dao.UserDAO;
import util.PasswordUtil;
import util.SessionKeys;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del login.
 *  GET  /login -> form (se già loggato, torna alla home)
 *  POST /login -> verifica email+password; se ok crea la sessione e fa redirect
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/login.jsp";
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (isLogged(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.setAttribute("next", safeNext(req.getParameter("next")));
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        email = (email == null) ? "" : email.trim().toLowerCase();
        String password = req.getParameter("password");
        String next = safeNext(req.getParameter("next"));

        User u;
        try {
            u = email.isEmpty() ? null : userDAO.findByEmail(email);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        // Messaggio VOLUTAMENTE generico: non dice se a essere sbagliata è l'email o la password,
        // così nessuno può scoprire quali email sono registrate.
        if (u == null || password == null || !PasswordUtil.matches(password, u.getPassword())) {
            req.setAttribute("errore", "Email o password errate.");
            req.setAttribute("email", email);
            req.setAttribute("next", next);
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        // Login riuscito.
        u.setPassword(null);                       // l'hash non deve restare in sessione
        HttpSession session = req.getSession(true);   // la sessione che il visitatore ha già (o una nuova se manca)
        req.changeSessionId();                        // stessi dati (es. il carrello!), NUOVO id: anti session fixation
        session.setAttribute(SessionKeys.USER, u);

        String target = next != null ? next : "/";
        resp.sendRedirect(req.getContextPath() + target);
    }

    private static boolean isLogged(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute(SessionKeys.USER) != null;
    }

    /**
     * Accetta come "pagina di ritorno" solo un percorso interno all'applicazione (es. /profilo).
     * Rifiuta indirizzi esterni (http://..., //sito.com): sarebbe un "open redirect".
     */
    static String safeNext(String next) {
        if (next == null || next.isEmpty()) return null;
        if (!next.startsWith("/") || next.startsWith("//") || next.contains("\\")
                || next.contains(":") || next.contains("\r") || next.contains("\n")) {
            return null;
        }
        return next;
    }
}
