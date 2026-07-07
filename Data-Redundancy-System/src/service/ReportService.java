package service;

import dao.DuplicateLogDAO;
import dao.RecordDAO;
import dao.ValidationLogDAO;
import models.DuplicateLog;
import models.Record;
import models.ValidationLog;

import java.sql.SQLException;
import java.util.List;

/**
 * ReportService
 * ─────────────────────────────────────────────────────────────────
 * Generates three types of plain-text reports:
 *   1. Duplicate Report   – all DUPLICATE & POTENTIAL_DUPLICATE records
 *   2. Validation Report  – all FAIL validation log entries
 *   3. Summary Report     – counts and redundancy percentage
 */
public class ReportService {

    private final RecordDAO       recordDAO  = new RecordDAO();
    private final DuplicateLogDAO dupDAO     = new DuplicateLogDAO();
    private final ValidationLogDAO valDAO    = new ValidationLogDAO();

    // ── Report 1: Duplicates ───────────────────────────────────────
    public String generateDuplicateReport() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("              DUPLICATE DETECTION REPORT\n");
        sb.append("============================================================\n\n");

        List<DuplicateLog> logs = dupDAO.getAll();
        if (logs.isEmpty()) {
            sb.append("  No duplicates detected.\n");
        } else {
            for (DuplicateLog dl : logs) {
                sb.append(String.format("  Record ID    : %s%n", dl.getRecordId()));
                sb.append(String.format("  Matched With : %s%n", dl.getMatchedWith()));
                sb.append(String.format("  Reason       : %s%n", dl.getReason()));
                sb.append(String.format("  Similarity   : %.2f%%%n", dl.getSimilarityScore()));
                sb.append(String.format("  Detected At  : %s%n", dl.getDetectedAt()));
                sb.append("  ----------------------------------------------------\n");
            }
        }
        sb.append("\n  Total Duplicate Events: ").append(logs.size()).append("\n");
        return sb.toString();
    }

    // ── Report 2: Validation Failures ─────────────────────────────
    public String generateValidationReport() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("              VALIDATION FAILURE REPORT\n");
        sb.append("============================================================\n\n");

        long failCount = 0;
        for (ValidationLog vl : valDAO.getAll()) {
            if ("FAIL".equals(vl.getValidationResult())) {
                failCount++;
                sb.append(String.format("  Record ID  : %s%n", vl.getRecordId()));
                sb.append(String.format("  Field      : %s%n", vl.getFieldName()));
                sb.append(String.format("  Message    : %s%n", vl.getMessage()));
                sb.append(String.format("  Checked At : %s%n", vl.getCheckedAt()));
                sb.append("  ----------------------------------------------------\n");
            }
        }
        if (failCount == 0) sb.append("  No validation failures found.\n");
        sb.append("\n  Total Failures: ").append(failCount).append("\n");
        return sb.toString();
    }

    // ── Report 3: Summary ─────────────────────────────────────────
    public String generateSummaryReport() throws SQLException {
        int total    = recordDAO.countTotal();
        int unique   = recordDAO.countByStatus("UNIQUE");
        int dup      = recordDAO.countByStatus("DUPLICATE");
        int potDup   = recordDAO.countByStatus("POTENTIAL_DUPLICATE");
        int falsePos = recordDAO.countByStatus("FALSE_POSITIVE");

        double redundancy = total > 0
                ? ((double)(dup + potDup) / total) * 100
                : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("                   SUMMARY REPORT\n");
        sb.append("============================================================\n\n");
        sb.append(String.format("  Total Records       : %d%n", total));
        sb.append(String.format("  Unique Records      : %d%n", unique));
        sb.append(String.format("  Duplicate Records   : %d%n", dup));
        sb.append(String.format("  Potential Duplicates: %d%n", potDup));
        sb.append(String.format("  False Positives     : %d%n", falsePos));
        sb.append(String.format("  Redundancy %%       : %.2f%%%n", redundancy));
        sb.append("\n============================================================\n");
        return sb.toString();
    }
}
