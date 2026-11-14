<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'Meridian Bank' : pageTitle}"/> · Meridian</title>
    <link rel="icon" href="${ctx}/favicon.svg" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,520;9..144,640&family=Outfit:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/bank.css">
</head>
<body>
<a class="skip" href="#content">Skip to content</a>
<header class="site-header">
    <a class="brand" href="${ctx}${empty sessionScope.accountNumber ? '/' : '/dashboard'}">
        <svg viewBox="0 0 32 32" aria-hidden="true">
            <rect width="32" height="32" rx="6" fill="#1e4d3b"/>
            <path d="M5 21h22" stroke="#f3e6cf" stroke-width="1.4"/>
            <path d="M7 17c2.4-4.2 4.6-4.2 7 0s4.6 4.2 7 0 4.6-4.2 7 0" fill="none" stroke="#f3e6cf" stroke-width="1.6"/>
            <circle cx="16" cy="11" r="2.1" fill="#e4c48a"/>
        </svg>
        <div class="brand-name">Meridian<span>Private ledger</span></div>
    </a>
    <nav class="nav" aria-label="Primary">
        <c:choose>
            <c:when test="${not empty sessionScope.accountNumber}">
                <a class="${section == 'dashboard' ? 'is-current' : ''}" href="${ctx}/dashboard">Balance</a>
                <a class="${section == 'deposit' ? 'is-current' : ''}" href="${ctx}/deposit">Deposit</a>
                <a class="${section == 'withdraw' ? 'is-current' : ''}" href="${ctx}/withdraw">Withdraw</a>
                <a class="${section == 'transfer' ? 'is-current' : ''}" href="${ctx}/transfer">Transfer</a>
                <a class="${section == 'history' ? 'is-current' : ''}" href="${ctx}/history">History</a>
                <span class="who"><c:out value="${sessionScope.holderName}"/></span>
                <a href="${ctx}/logout">Sign out</a>
            </c:when>
            <c:otherwise>
                <a class="${section == 'login' ? 'is-current' : ''}" href="${ctx}/login">Sign in</a>
                <a class="btn" href="${ctx}/register">Open an account</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main id="content" class="wrap">
