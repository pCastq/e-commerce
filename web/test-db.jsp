<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Product" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Test database</title>
    <style>
        body { font-family: sans-serif; margin: 1.5rem; }
        table { border-collapse: collapse; }
        th, td { border: 1px solid #ccc; padding: .5rem .75rem; text-align: left; vertical-align: middle; }
        img { height: 60px; }
    </style>
</head>
<body>
    <h1>Test pool di connessioni</h1>
    <%-- Pagina TEMPORANEA: il ciclo è uno scriptlet perché JSTL non è ancora configurato. --%>
    <%
        List<Product> prodotti = (List<Product>) request.getAttribute("prodotti");
    %>
    <p>Prodotti letti dal database: <strong><%= prodotti.size() %></strong></p>
    <table>
        <tr><th>Foto</th><th>Nome</th><th>Prezzo</th><th>IVA</th><th>Giacenza</th></tr>
        <% for (Product p : prodotti) { %>
        <tr>
            <td><img src="${pageContext.request.contextPath}/img/prodotti/<%= p.getImmagine() %>" alt="<%= p.getNome() %>"></td>
            <td><%= p.getNome() %></td>
            <td><%= p.getPrezzo() %> €</td>
            <td><%= p.getIva() %> %</td>
            <td><%= p.getGiacenza() %></td>
        </tr>
        <% } %>
    </table>
    <p><a href="${pageContext.request.contextPath}/">Torna alla home</a></p>
</body>
</html>
