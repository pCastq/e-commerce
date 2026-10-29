<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW della fattura (da OrdineServlet su /fattura). Attributo: ordine (model.Ordine).
    A schermo si vede dentro il layout normale; in stampa (@media print nel CSS) restano solo
    i dati della fattura: header, menu, footer e pulsanti (classe "no-print") spariscono.
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Fattura"/>
    <jsp:param name="active" value="ordini"/>
</jsp:include>

<div class="invoice-actions no-print">
    <a class="btn btn-light" href="${pageContext.request.contextPath}/ordini">&larr; I miei ordini</a>
    <button type="button" id="btn-stampa" class="btn">Stampa / Salva PDF</button>
</div>

<article class="invoice card">
    <header class="invoice-head">
        <div>
            <h1>Fattura</h1>
            <p><strong>N. <fmt:formatDate value="${ordine.dataOrdine}" pattern="yyyy"/>/<fmt:formatNumber value="${ordine.id}" pattern="000000"/></strong><br>
               Data: <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy"/></p>
        </div>
        <div class="invoice-seller">
            <strong>ShopTSW S.r.l.</strong><br>
            Via dell'Esempio 1, 00100 Roma (RM)<br>
            P. IVA 00000000000
        </div>
    </header>

    <section class="invoice-parties">
        <div>
            <h2>Cliente</h2>
            <p><c:out value="${sessionScope.utente.nome}"/> <c:out value="${sessionScope.utente.cognome}"/><br>
               <c:out value="${sessionScope.utente.email}"/></p>
        </div>
        <div>
            <h2>Spedizione</h2>
            <p><c:out value="${ordine.indirizzoSpedizione}"/></p>
        </div>
    </section>

    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Descrizione</th><th class="num">Q.tà</th><th class="num">Prezzo unit.</th><th class="num">IVA</th><th class="num">Totale</th></tr>
            </thead>
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
    <p class="muted"><small>I prezzi unitari sono IVA inclusa.</small></p>

    <h2>Riepilogo IVA</h2>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Aliquota</th><th class="num">Imponibile</th><th class="num">IVA</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${ordine.riepilogoIva}">
                <tr>
                    <td><fmt:formatNumber value="${r.aliquota}" pattern="0.##"/>%</td>
                    <td class="num"><fmt:formatNumber value="${r.imponibile}" pattern="#,##0.00"/> &euro;</td>
                    <td class="num"><fmt:formatNumber value="${r.iva}" pattern="#,##0.00"/> &euro;</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="invoice-total">
        <div>Imponibile: <fmt:formatNumber value="${ordine.totaleImponibile}" pattern="#,##0.00"/> &euro;</div>
        <div>IVA: <fmt:formatNumber value="${ordine.totaleIva}" pattern="#,##0.00"/> &euro;</div>
        <div class="cart-total-value">Totale: <fmt:formatNumber value="${ordine.totale}" pattern="#,##0.00"/> &euro;</div>
    </div>

</article>

<script src="${pageContext.request.contextPath}/js/fattura.js" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
