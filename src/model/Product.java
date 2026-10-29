package model;

import java.math.BigDecimal;

/** Bean (MODEL): una riga della tabella prodotti. */
public class Product {

    private int id;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private BigDecimal iva;
    private int giacenza;
    private boolean attivo;
    private String immagine;

    public Product() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public int getGiacenza() { return giacenza; }
    public void setGiacenza(int giacenza) { this.giacenza = giacenza; }

    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }

    public String getImmagine() { return immagine; }

    /** Nome del file da mostrare: se il prodotto non ha un'immagine, un segnaposto. Usabile in EL come ${p.immagineFile}. */
    public String getImmagineFile() {
        return (immagine == null || immagine.isBlank()) ? "nessuna.svg" : immagine;
    }
    public void setImmagine(String immagine) { this.immagine = immagine; }
}
