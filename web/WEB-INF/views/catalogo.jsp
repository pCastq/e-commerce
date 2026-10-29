<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW del catalogo. Si raggiunge solo tramite CatalogoServlet (/catalogo):
    sta in WEB-INF/views, quindi il browser non può aprirla direttamente.
    Attributi ricevuti dalla servlet: prodotti (List<Product>), q (testo cercato).
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Catalogo"/>
    <jsp:param name="active" value="catalogo"/>
</jsp:include>

<h1>Catalogo</h1>

<c:if test="${not empty q}">
    <p class="muted">
        Risultati per &laquo;<strong><c:out value="${q}"/></strong>&raquo;
        &ndash; <a href="${pageContext.request.contextPath}/catalogo">mostra tutto il catalogo</a>
    </p>
</c:if>

<c:choose>
    <c:when test="${empty prodotti}">
        <div class="card">
            <p>Nessun prodotto trovato.</p>
            <a class="btn" href="${pageContext.request.contextPath}/catalogo">Torna al catalogo</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="grid-cards">
            <c:forEach var="p" items="${prodotti}">
                <article class="product-card">
                    <a class="product-link" href="${pageContext.request.contextPath}/prodotto?id=${p.id}">
                        <div class="product-image">
                            <img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${p.immagineFile}'/>"
                                 alt="<c:out value='${p.nome}'/>" loading="lazy">
                        </div>
                        <h2 class="product-name"><c:out value="${p.nome}"/></h2>
                    </a>
                    <p class="product-price"><fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/> &euro;</p>
                    <c:choose>
                        <c:when test="${p.giacenza > 0}">
                            <p class="stock stock-ok">Disponibile</p>
                        </c:when>
                        <c:otherwise>
                            <p class="stock stock-out">Non disponibile</p>
                        </c:otherwise>
                    </c:choose>
                    <div class="card-actions">
                        <c:if test="${p.giacenza > 0}">
                            <form method="post" action="${pageContext.request.contextPath}/carrello">
                                <input type="hidden" name="azione" value="aggiungi">
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="hidden" name="quantita" value="1">
                                <button type="submit" class="btn btn-block">Aggiungi al carrello</button>
                            </form>
                        </c:if>
                        <a class="btn btn-light btn-block" href="${pageContext.request.contextPath}/prodotto?id=${p.id}">Vedi dettagli</a>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
