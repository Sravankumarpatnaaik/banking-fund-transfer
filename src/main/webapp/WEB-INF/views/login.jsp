<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign in" scope="request"/>
<c:set var="section" value="login" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>
<div class="split">
    <section class="panel">
        <h2>Sign in</h2>
        <p>Use an account number or the email on the account.</p>
        <jsp:include page="/WEB-INF/includes/alerts.jsp"/>
        <form method="post" action="${ctx}/login" data-once>
            <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}'/>">
            <label>Account number or email
                <input name="identifier" autocomplete="username" required maxlength="120" value="<c:out value='${identifier}'/>">
            </label>
            <label>Password
                <input name="password" type="password" autocomplete="current-password" required maxlength="72">
            </label>
            <button class="btn" type="submit">Sign in</button>
        </form>
    </section>
    <aside class="aside">
        <h2>Seeded sign-in</h2>
        <c:forEach var="demo" items="${demoAccounts}">
            <p><c:out value="${demo.name}"/><br><span class="mono"><c:out value="${demo.number}"/></span></p>
        </c:forEach>
        <p>Password <strong><c:out value="${demoPassword}"/></strong></p>
    </aside>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp"/>
