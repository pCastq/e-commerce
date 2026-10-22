<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>E-commerce</title>
</head>
<body>
    <h1>Funziona!</h1>
    <p>Tomcat sta servendo questa pagina JSP.</p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ciao?nome=Mario">Prova la servlet (MVC)</a></li>
        <li><a href="${pageContext.request.contextPath}/pagina-che-non-esiste">Prova la pagina 404</a></li>
    </ul>
</body>
</html>
