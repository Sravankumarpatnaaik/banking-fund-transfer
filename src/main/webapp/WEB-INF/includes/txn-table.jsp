<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
    <c:when test="${empty rows}">
        <p class="empty">No movements yet. A deposit, withdrawal, or transfer will be listed here.</p>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>When (IST)</th>
                    <th>Type</th>
                    <th>Details</th>
                    <th class="num">Amount</th>
                    <th class="num">Balance after</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="txn" items="${rows}">
                    <tr>
                        <td><c:out value="${txn.whenDisplay}"/></td>
                        <td><span class="pill ${txn.type.credit ? 'pill-in' : 'pill-out'}"><c:out value="${txn.type.label}"/></span></td>
                        <td>
                            <c:out value="${txn.detail}"/>
                            <span class="ref"><c:out value="${txn.reference}"/></span>
                        </td>
                        <td class="num"><c:out value="${txn.signedAmountDisplay}"/></td>
                        <td class="num"><c:out value="${txn.balanceAfterDisplay}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
