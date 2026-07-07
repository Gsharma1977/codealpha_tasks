<?php
session_start();
require 'includes/db.php';
require 'includes/functions.php';

$error = "";
$activeTab = $_POST['login_type'] ?? 'student';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $loginType = $_POST['login_type'];

    if ($loginType === 'student') {
        $email = clean($_POST['email']);
        $password = $_POST['password'];

        $stmt = $pdo->prepare("SELECT * FROM student WHERE email = ?");
        $stmt->execute([$email]);
        $student = $stmt->fetch();

        if ($student && password_verify($password, $student['password'])) {
            $_SESSION['student_id'] = $student['student_id'];
            $_SESSION['student_name'] = $student['name'];
            header("Location: student/dashboard.php");
            exit();
        } else {
            $error = "Invalid email or password.";
        }
    } else {
        $username = clean($_POST['username']);
        $password = $_POST['password'];

        $stmt = $pdo->prepare("SELECT * FROM admin WHERE username = ?");
        $stmt->execute([$username]);
        $admin = $stmt->fetch();

        if ($admin && password_verify($password, $admin['password'])) {
            $_SESSION['admin_id'] = $admin['admin_id'];
            $_SESSION['admin_username'] = $admin['username'];
            header("Location: admin/dashboard.php");
            exit();
        } else {
            $error = "Invalid admin username or password.";
        }
    }
}

$pageTitle = "Login";
$basePath = "";
include 'includes/header.php';
?>

<div class="container">
  <div class="card auth-card p-4">
    <ul class="nav nav-pills nav-justified mb-4" id="loginTabs">
      <li class="nav-item">
        <button class="nav-link <?php echo $activeTab==='student' ? 'active' : ''; ?>" data-bs-toggle="pill" data-bs-target="#studentPane">Student Login</button>
      </li>
      <li class="nav-item">
        <button class="nav-link <?php echo $activeTab==='admin' ? 'active' : ''; ?>" data-bs-toggle="pill" data-bs-target="#adminPane">Admin Login</button>
      </li>
    </ul>

    <?php if ($error): ?>
      <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <div class="tab-content">
      <div class="tab-pane fade <?php echo $activeTab==='student' ? 'show active' : ''; ?>" id="studentPane">
        <form method="POST">
          <input type="hidden" name="login_type" value="student">
          <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" required>
          </div>
          <button type="submit" class="btn btn-accent w-100">Login as Student</button>
        </form>
      </div>

      <div class="tab-pane fade <?php echo $activeTab==='admin' ? 'show active' : ''; ?>" id="adminPane">
        <form method="POST">
          <input type="hidden" name="login_type" value="admin">
          <div class="mb-3">
            <label class="form-label">Username</label>
            <input type="text" name="username" class="form-control" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" required>
          </div>
          <button type="submit" class="btn btn-accent w-100">Login as Admin</button>
        </form>
        <small class="text-muted d-block mt-2">Default: admin / Admin@123</small>
      </div>
    </div>
    <p class="text-center mt-3">New student? <a href="register.php">Register here</a></p>
  </div>
</div>

<?php include 'includes/footer.php'; ?>
