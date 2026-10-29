<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW del carrello (da CarrelloServlet).
    Attributi: righe (List<CartLine>), totale (BigDecimal, IVA inclusa), avviso (String o null).
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Carrello"/>
    <jsp:param name="active" value="carrello"/>
</jsp:include>

<h1>Il tuo carrello</h1>

<c:if test="${not empty avviso}">
    <div class="alert alert-error" role="alert"><c:out value="${avviso}"/></div>
</c:if>

<c:choose>
    <c:when test="${empty righe}">
        <div class="card">
            <p>Il carrello è vuoto.</p>
            <a class="btn" href="${pageContext.request.contextPath}/catalogo">Vai al catalogo</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cart-list">
            <c:forEach var="riga" items="${righe}">
                <article class="cart-line" data-id="${riga.prodotto.id}">
                    <img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${riga.prodotto.immagineFile}'/>"
                         alt="<c:out value='${riga.prodotto.nome}'/>">
                    <div class="cart-info">
                        <a href="${pageContext.request.contextPath}/prodotto?id=${riga.prodotto.id}"><strong><c:out value="${riga.prodotto.nome}"/></strong></a>
                        <div class="muted"><fmt:formatNumber value="${riga.prodotto.prezzo}" pattern="#,##0.00"/> &euro; cad.</div>
                    </div>

                    <%-- Quantità: stepper - [ n ] +. carrello.js manda una fetch (AJAX) a ogni modifica valida.
                         Senza JavaScript resta il pulsante "Aggiorna" dentro <noscript>. --%>
                    <form class="qty-form js-cart-qty cart-qty" method="post" action="${pageContext.request.contextPath}/carrello" novalidate>
                        <input type="hidden" name="azione" value="aggiorna">
                        <input type="hidden" name="id" value="${riga.prodotto.id}">
                        <div class="form-group">
                            <label for="q-${riga.prodotto.id}">Quantità</label>
                            <div class="qty-stepper">
                                <button type="button" class="qty-btn" data-delta="-1" aria-label="Diminuisci la quantità">&minus;</button>
                                <input type="text" id="q-${riga.prodotto.id}" name="quantita" class="qty-input"
                                       value="${riga.quantita}" inputmode="numeric" maxlength="2" autocomplete="off"
                                       placeholder="1-99" aria-label="Quantità di <c:out value='${riga.prodotto.nome}'/>">
                                <button type="button" class="qty-btn" data-delta="1" aria-label="Aumenta la quantità">+</button>
                            </div>
                            <p class="field-error">Inserisci un numero da 1 a 99.</p>
                            <p class="qty-msg" role="status" aria-live="polite"></p>
                        </div>
                        <noscript><button type="submit" class="btn btn-light">Aggiorna</button></noscript>
                    </form>

                    <div class="cart-sub">
                        <div class="cart-subtotal"><fmt:formatNumber value="${riga.subtotale}" pattern="#,##0.00"/> &euro;</div>
                        <form method="post" action="${pageContext.request.contextPath}/carrello">
                            <input type="hidden" name="azione" value="rimuovi">
                            <input type="hidden" name="id" value="${riga.prodotto.id}">
                            <button type="submit" class="btn btn-danger">Rimuovi</button>
                        </form>
                    </div>
                </article>
            </c:forEach>
        </div>

        <div class="card cart-total">
            <div>Totale <small class="muted">(IVA inclusa)</small></div>
            <div class="cart-total-value" id="cart-total"><fmt:formatNumber value="${totale}" pattern="#,##0.00"/> &euro;</div>
        </div>

        <div class="cart-buttons">
            <a class="btn btn-light" href="${pageContext.request.contextPath}/catalogo">Continua gli acquisti</a>
            <form method="post" action="${pageContext.request.contextPath}/carrello">
                <input type="hidden" name="azione" value="svuota">
                <button type="submit" class="btn btn-light">Svuota il carrello</button>
            </form>
            <a class="btn" href="${pageContext.request.contextPath}/checkout">Procedi all'ordine</a>
        </div>
    </c:otherwise>
</c:choose>

<script src="${pageContext.request.contextPath}/js/carrello.js?v=2" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
