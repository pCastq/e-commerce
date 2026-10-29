/*
 * CATALOGO: scelta della quantità e "Aggiungi al carrello" senza lasciare la pagina (AJAX).
 * Senza JavaScript il form funziona comunque: POST a /carrello e redirect al carrello.
 * Con JavaScript: fetch POST (Accept: application/json) -> la servlet risponde in JSON ->
 * si aggiorna il numero accanto a "Carrello" e si mostra un messaggio temporaneo.
 *
 * Qui +/- cambiano solo il numero nel campo: NON parte nessuna richiesta finché non si preme
 * "Aggiungi al carrello". Per questo non serve il ritardo (debounce) usato nel carrello; contro i
 * doppi clic sul pulsante si disattiva il pulsante finché la richiesta non è terminata.
 * Dipende da quantita.js (caricato prima).
 */
(function () {
    "use strict";

    var toast = document.getElementById("toast");
    var timerToast = null;

    function mostra(testo, errore) {
        toast.textContent = testo;                        // textContent: mai innerHTML con testi dinamici (XSS)
        toast.classList.toggle("toast-error", !!errore);
        toast.hidden = false;
        clearTimeout(timerToast);
        timerToast = setTimeout(function () { toast.hidden = true; }, 3500);
    }

    function aggiungi(form, sel, bottone) {
        var q = sel.valore();
        if (q === null) {                                 // vuoto/non valido: errore inline, nessuna richiesta
            sel.errore(true);
            sel.input.focus();
            return;
        }
        bottone.disabled = true;                          // evita doppi clic mentre la richiesta è in corso

        fetch(form.action, {
            method: "POST",
            headers: { "Accept": "application/json" },
            body: new URLSearchParams(new FormData(form))
        })
        .then(function (r) { return r.json(); })
        .then(function (d) {
            Quantita.aggiornaBadge(d.totaleArticoli);
            mostra(d.messaggio, !d.ok || d.stato === "AVVISO");
            sel.imposta(Quantita.MIN);                    // pronto per un nuovo inserimento
        })
        .catch(function () { mostra("Impossibile aggiungere al carrello. Riprova.", true); })
        .finally(function () { bottone.disabled = false; });
    }

    document.querySelectorAll(".js-add-form").forEach(function (form) {
        var sel = Quantita.selettore(form);
        var bottone = form.querySelector("button[type=submit]");
        form.addEventListener("submit", function (ev) {   // vale anche per Invio dentro il campo
            ev.preventDefault();
            aggiungi(form, sel, bottone);
        });
    });
})();
