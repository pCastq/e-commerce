<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Pagina riservata di esempio: ci si arriva solo da loggati (AuthFilter). --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Il mio account"/>
    <jsp:param name="active" value="profilo"/>
</jsp:include>

<div class="form-page">
<h1>Il mio account</h1>
<div class="card">
    <p><strong>Nome:</strong> <c:out value="${sessionScope.utente.nome}"/> <c:out value="${sessionScope.utente.cognome}"/></p>
    <p><strong>Email:</strong> <c:out value="${sessionScope.utente.email}"/></p>
    <p><strong>Ruolo:</strong> <c:out value="${sessionScope.utente.ruolo}"/></p>
</div>
</div>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
