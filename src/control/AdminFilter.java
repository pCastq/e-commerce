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
import model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

        HttpSession session = req.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute(LoginServlet.SESSION_USER);

        if (u == null) {
            String path = req.getRequestURI().substring(req.getContextPath().length());
            resp.sendRedirect(req.getContextPath() + "/login?next="
                    + URLEncoder.encode(path, StandardCharsets.UTF_8));
        } else if (!u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            chain.doFilter(request, response);
        }
    }
}
