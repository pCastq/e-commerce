<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%--
    VIEW admin: elenco ordini con filtri (da AdminOrdiniServlet).
    Attributi: ordini, errori (Map campo -> messaggio), troncato, totale, limite, e i filtri dal, al, cliente.
--%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Ordini"/>
    <jsp:param name="active" value="admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/fragments/admin-nav.jsp"><jsp:param name="section" value="ordini"/></jsp:include>

<h1>Ordini</h1>

<%-- method="get": i filtri finiscono nell'URL (?dal=...&al=...&cliente=...) --%>
<form class="card filter-form" method="get" action="${pageContext.request.contextPath}/admin/ordini" novalidate>
    <div class="form-group ${not empty errori.dal ? 'has-error' : ''}">
        <label for="dal">Dal</label>
        <input type="date" id="dal" name="dal" value="<c:out value='${dal}'/>">
        <p class="field-error"><c:out value="${errori.dal}"/></p>
    </div>
    <div class="form-group ${not empty errori.al ? 'has-error' : ''}">
        <label for="al">Al</label>
        <input type="date" id="al" name="al" value="<c:out value='${al}'/>">
        <p class="field-error"><c:out value="${errori.al}"/></p>
    </div>
    <div class="form-group ${not empty errori.cliente ? 'has-error' : ''}">
        <label for="cliente">Cliente (nome, cognome o email)</label>
        <input type="search" id="cliente" name="cliente" maxlength="100" value="<c:out value='${cliente}'/>" placeholder="Es. rossi">
        <p class="field-error"><c:out value="${errori.cliente}"/></p>
    </div>
    <div class="filter-buttons">
        <button type="submit" class="btn">Filtra</button>
        <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/ordini">Azzera</a>
    </div>
</form>

<c:if test="${empty errori}">
    <c:choose>
        <c:when test="${empty ordini}">
            <div class="card"><p>Nessun ordine corrisponde ai filtri.</p></div>
        </c:when>
        <c:otherwise>
            <c:if test="${troncato}">
                <div class="alert alert-error" role="alert">Ci sono più di ${limite} ordini: ne vedi solo i ${limite} più recenti. Restringi i filtri.</div>
            </c:if>
            <div class="card">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>N.</th><th>Data</th><th>Cliente</th><th class="hide-mobile">Articoli</th><th class="num">Totale</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="o" items="${ordini}">
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/admin/ordini/dettaglio?id=${o.id}"><strong>${o.id}</strong></a></td>
                                <td><fmt:formatDate value="${o.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td><c:out value="${o.clienteNome}"/> <c:out value="${o.clienteCognome}"/><br>
                                    <small class="muted"><c:out value="${o.clienteEmail}"/></small></td>
                                <td class="hide-mobile">${o.numeroArticoli}</td>
                                <td class="num"><fmt:formatNumber value="${o.totale}" pattern="#,##0.00"/> &euro;</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <p class="table-summary"><strong>${ordini.size()} ordini</strong> &middot; totale
                    <strong><fmt:formatNumber value="${totale}" pattern="#,##0.00"/> &euro;</strong> (IVA inclusa)</p>
            </div>
        </c:otherwise>
    </c:choose>
</c:if>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
