<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<%-- VIEW admin: elenco prodotti (da AdminProdottiServlet). Attributo: prodotti (List<Product>, anche eliminati). --%>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Gestione prodotti"/>
    <jsp:param name="active" value="admin"/>
</jsp:include>

<jsp:include page="/WEB-INF/fragments/admin-nav.jsp"><jsp:param name="section" value="prodotti"/></jsp:include>

<div class="admin-head">
    <h1>Gestione prodotti</h1>
    <a class="btn" href="${pageContext.request.contextPath}/admin/prodotti/form">+ Nuovo prodotto</a>
</div>

<div class="card">
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th></th><th>Prodotto</th><th class="num">Prezzo</th><th class="num">Giacenza</th><th>Stato</th><th>Azioni</th></tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${prodotti}">
                <tr class="${p.attivo ? '' : 'row-off'}">
                    <td><img class="thumb" src="${pageContext.request.contextPath}/img/prodotti/<c:out value='${p.immagineFile}'/>" alt=""></td>
                    <td><c:out value="${p.nome}"/></td>
                    <td class="num"><fmt:formatNumber value="${p.prezzo}" pattern="#,##0.00"/> &euro;<br>
                        <small class="muted">IVA <fmt:formatNumber value="${p.iva}" pattern="0.##"/>%</small></td>
                    <td class="num">${p.giacenza}</td>
                    <td><c:choose>
                        <c:when test="${p.attivo}"><span class="badge badge-ok">A catalogo</span></c:when>
                        <c:otherwise><span class="badge badge-off">Eliminato</span></c:otherwise>
                    </c:choose></td>
                    <td class="row-actions">
                        <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/prodotti/form?id=${p.id}">Modifica</a>
                        <c:choose>
                            <c:when test="${p.attivo}">
                                <%-- data-confirm: admin.js mostra una finestra di conferma prima di inviare il form --%>
                                <form method="post" action="${pageContext.request.contextPath}/admin/prodotti"
                                      data-confirm="Eliminare «<c:out value='${p.nome}'/>» dal catalogo? Resterà nello storico degli ordini e potrai ripristinarlo.">
                                    <input type="hidden" name="azione" value="elimina">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-danger">Elimina</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/admin/prodotti">
                                    <input type="hidden" name="azione" value="ripristina">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-light">Ripristina</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%-- Finestra di conferma (elemento <dialog> nativo del browser): la riempie e la apre admin.js --%>
<dialog id="dlg-conferma" class="dialog">
    <form method="dialog">
        <p id="dlg-testo"></p>
        <div class="dialog-buttons">
            <button type="submit" value="annulla" class="btn btn-light">Annulla</button>
            <button type="submit" value="ok" class="btn btn-danger">Elimina</button>
        </div>
    </form>
</dialog>

<script src="${pageContext.request.contextPath}/js/admin.js" defer></script>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
