/* Validazione del form di registrazione (lato browser).
   Le regex sono le stesse di util/Validator.java: il server le rifà sempre. */
(function () {
    "use strict";

    var RE_NOME = /^[\p{L}' -]{2,50}$/u;
    var RE_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    var RE_PASSWORD = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[.,_\-!?@]).{8,100}$/;

    var form = document.getElementById("form-registrazione");
    if (!form) { return; }

    var contextPath = (document.body.dataset.contextPath || "");
    var emailStato = "sconosciuto";   // "sconosciuto" | "libera" | "occupata"
    var richiestaEmail = 0;           // numero progressivo, per ignorare risposte vecchie

    var campi = {
        nome: {
            msg: "Inserisci un nome valido (solo lettere, 2-50 caratteri).",
            ok: function (v) { return RE_NOME.test(v.trim()); }
        },
        cognome: {
            msg: "Inserisci un cognome valido (solo lettere, 2-50 caratteri).",
            ok: function (v) { return RE_NOME.test(v.trim()); }
        },
        email: {
            msg: "Inserisci un indirizzo email valido.",
            ok: function (v) { return RE_EMAIL.test(v.trim()); }
        },
        password: {
            msg: "La password deve avere almeno 8 caratteri, con maiuscola, minuscola, numero e un simbolo tra . , _ - ! ? @",
            ok: function (v) { return RE_PASSWORD.test(v); }
        },
        conferma: {
            msg: "Le password non coincidono.",
            ok: function (v) { return v !== "" && v === form.password.value; }
        }
    };

    function mostraErrore(nome, testo) {
        var input = document.getElementById(nome);
        var gruppo = input.closest(".form-group");
        document.getElementById("err-" + nome).textContent = testo || "";
        gruppo.classList.toggle("has-error", !!testo);
    }

    function controlla(nome) {
        var input = document.getElementById(nome);
        var valido = campi[nome].ok(input.value);
        mostraErrore(nome, valido ? "" : campi[nome].msg);
        return valido;
    }

    /* ---- AJAX: l'email è già registrata? ---- */
    function controllaDisponibilita() {
        var okBox = document.getElementById("ok-email");
        var email = form.email.value.trim();
        okBox.textContent = "";
        emailStato = "sconosciuto";
        if (!campi.email.ok(email)) { return; }

        var numero = ++richiestaEmail;
        fetch(contextPath + "/api/email-disponibile?email=" + encodeURIComponent(email),
              { headers: { "Accept": "application/json" } })
            .then(function (r) { if (!r.ok) { throw new Error("HTTP " + r.status); } return r.json(); })
            .then(function (dati) {
                if (numero !== richiestaEmail) { return; }          // risposta superata da una più recente
                if (dati.disponibile) {
                    emailStato = "libera";
                    mostraErrore("email", "");
                    okBox.textContent = "Email disponibile";
                } else {
                    emailStato = "occupata";
                    mostraErrore("email", "Questa email è già registrata.");
                }
            })
            .catch(function () { /* se la richiesta fallisce il server controllerà comunque all'invio */ });
    }

    /* ---- eventi ---- */
    Object.keys(campi).forEach(function (nome) {
        var input = document.getElementById(nome);
        input.addEventListener("blur", function () {            // quando si lascia il campo
            if (input.value === "" && nome !== "conferma") { return; }   // non gridare su campi appena saltati
            if (controlla(nome) && nome === "email") { controllaDisponibilita(); }
        });
        input.addEventListener("input", function () {           // mentre si scrive: toglie l'errore appena corretto
            if (input.closest(".form-group").classList.contains("has-error") && campi[nome].ok(input.value)) {
                mostraErrore(nome, "");
            }
            if (nome === "email") {
                emailStato = "sconosciuto";
                document.getElementById("ok-email").textContent = "";
            }
        });
    });

    form.addEventListener("submit", function (e) {
        var primoErrore = null;
        Object.keys(campi).forEach(function (nome) {
            if (!controlla(nome) && !primoErrore) { primoErrore = nome; }
        });
        if (!primoErrore && emailStato === "occupata") {
            mostraErrore("email", "Questa email è già registrata.");
            primoErrore = "email";
        }
        if (primoErrore) {
            e.preventDefault();                                  // blocca l'invio
            document.getElementById(primoErrore).focus();        // porta il cursore sul primo errore
        }
    });

    /* Focus iniziale: sul primo campo con errore (server) oppure sul nome. */
    var errServer = form.querySelector(".has-error input");
    (errServer || form.nome).focus();
})();
