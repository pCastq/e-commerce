<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    FRAGMENT: menu di navigazione (incluso dall'header).
    La voce evidenziata dipende dal parametro "active".
    Le voci cambiano in base alla sessione: se esiste l'attributo "utente" sei loggato
    (Account + Esci), altrimenti vedi Accedi + Registrati.
    Accanto a "Carrello" compare il numero di pezzi presenti (se ce ne sono).
--%>
<nav id="site-nav" class="site-nav" aria-label="Menu principale">
    <form class="search-form" role="search" action="${pageContext.request.contextPath}/catalogo" method="get">
        <input type="search" name="q" placeholder="Cerca un prodotto..." aria-label="Cerca un prodotto" autocomplete="off">
        <button type="submit">Cerca</button>
    </form>
    <a href="${pageContext.request.contextPath}/" class="${param.active == 'home' ? 'active' : ''}">Home</a>
    <a href="${pageContext.request.contextPath}/catalogo" class="${param.active == 'catalogo' ? 'active' : ''}">Catalogo</a>
    <a href="${pageContext.request.contextPath}/carrello" class="${param.active == 'carrello' ? 'active' : ''}">Carrello<c:if test="${not empty sessionScope.carrello and sessionScope.carrello.totalItems > 0}"> <span class="cart-badge"><c:out value="${sessionScope.carrello.totalItems}"/></span></c:if></a>
    <c:choose>
        <c:when test="${not empty sessionScope.utente}">
            <a href="${pageContext.request.contextPath}/ordini" class="${param.active == 'ordini' ? 'active' : ''}">I miei ordini</a>
            <c:if test="${sessionScope.utente.admin}">
                <a href="${pageContext.request.contextPath}/admin/prodotti" class="${param.active == 'admin' ? 'active' : ''}">Admin</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/profilo" class="${param.active == 'profilo' ? 'active' : ''}">Ciao, <c:out value="${sessionScope.utente.nome}"/></a>
            <form class="logout-form" method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="link-button">Esci</button>
            </form>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/login" class="${param.active == 'login' ? 'active' : ''}">Accedi</a>
            <a href="${pageContext.request.contextPath}/registrazione" class="${param.active == 'registrazione' ? 'active' : ''}">Registrati</a>
        </c:otherwise>
    </c:choose>
</nav>
