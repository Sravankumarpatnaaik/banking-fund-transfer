<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Open an account" scope="request"/>
<c:set var="section" value="register" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="split">
    <section class="panel">
        <h2>Open an account</h2>
        <p>The opening amount is the first deposit on the ledger. Minimum ₹500.00.</p>
        <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
        <form method="post" action="${ctx}/register" data-once>
            <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}'/>">
            <label>Account holder
                <input name="holderName" autocomplete="name" required maxlength="80" value="<c:out value='${param.holderName}'/>">
            </label>
            <label>Email
                <input name="email" type="email" autocomplete="email" required maxlength="120" value="<c:out value='${param.email}'/>">
            </label>
            <label>Phone <span class="help">Optional</span>
                <input name="phone" type="tel" autocomplete="tel" maxlength="20" value="<c:out value='${param.phone}'/>">
            </label>
            <label>Account type
                <select name="accountType">
                    <option value="SAVINGS" ${param.accountType == 'CURRENT' ? '' : 'selected'}>Savings</option>
                    <option value="CURRENT" ${param.accountType == 'CURRENT' ? 'selected' : ''}>Current</option>
                </select>
            </label>
            <label>Opening balance
                <input name="openingBalance" inputmode="decimal" required maxlength="16" placeholder="5000.00" value="<c:out value='${param.openingBalance}'/>">
            </label>
            <label>Password
                <input name="password" type="password" autocomplete="new-password" required minlength="8" maxlength="72">
            </label>
            <label>Confirm password
                <input name="confirmPassword" type="password" autocomplete="new-password" required minlength="8" maxlength="72">
            </label>
            <button class="btn" type="submit">Create account</button>
        </form>
    </section>
    <aside class="aside">
        <h2>What gets stored</h2>
        <p>Your name, email, optional phone, account type, and a bcrypt hash of the password. The balance lives in MySQL as a decimal, never as a floating-point number.</p>
    </aside>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
