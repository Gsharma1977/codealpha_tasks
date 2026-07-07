package dao;

import database.DatabaseConnection;
import models.Admin;

import java.sql.*;

/** AdminDAO - Data Access Object for the 'admin' table. */
public class AdminDAO {

    /** Returns Admin object if credentials match, null otherwise. */
    public Admin login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin a = new Admin();
                a.setAdminId(rs.getInt("admin_id"));
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                return a;
            }
        }
        return null;
    }
}
