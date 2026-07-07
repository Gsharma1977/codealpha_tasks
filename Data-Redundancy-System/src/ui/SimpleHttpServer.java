package ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.AdminDAO;
import dao.RecordDAO;
import models.Admin;
import models.Record;
import service.DuplicateDetectionService;
import service.ReportService;
import service.ValidationService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SimpleHttpServer
 * ─────────────────────────────────────────────────────────────────
 * A lightweight HTTP server using Java's built-in com.sun.net.httpserver.
 * No external frameworks needed!
 *
 * Routes:
 *   GET  /                        → redirect to /web/login.html
 *   GET  /web/*                   → serve static HTML/CSS/JS files
 *   POST /api/login               → authenticate admin
 *   GET  /api/records             → list all records (optional ?status=)
 *   POST /api/records             → create new record (full pipeline)
 *   DELETE /api/records/{id}      → delete record
 *   PUT  /api/records/{id}/false-positive → mark as FALSE_POSITIVE
 *   GET  /api/dashboard           → dashboard stats
 *   GET  /api/reports/summary     → summary report text
 *   GET  /api/reports/duplicate   → duplicate report text
 *   GET  /api/reports/validation  → validation report text
 */
public class SimpleHttpServer {

    private static final int PORT     = 8080;
    private static final String WEB_ROOT = "web"; // relative path to web folder

    private final RecordDAO              recordDAO      = new RecordDAO();
    private final AdminDAO               adminDAO       = new AdminDAO();
    private final ValidationService      validationSvc  = new ValidationService();
    private final DuplicateDetectionService dupSvc      = new DuplicateDetectionService();
    private final ReportService          reportSvc      = new ReportService();

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/",           new StaticHandler());
        server.createContext("/web/",       new StaticHandler());
        server.createContext("/api/login",  new LoginHandler());
        server.createContext("/api/records",new RecordsHandler());
        server.createContext("/api/dashboard",new DashboardHandler());
        server.createContext("/api/reports/",new ReportsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("=================================================");
        System.out.println(" Data Redundancy System Server Started");
        System.out.println(" Open in browser: http://localhost:" + PORT + "/web/login.html");
        System.out.println("=================================================");
    }

    // ── Static file handler ────────────────────────────────────────
    class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/web/login.html";

            // Strip leading /
            String filePath = path.startsWith("/") ? path.substring(1) : path;
            Path file = Paths.get(filePath);

            if (Files.exists(file) && !Files.isDirectory(file)) {
                byte[] data = Files.readAllBytes(file);
                String mime = getMime(filePath);
                ex.getResponseHeaders().set("Content-Type", mime);
                ex.sendResponseHeaders(200, data.length);
                ex.getResponseBody().write(data);
            } else {
                byte[] msg = ("404 Not Found: " + path).getBytes();
                ex.sendResponseHeaders(404, msg.length);
                ex.getResponseBody().write(msg);
            }
            ex.getResponseBody().close();
        }

        private String getMime(String path) {
            if (path.endsWith(".html")) return "text/html; charset=utf-8";
            if (path.endsWith(".css"))  return "text/css";
            if (path.endsWith(".js"))   return "application/javascript";
            return "text/plain";
        }
    }

    // ── Login handler ──────────────────────────────────────────────
    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCors(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204,-1); return; }
            if (!"POST".equals(ex.getRequestMethod()))   { send(ex, 405, "{\"error\":\"Method not allowed\"}"); return; }

            String body = readBody(ex);
            Map<String,String> j = parseSimpleJson(body);
            String username = j.get("username");
            String password = j.get("password");

            try {
                Admin a = adminDAO.login(username, password);
                if (a != null) {
                    send(ex, 200, "{\"success\":true,\"message\":\"Login successful\"}");
                } else {
                    send(ex, 401, "{\"success\":false,\"message\":\"Invalid credentials\"}");
                }
            } catch (SQLException e) {
                send(ex, 500, "{\"success\":false,\"message\":\"DB error: " + e.getMessage() + "\"}");
            }
        }
    }

    // ── Records handler ────────────────────────────────────────────
    class RecordsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCors(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204,-1); return; }

            String method = ex.getRequestMethod();
            String path   = ex.getRequestURI().getPath();  // e.g. /api/records or /api/records/REC001

            try {
                if ("GET".equals(method) && path.equals("/api/records")) {
                    handleGetRecords(ex);
                } else if ("POST".equals(method) && path.equals("/api/records")) {
                    handlePostRecord(ex);
                } else if ("DELETE".equals(method) && path.startsWith("/api/records/")) {
                    String id = path.replace("/api/records/","");
                    handleDelete(ex, id);
                } else if ("PUT".equals(method) && path.contains("/false-positive")) {
                    String id = path.replace("/api/records/","").replace("/false-positive","");
                    handleFalsePositive(ex, id);
                } else {
                    send(ex, 404, "{\"error\":\"Not found\"}");
                }
            } catch (SQLException e) {
                send(ex, 500, "{\"error\":\"" + escJson(e.getMessage()) + "\"}");
            }
        }

        private void handleGetRecords(HttpExchange ex) throws IOException, SQLException {
            String query = ex.getRequestURI().getQuery(); // e.g. status=DUPLICATE
            List<Record> records;
            if (query != null && query.startsWith("status=")) {
                String s = URLDecoder.decode(query.replace("status=",""), StandardCharsets.UTF_8);
                records = recordDAO.getRecordsByStatus(s);
            } else {
                records = recordDAO.getAllRecords();
            }
            send(ex, 200, recordsToJson(records));
        }

        private void handlePostRecord(HttpExchange ex) throws IOException, SQLException {
            String body = readBody(ex);
            Map<String,String> j = parseSimpleJson(body);

            Record r = new Record();
            r.setRecordId(j.getOrDefault("recordId",""));
            r.setName(j.getOrDefault("name",""));
            r.setEmail(j.getOrDefault("email",""));
            r.setPhone(j.getOrDefault("phone",""));
            r.setDepartment(j.getOrDefault("department",""));
            r.setAddress(j.getOrDefault("address",""));

            // Step 1: Validate
            List<ValidationService.ValidationResult> valResults = validationSvc.validate(r);
            if (!validationSvc.isValid(valResults)) {
                StringBuilder errors = new StringBuilder("[");
                for (ValidationService.ValidationResult vr : valResults) {
                    if (!vr.passed)
                        errors.append("\"").append(escJson(vr.message)).append("\",");
                }
                if (errors.charAt(errors.length()-1)==',') errors.deleteCharAt(errors.length()-1);
                errors.append("]");
                send(ex, 400, "{\"message\":\"Validation failed.\",\"saved\":false,"
                        + "\"validationErrors\":" + errors + "}");
                return;
            }

            // Step 2: Detect duplicates
            String status = dupSvc.detect(r);
            String reason = buildReason(status);

            // Step 3: Save only if UNIQUE or POTENTIAL_DUPLICATE (admin can review)
            boolean saved = false;
            if ("UNIQUE".equals(status) || "POTENTIAL_DUPLICATE".equals(status)) {
                saved = recordDAO.insertRecord(r);
            }

            String msg = switch (status) {
                case "UNIQUE"             -> "Record saved successfully as UNIQUE.";
                case "DUPLICATE"          -> "Record blocked — exact DUPLICATE found.";
                case "POTENTIAL_DUPLICATE"-> "Record saved but flagged as POTENTIAL_DUPLICATE for review.";
                default                   -> "Record processed.";
            };

            send(ex, 200, String.format(
                    "{\"message\":\"%s\",\"status\":\"%s\",\"reason\":\"%s\",\"saved\":%b,\"validationErrors\":[]}",
                    escJson(msg), status, escJson(reason), saved));
        }

        private void handleDelete(HttpExchange ex, String id) throws IOException, SQLException {
            boolean ok = recordDAO.deleteRecord(id);
            send(ex, 200, "{\"message\":\"" + (ok ? "Deleted successfully." : "Record not found.") + "\"}");
        }

        private void handleFalsePositive(HttpExchange ex, String id) throws IOException, SQLException {
            boolean ok = recordDAO.updateStatus(id, "FALSE_POSITIVE");
            send(ex, 200, "{\"message\":\"" + (ok ? "Marked as FALSE_POSITIVE." : "Record not found.") + "\"}");
        }

        private String buildReason(String status) {
            return switch (status) {
                case "DUPLICATE"          -> "Exact match found on email, phone, or record ID.";
                case "POTENTIAL_DUPLICATE"-> "Similar name or partial match detected via Levenshtein algorithm.";
                case "UNIQUE"             -> "No duplicates found. Record is unique.";
                default                   -> "";
            };
        }
    }

    // ── Dashboard handler ──────────────────────────────────────────
    class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCors(ex);
            try {
                int total    = recordDAO.countTotal();
                int unique   = recordDAO.countByStatus("UNIQUE");
                int dup      = recordDAO.countByStatus("DUPLICATE");
                int potential= recordDAO.countByStatus("POTENTIAL_DUPLICATE");
                int fp       = recordDAO.countByStatus("FALSE_POSITIVE");
                send(ex, 200, String.format(
                        "{\"total\":%d,\"unique\":%d,\"duplicate\":%d,\"potential\":%d,\"falsePosistive\":%d}",
                        total, unique, dup, potential, fp));
            } catch (SQLException e) {
                send(ex, 500, "{\"error\":\"" + escJson(e.getMessage()) + "\"}");
            }
        }
    }

    // ── Reports handler ────────────────────────────────────────────
    class ReportsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCors(ex);
            String path = ex.getRequestURI().getPath();
            try {
                String report;
                if (path.endsWith("summary"))    report = reportSvc.generateSummaryReport();
                else if (path.endsWith("duplicate")) report = reportSvc.generateDuplicateReport();
                else if (path.endsWith("validation"))report = reportSvc.generateValidationReport();
                else { send(ex, 404, "Unknown report type."); return; }

                byte[] data = report.getBytes(StandardCharsets.UTF_8);
                ex.getResponseHeaders().set("Content-Type","text/plain; charset=utf-8");
                ex.sendResponseHeaders(200, data.length);
                ex.getResponseBody().write(data);
                ex.getResponseBody().close();
            } catch (SQLException e) {
                send(ex, 500, "DB Error: " + e.getMessage());
            }
        }
    }

    // ── Utility helpers ────────────────────────────────────────────
    private void send(HttpExchange ex, int code, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin","*");
        ex.sendResponseHeaders(code, data.length);
        ex.getResponseBody().write(data);
        ex.getResponseBody().close();
    }

    private void addCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin","*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers","Content-Type");
    }

    private String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    /** Very simple JSON key-value parser (no library needed). */
    private Map<String,String> parseSimpleJson(String json) {
        Map<String,String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;
        // Remove braces
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length()-1);

        // Split by comma — naïve but sufficient for flat JSON
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("\"","");
                String val = kv[1].trim().replaceAll("\"","");
                map.put(key, val);
            }
        }
        return map;
    }

    private String recordsToJson(List<Record> records) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            Record r = records.get(i);
            sb.append(String.format(
                "{\"recordId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"," +
                "\"department\":\"%s\",\"address\":\"%s\",\"status\":\"%s\",\"dateCreated\":\"%s\"}",
                escJson(r.getRecordId()), escJson(r.getName()), escJson(r.getEmail()),
                escJson(r.getPhone()), escJson(r.getDepartment()), escJson(r.getAddress()),
                escJson(r.getStatus()),
                r.getDateCreated() != null ? r.getDateCreated().toString() : ""));
            if (i < records.size()-1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }
}
