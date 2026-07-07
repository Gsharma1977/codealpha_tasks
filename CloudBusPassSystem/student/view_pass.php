<?php
require '../includes/auth_student.php';
require '../includes/db.php';
require '../includes/functions.php';

$studentId = $_SESSION['student_id'];
$passId = intval($_GET['pass_id'] ?? 0);

$stmt = $pdo->prepare("
    SELECT bp.*, s.name AS student_name
    FROM buspass bp
    JOIN student s ON bp.student_id = s.student_id
    WHERE bp.pass_id = ? AND bp.student_id = ?
");
$stmt->execute([$passId, $studentId]);
$pass = $stmt->fetch();

if (!$pass) {
    die("Pass not found or access denied.");
}

$pageTitle = "Digital Pass";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <div class="pass-card" id="passCard">
    <div class="pass-header d-flex justify-content-between align-items-center">
      <span><strong>Cloud Bus Pass</strong></span>
      <span class="badge status-<?php echo htmlspecialchars($pass['status']); ?>"><?php echo htmlspecialchars($pass['status']); ?></span>
    </div>
    <div class="p-4">
      <p><strong>Pass ID:</strong> #<?php echo str_pad($pass['pass_id'], 6, '0', STR_PAD_LEFT); ?></p>
      <p><strong>Student Name:</strong> <?php echo htmlspecialchars($pass['student_name']); ?></p>
      <p><strong>Student ID:</strong> <?php echo htmlspecialchars($pass['student_id']); ?></p>
      <p><strong>Route:</strong> <?php echo htmlspecialchars($pass['route']); ?></p>
      <p><strong>Boarding Point:</strong> <?php echo htmlspecialchars($pass['boarding_point']); ?></p>
      <p><strong>Destination:</strong> <?php echo htmlspecialchars($pass['destination']); ?></p>
      <p><strong>Pass Type:</strong> <?php echo htmlspecialchars($pass['pass_type']); ?></p>
      <p><strong>Issue Date:</strong> <?php echo htmlspecialchars($pass['issue_date']); ?></p>
      <p><strong>Expiry Date:</strong> <?php echo htmlspecialchars($pass['expiry_date']); ?></p>
      <div class="text-center mt-3" id="qrcode"></div>
    </div>
  </div>

  <div class="text-center mt-3">
    <button class="btn btn-accent" onclick="window.print()">Download / Print Pass</button>
    <a href="dashboard.php" class="btn btn-outline-primary">Back to Dashboard</a>
  </div>
</div>

<!-- QR Code generation library (client-side rendering) -->
<script src="https://cdn.jsdelivr.net/npm/qrcodejs@1.0.0/qrcode.min.js"></script>
<script>
  // The QR payload was generated server-side (see includes/functions.php -> buildQrPayload)
  // and stored in the buspass.qr_code_data column. It is rendered here as a scannable QR image.
  new QRCode(document.getElementById("qrcode"), {
    text: <?php echo json_encode($pass['qr_code_data']); ?>,
    width: 150,
    height: 150
  });
</script>

<style>
@media print {
  nav, footer, .btn { display: none !important; }
}
</style>

<?php include '../includes/footer.php'; ?>
