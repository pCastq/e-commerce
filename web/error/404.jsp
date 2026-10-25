<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Errore 404"/>
</jsp:include>

<section class="card">
    <h1>404 - Pagina non trovata</h1>
    <p>La risorsa richiesta non esiste.</p>
    <a class="btn" href="${pageContext.request.contextPath}/">Torna alla home</a>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
