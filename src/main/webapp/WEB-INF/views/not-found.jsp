<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Page not found" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<section class="panel">
    <h2>That page is not on the ledger</h2>
    <p>The address does not match an account screen.</p>
    <p><a class="btn" href="${ctx}/">Back to the start</a></p>
</section>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
