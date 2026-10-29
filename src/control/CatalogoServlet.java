package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.ProductService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CONTROLLER del catalogo: /catalogo (con ?q=testo per cercare per nome).
 * Legge il parametro, chiede i dati al service e passa tutto alla JSP con un forward.
 */
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String q = req.getParameter("q");
        q = (q == null) ? "" : q.trim();

        try {
            req.setAttribute("prodotti", productService.catalogo(q));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);   // -> pagina 500
        }
        req.setAttribute("q", q);
        req.getRequestDispatcher("/WEB-INF/views/catalogo.jsp").forward(req, resp);
    }
}
