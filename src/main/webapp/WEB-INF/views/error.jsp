<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<section class="panel">
    <h2>That did not go through</h2>
    <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
    <c:if test="${empty error}">
        <p>The ledger could not complete this request. Your last committed balance is unchanged.</p>
    </c:if>
    <p><a class="btn" href="${ctx}/">Back to the start</a></p>
</section>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
