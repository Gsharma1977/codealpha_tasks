<?php
require '../includes/auth_student.php';
require '../includes/db.php';
require '../includes/functions.php';

$studentId = $_SESSION['student_id'];
$stmt = $pdo->prepare("SELECT * FROM student WHERE student_id = ?");
$stmt->execute([$studentId]);
$student = $stmt->fetch();

$errors = [];
$success = false;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $boarding    = clean($_POST['boarding_point']);
    $destination = clean($_POST['destination']);
    $distance    = floatval($_POST['distance']);
    $passType    = clean($_POST['pass_type']);

    if (empty($boarding) || empty($destination) || $distance <= 0 || empty($passType)) {
        $errors[] = "All fields are required and distance must be greater than 0.";
    }

    // ---- Duplicate Detection (Module 7) ----
    if (empty($errors)) {
        $dupError = hasActivePassOrPending($pdo, $studentId, $student['email'], $student['phone']);
        if ($dupError) {
            $errors[] = $dupError;
        }
    }

    // ---- Automatic Fare Calculation (Module 6) — server-side, cannot be tampered with ----
    if (empty($errors)) {
        $fare = calculateFare($distance, $passType);

        $stmt = $pdo->prepare("
            INSERT INTO application (student_id, name, email, phone, boarding_point, destination, distance_km, pass_type, fare)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ");
        $stmt->execute([
            $studentId, $student['name'], $student['email'], $student['phone'],
            $boarding, $destination, $distance, $passType, $fare
        ]);
        $success = true;
    }
}

$pageTitle = "Apply for Bus Pass";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <div class="card dashboard-card p-4 mx-auto" style="max-width:600px;">
    <h4 class="mb-3">Apply for Bus Pass</h4>

    <?php if ($success): ?>
      <div class="alert alert-success">
        Application submitted successfully! Await admin approval on your dashboard.
      </div>
      <a href="dashboard.php" class="btn btn-accent">Back to Dashboard</a>
    <?php else: ?>

      <?php if (!empty($errors)): ?>
        <div class="alert alert-danger">
          <ul class="mb-0"><?php foreach ($errors as $e) echo "<li>" . htmlspecialchars($e) . "</li>"; ?></ul>
        </div>
      <?php endif; ?>

      <form method="POST" id="applyForm">
        <div class="mb-3">
          <label class="form-label">Boarding Point</label>
          <input type="text" name="boarding_point" class="form-control" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Destination</label>
          <input type="text" name="destination" class="form-control" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Distance (in KM)</label>
          <input type="number" step="0.1" min="0.1" name="distance" id="distance" class="form-control" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Pass Type</label>
          <select name="pass_type" id="pass_type" class="form-select" required>
            <option value="Monthly">Monthly</option>
            <option value="Quarterly">Quarterly</option>
          </select>
        </div>
        <div class="alert alert-info">
          Estimated Fare: ₹<span id="fareDisplay">0</span>
          <br><small>Fare is calculated automatically and cannot be edited manually.</small>
        </div>
        <button type="submit" class="btn btn-accent w-100">Submit Application</button>
      </form>
    <?php endif; ?>
  </div>
</div>

<script>
// Client-side live preview only — actual fare is always recalculated securely on the server
function previewFare() {
  const distance = parseFloat(document.getElementById('distance').value) || 0;
  const passType = document.getElementById('pass_type').value;
  let base = 0;
  if (distance <= 10) base = 300;
  else if (distance <= 20) base = 500;
  else if (distance <= 40) base = 700;
  else if (distance > 40) base = 900;

  let fare = base;
  if (passType === 'Quarterly') fare = base * 3 * 0.9;

  document.getElementById('fareDisplay').innerText = fare.toFixed(2);
}
document.getElementById('distance').addEventListener('input', previewFare);
document.getElementById('pass_type').addEventListener('change', previewFare);
</script>

<?php include '../includes/footer.php'; ?>
