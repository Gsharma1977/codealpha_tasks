package service;

import dao.DuplicateLogDAO;
import dao.RecordDAO;
import models.DuplicateLog;
import models.Record;
import utility.LevenshteinDistanceUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * DuplicateDetectionService
 * ─────────────────────────────────────────────────────────────────
 * Three-level duplicate detection:
 *
 *   LEVEL 1 – Exact Duplicate
 *     • Same email  OR  same phone  OR  same record_id  → DUPLICATE
 *
 *   LEVEL 2 – Partial Duplicate
 *     • Name contains existing name (or vice-versa) AND same phone → POTENTIAL_DUPLICATE
 *
 *   LEVEL 3 – Similar Name (Levenshtein Distance)
 *     • Similarity >= NAME_THRESHOLD (default 70%) → POTENTIAL_DUPLICATE
 *
 *   FALSE_POSITIVE : admin can manually reclassify a POTENTIAL_DUPLICATE
 *                    if it turns out to be a different person.
 */
public class DuplicateDetectionService {

    private static final double NAME_THRESHOLD = 70.0;  // 70% similarity

    private final RecordDAO      recordDAO      = new RecordDAO();
    private final DuplicateLogDAO dupLogDAO     = new DuplicateLogDAO();

    /**
     * Main entry point.
     * Examines 'incoming' against all existing records.
     * Sets incoming.status and logs the reason.
     * @return the final classification string
     */
    public String detect(Record incoming) throws SQLException {

        // ── LEVEL 1: Exact checks ──────────────────────────────────
        if (recordDAO.existsByRecordId(incoming.getRecordId())) {
            log(incoming.getRecordId(), incoming.getRecordId(),
                    "Exact duplicate: Record ID already exists.", 100.0);
            incoming.setStatus("DUPLICATE");
            return "DUPLICATE";
        }
        if (recordDAO.existsByEmail(incoming.getEmail())) {
            log(incoming.getRecordId(), findByEmail(incoming.getEmail()),
                    "Exact duplicate: Email already exists.", 100.0);
            incoming.setStatus("DUPLICATE");
            return "DUPLICATE";
        }
        if (recordDAO.existsByPhone(incoming.getPhone())) {
            log(incoming.getRecordId(), findByPhone(incoming.getPhone()),
                    "Exact duplicate: Phone number already exists.", 100.0);
            incoming.setStatus("DUPLICATE");
            return "DUPLICATE";
        }

        // ── LEVEL 2 & 3: Compare name against all existing records ─
        List<Record> all = recordDAO.getAllRecords();
        for (Record existing : all) {

            String inName  = incoming.getName().toLowerCase().trim();
            String exName  = existing.getName().toLowerCase().trim();

            // Level 2 – partial containment
            if ((inName.contains(exName) || exName.contains(inName))
                    && incoming.getPhone().equals(existing.getPhone())) {
                log(incoming.getRecordId(), existing.getRecordId(),
                        "Partial duplicate: Name contains existing name + same phone.", 85.0);
                incoming.setStatus("POTENTIAL_DUPLICATE");
                return "POTENTIAL_DUPLICATE";
            }

            // Level 3 – Levenshtein similarity
            double sim = LevenshteinDistanceUtil.calculateSimilarity(
                    incoming.getName(), existing.getName());
            if (sim >= NAME_THRESHOLD) {
                log(incoming.getRecordId(), existing.getRecordId(),
                        String.format("Similar name detected (%.1f%% similar to '%s').",
                                sim, existing.getName()), sim);
                incoming.setStatus("POTENTIAL_DUPLICATE");
                return "POTENTIAL_DUPLICATE";
            }
        }

        // ── No match found → UNIQUE ────────────────────────────────
        incoming.setStatus("UNIQUE");
        return "UNIQUE";
    }

    // ── Helpers ────────────────────────────────────────────────────
    private void log(String recordId, String matchedWith, String reason, double score)
            throws SQLException {
        DuplicateLog dl = new DuplicateLog(recordId, matchedWith, reason, score);
        dupLogDAO.insert(dl);
    }

    private String findByEmail(String email) throws SQLException {
        List<Record> all = recordDAO.getAllRecords();
        return all.stream().filter(r -> r.getEmail().equalsIgnoreCase(email))
                .map(Record::getRecordId).findFirst().orElse("N/A");
    }

    private String findByPhone(String phone) throws SQLException {
        List<Record> all = recordDAO.getAllRecords();
        return all.stream().filter(r -> r.getPhone().equals(phone))
                .map(Record::getRecordId).findFirst().orElse("N/A");
    }
}
