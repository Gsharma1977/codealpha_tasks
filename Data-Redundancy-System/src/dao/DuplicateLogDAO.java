package dao;

import database.DatabaseConnection;
import models.DuplicateLog;

import java.sql.*;
import java.util.*;

/** DuplicateLogDAO - Data Access Object for duplicate_log table. */
public class DuplicateLogDAO {

    public boolean insert(DuplicateLog dl) throws SQLException {
        String sql = "INSERT INTO duplicate_log (record_id, matched_with, reason, similarity_score) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dl.getRecordId());
            ps.setString(2, dl.getMatchedWith());
            ps.setString(3, dl.getReason());
            ps.setDouble(4, dl.getSimilarityScore());
            return ps.executeUpdate() > 0;
        }
    }

    public List<DuplicateLog> getAll() throws SQLException {
        List<DuplicateLog> list = new ArrayList<>();
        String sql = "SELECT * FROM duplicate_log ORDER BY detected_at DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DuplicateLog dl = new DuplicateLog();
                dl.setDuplicateId(rs.getInt("duplicate_id"));
                dl.setRecordId(rs.getString("record_id"));
                dl.setMatchedWith(rs.getString("matched_with"));
                dl.setReason(rs.getString("reason"));
                dl.setSimilarityScore(rs.getDouble("similarity_score"));
                dl.setDetectedAt(rs.getTimestamp("detected_at"));
                list.add(dl);
            }
        }
        return list;
    }
}
