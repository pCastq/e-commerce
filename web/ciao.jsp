<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ciao</title>
</head>
<body>
    <%-- ${nome} è l'attributo messo dalla servlet con req.setAttribute("nome", ...) --%>
    <h1>Ciao ${nome} ${cognome}</h1>
    <a href="${pageContext.request.contextPath}/">Torna alla home</a>
</body>
</html>
