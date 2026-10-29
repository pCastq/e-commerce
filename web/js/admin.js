/* Area admin: finestra di conferma prima di eliminare un prodotto.
   Ogni form con l'attributo data-confirm, prima di essere inviato, apre una <dialog> con il messaggio.
   Si invia solo se l'utente preme "Elimina". Senza JavaScript il form parte direttamente:
   nessun danno, perché l'eliminazione è logica e si annulla con "Ripristina". */
(function () {
    "use strict";

    var dialog = document.getElementById("dlg-conferma");
    if (!dialog || typeof dialog.showModal !== "function") { return; }
    var testo = document.getElementById("dlg-testo");
    var daInviare = null;

    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (e) {
            e.preventDefault();                          // blocca l'invio: prima si chiede conferma
            daInviare = form;
            testo.textContent = form.getAttribute("data-confirm");
            dialog.showModal();
        });
    });

    dialog.addEventListener("close", function () {
        if (dialog.returnValue === "ok" && daInviare) {
            daInviare.submit();                          // submit() non riattiva questo controllo: parte davvero
        }
        daInviare = null;
        dialog.returnValue = "";
    });
})();
