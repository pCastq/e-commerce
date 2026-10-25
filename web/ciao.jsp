<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Ciao"/>
</jsp:include>

<section class="card">
    <%-- nome e cognome arrivano dalla servlet; c:out li "ripulisce" (difesa contro XSS) --%>
    <h1>Ciao <c:out value="${nome}"/> <c:out value="${cognome}"/></h1>
    <a class="btn" href="${pageContext.request.contextPath}/">Torna alla home</a>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
