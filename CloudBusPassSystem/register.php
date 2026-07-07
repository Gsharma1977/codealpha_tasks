<?php
session_start();
require 'includes/db.php';
require 'includes/functions.php';

$errors = [];
$success = false;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $student_id   = clean($_POST['student_id']);
    $name         = clean($_POST['name']);
    $email        = clean($_POST['email']);
    $phone        = clean($_POST['phone']);
    $college      = clean($_POST['college_name']);
    $password     = $_POST['password'];
    $confirm      = $_POST['confirm_password'];

    // ---- Required field validation ----
    if (empty($student_id) || empty($name) || empty($email) || empty($phone) || empty($college) || empty($password)) {
        $errors[] = "All fields are required.";
    }
    // ---- Email validation ----
    if (!isValidEmail($email)) {
        $errors[] = "Invalid email address.";
    }
    // ---- Phone validation ----
    if (!isValidPhone($phone)) {
        $errors[] = "Phone number must be a valid 10-digit Indian mobile number.";
    }
    // ---- Password confirmation ----
    if ($password !== $confirm) {
        $errors[] = "Passwords do not match.";
    }
    if (strlen($password) < 6) {
        $errors[] = "Password must be at least 6 characters long.";
    }

    // ---- Duplicate Student ID / Email detection ----
    if (empty($errors)) {
        $stmt = $pdo->prepare("SELECT student_id FROM student WHERE student_id = ? OR email = ? OR phone = ?");
        $stmt->execute([$student_id, $email, $phone]);
        if ($stmt->fetch()) {
            $errors[] = "A student with this Student ID, Email, or Phone already exists.";
        }
    }

    // ---- Insert into database ----
    if (empty($errors)) {
        $hashedPassword = password_hash($password, PASSWORD_BCRYPT);
        $stmt = $pdo->prepare("INSERT INTO student (student_id, name, email, phone, college_name, password) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->execute([$student_id, $name, $email, $phone, $college, $hashedPassword]);
        $success = true;
    }
}

$pageTitle = "Register";
$basePath = "";
include 'includes/header.php';
?>

<div class="container">
  <div class="card auth-card p-4">
    <h3 class="text-center mb-4">Student Registration</h3>

    <?php if ($success): ?>
      <div class="alert alert-success">Registration successful! You can now <a href="login.php">login</a>.</div>
    <?php endif; ?>

    <?php if (!empty($errors)): ?>
      <div class="alert alert-danger">
        <ul class="mb-0">
          <?php foreach ($errors as $e) echo "<li>" . htmlspecialchars($e) . "</li>"; ?>
        </ul>
      </div>
    <?php endif; ?>

    <form method="POST" novalidate>
      <div class="mb-3">
        <label class="form-label">Student ID</label>
        <input type="text" name="student_id" class="form-control" required value="<?php echo $_POST['student_id'] ?? ''; ?>">
      </div>
      <div class="mb-3">
        <label class="form-label">Full Name</label>
        <input type="text" name="name" class="form-control" required value="<?php echo $_POST['name'] ?? ''; ?>">
      </div>
      <div class="mb-3">
        <label class="form-label">Email</label>
        <input type="email" name="email" class="form-control" required value="<?php echo $_POST['email'] ?? ''; ?>">
      </div>
      <div class="mb-3">
        <label class="form-label">Phone Number</label>
        <input type="text" name="phone" class="form-control" maxlength="10" required value="<?php echo $_POST['phone'] ?? ''; ?>">
      </div>
      <div class="mb-3">
        <label class="form-label">College Name</label>
        <input type="text" name="college_name" class="form-control" required value="<?php echo $_POST['college_name'] ?? ''; ?>">
      </div>
      <div class="mb-3">
        <label class="form-label">Password</label>
        <input type="password" name="password" class="form-control" required minlength="6">
      </div>
      <div class="mb-3">
        <label class="form-label">Confirm Password</label>
        <input type="password" name="confirm_password" class="form-control" required minlength="6">
      </div>
      <button type="submit" class="btn btn-accent w-100">Register</button>
    </form>
    <p class="text-center mt-3">Already have an account? <a href="login.php">Login here</a></p>
  </div>
</div>

<?php include 'includes/footer.php'; ?>
