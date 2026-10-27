package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.dao.ProductDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * CONTROLLER del catalogo: elenca i prodotti a catalogo.
 * Se c'è il parametro ?q=... mostra solo i prodotti il cui nome contiene quel testo.
 * Non stampa HTML: prepara i dati e li passa alla JSP con un forward.
 */
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String q = req.getParameter("q");
        q = (q == null) ? "" : q.trim();

        try {
            List<Product> prodotti = q.isEmpty()
                    ? productDAO.findAllActive()
                    : productDAO.findActiveByName(q);
            req.setAttribute("prodotti", prodotti);
            req.setAttribute("q", q);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);   // -> pagina 500
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogo.jsp").forward(req, resp);
    }
}
