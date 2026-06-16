package com.ems.servlet;

import com.ems.dao.EmployeeDAO;
import com.ems.model.Employee;
import com.ems.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/employee/profile")
public class EmployeeProfileServlet extends HttpServlet {

    private final EmployeeDAO dao = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        if (user.getEmployeeId() == null) {
            req.setAttribute("error", "No employee record is linked to this account.");
            req.getRequestDispatcher("/employee/profile.jsp").forward(req, resp);
            return;
        }
        try {
            Employee e = dao.findById(user.getEmployeeId());
            req.setAttribute("employee", e);
            req.getRequestDispatcher("/employee/profile.jsp").forward(req, resp);
        } catch (SQLException ex) {
            throw new ServletException("Failed to load profile.", ex);
        }
    }
}
