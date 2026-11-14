<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Withdraw" scope="request"/>
<c:set var="section" value="withdraw" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="split">
    <section class="panel">
        <h2>Withdraw</h2>
        <p>Available balance <strong><c:out value="${account.balanceDisplay}"/></strong>. You can take up to that amount.</p>
        <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
        <form method="post" action="${ctx}/withdraw" data-once>
            <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}'/>">
            <label>Amount
                <input name="amount" inputmode="decimal" required maxlength="16" placeholder="500.00" value="<c:out value='${param.amount}'/>">
            </label>
            <label>Remarks <span class="help">Optional</span>
                <input name="remarks" maxlength="120" value="<c:out value='${param.remarks}'/>">
            </label>
            <button class="btn danger" type="submit">Debit this account</button>
        </form>
    </section>
    <aside class="aside">
        <h2>Checked, then committed</h2>
        <p>The account row is locked, the balance is compared with the request, and the debit is committed only together with its history row. A short balance leaves the account untouched.</p>
    </aside>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
