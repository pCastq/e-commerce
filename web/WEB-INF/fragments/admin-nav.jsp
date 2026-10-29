<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- FRAGMENT: barra di navigazione dell'area admin. Parametro: section = prodotti | ordini --%>
<nav class="admin-tabs" aria-label="Area amministratore">
    <a href="${pageContext.request.contextPath}/admin/prodotti" class="${param.section == 'prodotti' ? 'active' : ''}">Prodotti</a>
    <a href="${pageContext.request.contextPath}/admin/ordini" class="${param.section == 'ordini' ? 'active' : ''}">Ordini</a>
</nav>
