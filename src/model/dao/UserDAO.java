package model.dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** DAO: query SQL sugli utenti. */
public class UserDAO {

    /** Codice SQLSTATE di PostgreSQL per "violazione di UNIQUE". */
    public static final String UNIQUE_VIOLATION = "23505";

    /** Vero se esiste già un utente con questa email. */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM utenti WHERE email = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserisce un nuovo cliente. Il ruolo è sempre 'cliente' (default del database):
     * un amministratore non si crea dal form pubblico.
     * Se l'email esiste già, il database lancia SQLException con SQLState 23505.
     */
    public void insertCliente(User u) throws SQLException {
        String sql = "INSERT INTO utenti (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.executeUpdate();
        }
    }

    /** L'utente con questa email (con l'hash della password), oppure null se non esiste. */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, cognome, email, password, ruolo FROM utenti WHERE email = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setCognome(rs.getString("cognome"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRuolo(rs.getString("ruolo"));
                return u;
            }
        }
    }
}
