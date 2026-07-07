package dao;

import database.DatabaseConnection;
import models.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RecordDAO - Data Access Object for the 'records' table.
 * All SQL queries for records live here (no SQL in service/UI layers).
 */
public class RecordDAO {

    // ── CREATE ─────────────────────────────────────────────────────
    public boolean insertRecord(Record r) throws SQLException {
        String sql = "INSERT INTO records (record_id, name, email, phone, department, address, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, r.getRecordId());
            ps.setString(2, r.getName());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getPhone());
            ps.setString(5, r.getDepartment());
            ps.setString(6, r.getAddress());
            ps.setString(7, r.getStatus() != null ? r.getStatus() : "UNIQUE");
            return ps.executeUpdate() > 0;
        }
    }

    // ── READ: all records ──────────────────────────────────────────
    public List<Record> getAllRecords() throws SQLException {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records ORDER BY date_created DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── READ: records by status ────────────────────────────────────
    public List<Record> getRecordsByStatus(String status) throws SQLException {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE status = ? ORDER BY date_created DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── READ: single record ────────────────────────────────────────
    public Record getRecordById(String id) throws SQLException {
        String sql = "SELECT * FROM records WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ── EXISTS checks (used by duplicate detection) ────────────────
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM records WHERE email = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM records WHERE phone = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, phone);
            return ps.executeQuery().next();
        }
    }

    public boolean existsByRecordId(String id) throws SQLException {
        String sql = "SELECT 1 FROM records WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        }
    }

    // ── UPDATE: status only ────────────────────────────────────────
    public boolean updateStatus(String recordId, String status) throws SQLException {
        String sql = "UPDATE records SET status = ? WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, recordId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────
    public boolean deleteRecord(String id) throws SQLException {
        String sql = "DELETE FROM records WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DASHBOARD stats ────────────────────────────────────────────
    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM records WHERE status = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM records";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // ── Private helper: map ResultSet row → Record object ──────────
    private Record mapRow(ResultSet rs) throws SQLException {
        Record r = new Record();
        r.setRecordId(rs.getString("record_id"));
        r.setName(rs.getString("name"));
        r.setEmail(rs.getString("email"));
        r.setPhone(rs.getString("phone"));
        r.setDepartment(rs.getString("department"));
        r.setAddress(rs.getString("address"));
        r.setStatus(rs.getString("status"));
        r.setDateCreated(rs.getTimestamp("date_created"));
        return r;
    }
}
