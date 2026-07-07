package service;

import dao.ValidationLogDAO;
import models.Record;
import models.ValidationLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ValidationService
 * ─────────────────────────────────────────────────────────────────
 * Validates each field of a Record before it is saved.
 * Returns a list of ValidationResult objects (one per field).
 *
 * Rules:
 *   1. record_id  – required, non-empty
 *   2. name       – required, non-empty
 *   3. email      – required + must match RFC-style regex
 *   4. phone      – required + exactly 10 digits
 */
public class ValidationService {

    // Simple but solid email regex
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    // 10 digit phone (no spaces, no dashes)
    private static final String PHONE_REGEX = "^[6-9][0-9]{9}$";  // Indian mobile pattern

    private final ValidationLogDAO logDAO = new ValidationLogDAO();

    /** Container for one field's validation result */
    public static class ValidationResult {
        public final String  field;
        public final boolean passed;
        public final String  message;

        public ValidationResult(String field, boolean passed, String message) {
            this.field   = field;
            this.passed  = passed;
            this.message = message;
        }
    }

    /**
     * Runs all validations. Persists logs to DB. Returns list of results.
     * @return list — if every item.passed == true, the record is valid.
     */
    public List<ValidationResult> validate(Record r) throws SQLException {
        List<ValidationResult> results = new ArrayList<>();

        results.add(check("record_id", r.getRecordId() != null && !r.getRecordId().isBlank(),
                "Record ID is required."));

        results.add(check("name", r.getName() != null && !r.getName().isBlank(),
                "Name is required."));

        boolean emailOk = r.getEmail() != null && r.getEmail().matches(EMAIL_REGEX);
        results.add(check("email", emailOk,
                emailOk ? "Email is valid." : "Invalid email format. Example: john@gmail.com"));

        boolean phoneOk = r.getPhone() != null && r.getPhone().matches(PHONE_REGEX);
        results.add(check("phone", phoneOk,
                phoneOk ? "Phone is valid." : "Phone must be a 10-digit Indian mobile number."));

        // Persist each result to validation_log table
        for (ValidationResult vr : results) {
            ValidationLog vl = new ValidationLog(
                    r.getRecordId(), vr.field,
                    vr.passed ? "PASS" : "FAIL",
                    vr.message);
            logDAO.insert(vl);
        }

        return results;
    }

    /** Returns true only if ALL fields pass */
    public boolean isValid(List<ValidationResult> results) {
        return results.stream().allMatch(vr -> vr.passed);
    }

    private ValidationResult check(String field, boolean condition, String message) {
        return new ValidationResult(field, condition,
                condition ? field + " validation passed." : message);
    }
}
