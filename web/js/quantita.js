/*
 * CODICE CONDIVISO per la quantità: serve sia al catalogo sia al carrello.
 * Contiene solo la parte "interfaccia": il selettore  - [ n ] +  e il numero accanto a "Carrello".
 * Cosa fare quando la quantità cambia (inviare al server, aggiungere, ...) lo decide chi lo usa.
 * Il server rifà comunque tutti i controlli: qui si dà solo un riscontro immediato.
 */
var Quantita = (function () {
    "use strict";

    var MIN = 1, MAX = 99;
    var RE = /^[1-9][0-9]?$/;                   // intero da 1 a 99

    /*
     * Attiva un selettore dentro "radice" (un form). Si aspetta:
     *   .qty-input (testo), .qty-btn[data-delta="-1"], .qty-btn[data-delta="1"], .form-group (per l'errore inline).
     * Opzioni:
     *   alDigitare(v)    chiamata mentre si scrive (v = numero valido, oppure null se vuoto/non valido)
     *   alPulsante(v)    chiamata dopo un clic su - / + (v = nuovo valore)
     *   alBlur(v)        chiamata quando il campo perde il focus con un valore valido
     *   ripristino()     valore da rimettere se il campo viene lasciato vuoto/non valido (default: MIN)
     */
    function selettore(radice, opzioni) {
        var opz = opzioni || {};
        var input = radice.querySelector(".qty-input");
        var gruppo = input.closest(".form-group");
        var btnMeno = radice.querySelector("[data-delta='-1']");
        var btnPiu = radice.querySelector("[data-delta='1']");

        function valore() {                     // numero valido oppure null
            return RE.test(input.value) ? parseInt(input.value, 10) : null;
        }
        function aggiornaPulsanti() {
            var v = valore();
            btnMeno.disabled = (v !== null) && v <= MIN;
            btnPiu.disabled = (v !== null) && v >= MAX;
        }
        function errore(attivo) { gruppo.classList.toggle("has-error", attivo); }
        function imposta(n) { input.value = n; errore(false); aggiornaPulsanti(); }

        input.addEventListener("input", function () {
            input.value = input.value.replace(/\D/g, "").slice(0, 2);       // solo cifre, al massimo 2
            var v = valore();
            aggiornaPulsanti();
            errore(v === null && input.value !== "");                       // vuoto = "sto scrivendo", non è un errore
            if (opz.alDigitare) { opz.alDigitare(v); }
        });

        input.addEventListener("blur", function () {
            var v = valore();
            if (v === null) {                                               // lasciato vuoto/non valido: si ripristina
                imposta(opz.ripristino ? opz.ripristino() : MIN);
            } else if (opz.alBlur) {
                opz.alBlur(v);
            }
        });

        [btnMeno, btnPiu].forEach(function (btn) {
            btn.addEventListener("click", function () {
                var base = valore();
                if (base === null) { base = opz.ripristino ? opz.ripristino() : MIN; }
                var nuovo = Math.min(MAX, Math.max(MIN, base + parseInt(btn.dataset.delta, 10)));
                imposta(nuovo);
                if (opz.alPulsante) { opz.alPulsante(nuovo); }
            });
        });

        aggiornaPulsanti();
        return { input: input, valore: valore, imposta: imposta, errore: errore, aggiornaPulsanti: aggiornaPulsanti };
    }

    /* Numero di pezzi accanto a "Carrello" nel menu. Lo spazio è sempre riservato: si nasconde solo quando è 0. */
    function aggiornaBadge(totale) {
        var badge = document.querySelector("#cart-link .cart-badge");
        if (!badge) { return; }
        badge.textContent = totale;
        badge.classList.toggle("is-empty", !(totale > 0));
    }

    return { MIN: MIN, MAX: MAX, RE: RE, selettore: selettore, aggiornaBadge: aggiornaBadge };
})();
