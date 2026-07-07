<?php
/**
 * Database Connection File
 * Uses PDO with prepared-statement support (prevents SQL Injection)
 */

$DB_HOST = "localhost";
$DB_NAME = "bus_pass_system";
$DB_USER = "root";
$DB_PASS = "";   // default XAMPP MySQL password is empty

try {
    $pdo = new PDO(
        "mysql:host=$DB_HOST;port=3307;dbname=$DB_NAME;charset=utf8mb4",
        $DB_USER,
        $DB_PASS,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]
    );
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}
