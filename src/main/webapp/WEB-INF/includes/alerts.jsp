<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty error}">
    <p class="flash error" role="alert"><c:out value="${error}"/></p>
</c:if>
<c:if test="${not empty flash}">
    <p class="flash ${flash.kind == 'success' ? 'success' : 'error'}" role="status"><c:out value="${flash.text}"/></p>
</c:if>
