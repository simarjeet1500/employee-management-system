<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card">
        <h1 style="color:#e53e3e;">Something went wrong</h1>
        <p style="margin-top:10px;">An unexpected error occurred. Please try again or contact the administrator.</p>
        <% if (exception != null) { %>
            <pre style="margin-top:16px; padding:12px; background:#f7fafc; border-radius:8px; font-size:12px; overflow:auto;"><%= exception %></pre>
        <% } %>
        <p style="margin-top:20px;">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/login.jsp">Back to Login</a>
        </p>
    </div>
</div>
</body>
</html>
