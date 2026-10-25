<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Errore 403"/>
</jsp:include>

<section class="card">
    <h1>403 - Accesso negato</h1>
    <p>Non hai i permessi per vedere questa pagina.</p>
    <a class="btn" href="${pageContext.request.contextPath}/">Torna alla home</a>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
