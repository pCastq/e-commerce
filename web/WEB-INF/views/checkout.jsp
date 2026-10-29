<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW del checkout (da CheckoutServlet).
    Attributi: righe, totale, avviso; dopo un errore anche errore (String) e indirizzo (già scritto).
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Conferma ordine"/>
    <jsp:param name="active" value="carrello"/>
</jsp:include>

<h1>Conferma ordine</h1>

<c:if test="${not empty avviso}">
    <div class="alert alert-error" role="alert"><c:out value="${avviso}"/></div>
</c:if>

<div class="card">
    <h2>Riepilogo</h2>
    <div class="table-wrap">
        <table class="table">
            <thead><tr><th>Prodotto</th><th>Quantità</th><th>Prezzo</th><th>Totale</th></tr></thead>
            <tbody>
            <c:forEach var="riga" items="${righe}">
                <tr>
                    <td><c:out value="${riga.prodotto.nome}"/></td>
                    <td>${riga.quantita}</td>
                    <td><fmt:formatNumber value="${riga.prodotto.prezzo}" pattern="#,##0.00"/> &euro;</td>
                    <td><fmt:formatNumber value="${riga.subtotale}" pattern="#,##0.00"/> &euro;</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <p class="cart-total-value">Totale <small class="muted">(IVA inclusa)</small>:
        <fmt:formatNumber value="${totale}" pattern="#,##0.00"/> &euro;</p>
</div>

<form class="card" method="post" action="${pageContext.request.contextPath}/checkout" novalidate>
    <div class="form-group ${not empty errore ? 'has-error' : ''}">
        <label for="indirizzo">Indirizzo di spedizione</label>
        <textarea id="indirizzo" name="indirizzo" rows="3" maxlength="255" required
                  placeholder="Via, numero civico, CAP, città, provincia"><c:out value="${indirizzo}"/></textarea>
        <p class="field-error"><c:out value="${errore}"/></p>
    </div>
    <p class="muted">Il pagamento non è previsto in questo progetto: confermando, l'ordine viene registrato.</p>
    <div class="cart-buttons">
        <a class="btn btn-light" href="${pageContext.request.contextPath}/carrello">Torna al carrello</a>
        <button type="submit" class="btn">Conferma l'ordine</button>
    </div>
</form>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
