package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.UserService;
import model.service.UserService.RegistrazioneRisultato;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER della registrazione.
 *  GET  /registrazione -> mostra il form
 *  POST /registrazione -> passa i dati al service (che valida e salva), poi redirect o ritorno al form
 */
@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/registrazione.jsp";
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // (la codifica UTF-8 della richiesta la imposta EncodingFilter, prima di ogni servlet)
        RegistrazioneRisultato r;
        try {
            r = userService.registra(req.getParameter("nome"), req.getParameter("cognome"),
                    req.getParameter("email"), req.getParameter("password"), req.getParameter("conferma"));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);   // -> pagina 500
        }

        if (r.ok()) {
            // REDIRECT (non forward): un refresh non reinvia il form (Post/Redirect/Get)
            resp.sendRedirect(req.getContextPath() + "/login?registrato=1");
            return;
        }

        // Errore: si rimostra il form con i dati già scritti (MAI la password) e i messaggi.
        req.setAttribute("errori", r.errori());
        req.setAttribute("nome", r.nome());
        req.setAttribute("cognome", r.cognome());
        req.setAttribute("email", r.email());
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
