package com.ems.servlet;

import com.ems.dao.EmployeeDAO;
import com.ems.model.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin dashboard - lists employees with pagination + sorting + simple search.
 * URL: /admin/dashboard?page=1&size=5&sortBy=name&sortDir=ASC&q=foo
 */
@WebServlet("/admin/dashboard")
public class EmployeeListServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page     = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("size"), 5);
        String sortBy  = orDefault(req.getParameter("sortBy"), "id");
        String sortDir = orDefault(req.getParameter("sortDir"), "ASC");
        String search  = req.getParameter("q");

        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 5;

        try {
            int total = employeeDAO.countAll(search);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
            if (page > totalPages) page = totalPages;

            List<Employee> employees = employeeDAO.findPage(page, pageSize, sortBy, sortDir, search);

            req.setAttribute("employees", employees);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
            req.setAttribute("sortBy", sortBy);
            req.setAttribute("sortDir", sortDir);
            req.setAttribute("q", search == null ? "" : search);

            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load employees.", e);
        }
    }

    private int parseInt(String s, int def) {
        try { return s == null ? def : Integer.parseInt(s); }
        catch (NumberFormatException ex) { return def; }
    }

    private String orDefault(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }
}
