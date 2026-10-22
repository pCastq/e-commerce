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
 * Servlet di prova (CONTROLLER) per verificare il pool di connessioni.
 * Legge i prodotti dal database e li passa alla JSP. Da eliminare quando
 * ci sarà la vera servlet del catalogo.
 */
@WebServlet("/test-db")
public class TestDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Product> prodotti = new ProductDAO().findAllActive();
            req.setAttribute("prodotti", prodotti);
        } catch (SQLException e) {
            // L'errore finisce nella pagina 500 configurata in web.xml
            throw new ServletException("Errore di accesso al database", e);
        }
        req.getRequestDispatcher("/test-db.jsp").forward(req, resp);
    }
}
