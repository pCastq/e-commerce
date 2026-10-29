<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%-- VIEW admin: dettaglio di un ordine con i dati del cliente (da AdminOrdineServlet). Attributo: ordine. --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Ordine"/>
    <jsp:param name="active" value="admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/fragments/admin-nav.jsp"><jsp:param name="section" value="ordini"/></jsp:include>

<h1>Ordine n. ${ordine.id}</h1>

<div class="card">
    <p><strong>Data:</strong> <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/></p>
    <p><strong>Cliente:</strong> <c:out value="${ordine.clienteNome}"/> <c:out value="${ordine.clienteCognome}"/>
       (<c:out value="${ordine.clienteEmail}"/>)</p>
    <p><strong>Spedizione a:</strong> <c:out value="${ordine.indirizzoSpedizione}"/></p>

    <div class="table-wrap">
        <table>
            <thead><tr><th>Prodotto</th><th class="num">Q.tà</th><th class="num">Prezzo</th><th class="num">IVA</th><th class="num">Totale</th></tr></thead>
            <tbody>
            <c:forEach var="riga" items="${ordine.righe}">
                <tr>
                    <td><c:out value="${riga.nomeProdotto}"/></td>
                    <td class="num">${riga.quantita}</td>
                    <td class="num"><fmt:formatNumber value="${riga.prezzoUnitario}" pattern="#,##0.00"/> &euro;</td>
                    <td class="num"><fmt:formatNumber value="${riga.iva}" pattern="0.##"/>%</td>
                    <td class="num"><fmt:formatNumber value="${riga.subtotale}" pattern="#,##0.00"/> &euro;</td>
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
    <a class="btn" href="${pageContext.request.contextPath}/admin/ordini">&larr; Tutti gli ordini</a>
</div>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
