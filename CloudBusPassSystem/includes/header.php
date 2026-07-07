<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><?php echo isset($pageTitle) ? $pageTitle . " - Cloud Bus Pass System" : "Cloud Bus Pass System"; ?></title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
<link href="<?php echo isset($basePath) ? $basePath : ''; ?>assets/css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-custom">
  <div class="container">
    <a class="navbar-brand" href="<?php echo isset($basePath) ? $basePath : ''; ?>index.php">🚌 Cloud Bus Pass</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMenu">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>index.php#about">About</a></li>
        <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>index.php#features">Features</a></li>
        <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>index.php#contact">Contact</a></li>
        <?php if (isset($_SESSION['student_id'])): ?>
          <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>student/dashboard.php">Dashboard</a></li>
          <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>logout.php">Logout</a></li>
        <?php elseif (isset($_SESSION['admin_id'])): ?>
          <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>admin/dashboard.php">Admin Panel</a></li>
          <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>logout.php">Logout</a></li>
        <?php else: ?>
          <li class="nav-item"><a class="nav-link" href="<?php echo isset($basePath) ? $basePath : ''; ?>login.php">Login</a></li>
          <li class="nav-item"><a class="nav-link btn btn-accent text-white ms-2 px-3" href="<?php echo isset($basePath) ? $basePath : ''; ?>register.php">Register</a></li>
        <?php endif; ?>
      </ul>
    </div>
  </div>
</nav>
