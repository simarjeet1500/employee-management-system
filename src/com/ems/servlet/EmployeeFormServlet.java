package com.ems.servlet;

import com.ems.dao.EmployeeDAO;
import com.ems.dao.UserDAO;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Handles create + update + view + delete via the action parameter.
 * URLs:
 *   /admin/employee?action=new            (GET  - show add form)
 *   /admin/employee?action=edit&id=#      (GET  - show edit form)
 *   /admin/employee?action=view&id=#      (GET  - read-only view)
 *   /admin/employee?action=delete&id=#    (GET  - delete)
 *   /admin/employee?action=save           (POST - create or update)
 */
@WebServlet("/admin/employee")
public class EmployeeFormServlet extends HttpServlet {

    private final EmployeeDAO dao = new EmployeeDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "new";

        try {
            switch (action) {
                case "new":
                    req.setAttribute("formTitle", "Add Employee");
                    req.getRequestDispatcher("/admin/employee-form.jsp").forward(req, resp);
                    break;

                case "edit": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Employee e = dao.findById(id);
                    if (e == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=notfound");
                        return;
                    }
                    req.setAttribute("employee", e);
                    req.setAttribute("formTitle", "Edit Employee");
                    req.getRequestDispatcher("/admin/employee-form.jsp").forward(req, resp);
                    break;
                }

                case "view": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Employee e = dao.findById(id);
                    if (e == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=notfound");
                        return;
                    }
                    req.setAttribute("employee", e);
                    req.getRequestDispatcher("/admin/employee-view.jsp").forward(req, resp);
                    break;
                }

                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    boolean ok = dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard?msg=" + (ok ? "deleted" : "deletefail"));
                    break;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }
        } catch (NumberFormatException nfe) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=badid");
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        Integer id = (idParam != null && !idParam.isBlank()) ? Integer.parseInt(idParam) : null;

        Employee e = new Employee();
        if (id != null) e.setId(id);
        e.setName(trim(req.getParameter("name")));
        e.setEmail(trim(req.getParameter("email")));
        e.setDepartment(trim(req.getParameter("department")));
        e.setDesignation(trim(req.getParameter("designation")));
        e.setPhone(trim(req.getParameter("phone")));
        e.setAddress(trim(req.getParameter("address")));

        // Get credentials only for new employees
        String username = trim(req.getParameter("username"));
        String password = trim(req.getParameter("password"));

        String error = validate(e, req.getParameter("salary"), req.getParameter("joinDate"), id, username, password);
        try {
            if (error == null) {
                e.setSalary(new BigDecimal(req.getParameter("salary").trim()));
                String jd = req.getParameter("joinDate");
                if (jd != null && !jd.isBlank()) e.setJoinDate(Date.valueOf(jd));

                if (dao.emailExists(e.getEmail(), id)) {
                    error = "Email already exists for another employee.";
                }
            }

            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("employee", e);
                req.setAttribute("formTitle", id == null ? "Add Employee" : "Edit Employee");
                req.getRequestDispatcher("/admin/employee-form.jsp").forward(req, resp);
                return;
            }

            String emailSubject;
            String emailBody;
            if (id == null) {
                // Create new employee
                int empId = dao.create(e);
                
                // Create user account for the new employee
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setRole("EMPLOYEE");
                newUser.setEmployeeId(empId);
                userDAO.create(newUser);
                
                emailSubject = "Welcome to the Company - Your record has been created";
                emailBody = buildEmailWithCredentials(e, 
                    "Your employee record has just been created in our Employee Management System. "
                    + "You can now login with your credentials.",
                    username, password);
            } else {
                dao.update(e);
                emailSubject = "Your employee record has been updated";
                emailBody = buildEmail(e, "Your employee record was updated by the administrator.");
            }

            // fire & forget email
            try { EmailUtil.sendMail(e.getEmail(), emailSubject, emailBody); }
            catch (Exception ignored) { /* never block CRUD on email failure */ }

            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?msg=" + (id == null ? "created" : "updated"));
        } catch (SQLException sqlex) {
            throw new ServletException("Save failed: " + sqlex.getMessage(), sqlex);
        } catch (IllegalArgumentException badNum) {
            req.setAttribute("error", "Invalid number or date format.");
            req.setAttribute("employee", e);
            req.setAttribute("formTitle", id == null ? "Add Employee" : "Edit Employee");
            req.getRequestDispatcher("/admin/employee-form.jsp").forward(req, resp);
        }
    }

    private String validate(Employee e, String salary, String joinDate, Integer id, String username, String password) {
        // Basic validation
        String basicError = validate(e, salary, joinDate);
        if (basicError != null) return basicError;

        // Validate credentials only for new employees
        if (id == null) {
            if (isBlank(username)) return "Username is required for new employees.";
            if (isBlank(password)) return "Password is required for new employees.";
            if (username.length() < 4) return "Username must be at least 4 characters.";
            if (password.length() < 6) return "Password must be at least 6 characters.";
            if (!username.matches("^[a-zA-Z0-9_]+$")) return "Username can only contain letters, numbers, and underscores.";
            
            try {
                if (userDAO.usernameExists(username)) {
                    return "Username already exists. Please choose a different one.";
                }
            } catch (SQLException sqlex) {
                return "Error checking username availability. Please try again.";
            }
        }
        
        return null;
    }

    private String validate(Employee e, String salary, String joinDate) {
        if (isBlank(e.getName()))        return "Name is required.";
        if (isBlank(e.getEmail()))       return "Email is required.";
        if (!e.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "Email format is invalid.";
        if (isBlank(e.getDepartment()))  return "Department is required.";
        if (isBlank(e.getDesignation())) return "Designation is required.";
        if (isBlank(salary))             return "Salary is required.";
        try {
            BigDecimal s = new BigDecimal(salary.trim());
            if (s.signum() < 0) return "Salary cannot be negative.";
        } catch (NumberFormatException ex) {
            return "Salary must be a valid number.";
        }
        if (joinDate != null && !joinDate.isBlank()) {
            try { Date.valueOf(joinDate); }
            catch (IllegalArgumentException ex) { return "Join date must be in yyyy-MM-dd format."; }
        }
        return null;
    }

    private String buildEmailWithCredentials(Employee e, String intro, String username, String password) {
        return "<div style=\"font-family:Arial,sans-serif\">"
             + "<h2 style=\"color:#2d3748\">Hello " + esc(e.getName()) + ",</h2>"
             + "<p>" + intro + "</p>"
             + "<table cellpadding=\"6\" style=\"border-collapse:collapse;border:1px solid #ddd\">"
             + row("Name", e.getName())
             + row("Department", e.getDepartment())
             + row("Designation", e.getDesignation())
             + row("Salary", e.getSalary() != null ? e.getSalary().toPlainString() : "")
             + row("Phone", e.getPhone())
             + "</table>"
             + "<h3 style=\"color:#2d3748;margin-top:20px\">Your Login Credentials:</h3>"
             + "<table cellpadding=\"6\" style=\"border-collapse:collapse;border:1px solid #ddd\">"
             + row("Username", username)
             + row("Password", password)
             + "</table>"
             + "<p style=\"color:#DC2626;font-size:11px;margin-top:16px\"><strong>Important:</strong> Keep your password secure and do not share it with anyone.</p>"
             + "<p style=\"color:#718096;font-size:12px;margin-top:24px\">— Employee Management System</p>"
             + "</div>";
    }

    private String buildEmail(Employee e, String intro) {
        return "<div style=\"font-family:Arial,sans-serif\">"
             + "<h2 style=\"color:#2d3748\">Hello " + esc(e.getName()) + ",</h2>"
             + "<p>" + intro + "</p>"
             + "<table cellpadding=\"6\" style=\"border-collapse:collapse;border:1px solid #ddd\">"
             + row("Name", e.getName())
             + row("Department", e.getDepartment())
             + row("Designation", e.getDesignation())
             + row("Salary", e.getSalary() != null ? e.getSalary().toPlainString() : "")
             + row("Phone", e.getPhone())
             + "</table>"
             + "<p style=\"color:#718096;font-size:12px;margin-top:24px\">— Employee Management System</p>"
             + "</div>";
    }
    private String row(String k, String v) {
        return "<tr><td style=\"border:1px solid #ddd;background:#f7fafc\"><b>" + esc(k) + "</b></td>"
             + "<td style=\"border:1px solid #ddd\">" + esc(v == null ? "" : v) + "</td></tr>";
    }
    private String esc(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
}
