<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    VIEW della registrazione (si raggiunge da RegistrazioneServlet).
    Attributi usati solo dopo un errore del server: errori (Map campo->messaggio),
    nome, cognome, email (per non far riscrivere tutto; la password MAI).
    novalidate: disattiva i fumetti del browser, gli errori li mostriamo noi sotto i campi.
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Registrazione"/>
    <jsp:param name="active" value="registrazione"/>
</jsp:include>

<div class="form-page">
<h1>Crea il tuo account</h1>

<form id="form-registrazione" class="card" method="post"
      action="${pageContext.request.contextPath}/registrazione" novalidate>

    <div class="form-group ${not empty errori.nome ? 'has-error' : ''}">
        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" maxlength="50" autocomplete="given-name"
               placeholder="Es. Mario (solo lettere, 2-50 caratteri)"
               value="<c:out value='${nome}'/>">
        <p class="field-error" id="err-nome"><c:out value="${errori.nome}"/></p>
    </div>

    <div class="form-group ${not empty errori.cognome ? 'has-error' : ''}">
        <label for="cognome">Cognome</label>
        <input type="text" id="cognome" name="cognome" maxlength="50" autocomplete="family-name"
               placeholder="Es. Rossi (solo lettere, 2-50 caratteri)"
               value="<c:out value='${cognome}'/>">
        <p class="field-error" id="err-cognome"><c:out value="${errori.cognome}"/></p>
    </div>

    <div class="form-group ${not empty errori.email ? 'has-error' : ''}">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" maxlength="255" autocomplete="email"
               placeholder="Es. mario.rossi@example.com"
               value="<c:out value='${email}'/>">
        <p class="field-error" id="err-email"><c:out value="${errori.email}"/></p>
        <p class="field-ok" id="ok-email"></p>
    </div>

    <div class="form-group ${not empty errori.password ? 'has-error' : ''}">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" maxlength="100" autocomplete="new-password"
               placeholder="Min. 8 caratteri: maiuscola, minuscola, numero e . , _ - ! ? @">
        <p class="field-error" id="err-password"><c:out value="${errori.password}"/></p>
    </div>

    <div class="form-group ${not empty errori.conferma ? 'has-error' : ''}">
        <label for="conferma">Conferma password</label>
        <input type="password" id="conferma" name="conferma" maxlength="100" autocomplete="new-password"
               placeholder="Riscrivi la stessa password">
        <p class="field-error" id="err-conferma"><c:out value="${errori.conferma}"/></p>
    </div>

    <button type="submit" class="btn btn-block">Registrati</button>
    <p class="muted form-foot">Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi</a></p>
</form>
</div>

<script src="${pageContext.request.contextPath}/js/registrazione.js?v=1" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
