package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.service.ProductService;

import java.io.IOException;
import java.sql.SQLException;

/** CONTROLLER della pagina di dettaglio: /prodotto?id=3. Id assente/errato/inattivo -> 404. */
@WebServlet("/prodotto")
public class ProdottoServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));   // null o "abc" -> NumberFormatException
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Product p;
        try {
            p = productService.trovaAttivo(id);
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }
        if (p == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("prodotto", p);
        req.getRequestDispatcher("/WEB-INF/views/prodotto.jsp").forward(req, resp);
    }
}
