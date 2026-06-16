<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Employee Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-wrapper">
    <div class="login-card">
        <h2>Employee Management</h2>
        <p class="subtitle">Sign in to continue</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" required autofocus>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Login</button>
        </form>

        <div style="margin-top:22px; padding:12px; background:#f7fafc; border-radius:8px; font-size:12px; color:#4a5568;">
            <b>Demo accounts:</b><br>
            Admin &nbsp;&rarr; <code>admin / admin123</code><br>
            Employee &rarr; <code>john / john123</code>
        </div>
    </div>
</div>
</body>
</html>
