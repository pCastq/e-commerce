<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW della home/catalogo. Si raggiunge solo tramite CatalogoServlet ("/"):
    sta in WEB-INF/views, quindi il browser non può aprirla direttamente.
    Attributi ricevuti dalla servlet: prodotti (List<Product>), q (testo cercato).
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Catalogo"/>
    <jsp:param name="active" value="catalogo"/>
</jsp:include>

<%-- Il banner si vede solo sulla home "pulita"; durante una ricerca si va dritti ai risultati. --%>
<c:if test="${empty q}">
    <section class="hero">
        <h1>Benvenuto su ShopTSW</h1>
        <p>Il tuo negozio online: tutto per la casa e per la tavola, a portata di click.</p>
    </section>
</c:if>

<c:choose>
    <c:when test="${empty q}"><h2 class="section-title">Catalogo</h2></c:when>
    <c:otherwise><h1>Catalogo</h1></c:otherwise>
</c:choose>

<c:if test="${not empty q}">
    <p class="muted">
        Risultati per &laquo;<strong><c:out value="${q}"/></strong>&raquo;
        &ndash; <a href="${pageContext.request.contextPath}/">mostra tutto il catalogo</a>
    </p>
</c:if>

<c:choose>
    <c:when test="${empty prodotti}">
        <div class="card">
            <p>Nessun prodotto trovato.</p>
            <a class="btn" href="${pageContext.request.contextPath}/">Torna al catalogo</a>
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
                    <div class="product-buy">
                        <p class="product-price"><fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/> &euro;</p>
                        <c:choose>
                            <c:when test="${p.giacenza > 0}">
                                <p class="stock stock-ok">Disponibile</p>
                                <%-- Quantità + aggiunta. quantita.js + catalogo.js la inviano con AJAX; senza JS è un normale POST. --%>
                                <form class="js-add-form" method="post" action="${pageContext.request.contextPath}/carrello" novalidate>
                                    <input type="hidden" name="azione" value="aggiungi">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <div class="form-group">
                                        <div class="qty-stepper">
                                            <button type="button" class="qty-btn" data-delta="-1" aria-label="Diminuisci la quantità">&minus;</button>
                                            <input type="text" name="quantita" class="qty-input" value="1" inputmode="numeric"
                                                   maxlength="2" autocomplete="off" placeholder="1-99"
                                                   aria-label="Quantità di <c:out value='${p.nome}'/>">
                                            <button type="button" class="qty-btn" data-delta="1" aria-label="Aumenta la quantità">+</button>
                                        </div>
                                        <p class="field-error">Inserisci un numero da 1 a 99.</p>
                                    </div>
                                    <button type="submit" class="btn btn-block">Aggiungi al carrello</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <p class="stock stock-out">Non disponibile</p>
                            </c:otherwise>
                        </c:choose>
                        <a class="btn btn-light btn-block" href="${pageContext.request.contextPath}/prodotto?id=${p.id}">Vedi dettagli</a>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<div id="toast" class="toast" role="status" aria-live="polite" hidden></div>
<script src="${pageContext.request.contextPath}/js/quantita.js?v=1" defer></script>
<script src="${pageContext.request.contextPath}/js/catalogo.js?v=3" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
