package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.service.ProductService;
import model.service.ProductService.SalvaRisultato;
import util.SessionUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CONTROLLER del form prodotto (creazione e modifica). Protetto da AdminFilter.
 *  GET  /admin/prodotti/form        -> form vuoto (nuovo prodotto)
 *  GET  /admin/prodotti/form?id=N   -> form precompilato (modifica)
 *  POST /admin/prodotti/form        -> salva (con campo nascosto id se è una modifica)
 */
@WebServlet("/admin/prodotti/form")
public class AdminProdottoFormServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/admin/prodotto-form.jsp";
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Product p = null;                       // null = nuovo prodotto
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Integer id = parseIdOrNull(idParam);
            try {
                p = (id == null) ? null : productService.trova(id);
            } catch (SQLException e) {
                throw new ServletException("Errore di accesso al database", e);
            }
            if (p == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        Map<String, String> valori = productService.valoriForm(p);
        mostra(req, resp, valori, Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        Integer id = (idParam == null || idParam.isBlank()) ? null : parseIdOrNull(idParam);
        if (idParam != null && !idParam.isBlank() && id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        SalvaRisultato r;
        try {
            r = productService.salva(id, req.getParameter("nome"), req.getParameter("descrizione"),
                    req.getParameter("prezzo"), req.getParameter("iva"),
                    req.getParameter("giacenza"), req.getParameter("immagine"));
        } catch (SQLException e) {
            throw new ServletException("Errore di accesso al database", e);
        }

        if (r.ok()) {
            SessionUtil.flashOk(req, id == null ? "Prodotto creato." : "Prodotto aggiornato.");
            resp.sendRedirect(req.getContextPath() + "/admin/prodotti");
            return;
        }

        // Errore: si rimostra il form con quanto scritto (i valori grezzi del form) e i messaggi
        Map<String, String> valori = new LinkedHashMap<>();
        for (String campo : ProductService.CAMPI_FORM) {
            valori.put(campo, req.getParameter(campo) == null ? "" : req.getParameter(campo));
        }
        if (id != null) {
            valori.put("id", String.valueOf(id));
        }
        mostra(req, resp, valori, r.errori());
    }

    private void mostra(HttpServletRequest req, HttpServletResponse resp,
                        Map<String, String> valori, Map<String, String> errori)
            throws ServletException, IOException {
        req.setAttribute("f", valori);
        req.setAttribute("errori", errori);
        req.setAttribute("aliquote", ProductService.ALIQUOTE_IVA);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    private static Integer parseIdOrNull(String s) {
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
