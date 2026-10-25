<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Errore 500"/>
</jsp:include>

<section class="card">
    <h1>500 - Errore del server</h1>
    <p>Si è verificato un problema. Riprova più tardi.</p>
    <a class="btn" href="${pageContext.request.contextPath}/">Torna alla home</a>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
