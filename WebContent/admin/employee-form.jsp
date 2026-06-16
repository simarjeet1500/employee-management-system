<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ems.model.Employee, com.ems.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    Employee e = (Employee) request.getAttribute("employee");
    String formTitle = (String) request.getAttribute("formTitle");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
    boolean isNew = e == null || e.getId() == 0;

    String name = e != null && e.getName() != null ? e.getName() : "";
    String email = e != null && e.getEmail() != null ? e.getEmail() : "";
    String dept  = e != null && e.getDepartment() != null ? e.getDepartment() : "";
    String desig = e != null && e.getDesignation() != null ? e.getDesignation() : "";
    String salary = e != null && e.getSalary() != null ? e.getSalary().toPlainString() : "";
    String phone = e != null && e.getPhone() != null ? e.getPhone() : "";
    String addr  = e != null && e.getAddress() != null ? e.getAddress() : "";
    String joinDate = e != null && e.getJoinDate() != null ? e.getJoinDate().toString() : "";
    String id = e != null && e.getId() != 0 ? String.valueOf(e.getId()) : "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= formTitle %> - EMS</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<div class="navbar">
    <div class="brand">EMS &mdash; <%= formTitle %></div>
    <div>
        <a href="<%= ctx %>/admin/dashboard">Dashboard</a>
        <a href="<%= ctx %>/logout">Logout</a>
    </div>
</div>

<div class="container">
    <div class="card" style="max-width:760px; margin:0 auto;">
        <h1 style="margin-bottom:20px;"><%= formTitle %></h1>

        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>

        <% if (isNew) { %>
            <div class="alert alert-info" style="margin-bottom:24px;">
                <strong>Login Credentials:</strong> Create a username and password below so the employee can login and view their profile.
            </div>
        <% } %>

        <form method="post" action="<%= ctx %>/admin/employee?action=save">
            <input type="hidden" name="id" value="<%= id %>">

            <div class="form-row">
                <div class="form-group">
                    <label>Name *</label>
                    <input type="text" name="name" value="<%= name %>" required>
                </div>
                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" name="email" value="<%= email %>" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Department *</label>
                    <select name="department" required>
                        <%
                            String[] depts = {"Engineering","HR","Finance","Marketing","Operations","Sales","Support"};
                            for (String d : depts) {
                        %>
                            <option value="<%= d %>" <%= d.equals(dept) ? "selected" : "" %>><%= d %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Designation *</label>
                    <input type="text" name="designation" value="<%= desig %>" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Salary (INR) *</label>
                    <input type="number" step="0.01" min="0" name="salary" value="<%= salary %>" required>
                </div>
                <div class="form-group">
                    <label>Phone</label>
                    <input type="text" name="phone" value="<%= phone %>">
                </div>
            </div>

            <div class="form-group">
                <label>Join Date</label>
                <input type="date" name="joinDate" value="<%= joinDate %>">
            </div>

            <div class="form-group">
                <label>Address</label>
                <textarea name="address" rows="3"><%= addr %></textarea>
            </div>

            <% if (isNew) { %>
                <div style="margin-top:24px; padding-top:24px; border-top:2px solid #E5E7EB;">
                    <h3 style="margin-bottom:16px; font-size:16px;">Login Credentials</h3>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Username *</label>
                            <input type="text" name="username" placeholder="Enter username for login" required>
                        </div>
                        <div class="form-group">
                            <label>Password *</label>
                            <input type="password" name="password" placeholder="Enter password for login" required>
                        </div>
                    </div>
                </div>
            <% } %>

            <div style="display:flex; gap:10px; margin-top:24px;">
                <button type="submit" class="btn btn-primary"><%= id.isEmpty() ? "Create Employee" : "Update Employee" %></button>
                <a href="<%= ctx %>/admin/dashboard" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
