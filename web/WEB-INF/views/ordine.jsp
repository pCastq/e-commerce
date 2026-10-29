<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%-- VIEW del dettaglio di un ordine (da OrdineServlet). Attributo: ordine (model.Ordine). --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Ordine"/>
    <jsp:param name="active" value="ordini"/>
</jsp:include>

<h1>Ordine n. ${ordine.id}</h1>

<div class="card">
    <p><strong>Data:</strong> <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/></p>
    <p><strong>Spedizione a:</strong> <c:out value="${ordine.indirizzoSpedizione}"/></p>

    <div class="table-wrap">
        <table>
            <thead><tr><th>Prodotto</th><th>Quantità</th><th>Prezzo</th><th>IVA</th><th>Totale</th></tr></thead>
            <tbody>
            <c:forEach var="riga" items="${ordine.righe}">
                <tr>
                    <td><c:out value="${riga.nomeProdotto}"/></td>
                    <td>${riga.quantita}</td>
                    <td><fmt:formatNumber value="${riga.prezzoUnitario}" pattern="#,##0.00"/> &euro;</td>
                    <td><fmt:formatNumber value="${riga.iva}" pattern="0.##"/>%</td>
                    <td><fmt:formatNumber value="${riga.subtotale}" pattern="#,##0.00"/> &euro;</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <p>Imponibile: <fmt:formatNumber value="${ordine.totaleImponibile}" pattern="#,##0.00"/> &euro;
       &middot; IVA: <fmt:formatNumber value="${ordine.totaleIva}" pattern="#,##0.00"/> &euro;</p>
    <p class="cart-total-value">Totale: <fmt:formatNumber value="${ordine.totale}" pattern="#,##0.00"/> &euro;</p>
</div>

<div class="cart-buttons">
    <a class="btn btn-light" href="${pageContext.request.contextPath}/ordini">Tutti i miei ordini</a>
    <a class="btn" href="${pageContext.request.contextPath}/fattura?id=${ordine.id}">Vedi la fattura</a>
</div>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
