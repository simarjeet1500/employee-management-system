package com.ems.servlet;

import com.ems.dao.UserDAO;
import com.ems.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles password change for logged-in employees.
 * URLs:
 *   /change-password        (GET  - show form)
 *   /change-password        (POST - process change)
 */
@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String message = (String) req.getAttribute("message");
        String error = (String) req.getAttribute("error");

        req.setAttribute("message", message);
        req.setAttribute("error", error);
        req.getRequestDispatcher("/employee/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        String currentPassword = trim(req.getParameter("currentPassword"));
        String newPassword = trim(req.getParameter("newPassword"));
        String confirmPassword = trim(req.getParameter("confirmPassword"));

        String error = validate(currentPassword, newPassword, confirmPassword);

        try {
            if (error == null) {
                // Verify current password
                User authenticatedUser = userDAO.authenticate(currentUser.getUsername(), currentPassword);
                if (authenticatedUser == null) {
                    error = "Current password is incorrect.";
                }
            }

            if (error != null) {
                req.setAttribute("error", error);
                req.getRequestDispatcher("/employee/change-password.jsp").forward(req, resp);
                return;
            }

            // Update password
            boolean success = userDAO.updatePassword(currentUser.getId(), newPassword);

            if (success) {
                // Update session user object with new password
                currentUser.setPassword(newPassword);
                session.setAttribute("user", currentUser);

                req.setAttribute("message", "Password changed successfully!");
                req.getRequestDispatcher("/employee/change-password.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Failed to update password. Please try again.");
                req.getRequestDispatcher("/employee/change-password.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    private String validate(String currentPassword, String newPassword, String confirmPassword) {
        if (isBlank(currentPassword)) return "Current password is required.";
        if (isBlank(newPassword)) return "New password is required.";
        if (isBlank(confirmPassword)) return "Please confirm your new password.";

        if (newPassword.length() < 6) return "New password must be at least 6 characters.";
        if (!newPassword.equals(confirmPassword)) return "New password and confirm password do not match.";
        if (currentPassword.equals(newPassword)) return "New password must be different from current password.";

        return null;
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
}
