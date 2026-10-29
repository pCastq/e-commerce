/* Ricerca con suggerimenti (AJAX).
   Mentre si scrive nella barra di ricerca del menu, dopo una breve pausa (debounce) si chiede al server
   /api/suggerimenti?q=... e si mostra un elenco di prodotti cliccabili.
   Senza JavaScript la barra funziona lo stesso: il form porta a /catalogo?q=... */
(function () {
    "use strict";

    var MIN_CARATTERI = 2;      // come sul server (ProductService.SUGGERIMENTI_MIN_CARATTERI)
    var ATTESA_MS = 250;        // pausa di digitazione prima di interrogare il server

    var form = document.querySelector(".search-form");
    if (!form) { return; }
    var input = form.querySelector("input[name='q']");
    if (!input) { return; }

    var ctx = document.body.getAttribute("data-context-path") || "";
    var lista = document.createElement("ul");
    lista.className = "suggest-list";
    lista.id = "suggest-list";
    lista.setAttribute("role", "listbox");
    lista.hidden = true;
    form.appendChild(lista);

    input.setAttribute("role", "combobox");
    input.setAttribute("aria-autocomplete", "list");
    input.setAttribute("aria-expanded", "false");
    input.setAttribute("aria-controls", "suggest-list");

    var timer = null;
    var richiesta = null;       // AbortController della richiesta in corso
    var attivo = -1;            // indice della voce evidenziata con la tastiera

    function voci() { return lista.querySelectorAll("li[data-href]"); }

    function chiudi() {
        lista.hidden = true;
        lista.textContent = "";
        attivo = -1;
        input.setAttribute("aria-expanded", "false");
        input.removeAttribute("aria-activedescendant");
    }

    function evidenzia(i) {
        var v = voci();
        if (v.length === 0) { return; }
        attivo = (i + v.length) % v.length;
        for (var k = 0; k < v.length; k++) {
            v[k].classList.toggle("is-active", k === attivo);
            v[k].setAttribute("aria-selected", String(k === attivo));
        }
        input.setAttribute("aria-activedescendant", v[attivo].id);
    }

    function mostra(prodotti) {
        lista.textContent = "";
        attivo = -1;
        if (prodotti.length === 0) {
            var vuoto = document.createElement("li");
            vuoto.className = "suggest-empty";
            vuoto.textContent = "Nessun prodotto trovato";
            lista.appendChild(vuoto);
        }
        prodotti.forEach(function (p, i) {
            var li = document.createElement("li");
            li.id = "suggest-" + i;
            li.setAttribute("role", "option");
            li.setAttribute("data-href", ctx + "/prodotto?id=" + encodeURIComponent(p.id));
            var a = document.createElement("a");
            a.href = ctx + "/prodotto?id=" + encodeURIComponent(p.id);
            a.textContent = p.nome;              // textContent: il nome non viene mai interpretato come HTML (anti XSS)
            li.appendChild(a);
            lista.appendChild(li);
        });
        lista.hidden = false;
        input.setAttribute("aria-expanded", "true");
    }

    function cerca() {
        var q = input.value.trim();
        if (q.length < MIN_CARATTERI) { chiudi(); return; }

        if (richiesta) { richiesta.abort(); }    // la risposta di una ricerca vecchia non deve scavalcare quella nuova
        richiesta = new AbortController();

        fetch(ctx + "/api/suggerimenti?q=" + encodeURIComponent(q), {
            headers: { "Accept": "application/json" },
            signal: richiesta.signal
        })
            .then(function (r) { if (!r.ok) { throw new Error("HTTP " + r.status); } return r.json(); })
            .then(mostra)
            .catch(function (err) { if (err.name !== "AbortError") { chiudi(); } });
    }

    input.addEventListener("input", function () {
        clearTimeout(timer);
        timer = setTimeout(cerca, ATTESA_MS);
    });

    input.addEventListener("keydown", function (e) {
        if (lista.hidden) { return; }
        if (e.key === "ArrowDown") { e.preventDefault(); evidenzia(attivo + 1); }
        else if (e.key === "ArrowUp") { e.preventDefault(); evidenzia(attivo - 1); }
        else if (e.key === "Escape") { chiudi(); }
        else if (e.key === "Enter" && attivo >= 0) {
            e.preventDefault();                  // niente invio del form: si apre il prodotto scelto
            window.location.href = voci()[attivo].getAttribute("data-href");
        }
    });

    document.addEventListener("click", function (e) {
        if (!form.contains(e.target)) { chiudi(); }
    });
})();
