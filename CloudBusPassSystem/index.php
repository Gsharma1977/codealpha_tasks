<?php
session_start();
$pageTitle = "Home";
$basePath = "";
include 'includes/header.php';
?>

<section class="hero-section text-center">
  <div class="container">
    <h1 class="display-5">Cloud-Based Bus Pass Management System</h1>
    <p class="lead mt-3">Apply, track, and manage your student bus pass online — no queues, no paper, no lost passes.</p>
    <a href="register.php" class="btn btn-accent btn-lg me-2 mt-3">Get Started</a>
    <a href="login.php" class="btn btn-outline-light btn-lg mt-3">Login</a>
  </div>
</section>

<section id="about" class="container py-5">
  <h2 class="section-title text-center">About the System</h2>
  <p class="text-center mx-auto" style="max-width:750px;">
    Traditional paper-based bus passes are prone to loss, forgery, manual verification delays, and human error
    in fare calculation. This system digitizes the entire bus pass lifecycle — application, fare calculation,
    approval, issuance, and renewal — through a secure, centralized, cloud-hosted platform accessible to
    students and administrators from any browser.
  </p>
</section>

<section id="features" class="container py-5">
  <h2 class="section-title text-center">Key Features</h2>
  <div class="row g-4">
    <div class="col-md-4">
      <div class="card card-feature p-4 text-center">
        <i class="bi bi-lightning-charge fs-1 text-primary"></i>
        <h5 class="mt-3">Automatic Fare Calculation</h5>
        <p>Fare is calculated instantly based on distance slabs — no manual entry, no errors.</p>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card card-feature p-4 text-center">
        <i class="bi bi-shield-check fs-1 text-primary"></i>
        <h5 class="mt-3">Duplicate Prevention</h5>
        <p>The system blocks duplicate applications using Student ID, email, and phone checks.</p>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card card-feature p-4 text-center">
        <i class="bi bi-qr-code fs-1 text-primary"></i>
        <h5 class="mt-3">Digital Pass with QR Code</h5>
        <p>Every approved pass is issued digitally with a scannable QR code for quick verification.</p>
      </div>
    </div>
  </div>
</section>

<section id="services" class="container py-5">
  <h2 class="section-title text-center">Services</h2>
  <div class="row g-4">
    <div class="col-md-3 col-6 text-center">
      <i class="bi bi-person-plus fs-2 text-primary"></i>
      <p class="mt-2">Online Registration</p>
    </div>
    <div class="col-md-3 col-6 text-center">
      <i class="bi bi-file-earmark-text fs-2 text-primary"></i>
      <p class="mt-2">Pass Application</p>
    </div>
    <div class="col-md-3 col-6 text-center">
      <i class="bi bi-clipboard-check fs-2 text-primary"></i>
      <p class="mt-2">Admin Approval</p>
    </div>
    <div class="col-md-3 col-6 text-center">
      <i class="bi bi-bar-chart fs-2 text-primary"></i>
      <p class="mt-2">Reports & Analytics</p>
    </div>
  </div>
</section>

<section id="contact" class="container py-5">
  <h2 class="section-title text-center">Contact Us</h2>
  <div class="mx-auto" style="max-width:500px;">
    <p class="text-center">Have questions about your bus pass? Reach out to the transport office.</p>
    <p class="text-center mb-0"><i class="bi bi-envelope"></i> support@campusbuspass.edu</p>
    <p class="text-center"><i class="bi bi-telephone"></i> +91-98765-43210</p>
  </div>
</section>

<?php include 'includes/footer.php'; ?>
