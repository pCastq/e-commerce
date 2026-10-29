package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Navigation;
import util.SessionUtil;

import java.io.IOException;

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

        if (SessionUtil.isLogged(req)) {
            chain.doFilter(request, response);            // ok, prosegui verso la servlet
            return;
        }
        Navigation.redirectToLogin(req, resp);            // altrimenti al login, ricordando la pagina richiesta
    }
}
