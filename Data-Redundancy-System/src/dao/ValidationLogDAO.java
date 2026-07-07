package dao;

import database.DatabaseConnection;
import models.ValidationLog;

import java.sql.*;
import java.util.*;

/** ValidationLogDAO - Data Access Object for validation_log table. */
public class ValidationLogDAO {

    public boolean insert(ValidationLog vl) throws SQLException {
        String sql = "INSERT INTO validation_log (record_id, field_name, validation_result, message) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, vl.getRecordId());
            ps.setString(2, vl.getFieldName());
            ps.setString(3, vl.getValidationResult());
            ps.setString(4, vl.getMessage());
            return ps.executeUpdate() > 0;
        }
    }

    public List<ValidationLog> getAll() throws SQLException {
        List<ValidationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM validation_log ORDER BY checked_at DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ValidationLog vl = new ValidationLog();
                vl.setLogId(rs.getInt("log_id"));
                vl.setRecordId(rs.getString("record_id"));
                vl.setFieldName(rs.getString("field_name"));
                vl.setValidationResult(rs.getString("validation_result"));
                vl.setMessage(rs.getString("message"));
                vl.setCheckedAt(rs.getTimestamp("checked_at"));
                list.add(vl);
            }
        }
        return list;
    }
}
