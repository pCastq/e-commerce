<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    VIEW del login (da LoginServlet).
    Attributi: errore (messaggio generico se le credenziali sono sbagliate),
    email (da ripresentare), next (pagina a cui tornare dopo l'accesso).
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Accedi"/>
    <jsp:param name="active" value="login"/>
</jsp:include>

<div class="form-page">
<h1>Accedi</h1>

<c:if test="${param.registrato == '1'}">
    <div class="alert alert-success">Registrazione completata! Ora puoi accedere con la tua email e la tua password.</div>
</c:if>
<c:if test="${not empty errore}">
    <div class="alert alert-error" role="alert"><c:out value="${errore}"/></div>
</c:if>
<c:if test="${not empty next and empty errore}">
    <p class="muted">Per continuare devi accedere.</p>
</c:if>

<form id="form-login" class="card" method="post" action="${pageContext.request.contextPath}/login" novalidate>
    <input type="hidden" name="next" value="<c:out value='${next}'/>">

    <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" maxlength="255" autocomplete="username"
               placeholder="Es. mario.rossi@example.com"
               value="<c:out value='${email}'/>">
        <p class="field-error" id="err-email">Inserisci un indirizzo email valido.</p>
    </div>

    <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" maxlength="100" autocomplete="current-password"
               placeholder="La tua password">
        <p class="field-error" id="err-password">Inserisci la password.</p>
    </div>

    <button type="submit" class="btn btn-block">Accedi</button>
    <p class="muted form-foot">Non hai un account? <a href="${pageContext.request.contextPath}/registrazione">Registrati</a></p>
</form>
</div>

<script src="${pageContext.request.contextPath}/js/login.js?v=1" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
