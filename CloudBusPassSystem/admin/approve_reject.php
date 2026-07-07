<?php
require '../includes/auth_admin.php';
require '../includes/db.php';
require '../includes/functions.php';

$id = intval($_GET['id'] ?? 0);
$action = $_GET['action'] ?? '';

$stmt = $pdo->prepare("SELECT * FROM application WHERE application_id = ?");
$stmt->execute([$id]);
$app = $stmt->fetch();

if (!$app || $app['status'] !== 'Pending') {
    header("Location: applications.php?msg=Invalid or already processed application.");
    exit();
}

/**
 * Pass Approval Algorithm
 * -----------------------
 * 1. Validate application exists & is Pending
 * 2. If action = approve:
 *      a. Update application status -> Approved
 *      b. Compute issue_date = today, expiry_date = today + 30/90 days
 *      c. Build QR payload
 *      d. Insert into buspass table
 *      e. Insert a Payment record (Pending until student pays)
 * 3. If action = reject:
 *      a. Update application status -> Rejected
 * Time Complexity: O(1)
 */
if ($action === 'approve') {
    $pdo->beginTransaction();
    try {
        $stmt = $pdo->prepare("UPDATE application SET status = 'Approved' WHERE application_id = ?");
        $stmt->execute([$id]);

        $issueDate = date('Y-m-d');
        $days = $app['pass_type'] === 'Quarterly' ? 90 : 30;
        $expiryDate = date('Y-m-d', strtotime("+$days days"));
        $route = $app['boarding_point'] . ' - ' . $app['destination'];

        // Insert placeholder pass first to get pass_id, then build QR payload
        $stmt = $pdo->prepare("
            INSERT INTO buspass (application_id, student_id, route, boarding_point, destination, pass_type, issue_date, expiry_date, status, qr_code_data)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active', '')
        ");
        $stmt->execute([$id, $app['student_id'], $route, $app['boarding_point'], $app['destination'], $app['pass_type'], $issueDate, $expiryDate]);
        $passId = $pdo->lastInsertId();

        $stmtName = $pdo->prepare("SELECT name FROM student WHERE student_id = ?");
        $stmtName->execute([$app['student_id']]);
        $studentName = $stmtName->fetchColumn();

        $qrPayload = buildQrPayload($passId, $app['student_id'], $studentName, $expiryDate);
        $stmt = $pdo->prepare("UPDATE buspass SET qr_code_data = ? WHERE pass_id = ?");
        $stmt->execute([$qrPayload, $passId]);

        $stmt = $pdo->prepare("INSERT INTO payment (application_id, student_id, amount, payment_status) VALUES (?, ?, ?, 'Pending')");
        $stmt->execute([$id, $app['student_id'], $app['fare']]);

        $pdo->commit();
        header("Location: applications.php?msg=Application approved and pass issued successfully.");
    } catch (Exception $e) {
        $pdo->rollBack();
        header("Location: applications.php?msg=Error: " . urlencode($e->getMessage()));
    }
} elseif ($action === 'reject') {
    $stmt = $pdo->prepare("UPDATE application SET status = 'Rejected' WHERE application_id = ?");
    $stmt->execute([$id]);
    header("Location: applications.php?msg=Application rejected.");
} else {
    header("Location: applications.php");
}
exit();
