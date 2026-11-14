<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Transaction history" scope="request"/>
<c:set var="section" value="history" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<section class="panel">
    <h2>Transaction history</h2>
    <p><span class="mono"><c:out value="${account.accountNumber}"/></span> · balance <strong><c:out value="${account.balanceDisplay}"/></strong></p>
    <div class="stats">
        <article class="stat"><span>Deposits</span><strong><c:out value="${totals.deposits}"/></strong></article>
        <article class="stat"><span>Withdrawals</span><strong><c:out value="${totals.withdrawals}"/></strong></article>
        <article class="stat"><span>Transfer in</span><strong><c:out value="${totals.transferIn}"/></strong></article>
        <article class="stat"><span>Transfer out</span><strong><c:out value="${totals.transferOut}"/></strong></article>
    </div>
    <nav class="filters" aria-label="Filter history">
        <a class="${empty activeType ? 'is-current' : ''}" href="${ctx}/history">All</a>
        <a class="${activeType == 'DEPOSIT' ? 'is-current' : ''}" href="${ctx}/history?type=DEPOSIT">Deposits</a>
        <a class="${activeType == 'WITHDRAWAL' ? 'is-current' : ''}" href="${ctx}/history?type=WITHDRAWAL">Withdrawals</a>
        <a class="${activeType == 'TRANSFER_IN' ? 'is-current' : ''}" href="${ctx}/history?type=TRANSFER_IN">Transfer in</a>
        <a class="${activeType == 'TRANSFER_OUT' ? 'is-current' : ''}" href="${ctx}/history?type=TRANSFER_OUT">Transfer out</a>
    </nav>
    <p class="help">Showing the latest 50 movements. Totals above cover the whole account.</p>
    <jsp:include page="/WEB-INF/includes/txn-table.jsp"/>
</section>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
