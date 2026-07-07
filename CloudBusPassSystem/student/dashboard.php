<?php
require '../includes/auth_student.php';
require '../includes/db.php';
require '../includes/functions.php';

$studentId = $_SESSION['student_id'];

// Fetch student info
$stmt = $pdo->prepare("SELECT * FROM student WHERE student_id = ?");
$stmt->execute([$studentId]);
$student = $stmt->fetch();

// Fetch latest pass (active or otherwise)
$stmt = $pdo->prepare("
    SELECT bp.*, a.fare, p.payment_status
    FROM buspass bp
    LEFT JOIN application a ON bp.application_id = a.application_id
    LEFT JOIN payment p ON p.application_id = a.application_id
    WHERE bp.student_id = ?
    ORDER BY bp.pass_id DESC LIMIT 1
");
$stmt->execute([$studentId]);
$pass = $stmt->fetch();

// Fetch latest application status (in case pending/rejected)
$stmt = $pdo->prepare("SELECT * FROM application WHERE student_id = ? ORDER BY application_id DESC LIMIT 1");
$stmt->execute([$studentId]);
$latestApp = $stmt->fetch();

$pageTitle = "Dashboard";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <h3 class="mb-4">Welcome, <?php echo htmlspecialchars($student['name']); ?> 👋</h3>

  <?php if ($pass && $pass['status'] === 'Active'): ?>
    <div class="card dashboard-card p-4 mb-4">
      <h5>Your Active Bus Pass</h5>
      <div class="row mt-3">
        <div class="col-md-6">
          <p><strong>Route:</strong> <?php echo htmlspecialchars($pass['route']); ?></p>
          <p><strong>Boarding Point:</strong> <?php echo htmlspecialchars($pass['boarding_point']); ?></p>
          <p><strong>Destination:</strong> <?php echo htmlspecialchars($pass['destination']); ?></p>
        </div>
        <div class="col-md-6">
          <p><strong>Pass Type:</strong> <?php echo htmlspecialchars($pass['pass_type']); ?></p>
          <p><strong>Expiry Date:</strong> <?php echo htmlspecialchars($pass['expiry_date']); ?></p>
          <p><strong>Payment Status:</strong>
            <span class="badge status-<?php echo htmlspecialchars($pass['payment_status'] ?? 'Pending'); ?>">
              <?php echo htmlspecialchars($pass['payment_status'] ?? 'Pending'); ?>
            </span>
          </p>
        </div>
      </div>
      <div class="mt-2">
        <a href="view_pass.php?pass_id=<?php echo $pass['pass_id']; ?>" class="btn btn-accent">View / Download Digital Pass</a>
        <a href="apply_pass.php?renew=1" class="btn btn-outline-primary">Renew Pass</a>
      </div>
    </div>
  <?php elseif ($latestApp && $latestApp['status'] === 'Pending'): ?>
    <div class="alert alert-warning">Your application (ID #<?php echo $latestApp['application_id']; ?>) is <strong>Pending</strong> admin approval.</div>
  <?php elseif ($latestApp && $latestApp['status'] === 'Rejected'): ?>
    <div class="alert alert-danger">Your last application was <strong>Rejected</strong>. You may re-apply.
      <a href="apply_pass.php" class="btn btn-sm btn-accent ms-2">Apply Again</a>
    </div>
  <?php else: ?>
    <div class="card dashboard-card p-4 mb-4 text-center">
      <p>You don't have an active bus pass yet.</p>
      <a href="apply_pass.php" class="btn btn-accent">Apply for Bus Pass</a>
    </div>
  <?php endif; ?>

  <div class="card dashboard-card p-4">
    <h5>Your Profile</h5>
    <p><strong>Student ID:</strong> <?php echo htmlspecialchars($student['student_id']); ?></p>
    <p><strong>Email:</strong> <?php echo htmlspecialchars($student['email']); ?></p>
    <p><strong>Phone:</strong> <?php echo htmlspecialchars($student['phone']); ?></p>
    <p><strong>College:</strong> <?php echo htmlspecialchars($student['college_name']); ?></p>
  </div>
</div>

<?php include '../includes/footer.php'; ?>
