package model;

import java.math.BigDecimal;

/** Una riga del carrello pronta per essere mostrata: il prodotto (dal database) e la quantità. */
public class CartLine {

    private final Product prodotto;
    private final int quantita;

    public CartLine(Product prodotto, int quantita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Product getProdotto() { return prodotto; }
    public int getQuantita() { return quantita; }

    /** prezzo (IVA inclusa) x quantità. BigDecimal: mai double per i soldi. */
    public BigDecimal getSubtotale() {
        return prodotto.getPrezzo().multiply(BigDecimal.valueOf(quantita));
    }
}
