<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    FRAGMENT: testata comune a tutte le pagine.
    Apre <html>, <head>, <body>, <header> e <main>; il footer li richiude.
    Parametri (passati con <jsp:param>):
      pageTitle -> titolo della scheda del browser
      active    -> voce di menu da evidenziare (home, catalogo, carrello, login, registrazione)
--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.pageTitle}" default="Benvenuto"/> - ShopTSW</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=3">   <%-- ?v=N: cambia N quando modifichi il CSS, cosi il browser scarica la versione nuova --%>
    <script src="${pageContext.request.contextPath}/js/layout.js" defer></script>
</head>
<body>
<header class="site-header">
    <div class="container header-inner">
        <a class="brand" href="${pageContext.request.contextPath}/">Shop<span>TSW</span></a>
        <button type="button" id="nav-toggle" class="nav-toggle"
                aria-controls="site-nav" aria-expanded="false">&#9776; Menu</button>
        <jsp:include page="/WEB-INF/fragments/menu.jsp"/>
    </div>
</header>
<main class="container">
