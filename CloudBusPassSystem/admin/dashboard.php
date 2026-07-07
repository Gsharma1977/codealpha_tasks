<?php
require '../includes/auth_admin.php';
require '../includes/db.php';

$stats = $pdo->query("SELECT * FROM report_summary")->fetch();

$pageTitle = "Admin Dashboard";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <h3 class="mb-4">Admin Dashboard</h3>
  <div class="row g-3 mb-4">
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4><?php echo $stats['total_applications']; ?></h4>
        <small>Total Applications</small>
      </div>
    </div>
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4><?php echo $stats['approved_passes']; ?></h4>
        <small>Approved</small>
      </div>
    </div>
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4><?php echo $stats['rejected_passes']; ?></h4>
        <small>Rejected</small>
      </div>
    </div>
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4><?php echo $stats['active_passes']; ?></h4>
        <small>Active Passes</small>
      </div>
    </div>
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4><?php echo $stats['expired_passes']; ?></h4>
        <small>Expired</small>
      </div>
    </div>
    <div class="col-md-2 col-6">
      <div class="card dashboard-card p-3 text-center">
        <h4>₹<?php echo number_format($stats['total_revenue'], 2); ?></h4>
        <small>Revenue</small>
      </div>
    </div>
  </div>

  <div class="d-flex gap-2 mb-4">
    <a href="applications.php" class="btn btn-accent">Manage Applications</a>
    <a href="manage_passes.php" class="btn btn-outline-primary">Manage Passes</a>
    <a href="reports.php" class="btn btn-outline-primary">Reports</a>
  </div>
</div>

<?php include '../includes/footer.php'; ?>
