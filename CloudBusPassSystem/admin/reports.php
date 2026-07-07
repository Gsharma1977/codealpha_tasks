<?php
require '../includes/auth_admin.php';
require '../includes/db.php';

$stats = $pdo->query("SELECT * FROM report_summary")->fetch();

$daily = $pdo->query("
    SELECT DATE(applied_date) AS day, COUNT(*) AS total
    FROM application GROUP BY DATE(applied_date) ORDER BY day DESC LIMIT 7
")->fetchAll();

$monthly = $pdo->query("
    SELECT DATE_FORMAT(applied_date, '%Y-%m') AS month, COUNT(*) AS total, SUM(fare) AS revenue
    FROM application WHERE status='Approved' GROUP BY month ORDER BY month DESC LIMIT 6
")->fetchAll();

$pageTitle = "Reports";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <h3 class="mb-4">Reports & Analytics</h3>

  <div class="row g-3 mb-4">
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5><?php echo $stats['total_applications']; ?></h5><small>Total Applications</small></div></div>
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5><?php echo $stats['approved_passes']; ?></h5><small>Approved</small></div></div>
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5><?php echo $stats['rejected_passes']; ?></h5><small>Rejected</small></div></div>
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5><?php echo $stats['active_passes']; ?></h5><small>Active</small></div></div>
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5><?php echo $stats['expired_passes']; ?></h5><small>Expired</small></div></div>
    <div class="col-md-2 col-6"><div class="card dashboard-card p-3 text-center"><h5>₹<?php echo number_format($stats['total_revenue'],2); ?></h5><small>Revenue</small></div></div>
  </div>

  <div class="row g-4">
    <div class="col-md-6">
      <div class="card dashboard-card p-3">
        <h5>Daily Applications (Last 7 Days)</h5>
        <table class="table table-sm mt-3">
          <thead><tr><th>Date</th><th>Applications</th></tr></thead>
          <tbody>
            <?php foreach ($daily as $d): ?>
              <tr><td><?php echo htmlspecialchars($d['day']); ?></td><td><?php echo $d['total']; ?></td></tr>
            <?php endforeach; ?>
            <?php if (empty($daily)): ?><tr><td colspan="2" class="text-center text-muted">No data yet</td></tr><?php endif; ?>
          </tbody>
        </table>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card dashboard-card p-3">
        <h5>Monthly Revenue Report</h5>
        <table class="table table-sm mt-3">
          <thead><tr><th>Month</th><th>Approved</th><th>Revenue</th></tr></thead>
          <tbody>
            <?php foreach ($monthly as $m): ?>
              <tr><td><?php echo htmlspecialchars($m['month']); ?></td><td><?php echo $m['total']; ?></td><td>₹<?php echo number_format($m['revenue'],2); ?></td></tr>
            <?php endforeach; ?>
            <?php if (empty($monthly)): ?><tr><td colspan="3" class="text-center text-muted">No data yet</td></tr><?php endif; ?>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<?php include '../includes/footer.php'; ?>
