<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
    FRAGMENT: menu di navigazione (incluso dall'header).
    La voce evidenziata dipende dal parametro "active".
    I link verso pagine non ancora realizzate (catalogo, carrello, login, registrazione)
    mostrano per ora la pagina 404: verranno attivati nei prossimi passi.
--%>
<nav id="site-nav" class="site-nav" aria-label="Menu principale">
    <form class="search-form" role="search" action="${pageContext.request.contextPath}/catalogo" method="get">
        <input type="search" name="q" placeholder="Cerca un prodotto..." aria-label="Cerca un prodotto" autocomplete="off">
        <button type="submit">Cerca</button>
    </form>
    <a href="${pageContext.request.contextPath}/" class="${param.active == 'home' ? 'active' : ''}">Home</a>
    <a href="${pageContext.request.contextPath}/catalogo" class="${param.active == 'catalogo' ? 'active' : ''}">Catalogo</a>
    <a href="${pageContext.request.contextPath}/carrello" class="${param.active == 'carrello' ? 'active' : ''}">Carrello</a>
    <a href="${pageContext.request.contextPath}/login" class="${param.active == 'login' ? 'active' : ''}">Accedi</a>
    <a href="${pageContext.request.contextPath}/registrazione" class="${param.active == 'registrazione' ? 'active' : ''}">Registrati</a>
</nav>
