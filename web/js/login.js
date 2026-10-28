/* Controllo del form di login nel browser: campi non vuoti e email ben formata.
   (La verifica vera delle credenziali la fa solo il server.) */
(function () {
    "use strict";

    var RE_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    var form = document.getElementById("form-login");
    if (!form) { return; }

    function mostra(id, errore) {
        document.getElementById(id).closest(".form-group").classList.toggle("has-error", errore);
    }
    function emailOk() { return RE_EMAIL.test(form.email.value.trim()); }
    function passwordOk() { return form.password.value !== ""; }

    form.email.addEventListener("blur", function () {
        if (form.email.value !== "") { mostra("email", !emailOk()); }
    });
    form.email.addEventListener("input", function () { if (emailOk()) { mostra("email", false); } });
    form.password.addEventListener("input", function () { if (passwordOk()) { mostra("password", false); } });

    form.addEventListener("submit", function (e) {
        var okEmail = emailOk(), okPass = passwordOk();
        mostra("email", !okEmail);
        mostra("password", !okPass);
        if (!okEmail || !okPass) {
            e.preventDefault();
            (!okEmail ? form.email : form.password).focus();
        }
    });

    form.email.focus();
})();
