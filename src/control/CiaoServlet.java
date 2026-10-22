package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet di prova (CONTROLLER). Non stampa HTML: prepara i dati
 * e li passa alla JSP (VIEW) con forward.
 */
@WebServlet("/ciao")
public class CiaoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        if (nome == null || nome.isBlank()) {
            nome = "sconosciuto";
        }
        req.setAttribute("nome", nome);
        req.setAttribute("cognome",cognome);
        req.getRequestDispatcher("/ciao.jsp").forward(req, resp);
    }
}
