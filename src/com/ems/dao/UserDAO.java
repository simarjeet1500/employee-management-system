package com.ems.dao;

import com.ems.model.User;
import com.ems.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Authenticate by username/password. Returns the User (with role) or null
     * if credentials are invalid.
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, role, employee_id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    int empId = rs.getInt("employee_id");
                    u.setEmployeeId(rs.wasNull() ? null : empId);
                    return u;
                }
            }
        }
        return null;
    }

    /**
     * Create a new user account. Used when creating a new employee.
     * Returns true if successful, false otherwise.
     */
    public boolean create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, employee_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            if (user.getEmployeeId() != null) {
                ps.setInt(4, user.getEmployeeId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Check if a username already exists in the database.
     * Returns true if exists, false otherwise.
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Delete user by employee_id. Used when an employee is deleted.
     * Returns true if a user was deleted, false otherwise.
     */
    public boolean deleteByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM users WHERE employee_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Update password for a user by user ID.
     * Returns true if successful, false otherwise.
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
