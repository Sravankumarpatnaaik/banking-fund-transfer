<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Transfer" scope="request"/>
<c:set var="section" value="transfer" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="split">
    <section class="panel">
        <h2>Transfer</h2>
        <p>From <span class="mono"><c:out value="${account.accountNumber}"/></span>, balance <strong><c:out value="${account.balanceDisplay}"/></strong>.</p>
        <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
        <form method="post" action="${ctx}/transfer" data-once>
            <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}'/>">
            <label>Recipient account number
                <input name="toAccount" required maxlength="16" placeholder="MB1000000002" value="<c:out value='${param.toAccount}'/>">
            </label>
            <label>Amount
                <input name="amount" inputmode="decimal" required maxlength="16" placeholder="1000.00" value="<c:out value='${param.amount}'/>">
            </label>
            <label>Remarks <span class="help">Optional</span>
                <input name="remarks" maxlength="120" value="<c:out value='${param.remarks}'/>">
            </label>
            <button class="btn" type="submit">Send money</button>
        </form>
    </section>
    <aside class="aside">
        <h2>Both sides, or neither</h2>
        <p>Both accounts are locked in account-number order. The debit, the credit, and both history rows then commit as one transaction. If any write fails, MySQL rolls the whole transfer back.</p>
        <p>Try the other seeded account if you are signed in as Ananya or Rohan.</p>
    </aside>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
