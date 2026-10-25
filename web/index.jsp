<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Home"/>
    <jsp:param name="active" value="home"/>
</jsp:include>

<section class="hero">
    <h1>Benvenuto su ShopTSW</h1>
    <p>Il tuo negozio online: tutto per la casa e per la tavola, a portata di click.</p>
    <a class="btn btn-light" href="${pageContext.request.contextPath}/catalogo">Vai al catalogo</a>
</section>

<section class="card dev-box">
    <h2>Pagine di prova (sviluppo)</h2>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ciao?nome=Mario">Prova la servlet (MVC)</a></li>
        <li><a href="${pageContext.request.contextPath}/test-db">Prova il database (pool)</a></li>
        <li><a href="${pageContext.request.contextPath}/pagina-che-non-esiste">Prova la pagina 404</a></li>
    </ul>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
