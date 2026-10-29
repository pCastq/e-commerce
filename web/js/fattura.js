/* Pulsante "Stampa / Salva PDF" della fattura: apre la finestra di stampa del browser.
   Lì si può scegliere "Salva come PDF". Cosa si stampa lo decide la sezione @media print del CSS. */
(function () {
    "use strict";
    var btn = document.getElementById("btn-stampa");
    if (btn) {
        btn.addEventListener("click", function () { window.print(); });
    }
})();
