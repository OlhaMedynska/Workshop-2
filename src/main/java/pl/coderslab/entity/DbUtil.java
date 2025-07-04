package pl.coderslab.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users?useSSL=false&characterEncoding=utf8";
    private static final String DB_USER = "newroot";
    private static final String DB_PASS = "coderslab";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
