<?php
/**
 * Shared Helper Functions
 */

/**
 * Fare Calculation Algorithm
 * ----------------------------------------
 * Logic: distance-slab based fare, doubled for Quarterly pass
 * (Quarterly = 3x monthly usage but priced at ~2.5x for a discount incentive;
 *  here we keep it simple: Quarterly = 3 x Monthly slab fare)
 *
 * Slabs:
 *   0-10 km   -> Rs.300
 *   11-20 km  -> Rs.500
 *   21-40 km  -> Rs.700
 *   >40 km    -> Rs.900
 *
 * Time Complexity: O(1) — constant time slab lookup
 */
function calculateFare($distance, $passType) {
    $distance = floatval($distance);

    if ($distance <= 10) {
        $base = 300;
    } elseif ($distance <= 20) {
        $base = 500;
    } elseif ($distance <= 40) {
        $base = 700;
    } else {
        $base = 900;
    }

    if ($passType === 'Quarterly') {
        return $base * 3 * 0.9; // 10% discount for quarterly commitment
    }
    return $base;
}

/**
 * Duplicate Detection Algorithm
 * ----------------------------------------
 * Checks whether the student already holds an Active (non-expired) pass
 * OR already has a Pending application, using student_id / email / phone.
 * Time Complexity: O(1) indexed lookup (student_id, email, phone are indexed)
 */
function hasActivePassOrPending($pdo, $studentId, $email, $phone) {
    // Check active pass
    $stmt = $pdo->prepare("
        SELECT bp.pass_id FROM buspass bp
        JOIN student s ON bp.student_id = s.student_id
        WHERE (bp.student_id = ? OR s.email = ? OR s.phone = ?)
          AND bp.status = 'Active' AND bp.expiry_date >= CURDATE()
        LIMIT 1
    ");
    $stmt->execute([$studentId, $email, $phone]);
    if ($stmt->fetch()) {
        return "You already hold an active bus pass. Duplicate applications are not allowed.";
    }

    // Check pending application
    $stmt = $pdo->prepare("
        SELECT application_id FROM application
        WHERE (student_id = ? OR email = ? OR phone = ?) AND status = 'Pending'
        LIMIT 1
    ");
    $stmt->execute([$studentId, $email, $phone]);
    if ($stmt->fetch()) {
        return "You already have a pending application. Please wait for admin approval.";
    }

    return null; // no duplicate found
}

/**
 * Generates a unique text payload to encode into the QR code.
 * The actual QR image is rendered client-side via qrcode.js (see pass view page).
 */
function buildQrPayload($passId, $studentId, $studentName, $expiry) {
    return "BUSPASS|ID:$passId|STUDENT:$studentId|NAME:$studentName|EXPIRES:$expiry";
}

/** Simple input sanitizer */
function clean($value) {
    return htmlspecialchars(trim($value), ENT_QUOTES, 'UTF-8');
}

/** Validate email format */
function isValidEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
}

/** Validate Indian-style 10-digit phone number */
function isValidPhone($phone) {
    return preg_match('/^[6-9]\d{9}$/', $phone) === 1;
}
