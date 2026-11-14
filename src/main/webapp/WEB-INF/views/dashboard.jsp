<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Balance enquiry" scope="request"/>
<c:set var="section" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<jsp:include page="/WEB-INF/includes/alerts.jsp"/>
<section class="panel balance-block">
    <p class="eyebrow">Balance enquiry</p>
    <p class="meta"><c:out value="${greeting}"/>, <c:out value="${account.holderName}"/>.</p>
    <p class="figure"><c:out value="${account.balanceDisplay}"/></p>
    <p class="meta">
        <c:out value="${account.accountType.label}"/>
        · <span class="mono"><c:out value="${account.accountNumber}"/></span>
        · <c:out value="${account.status}"/>
        · opened <c:out value="${account.openedDisplay}"/>
    </p>
    <div class="button-row">
        <a class="btn" href="${ctx}/deposit">Deposit</a>
        <a class="btn ghost" href="${ctx}/withdraw">Withdraw</a>
        <a class="btn ghost" href="${ctx}/transfer">Transfer</a>
        <a class="btn ghost" href="${ctx}/history">Full history</a>
    </div>
</section>
<section>
    <h2>Recent movements</h2>
    <jsp:include page="/WEB-INF/includes/txn-table.jsp"/>
</section>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
