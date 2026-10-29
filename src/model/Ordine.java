package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/** Un ordine con le sue righe (MODEL). */
public class Ordine {

    private final int id;
    private final Timestamp dataOrdine;
    private final String indirizzoSpedizione;
    private final List<OrdineRiga> righe;

    public Ordine(int id, Timestamp dataOrdine, String indirizzoSpedizione, List<OrdineRiga> righe) {
        this.id = id;
        this.dataOrdine = dataOrdine;
        this.indirizzoSpedizione = indirizzoSpedizione;
        this.righe = righe;
    }

    public int getId() { return id; }
    public Timestamp getDataOrdine() { return dataOrdine; }
    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public List<OrdineRiga> getRighe() { return righe; }

    /** Totale pagato (IVA inclusa). */
    public BigDecimal getTotale() {
        BigDecimal t = BigDecimal.ZERO;
        for (OrdineRiga r : righe) t = t.add(r.getSubtotale());
        return t;
    }

    public BigDecimal getTotaleImponibile() {
        BigDecimal t = BigDecimal.ZERO;
        for (OrdineRiga r : righe) t = t.add(r.getImponibile());
        return t;
    }

    public BigDecimal getTotaleIva() {
        return getTotale().subtract(getTotaleImponibile());
    }
}
