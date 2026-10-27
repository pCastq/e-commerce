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
        <img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${prodotto.immagine}'/>"
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
    </div>
</section>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
