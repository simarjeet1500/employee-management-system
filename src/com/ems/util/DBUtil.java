package com.ems.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC connections to the MySQL "clg" database (XAMPP defaults).
 * Update USER/PASSWORD here if your XAMPP MySQL uses non-default credentials.
 */
public class DBUtil {

    private static final String URL      = "jdbc:mysql://localhost:3306/clg?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    static {
        try {
            // MySQL Connector/J 8+
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. Place mysql-connector-j-*.jar in WebContent/WEB-INF/lib", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
