<?php
require '../includes/auth_admin.php';
require '../includes/db.php';

// Handle cancel action
if (isset($_GET['cancel'])) {
    $stmt = $pdo->prepare("UPDATE buspass SET status = 'Cancelled' WHERE pass_id = ?");
    $stmt->execute([intval($_GET['cancel'])]);
    header("Location: manage_passes.php?msg=Pass cancelled successfully.");
    exit();
}

// Auto-expire passes whose expiry date has passed
$pdo->exec("UPDATE buspass SET status = 'Expired' WHERE status = 'Active' AND expiry_date < CURDATE()");

$passes = $pdo->query("
    SELECT bp.*, s.name AS student_name
    FROM buspass bp
    JOIN student s ON bp.student_id = s.student_id
    ORDER BY bp.pass_id DESC
")->fetchAll();

$pageTitle = "Manage Passes";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <h3 class="mb-4">All Bus Passes</h3>

  <?php if (isset($_GET['msg'])): ?>
    <div class="alert alert-success"><?php echo htmlspecialchars($_GET['msg']); ?></div>
  <?php endif; ?>

  <div class="table-responsive">
    <table class="table table-modern table-bordered bg-white">
      <thead>
        <tr><th>Pass ID</th><th>Student</th><th>Route</th><th>Type</th><th>Issue</th><th>Expiry</th><th>Status</th><th>Action</th></tr>
      </thead>
      <tbody>
        <?php foreach ($passes as $p): ?>
        <tr>
          <td>#<?php echo str_pad($p['pass_id'],6,'0',STR_PAD_LEFT); ?></td>
          <td><?php echo htmlspecialchars($p['student_name']); ?> (<?php echo htmlspecialchars($p['student_id']); ?>)</td>
          <td><?php echo htmlspecialchars($p['route']); ?></td>
          <td><?php echo htmlspecialchars($p['pass_type']); ?></td>
          <td><?php echo htmlspecialchars($p['issue_date']); ?></td>
          <td><?php echo htmlspecialchars($p['expiry_date']); ?></td>
          <td><span class="badge status-<?php echo htmlspecialchars($p['status']); ?>"><?php echo htmlspecialchars($p['status']); ?></span></td>
          <td>
            <?php if ($p['status'] === 'Active'): ?>
              <a href="manage_passes.php?cancel=<?php echo $p['pass_id']; ?>" class="btn btn-sm btn-danger" onclick="return confirm('Cancel this pass?')">Cancel</a>
            <?php else: ?>&mdash;<?php endif; ?>
          </td>
        </tr>
        <?php endforeach; ?>
      </tbody>
    </table>
  </div>
</div>

<?php include '../includes/footer.php'; ?>
