<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ems.model.Employee, com.ems.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    Employee e = (Employee) request.getAttribute("employee");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile - EMS</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<div class="navbar">
    <div class="brand">EMS &mdash; My Profile</div>
    <div>
        <span>Hello, <b><%= currentUser.getUsername() %></b></span>
        <a href="<%= ctx %>/change-password">Change Password</a>
        <a href="<%= ctx %>/logout">Logout</a>
    </div>
</div>

<div class="container">
    <div class="card" style="max-width:760px; margin:0 auto;">
        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } else if (e != null) { %>
            <h1 style="margin-bottom:20px;">Welcome, <%= e.getName() %>!</h1>

            <div class="profile-grid">
                <div class="k">Employee ID</div>          <div class="v">#<%= e.getId() %></div>
                <div class="k">Email</div>                <div class="v"><%= e.getEmail() %></div>
                <div class="k">Department</div>           <div class="v"><%= e.getDepartment() %></div>
                <div class="k">Designation</div>          <div class="v"><%= e.getDesignation() %></div>
                <div class="k">Salary</div>               <div class="v">&#8377;<%= e.getSalary() %></div>
                <div class="k">Phone</div>                <div class="v"><%= e.getPhone() == null ? "-" : e.getPhone() %></div>
                <div class="k">Address</div>              <div class="v"><%= e.getAddress() == null ? "-" : e.getAddress() %></div>
                <div class="k">Join Date</div>            <div class="v"><%= e.getJoinDate() == null ? "-" : e.getJoinDate() %></div>
            </div>

            <div class="alert alert-info" style="margin-top:24px;">
                For any updates to your profile information, please contact the administrator.
            </div>
        <% } %>
    </div>
</div>
</body>
</html>
