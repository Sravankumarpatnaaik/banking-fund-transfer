<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Ledger" scope="request"/>
<c:set var="section" value="home" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<section class="hero">
    <div>
        <p class="kicker">Online banking</p>
        <h1>A ledger that finishes what it starts.</h1>
        <p class="lede">Open an account, check the balance, deposit, withdraw, and transfer. A withdrawal or transfer is one MySQL transaction: both the balance and the history commit together, or both roll back.</p>
        <div class="actions">
            <a class="btn" href="${ctx}/register">Open an account</a>
            <a class="btn ghost" href="${ctx}/login">Sign in</a>
        </div>
    </div>
    <aside class="passbook" aria-label="Illustration of a balance">
        <p class="label">Illustration</p>
        <strong>₹ 85,000.00</strong>
        <div class="pass-row"><span>Transfer out</span><span>− ₹ 5,000.00</span></div>
        <div class="pass-row"><span>Committed</span><span>one unit</span></div>
    </aside>
</section>

<section>
    <h2>What you can do</h2>
    <div class="grid-3">
        <article class="card">
            <h3>Open and sign in</h3>
            <p>Create a savings or current account with an opening balance, then sign in with the account number or email.</p>
        </article>
        <article class="card">
            <h3>Move money</h3>
            <p>Deposit, withdraw, and transfer. The debit is refused when the balance is short, and nothing is left half-written.</p>
        </article>
        <article class="card">
            <h3>Read the history</h3>
            <p>Every account has its own list of deposits, withdrawals, and transfers, with the balance after each one.</p>
        </article>
    </div>
</section>

<section class="panel">
    <h2>Seeded accounts</h2>
    <p>These two accounts are loaded the first time the ledger starts. The password for both is <strong><c:out value="${demoPassword}"/></strong>.</p>
    <div class="demos">
        <c:forEach var="demo" items="${demoAccounts}">
            <article class="card">
                <h3><c:out value="${demo.name}"/></h3>
                <p class="mono"><c:out value="${demo.number}"/></p>
                <p><c:out value="${demo.typeLabel}"/> · <c:out value="${demo.email}"/></p>
                <p><c:out value="${demo.summary}"/></p>
            </article>
        </c:forEach>
    </div>
</section>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
