<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ems.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Change Password - EMS</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
    <style>
        .password-strength {
            margin-top: 8px;
            font-size: 12px;
            font-weight: 600;
            height: 20px;
        }
        .password-strength.weak {
            color: #DC2626;
        }
        .password-strength.medium {
            color: #F59E0B;
        }
        .password-strength.strong {
            color: #10B981;
        }
        .password-requirements {
            background: #EFF6FF;
            border: 1px solid #BFDBFE;
            border-radius: 10px;
            padding: 14px;
            margin-top: 16px;
            font-size: 13px;
            color: #1E40AF;
        }
        .password-requirements li {
            margin-bottom: 6px;
        }
        .password-requirements .check {
            color: #10B981;
            font-weight: 600;
        }
        .password-requirements .uncheck {
            color: #DC2626;
            font-weight: 600;
        }
    </style>
</head>
<body>

<div class="navbar">
    <div class="brand">EMS &mdash; Change Password</div>
    <div>
        <span>Hello, <b><%= currentUser.getUsername() %></b></span>
        <a href="<%= ctx %>/employee/profile">Back to Profile</a>
        <a href="<%= ctx %>/logout">Logout</a>
    </div>
</div>

<div class="container">
    <div class="card" style="max-width:500px; margin:0 auto;">
        <h1 style="margin-bottom:24px;">Change Your Password</h1>

        <% if (message != null) { %>
            <div class="alert alert-success" style="margin-bottom:20px;">
                <strong>Success!</strong> <%= message %>
            </div>
        <% } %>

        <% if (error != null) { %>
            <div class="alert alert-error" style="margin-bottom:20px;">
                <strong>Error:</strong> <%= error %>
            </div>
        <% } %>

        <form method="post" action="<%= ctx %>/change-password">
            <div class="form-group">
                <label>Current Password *</label>
                <input type="password" name="currentPassword" required autofocus>
            </div>

            <div class="form-group">
                <label>New Password *</label>
                <input type="password" id="newPassword" name="newPassword" required 
                       onkeyup="checkPasswordStrength(this.value)">
                <div class="password-strength" id="strength"></div>
            </div>

            <div class="form-group">
                <label>Confirm New Password *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required 
                       onkeyup="checkPasswordMatch()">
                <div id="matchStatus" style="margin-top:8px; font-size:12px; font-weight:600;"></div>
            </div>

            <div class="password-requirements">
                <strong>Password Requirements:</strong>
                <ul style="list-style:none; padding-left:0; margin-top:8px;">
                    <li><span id="lengthCheck" class="uncheck">✗</span> At least 6 characters</li>
                    <li><span id="matchCheck" class="uncheck">✗</span> Passwords match</li>
                </ul>
            </div>

            <div style="display:flex; gap:10px; margin-top:24px;">
                <button type="submit" class="btn btn-primary">Change Password</button>
                <a href="<%= ctx %>/employee/profile" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    function checkPasswordStrength(password) {
        const strengthDiv = document.getElementById('strength');
        const lengthCheck = document.getElementById('lengthCheck');
        
        if (password.length === 0) {
            strengthDiv.textContent = '';
            lengthCheck.className = 'uncheck';
            lengthCheck.textContent = '✗';
            return;
        }
        
        let strength = 'weak';
        let strengthText = 'Weak';
        
        // Check length
        if (password.length >= 6) {
            lengthCheck.className = 'check';
            lengthCheck.textContent = '✓';
            
            if (password.length >= 10) {
                strength = 'strong';
                strengthText = 'Strong';
            } else if (password.length >= 8) {
                strength = 'medium';
                strengthText = 'Medium';
            } else {
                strength = 'medium';
                strengthText = 'Medium';
            }
        } else {
            lengthCheck.className = 'uncheck';
            lengthCheck.textContent = '✗';
        }
        
        strengthDiv.className = 'password-strength ' + strength;
        strengthDiv.textContent = 'Password strength: ' + strengthText;
        
        checkPasswordMatch();
    }
    
    function checkPasswordMatch() {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const matchStatus = document.getElementById('matchStatus');
        const matchCheck = document.getElementById('matchCheck');
        
        if (confirmPassword.length === 0) {
            matchStatus.textContent = '';
            matchCheck.className = 'uncheck';
            matchCheck.textContent = '✗';
            return;
        }
        
        if (newPassword === confirmPassword) {
            matchStatus.className = 'text-success';
            matchStatus.style.color = '#10B981';
            matchStatus.textContent = '✓ Passwords match';
            matchCheck.className = 'check';
            matchCheck.textContent = '✓';
        } else {
            matchStatus.className = 'text-error';
            matchStatus.style.color = '#DC2626';
            matchStatus.textContent = '✗ Passwords do not match';
            matchCheck.className = 'uncheck';
            matchCheck.textContent = '✗';
        }
    }
</script>

</body>
</html>
