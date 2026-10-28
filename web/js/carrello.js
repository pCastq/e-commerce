/* Comportamento delle pagine con quantità (dettaglio prodotto e carrello).
   Il server rifà sempre i controlli: qui si dà solo un riscontro immediato, senza alert(). */
(function () {
    "use strict";

    var RE_QUANTITA = /^[1-9][0-9]?$/;          // intero da 1 a 99
    var MIN = 1, MAX = 99;
    var ATTESA_DIGITAZIONE = 600;               // ms di pausa dopo l'ultimo tasto prima di inviare
    var ATTESA_PULSANTI = 300;                  // ms dopo l'ultimo clic su - / +
    var euro = new Intl.NumberFormat("it-IT", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    function formatta(testo) { return euro.format(Number(testo)) + " €"; }

    /* ======================================================================
       1) Pagina di dettaglio: form "Aggiungi al carrello" (invio normale, con controllo)
       ====================================================================== */
    document.querySelectorAll(".js-qty-form").forEach(function (form) {
        var input = form.querySelector("input[name='quantita']");
        var gruppo = input.closest(".form-group");

        function valido() { return RE_QUANTITA.test(input.value.trim()); }

        input.addEventListener("input", function () {
            if (valido()) { gruppo.classList.remove("has-error"); }
        });
        form.addEventListener("submit", function (e) {
            if (!valido()) {
                e.preventDefault();
                gruppo.classList.add("has-error");
                input.focus();
            }
        });
    });

    /* ======================================================================
       2) Carrello: la quantità si aggiorna con fetch (AJAX) a ogni modifica valida
       ====================================================================== */
    document.querySelectorAll(".js-cart-qty").forEach(function (form) {
        var riga = form.closest(".cart-line");
        var id = form.querySelector("input[name='id']").value;
        var input = form.querySelector("input[name='quantita']");
        var gruppo = input.closest(".form-group");
        var msg = form.querySelector(".qty-msg");
        var btnMeno = form.querySelector("[data-delta='-1']");
        var btnPiu = form.querySelector("[data-delta='1']");
        var url = form.getAttribute("action");

        var ultimaValida = parseInt(input.value, 10);   // quantità confermata dal server
        var timer = null;                               // timer della pausa
        var inCorso = false;                            // c'è una richiesta in viaggio per questa riga?
        var inAttesa = null;                            // ultimo valore voluto mentre la richiesta era in viaggio

        function valoreDigitato() {
            return RE_QUANTITA.test(input.value) ? parseInt(input.value, 10) : null;
        }

        function aggiornaPulsanti() {
            var v = valoreDigitato();
            btnMeno.disabled = (v === null) ? false : v <= MIN;
            btnPiu.disabled = (v === null) ? false : v >= MAX;
        }

        function mostraMessaggio(testo, errore) {
            msg.textContent = testo || "";
            msg.classList.toggle("is-error", !!errore);
        }

        /* ---- invio: una sola richiesta alla volta per riga ---- */
        function invia(q) {
            if (inCorso) { inAttesa = q; return; }          // la rimando quando arriva la risposta
            if (q === ultimaValida) { return; }             // niente da aggiornare

            inCorso = true;
            riga.classList.add("is-busy");

            var corpo = new URLSearchParams({ azione: "aggiorna", id: id, quantita: String(q) });
            fetch(url, {
                method: "POST",
                headers: { "Accept": "application/json", "Content-Type": "application/x-www-form-urlencoded" },
                body: corpo
            })
            .then(function (r) {
                return r.json().then(function (dati) { return { ok: r.ok, dati: dati }; });
            })
            .then(function (esito) { applica(esito.ok && esito.dati.ok, esito.dati, q); })
            .catch(function () { applica(false, { messaggio: "Impossibile aggiornare il carrello. Riprova." }, q); })
            .then(function () {
                inCorso = false;
                riga.classList.remove("is-busy");
                if (inAttesa !== null) {                    // nel frattempo l'utente ha cambiato idea
                    var prossimo = inAttesa;
                    inAttesa = null;
                    invia(prossimo);
                }
            });
        }

        /* ---- applica la risposta del server (che è l'autorità) ---- */
        function applica(ok, dati, inviata) {
            if (!ok) {
                mostraMessaggio(dati.messaggio, true);
                if (document.activeElement !== input) { input.value = ultimaValida; }
                aggiornaPulsanti();
                return;
            }

            if (dati.rimosso) {                             // prodotto non più disponibile: via la riga
                riga.remove();
                aggiornaTotali(dati);
                if (!document.querySelector(".cart-line")) { window.location.reload(); }
                return;
            }

            ultimaValida = dati.quantita;
            // Non si tocca il campo se l'utente sta già scrivendo altro.
            if (document.activeElement !== input || input.value === String(inviata)) {
                input.value = dati.quantita;
            }
            riga.querySelector(".cart-subtotal").textContent = formatta(dati.subtotale);
            aggiornaTotali(dati);
            mostraMessaggio(dati.messaggio, true);
            aggiornaPulsanti();
        }

        function aggiornaTotali(dati) {
            document.getElementById("cart-total").textContent = formatta(dati.totale);
            var badge = document.querySelector(".cart-badge");
            if (badge) { badge.textContent = dati.totaleArticoli; }
        }

        function programma(q, attesa) {
            clearTimeout(timer);
            timer = setTimeout(function () { timer = null; invia(q); }, attesa);
        }

        function subito() {                                 // invia ora (blur, Invio) ciò che era in attesa
            if (timer !== null) {
                clearTimeout(timer);
                timer = null;
                var v = valoreDigitato();
                if (v !== null) { invia(v); }
            }
        }

        /* ---- eventi ---- */
        input.addEventListener("input", function () {
            input.value = input.value.replace(/\D/g, "").slice(0, 2);   // solo cifre
            mostraMessaggio("");
            var v = valoreDigitato();
            aggiornaPulsanti();
            clearTimeout(timer);
            timer = null;
            if (v === null) {
                // vuoto o 0: stato "in modifica", NESSUNA richiesta. Si avvisa solo se c'è qualcosa di scritto.
                gruppo.classList.toggle("has-error", input.value !== "");
                return;
            }
            gruppo.classList.remove("has-error");
            programma(v, ATTESA_DIGITAZIONE);
        });

        input.addEventListener("blur", function () {
            if (valoreDigitato() === null) {                // lasciato vuoto/non valido: si ripristina
                clearTimeout(timer);
                timer = null;
                input.value = ultimaValida;
                gruppo.classList.remove("has-error");
                aggiornaPulsanti();
            } else {
                subito();
            }
        });

        form.addEventListener("submit", function (e) {      // Invio nel campo: niente ricarica
            e.preventDefault();
            var v = valoreDigitato();
            if (v === null) {
                gruppo.classList.add("has-error");
            } else {
                clearTimeout(timer);
                timer = null;
                invia(v);
            }
        });

        [btnMeno, btnPiu].forEach(function (btn) {
            btn.addEventListener("click", function () {
                var corrente = valoreDigitato();
                if (corrente === null) { corrente = ultimaValida; }
                var nuovo = Math.min(MAX, Math.max(MIN, corrente + parseInt(btn.dataset.delta, 10)));
                input.value = nuovo;
                gruppo.classList.remove("has-error");
                mostraMessaggio("");
                aggiornaPulsanti();
                programma(nuovo, ATTESA_PULSANTI);          // più clic ravvicinati = una sola richiesta
            });
        });

        aggiornaPulsanti();
    });
})();
