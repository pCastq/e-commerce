<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%-- VIEW del dettaglio prodotto. Attributo ricevuto: prodotto (Product). --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="${prodotto.nome}"/>
    <jsp:param name="active" value="catalogo"/>
</jsp:include>

<p><a href="${pageContext.request.contextPath}/catalogo">&larr; Torna al catalogo</a></p>

<section class="product-detail">
    <div class="product-image product-image-big">
        <img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${prodotto.immagineFile}'/>"
             alt="<c:out value='${prodotto.nome}'/>">
    </div>
    <div class="product-info">
        <h1><c:out value="${prodotto.nome}"/></h1>
        <p class="product-price product-price-big">
            <fmt:formatNumber value="${prodotto.prezzo}" pattern="#,##0.00"/> &euro;
            <small>IVA inclusa (<fmt:formatNumber value="${prodotto.iva}" pattern="0.##"/>%)</small>
        </p>
        <c:choose>
            <c:when test="${prodotto.giacenza > 0}">
                <p class="stock stock-ok">Disponibile (<c:out value="${prodotto.giacenza}"/> pezzi)</p>
            </c:when>
            <c:otherwise>
                <p class="stock stock-out">Non disponibile al momento</p>
            </c:otherwise>
        </c:choose>
        <p><c:out value="${prodotto.descrizione}"/></p>

        <c:if test="${prodotto.giacenza > 0}">
            <form class="qty-form js-qty-form" method="post" action="${pageContext.request.contextPath}/carrello" novalidate>
                <input type="hidden" name="azione" value="aggiungi">
                <input type="hidden" name="id" value="${prodotto.id}">
                <div class="form-group">
                    <label for="quantita">Quantità (1-99)</label>
                    <input type="number" id="quantita" name="quantita" class="qty-input" value="1" min="1" max="99"
                           placeholder="1-99" inputmode="numeric">
                    <p class="field-error">Inserisci un numero intero da 1 a 99.</p>
                </div>
                <button type="submit" class="btn">Aggiungi al carrello</button>
            </form>
        </c:if>
    </div>
</section>

<script src="${pageContext.request.contextPath}/js/carrello.js?v=2" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
