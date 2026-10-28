package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.dao.UserDAO;
import util.PasswordUtil;
import util.Validator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CONTROLLER della registrazione.
 *  GET  /registrazione -> mostra il form
 *  POST /registrazione -> ricontrolla i dati (il JS si può aggirare), salva, poi redirect
 */
@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/registrazione.jsp";
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // (la codifica UTF-8 della richiesta la imposta EncodingFilter, prima di ogni servlet)

        String nome = trim(req.getParameter("nome"));
        String cognome = trim(req.getParameter("cognome"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        String conferma = req.getParameter("conferma");

        Map<String, String> errori = new LinkedHashMap<>();
        if (!Validator.isNome(nome))         errori.put("nome", "Inserisci un nome valido (solo lettere, 2-50 caratteri).");
        if (!Validator.isNome(cognome))      errori.put("cognome", "Inserisci un cognome valido (solo lettere, 2-50 caratteri).");
        if (!Validator.isEmail(email))       errori.put("email", "Inserisci un indirizzo email valido.");
        if (!Validator.isPassword(password)) errori.put("password", "La password deve avere almeno 8 caratteri, con maiuscola, minuscola, numero e un simbolo tra . , _ - ! ? @");
        else if (!password.equals(conferma)) errori.put("conferma", "Le password non coincidono.");

        try {
            if (!errori.containsKey("email") && userDAO.emailExists(email)) {
                errori.put("email", "Questa email è già registrata.");
            }

            if (errori.isEmpty()) {
                User u = new User();
                u.setNome(nome);
                u.setCognome(cognome);
                u.setEmail(email);
                u.setPassword(PasswordUtil.hash(password));
                try {
                    userDAO.insertCliente(u);
                } catch (SQLException e) {
                    if (UserDAO.UNIQUE_VIOLATION.equals(e.getSQLState())) {
                        // un altro utente ha preso la stessa email un istante prima del nostro INSERT
                        errori.put("email", "Questa email è già registrata.");
                    } else {
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);   // -> pagina 500
        }

        if (errori.isEmpty()) {
            // REDIRECT (non forward): evita che un refresh della pagina reinvii il form (Post/Redirect/Get)
            resp.sendRedirect(req.getContextPath() + "/login?registrato=1");
            return;
        }

        // Errore: si rimostra il form con i dati già scritti (MAI la password) e i messaggi.
        req.setAttribute("errori", errori);
        req.setAttribute("nome", nome);
        req.setAttribute("cognome", cognome);
        req.setAttribute("email", email);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
