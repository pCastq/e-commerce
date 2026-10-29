package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.Navigation;
import util.SessionUtil;

import java.io.IOException;

/**
 * FILTRO dell'area amministratore (/admin/*):
 *  - non loggato        -> redirect al login
 *  - loggato ma cliente -> 403 Forbidden (pagina di errore personalizzata)
 *  - admin              -> passa
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        User u = SessionUtil.currentUser(req);

        if (u == null) {
            Navigation.redirectToLogin(req, resp);
        } else if (!u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            chain.doFilter(request, response);
        }
    }
}
