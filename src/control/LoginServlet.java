package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.service.UserService;
import util.Navigation;
import util.SessionKeys;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del login.
 *  GET  /login -> form (se già loggato, torna alla home)
 *  POST /login -> il service verifica email+password; se ok si prepara la sessione e si fa redirect
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/login.jsp";
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (SessionUtil.isLogged(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.setAttribute("next", Navigation.safeNext(req.getParameter("next")));
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = UserService.normalizzaEmail(req.getParameter("email"));
        String next = Navigation.safeNext(req.getParameter("next"));

        User u;
        try {
            u = userService.autentica(email, req.getParameter("password"));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        // Messaggio VOLUTAMENTE generico: non dice se a essere sbagliata è l'email o la password,
        // così nessuno può scoprire quali email sono registrate.
        if (u == null) {
            req.setAttribute("errore", "Email o password errate.");
            req.setAttribute("email", email);
            req.setAttribute("next", next);
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        // Login riuscito: la sessione è una questione HTTP, quindi la gestisce la servlet.
        HttpSession session = req.getSession(true);   // la sessione che il visitatore ha già (o una nuova se manca)
        req.changeSessionId();                        // stessi dati (es. il carrello!), NUOVO id: anti session fixation
        session.setAttribute(SessionKeys.USER, u);

        // senza una pagina richiesta: l'admin va al suo pannello, il cliente alla home
        String target = next != null ? next : (u.isAdmin() ? "/admin/prodotti" : "/");
        resp.sendRedirect(req.getContextPath() + target);
    }
}
