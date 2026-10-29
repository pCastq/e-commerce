package model.service;

import model.User;
import model.dao.UserDAO;
import util.PasswordUtil;
import util.Validator;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SERVICE degli utenti: registrazione e autenticazione.
 * Qui stanno le regole (validazione, email unica, hash della password); le servlet si limitano
 * a leggere i parametri e a scegliere la pagina da mostrare.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Risultato di una registrazione: eventuali errori (campo -> messaggio; vuota = tutto ok)
     * e i dati già "ripuliti", utili per ripresentare il form senza far riscrivere tutto.
     */
    public record RegistrazioneRisultato(Map<String, String> errori, String nome, String cognome, String email) {
        public boolean ok() {
            return errori.isEmpty();
        }
    }

    /** Email senza spazi ai lati e in minuscolo (UNIQUE distingue maiuscole e minuscole). */
    public static String normalizzaEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    public boolean isEmailValida(String email) {
        return Validator.isEmail(normalizzaEmail(email));
    }

    /** Vero se nessun utente ha già questa email. */
    public boolean emailDisponibile(String email) throws SQLException {
        return !userDAO.emailExists(normalizzaEmail(email));
    }

    /**
     * Registra un nuovo cliente. I controlli sono quelli del JavaScript, rifatti qui perché
     * il browser non è affidabile. L'hash della password si calcola qui: la password in chiaro
     * non arriva mai al database.
     */
    public RegistrazioneRisultato registra(String nome, String cognome, String email,
                                           String password, String conferma) throws SQLException {
        nome = (nome == null) ? "" : nome.trim();
        cognome = (cognome == null) ? "" : cognome.trim();
        email = normalizzaEmail(email);

        Map<String, String> errori = new LinkedHashMap<>();
        if (!Validator.isNome(nome)) {
            errori.put("nome", "Inserisci un nome valido (solo lettere, 2-50 caratteri).");
        }
        if (!Validator.isNome(cognome)) {
            errori.put("cognome", "Inserisci un cognome valido (solo lettere, 2-50 caratteri).");
        }
        if (!Validator.isEmail(email)) {
            errori.put("email", "Inserisci un indirizzo email valido.");
        }
        if (!Validator.isPassword(password)) {
            errori.put("password", "La password deve avere almeno 8 caratteri, con maiuscola, minuscola, numero e un simbolo tra . , _ - ! ? @");
        } else if (!password.equals(conferma)) {
            errori.put("conferma", "Le password non coincidono.");
        }

        if (!errori.containsKey("email") && userDAO.emailExists(email)) {
            errori.put("email", "Questa email è già registrata.");
        }

        if (errori.isEmpty()) {
            User u = new User();
            u.setNome(nome);
            u.setCognome(cognome);
            u.setEmail(email);
            u.setPassword(PasswordUtil.hash(password));
            try {
                userDAO.insertCliente(u);
            } catch (SQLException e) {
                if (UserDAO.UNIQUE_VIOLATION.equals(e.getSQLState())) {
                    errori.put("email", "Questa email è già registrata.");
                } else {
                    throw e;
                }
            }
        }
        return new RegistrazioneRisultato(errori, nome, cognome, email);
    }

    /**
     * Verifica email e password. Restituisce l'utente (SENZA hash) oppure null se le credenziali
     * sono sbagliate: volutamente non distingue "email inesistente" da "password errata".
     */
    public User autentica(String email, String password) throws SQLException {
        email = normalizzaEmail(email);
        if (email.isEmpty() || password == null) {
            return null;
        }
        User u = userDAO.findByEmail(email);
        if (u == null || !PasswordUtil.matches(password, u.getPassword())) {
            return null;
        }
        u.setPassword(null);          // l'hash non deve uscire dal service
        return u;
    }
}
