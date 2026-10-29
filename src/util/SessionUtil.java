package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.User;

/**
 * Aiuti per leggere/scrivere la SESSIONE HTTP in un solo posto (prima la stessa cast + getAttribute
 * era ripetuta in molte servlet e filtri). Sono aiuti "web", non logica di business:
 * per questo stanno in util e non nei service (che non conoscono HTTP).
 */
public final class SessionUtil {

    private SessionUtil() { }

    /** L'utente loggato, oppure null. Non crea una sessione se non c'è (getSession(false)). */
    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute(SessionKeys.USER);
    }

    public static boolean isLogged(HttpServletRequest req) {
        return currentUser(req) != null;
    }

    /** Il carrello della sessione, oppure null se il visitatore non ne ha ancora uno. Non crea nulla. */
    public static Cart cartOrNull(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Cart) s.getAttribute(SessionKeys.CART);
    }

    /** Il carrello della sessione; se manca (anche la sessione) lo crea. */
    public static Cart cartOrCreate(HttpServletRequest req) {
        HttpSession s = req.getSession(true);
        Cart cart = (Cart) s.getAttribute(SessionKeys.CART);
        if (cart == null) {
            cart = new Cart();
            s.setAttribute(SessionKeys.CART, cart);
        }
        return cart;
    }

    /** Messaggio verde mostrato una sola volta alla prossima pagina (vedi header.jsp). */
    public static void flashOk(HttpServletRequest req, String messaggio) {
        req.getSession(true).setAttribute(SessionKeys.FLASH_OK, messaggio);
    }

    /** Messaggio rosso mostrato una sola volta alla prossima pagina. */
    public static void flashErr(HttpServletRequest req, String messaggio) {
        req.getSession(true).setAttribute(SessionKeys.FLASH_ERR, messaggio);
    }
}
