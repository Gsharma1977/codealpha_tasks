<?php
require '../includes/auth_admin.php';
require '../includes/db.php';

$applications = $pdo->query("SELECT * FROM application ORDER BY applied_date DESC")->fetchAll();

$pageTitle = "Manage Applications";
$basePath = "../";
include '../includes/header.php';
?>

<div class="container py-4">
  <h3 class="mb-4">All Applications</h3>

  <?php if (isset($_GET['msg'])): ?>
    <div class="alert alert-success"><?php echo htmlspecialchars($_GET['msg']); ?></div>
  <?php endif; ?>

  <div class="table-responsive">
    <table class="table table-modern table-bordered bg-white">
      <thead>
        <tr>
          <th>ID</th><th>Student</th><th>Route</th><th>Distance</th><th>Type</th><th>Fare</th><th>Status</th><th>Date</th><th>Action</th>
        </tr>
      </thead>
      <tbody>
        <?php foreach ($applications as $app): ?>
        <tr>
          <td>#<?php echo $app['application_id']; ?></td>
          <td><?php echo htmlspecialchars($app['name']); ?><br><small><?php echo htmlspecialchars($app['student_id']); ?></small></td>
          <td><?php echo htmlspecialchars($app['boarding_point'] . ' → ' . $app['destination']); ?></td>
          <td><?php echo htmlspecialchars($app['distance_km']); ?> km</td>
          <td><?php echo htmlspecialchars($app['pass_type']); ?></td>
          <td>₹<?php echo htmlspecialchars($app['fare']); ?></td>
          <td><span class="badge status-<?php echo htmlspecialchars($app['status']); ?>"><?php echo htmlspecialchars($app['status']); ?></span></td>
          <td><?php echo htmlspecialchars($app['applied_date']); ?></td>
          <td>
            <?php if ($app['status'] === 'Pending'): ?>
              <a href="approve_reject.php?id=<?php echo $app['application_id']; ?>&action=approve" class="btn btn-sm btn-success" onclick="return confirm('Approve this application?')">Approve</a>
              <a href="approve_reject.php?id=<?php echo $app['application_id']; ?>&action=reject" class="btn btn-sm btn-danger" onclick="return confirm('Reject this application?')">Reject</a>
            <?php else: ?>
              &mdash;
            <?php endif; ?>
          </td>
        </tr>
        <?php endforeach; ?>
      </tbody>
    </table>
  </div>
</div>

<?php include '../includes/footer.php'; ?>
