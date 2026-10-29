<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    VIEW admin: form di creazione/modifica prodotto (da AdminProdottoFormServlet).
    Attributi: f (Map campo -> valore da mostrare), errori (Map campo -> messaggio), aliquote (List<Integer>).
    Se f.id esiste si sta modificando.
--%>
<c:set var="modifica" value="${not empty f.id}"/>
<jsp:include page="/WEB-INF/fragments/header.jsp">
    <jsp:param name="pageTitle" value="Prodotto"/>
    <jsp:param name="active" value="admin"/>
</jsp:include>

<div class="form-page">
<h1>${modifica ? 'Modifica prodotto' : 'Nuovo prodotto'}</h1>

<form class="card" method="post" action="${pageContext.request.contextPath}/admin/prodotti/form" novalidate>
    <c:if test="${modifica}"><input type="hidden" name="id" value="<c:out value='${f.id}'/>"></c:if>

    <c:if test="${not empty errori.generale}">
        <div class="alert alert-error" role="alert"><c:out value="${errori.generale}"/></div>
    </c:if>

    <div class="form-group ${not empty errori.nome ? 'has-error' : ''}">
        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" maxlength="150" value="<c:out value='${f.nome}'/>">
        <p class="field-error"><c:out value="${errori.nome}"/></p>
    </div>

    <div class="form-group ${not empty errori.descrizione ? 'has-error' : ''}">
        <label for="descrizione">Descrizione</label>
        <textarea id="descrizione" name="descrizione" rows="4" maxlength="2000"><c:out value="${f.descrizione}"/></textarea>
        <p class="field-error"><c:out value="${errori.descrizione}"/></p>
    </div>

    <div class="form-group ${not empty errori.prezzo ? 'has-error' : ''}">
        <label for="prezzo">Prezzo di vendita, IVA inclusa (&euro;)</label>
        <input type="text" id="prezzo" name="prezzo" inputmode="decimal" maxlength="10"
               placeholder="Es. 2,49" value="<c:out value='${f.prezzo}'/>">
        <p class="field-error"><c:out value="${errori.prezzo}"/></p>
    </div>

    <div class="form-group ${not empty errori.iva ? 'has-error' : ''}">
        <label for="iva">Aliquota IVA contenuta nel prezzo</label>
        <select id="iva" name="iva">
            <c:forEach var="a" items="${aliquote}">
                <option value="${a}" ${f.iva == a ? 'selected' : ''}>${a}%</option>
            </c:forEach>
        </select>
        <p class="field-error"><c:out value="${errori.iva}"/></p>
    </div>

    <div class="form-group ${not empty errori.giacenza ? 'has-error' : ''}">
        <label for="giacenza">Giacenza (pezzi disponibili)</label>
        <input type="text" id="giacenza" name="giacenza" inputmode="numeric" maxlength="6" value="<c:out value='${f.giacenza}'/>">
        <p class="field-error"><c:out value="${errori.giacenza}"/></p>
    </div>

    <div class="form-group ${not empty errori.immagine ? 'has-error' : ''}">
        <label for="immagine">File dell'immagine (facoltativo)</label>
        <input type="text" id="immagine" name="immagine" maxlength="100" placeholder="Es. pasta.jpg" value="<c:out value='${f.immagine}'/>">
        <p class="muted"><small>Il file va copiato nella cartella <code>web/img/prodotti</code> del progetto.</small></p>
        <p class="field-error"><c:out value="${errori.immagine}"/></p>
    </div>

    <div class="cart-buttons">
        <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/prodotti">Annulla</a>
        <button type="submit" class="btn">${modifica ? 'Salva le modifiche' : 'Crea il prodotto'}</button>
    </div>
</form>
</div>

<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
