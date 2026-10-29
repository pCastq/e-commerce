package model.dao;

import model.Ordine;
import model.OrdineRiga;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

/**
 * DAO degli ordini.
 * I metodi di SCRITTURA ricevono la Connection già aperta: così OrdineService può usare
 * la STESSA connessione (e quindi la stessa transazione) per tutte le operazioni.
 */
public class OrdineDAO {

    /** Inserisce l'ordine e restituisce l'id generato dal database (RETURNING). */
    public int insert(Connection con, int idUtente, String indirizzo) throws SQLException {
        String sql = "INSERT INTO ordini (id_utente, indirizzo_spedizione) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setString(2, indirizzo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Inserisce una riga d'ordine, con prezzo e IVA del momento. */
    public void insertRiga(Connection con, int idOrdine, int idProdotto, int quantita,
                           BigDecimal prezzoUnitario, BigDecimal iva) throws SQLException {
        String sql = "INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita, prezzo_unitario, iva) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOrdine);
            ps.setInt(2, idProdotto);
            ps.setInt(3, quantita);
            ps.setBigDecimal(4, prezzoUnitario);
            ps.setBigDecimal(5, iva);
            ps.executeUpdate();
        }
    }

    /**
     * Tutti gli ordini di un utente, dal più recente, ciascuno con le sue righe.
     * Una sola query con JOIN; poi le righe si raggruppano per ordine in Java.
     */
    public List<Ordine> findByUser(int idUtente) throws SQLException {
        String sql = "SELECT o.id, o.data_ordine, o.indirizzo_spedizione, "
                   + "       op.id_prodotto, p.nome, op.quantita, op.prezzo_unitario, op.iva "
                   + "FROM ordini o "
                   + "JOIN ordini_prodotti op ON op.id_ordine = o.id "
                   + "JOIN prodotti p ON p.id = op.id_prodotto "
                   + "WHERE o.id_utente = ? "
                   + "ORDER BY o.data_ordine DESC, o.id DESC, p.nome";

        Map<Integer, Ordine> ordini = new LinkedHashMap<>();   // mantiene l'ordine di arrivo (più recente per primo)
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Ordine o = ordini.get(id);
                    if (o == null) {
                        o = new Ordine(id, rs.getTimestamp("data_ordine"),
                                rs.getString("indirizzo_spedizione"), new ArrayList<>());
                        ordini.put(id, o);
                    }
                    o.getRighe().add(new OrdineRiga(rs.getInt("id_prodotto"), rs.getString("nome"),
                            rs.getInt("quantita"), rs.getBigDecimal("prezzo_unitario"), rs.getBigDecimal("iva")));
                }
            }
        }
        return new ArrayList<>(ordini.values());
    }

    /**
     * Un ordine con le sue righe, SOLO se appartiene all'utente indicato (altrimenti null):
     * un cliente non deve poter vedere gli ordini degli altri cambiando l'id nell'URL.
     */
    public Ordine findByIdAndUser(int idOrdine, int idUtente) throws SQLException {
        String sqlOrdine = "SELECT data_ordine, indirizzo_spedizione FROM ordini WHERE id = ? AND id_utente = ?";
        String sqlRighe = "SELECT op.id_prodotto, p.nome, op.quantita, op.prezzo_unitario, op.iva "
                        + "FROM ordini_prodotti op JOIN prodotti p ON p.id = op.id_prodotto "
                        + "WHERE op.id_ordine = ? ORDER BY p.nome";

        try (Connection con = Database.getConnection()) {
            Timestamp data;
            String indirizzo;
            try (PreparedStatement ps = con.prepareStatement(sqlOrdine)) {
                ps.setInt(1, idOrdine);
                ps.setInt(2, idUtente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    data = rs.getTimestamp("data_ordine");
                    indirizzo = rs.getString("indirizzo_spedizione");
                }
            }
            List<OrdineRiga> righe = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlRighe)) {
                ps.setInt(1, idOrdine);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        righe.add(new OrdineRiga(rs.getInt("id_prodotto"), rs.getString("nome"),
                                rs.getInt("quantita"), rs.getBigDecimal("prezzo_unitario"), rs.getBigDecimal("iva")));
                    }
                }
            }
            return new Ordine(idOrdine, data, indirizzo, righe);
        }
    }
}
