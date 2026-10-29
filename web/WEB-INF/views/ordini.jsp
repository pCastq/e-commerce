<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%-- VIEW dello storico (da OrdiniServlet). Attributo: ordini (List<Ordine>). --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="I miei ordini"/>
    <jsp:param name="active" value="ordini"/>
</jsp:include>

<h1>I miei ordini</h1>

<c:choose>
    <c:when test="${empty ordini}">
        <div class="card">
            <p>Non hai ancora effettuato ordini.</p>
            <a class="btn" href="${pageContext.request.contextPath}/">Vai al catalogo</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr><th>N.</th><th>Data</th><th class="hide-mobile">Articoli</th><th>Totale</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="o" items="${ordini}">
                        <tr>
                            <td><a href="${pageContext.request.contextPath}/ordini/dettaglio?id=${o.id}" title="Vedi il dettaglio dell'ordine"><strong>${o.id}</strong></a></td>
                            <td><fmt:formatDate value="${o.dataOrdine}" pattern="dd/MM/yyyy"/></td>
                            <td class="hide-mobile">${o.numeroArticoli}</td>
                            <td><fmt:formatNumber value="${o.totale}" pattern="#,##0.00"/> &euro;</td>
                            <td class="row-links">
                                <a href="${pageContext.request.contextPath}/fattura?id=${o.id}">Fattura</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
