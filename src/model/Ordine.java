package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    /** Numero totale di pezzi acquistati (somma delle quantità). */
    public int getNumeroArticoli() {
        int n = 0;
        for (OrdineRiga r : righe) n += r.getQuantita();
        return n;
    }

    /** Una riga del riepilogo IVA di una fattura: per ogni aliquota, imponibile e imposta. */
    public static class RiepilogoIva {
        private final BigDecimal aliquota;
        private final BigDecimal imponibile;
        private final BigDecimal iva;

        public RiepilogoIva(BigDecimal aliquota, BigDecimal imponibile, BigDecimal iva) {
            this.aliquota = aliquota;
            this.imponibile = imponibile;
            this.iva = iva;
        }

        public BigDecimal getAliquota() { return aliquota; }
        public BigDecimal getImponibile() { return imponibile; }
        public BigDecimal getIva() { return iva; }
    }

    /** Imponibile e IVA raggruppati per aliquota (es. 4%, 10%, 22%), in ordine crescente. */
    public List<RiepilogoIva> getRiepilogoIva() {
        Map<BigDecimal, BigDecimal[]> perAliquota = new TreeMap<>();
        for (OrdineRiga r : righe) {
            BigDecimal[] v = perAliquota.computeIfAbsent(r.getIva().stripTrailingZeros(),
                    k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            v[0] = v[0].add(r.getImponibile());
            v[1] = v[1].add(r.getImportoIva());
        }
        List<RiepilogoIva> result = new ArrayList<>();
        for (Map.Entry<BigDecimal, BigDecimal[]> e : perAliquota.entrySet()) {
            result.add(new RiepilogoIva(e.getKey(), e.getValue()[0], e.getValue()[1]));
        }
        return result;
    }
}
