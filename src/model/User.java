package model;

/** Bean: un utente (riga della tabella utenti). */
public class User {

    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String password;   // hash, mai la password in chiaro
    private String ruolo;      // "cliente" oppure "admin"

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    /** Vero se l'utente è un amministratore. Usabile nelle JSP come ${utente.admin}. */
    public boolean isAdmin() { return "admin".equals(ruolo); }
}
