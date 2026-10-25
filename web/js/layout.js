/* Comportamento del layout: apre/chiude il menu su telefono. */
(function () {
    "use strict";

    var toggle = document.getElementById("nav-toggle");
    var nav = document.getElementById("site-nav");
    if (!toggle || !nav) { return; }

    toggle.addEventListener("click", function () {
        var open = nav.classList.toggle("is-open");
        toggle.setAttribute("aria-expanded", String(open));
    });
})();
