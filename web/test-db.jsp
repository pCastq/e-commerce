<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Test database"/>
</jsp:include>

<h1>Test pool di connessioni</h1>
<p>Prodotti letti dal database: <strong>${prodotti.size()}</strong></p>

<div class="table-wrap">
    <table>
        <tr><th>Foto</th><th>Nome</th><th>Prezzo</th><th>IVA</th><th>Giacenza</th></tr>
        <c:forEach var="p" items="${prodotti}">
            <tr>
                <td><img src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${p.immagine}'/>"
                         alt="<c:out value='${p.nome}'/>" class="thumb"></td>
                <td><c:out value="${p.nome}"/></td>
                <td><fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/> &euro;</td>
                <td><fmt:formatNumber value="${p.iva}" pattern="0.##"/> %</td>
                <td>${p.giacenza}</td>
            </tr>
        </c:forEach>
    </table>
</div>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
