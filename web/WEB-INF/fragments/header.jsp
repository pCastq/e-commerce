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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=10">   <%-- ?v=N: cambia N quando modifichi il CSS, cosi il browser scarica la versione nuova --%>
    <script src="${pageContext.request.contextPath}/js/layout.js" defer></script>
</head>
<body data-context-path="${pageContext.request.contextPath}">
<header class="site-header">
    <div class="container header-inner">
        <a class="brand" href="${pageContext.request.contextPath}/">Shop<span>TSW</span></a>
        <button type="button" id="nav-toggle" class="nav-toggle"
                aria-controls="site-nav" aria-expanded="false">&#9776; Menu</button>
        <jsp:include page="/WEB-INF/fragments/menu.jsp"/>
    </div>
</header>
<main class="container">
<%-- Messaggi "una tantum" salvati in sessione dalle servlet prima di un redirect: si mostrano e si cancellano. --%>
<c:if test="${not empty sessionScope.flashOk}">
    <div class="alert alert-success" role="status"><c:out value="${sessionScope.flashOk}"/></div>
    <c:remove var="flashOk" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashErr}">
    <div class="alert alert-error" role="alert"><c:out value="${sessionScope.flashErr}"/></div>
    <c:remove var="flashErr" scope="session"/>
</c:if>
