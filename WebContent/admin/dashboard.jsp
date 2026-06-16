<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.ems.model.Employee, com.ems.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    List<Employee> employees = (List<Employee>) request.getAttribute("employees");
    int page1       = (Integer) request.getAttribute("page");
    int pageSize   = (Integer) request.getAttribute("pageSize");
    int totalPages = (Integer) request.getAttribute("totalPages");
    int total      = (Integer) request.getAttribute("total");
    String sortBy  = (String)  request.getAttribute("sortBy");
    String sortDir = (String)  request.getAttribute("sortDir");
    String q       = (String)  request.getAttribute("q");
    String msg     = request.getParameter("msg");

    String ctx = request.getContextPath();
    String nextDir = "ASC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
    String arrow = "ASC".equalsIgnoreCase(sortDir) ? " &uarr;" : " &darr;";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - EMS</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<div class="navbar">
    <div class="brand">EMS &mdash; Admin Dashboard</div>
    <div>
        <span>Hello, <b><%= currentUser.getUsername() %></b></span>
        <a href="<%= ctx %>/logout">Logout</a>
    </div>
</div>

<div class="container">

    <% if (msg != null) {
         String text = null, cls = "alert-success";
         if ("created".equals(msg)) text = "Employee created successfully.";
         else if ("updated".equals(msg)) text = "Employee updated successfully.";
         else if ("deleted".equals(msg)) text = "Employee deleted.";
         else if ("deletefail".equals(msg)) { text = "Delete failed."; cls = "alert-error"; }
         if (text != null) { %>
            <div class="alert <%= cls %>"><%= text %></div>
    <%   }
       } %>

    <div class="page-header">
        <h1>Employees <span style="color:#718096;font-size:14px;">(<%= total %>)</span></h1>
        <a class="btn btn-primary" href="<%= ctx %>/admin/employee?action=new">+ Add Employee</a>
    </div>

    <div class="card">
        <div class="toolbar">
            <form method="get" action="<%= ctx %>/admin/dashboard" class="search-box">
                <input type="text" name="q" placeholder="Search by name, email, department" value="<%= q %>">
                <input type="hidden" name="sortBy"  value="<%= sortBy %>">
                <input type="hidden" name="sortDir" value="<%= sortDir %>">
                <input type="hidden" name="size"    value="<%= pageSize %>">
                <button type="submit" class="btn btn-secondary btn-sm">Search</button>
                <% if (q != null && !q.isBlank()) { %>
                    <a class="btn btn-sm" href="<%= ctx %>/admin/dashboard">Clear</a>
                <% } %>
            </form>

            <form method="get" action="<%= ctx %>/admin/dashboard">
                <label style="font-size:13px;color:#4a5568;">Rows per page:</label>
                <select name="size" onchange="this.form.submit()" style="padding:6px;border-radius:6px;">
                    <% for (int s : new int[]{5,10,20,50}) { %>
                        <option value="<%= s %>" <%= s == pageSize ? "selected" : "" %>><%= s %></option>
                    <% } %>
                </select>
                <input type="hidden" name="sortBy"  value="<%= sortBy %>">
                <input type="hidden" name="sortDir" value="<%= sortDir %>">
                <input type="hidden" name="q"       value="<%= q %>">
            </form>
        </div>

        <%
            // build sort link helper
            String baseQuery = "?size=" + pageSize + "&q=" + (q==null?"":q);
        %>
        <table class="data">
            <thead>
                <tr>
                    <th>#</th>
                    <th><a href="<%= ctx %>/admin/dashboard<%= baseQuery %>&sortBy=name&sortDir=<%= "name".equals(sortBy) ? nextDir : "ASC" %>">Name<%= "name".equals(sortBy) ? arrow : "" %></a></th>
                    <th>Email</th>
                    <th><a href="<%= ctx %>/admin/dashboard<%= baseQuery %>&sortBy=department&sortDir=<%= "department".equals(sortBy) ? nextDir : "ASC" %>">Department<%= "department".equals(sortBy) ? arrow : "" %></a></th>
                    <th>Designation</th>
                    <th><a href="<%= ctx %>/admin/dashboard<%= baseQuery %>&sortBy=salary&sortDir=<%= "salary".equals(sortBy) ? nextDir : "ASC" %>">Salary<%= "salary".equals(sortBy) ? arrow : "" %></a></th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <% if (employees == null || employees.isEmpty()) { %>
                <tr><td colspan="7" style="text-align:center;color:#718096;">No employees found.</td></tr>
            <% } else {
                int rowNum = (page1 - 1) * pageSize;
                for (Employee e : employees) { rowNum++; %>
                <tr>
                    <td><%= rowNum %></td>
                    <td><%= e.getName() %></td>
                    <td><%= e.getEmail() %></td>
                    <td><%= e.getDepartment() %></td>
                    <td><%= e.getDesignation() %></td>
                    <td>&#8377;<%= e.getSalary() %></td>
                    <td>
                        <a class="btn btn-sm btn-secondary" href="<%= ctx %>/admin/employee?action=view&id=<%= e.getId() %>">View</a>
                        <a class="btn btn-sm btn-primary"   href="<%= ctx %>/admin/employee?action=edit&id=<%= e.getId() %>">Edit</a>
                        <a class="btn btn-sm btn-danger"    href="<%= ctx %>/admin/employee?action=delete&id=<%= e.getId() %>" onclick="return confirm('Delete this employee?')">Delete</a>
                    </td>
                </tr>
            <% } } %>
            </tbody>
        </table>

        <% if (totalPages > 1) {
             String pageBase = "?size=" + pageSize + "&sortBy=" + sortBy + "&sortDir=" + sortDir + "&q=" + (q==null?"":q);
        %>
        <div class="pagination">
            <% if (page1 > 1) { %>
                <a href="<%= ctx %>/admin/dashboard<%= pageBase %>&page=<%= page1 - 1 %>">&laquo; Prev</a>
            <% } else { %>
                <span class="disabled">&laquo; Prev</span>
            <% } %>

            <% for (int i = 1; i <= totalPages; i++) {
                  if (i == page1) { %>
                    <span class="active"><%= i %></span>
            <%    } else { %>
                    <a href="<%= ctx %>/admin/dashboard<%= pageBase %>&page=<%= i %>"><%= i %></a>
            <%    }
               } %>

            <% if (page1 < totalPages) { %>
                <a href="<%= ctx %>/admin/dashboard<%= pageBase %>&page=<%= page1 + 1 %>">Next &raquo;</a>
            <% } else { %>
                <span class="disabled">Next &raquo;</span>
            <% } %>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
