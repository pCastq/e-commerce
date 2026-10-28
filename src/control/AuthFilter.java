package control;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * FILTRO di controllo accessi: le pagine elencate qui richiedono di essere loggati.
 * Se non lo sei, vieni mandato al login e, dopo l'accesso, riportato dove volevi andare (?next=).
 * Pagine riservate: /profilo ora; checkout e ordini si aggiungeranno qui.
 */
@WebFilter(urlPatterns = {"/profilo", "/checkout", "/ordini", "/ordini/*", "/fattura", "/fattura/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);      // false: NON creare una sessione solo per controllare
        boolean logged = session != null && session.getAttribute(LoginServlet.SESSION_USER) != null;

        if (logged) {
            chain.doFilter(request, response);            // ok, prosegui verso la servlet
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (req.getQueryString() != null) {
            path += "?" + req.getQueryString();
        }
        String next = URLEncoder.encode(path, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/login?next=" + next);
    }
}
