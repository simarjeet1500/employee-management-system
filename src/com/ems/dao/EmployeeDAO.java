package com.ems.dao;

import com.ems.model.Employee;
import com.ems.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeDAO {

    private static final List<String> ALLOWED_SORT_COLUMNS =
            Arrays.asList("id", "name", "department", "salary", "designation", "join_date");

    public int create(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, email, department, designation, salary, phone, address, join_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCommon(ps, e);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    e.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean update(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name=?, email=?, department=?, designation=?, salary=?, phone=?, address=?, join_date=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindCommon(ps, e);
            ps.setInt(9, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        // First, delete the user account associated with this employee (if exists)
        UserDAO userDAO = new UserDAO();
        userDAO.deleteByEmployeeId(id);
        
        // Then delete the employee record
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Employee findById(int id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Returns paginated list. sortBy must be one of ALLOWED_SORT_COLUMNS,
     * sortDir must be ASC or DESC.
     */
    public List<Employee> findPage(int page, int pageSize, String sortBy, String sortDir, String search) throws SQLException {
        if (!ALLOWED_SORT_COLUMNS.contains(sortBy)) sortBy = "id";
        if (!"ASC".equalsIgnoreCase(sortDir) && !"DESC".equalsIgnoreCase(sortDir)) sortDir = "ASC";

        StringBuilder sql = new StringBuilder("SELECT * FROM employees");
        boolean hasSearch = search != null && !search.trim().isEmpty();
        if (hasSearch) {
            sql.append(" WHERE name LIKE ? OR email LIKE ? OR department LIKE ?");
        }
        sql.append(" ORDER BY ").append(sortBy).append(' ').append(sortDir);
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasSearch) {
                String like = "%" + search.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, (page - 1) * pageSize);

            List<Employee> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        }
    }

    public int countAll(String search) throws SQLException {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        String sql = hasSearch
                ? "SELECT COUNT(*) FROM employees WHERE name LIKE ? OR email LIKE ? OR department LIKE ?"
                : "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + search.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean emailExists(String email, Integer ignoreId) throws SQLException {
        String sql = ignoreId == null
                ? "SELECT 1 FROM employees WHERE email = ?"
                : "SELECT 1 FROM employees WHERE email = ? AND id <> ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            if (ignoreId != null) ps.setInt(2, ignoreId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ---- helpers ----

    private void bindCommon(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getName());
        ps.setString(2, e.getEmail());
        ps.setString(3, e.getDepartment());
        ps.setString(4, e.getDesignation());
        ps.setBigDecimal(5, e.getSalary());
        ps.setString(6, e.getPhone());
        ps.setString(7, e.getAddress());
        if (e.getJoinDate() != null) ps.setDate(8, e.getJoinDate());
        else ps.setNull(8, java.sql.Types.DATE);
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setEmail(rs.getString("email"));
        e.setDepartment(rs.getString("department"));
        e.setDesignation(rs.getString("designation"));
        e.setSalary(rs.getBigDecimal("salary"));
        e.setPhone(rs.getString("phone"));
        e.setAddress(rs.getString("address"));
        Date d = rs.getDate("join_date");
        e.setJoinDate(d);
        return e;
    }
}
