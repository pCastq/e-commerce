package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.service.ProductService;
import util.Json;
import util.JsonResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint AJAX della ricerca con suggerimenti.
 * GET /api/suggerimenti?q=pen  ->  [{"id":2,"nome":"Penne Rigate 500 g"}, ...]
 * Risponde sempre 200 con un array JSON (vuoto se il testo è troppo corto o non c'è nulla).
 * È pubblico, come il catalogo: espone solo i nomi dei prodotti attivi.
 */
@WebServlet("/api/suggerimenti")
public class SuggerimentiServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Product> trovati;
        try {
            trovati = productService.suggerimenti(req.getParameter("q"));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        List<Json.Raw> voci = new ArrayList<>();
        for (Product p : trovati) {
            voci.add(new Json.Raw(Json.object("id", p.getId(), "nome", p.getNome())));   // Raw: già JSON, non va racchiuso di nuovo tra virgolette
        }
        JsonResponse.send(resp, Json.array(voci));
    }
}
