package util;

/**
 * Nomi (etichette) degli attributi che mettiamo nella sessione.
 * Stanno in un unico posto per evitare errori di battitura: scrivere "utnete" invece di "utente"
 * non darebbe nessun errore, ma getAttribute restituirebbe sempre null.
 */
public final class SessionKeys {

    private SessionKeys() { }

    /** L'utente loggato (oggetto model.User). Se manca, nessuno ha fatto login. */
    public static final String USER = "utente";

    /** Il carrello del visitatore (oggetto model.Cart): esiste anche per chi non ha fatto login. */
    public static final String CART = "carrello";

    /** Messaggio "una tantum" di conferma, mostrato alla prossima pagina e poi cancellato. */
    public static final String FLASH_OK = "flashOk";

    /** Messaggio "una tantum" di errore/avviso, mostrato alla prossima pagina e poi cancellato. */
    public static final String FLASH_ERR = "flashErr";
}
