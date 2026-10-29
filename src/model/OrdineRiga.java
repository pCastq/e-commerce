package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Una riga di un ordine già effettuato. Prezzo e IVA sono quelli DEL MOMENTO dell'acquisto
 * (copiati nella tabella ordini_prodotti): se il listino cambia, gli ordini vecchi non cambiano.
 */
public class OrdineRiga {

    private final int idProdotto;
    private final String nomeProdotto;
    private final int quantita;
    private final BigDecimal prezzoUnitario;   // IVA inclusa
    private final BigDecimal iva;              // % contenuta nel prezzo

    public OrdineRiga(int idProdotto, String nomeProdotto, int quantita, BigDecimal prezzoUnitario, BigDecimal iva) {
        this.idProdotto = idProdotto;
        this.nomeProdotto = nomeProdotto;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.iva = iva;
    }

    public int getIdProdotto() { return idProdotto; }
    public String getNomeProdotto() { return nomeProdotto; }
    public int getQuantita() { return quantita; }
    public BigDecimal getPrezzoUnitario() { return prezzoUnitario; }
    public BigDecimal getIva() { return iva; }

    /** Totale della riga, IVA inclusa. */
    public BigDecimal getSubtotale() {
        return prezzoUnitario.multiply(BigDecimal.valueOf(quantita));
    }

    /** Parte senza IVA: totale x 100 / (100 + aliquota). */
    public BigDecimal getImponibile() {
        return getSubtotale().multiply(BigDecimal.valueOf(100))
                .divide(iva.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP);
    }

    /** Importo dell'IVA contenuta nel totale della riga. */
    public BigDecimal getImportoIva() {
        return getSubtotale().subtract(getImponibile());
    }
}
