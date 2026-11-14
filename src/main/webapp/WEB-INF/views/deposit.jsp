<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Deposit" scope="request"/>
<c:set var="section" value="deposit" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="split">
    <section class="panel">
        <h2>Deposit</h2>
        <p>Available balance <strong><c:out value="${account.balanceDisplay}"/></strong></p>
        <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
        <form method="post" action="${ctx}/deposit" data-once>
            <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}'/>">
            <label>Amount
                <input name="amount" inputmode="decimal" required maxlength="16" placeholder="1500.50" value="<c:out value='${param.amount}'/>">
            </label>
            <label>Remarks <span class="help">Optional</span>
                <input name="remarks" maxlength="120" value="<c:out value='${param.remarks}'/>">
            </label>
            <button class="btn" type="submit">Credit this account</button>
        </form>
    </section>
    <aside class="aside">
        <h2>One unit of work</h2>
        <p>The credit and the history row share a JDBC transaction. If the history insert fails, the credit is rolled back and the balance stays as it was.</p>
    </aside>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
