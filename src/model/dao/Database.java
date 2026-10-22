package model.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto unico di accesso al database.
 * Cerca il DataSource (pool) dichiarato in META-INF/context.xml tramite JNDI
 * e restituisce le connessioni. Chiamare close() su una connessione
 * NON la chiude davvero: la restituisce al pool.
 */
public final class Database {

    private static final String JNDI_NAME = "java:comp/env/jdbc/ecommerce";
    private static DataSource dataSource;

    private Database() {
    }

    private static synchronized DataSource getDataSource() throws SQLException {
        if (dataSource == null) {
            try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup(JNDI_NAME);
            } catch (NamingException e) {
                throw new SQLException("DataSource non trovato (" + JNDI_NAME + ")", e);
            }
        }
        return dataSource;
    }

    /** Prende una connessione dal pool. Usare sempre con try-with-resources. */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
