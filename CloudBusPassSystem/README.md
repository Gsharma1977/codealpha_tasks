# Cloud-Based Bus Pass Management System

A full-stack internship project built with **HTML5, CSS3, Bootstrap 5, JavaScript, PHP 8, and MySQL**.
Students apply for bus passes online; fare is calculated automatically; admins approve/reject
applications; approved students get a digital pass with a QR code.

---

## 1. Folder Structure

```
CloudBusPassSystem/
├── assets/
│   ├── css/style.css        # Global styling
│   ├── js/                  # (reserved for future JS assets)
│   └── images/
├── admin/
│   ├── dashboard.php        # Stats overview
│   ├── applications.php     # List + approve/reject entry point
│   ├── approve_reject.php   # Approval algorithm, pass + QR generation
│   ├── manage_passes.php    # Cancel passes, auto-expire
│   └── reports.php          # Daily/monthly/revenue reports
├── student/
│   ├── dashboard.php        # Student's pass status & profile
│   ├── apply_pass.php       # Application + live fare calculator
│   └── view_pass.php        # Digital pass + QR code, printable
├── includes/
│   ├── db.php                # PDO DB connection
│   ├── functions.php         # Fare calculator, duplicate checker, sanitizers
│   ├── auth_student.php      # Session guard for student pages
│   ├── auth_admin.php        # Session guard for admin pages
│   ├── header.php / footer.php
├── sql/
│   ├── database.sql          # Schema + default admin
│   └── sample_data.sql       # 100 students + 100 applications (test data)
├── uploads/                  # reserved for future file uploads
├── reports/                  # reserved for exported reports
├── index.php                 # Landing page (Module 1)
├── register.php              # Student registration (Module 2)
├── login.php                 # Unified student/admin login (Module 3)
└── logout.php
```

---

## 2. How to Run on Your System (XAMPP)

**Step 1 — Install XAMPP**
Download from https://www.apachefriends.org and install Apache + MySQL + PHP.

**Step 2 — Copy the project**
Copy the `CloudBusPassSystem` folder into your XAMPP `htdocs` directory:
- Windows: `C:\xampp\htdocs\CloudBusPassSystem`
- Mac/Linux: `/Applications/XAMPP/htdocs/CloudBusPassSystem` or `/opt/lampp/htdocs/CloudBusPassSystem`

**Step 3 — Start services**
Open the XAMPP Control Panel and click **Start** on both **Apache** and **MySQL**.

**Step 4 — Create the database**
1. Go to `http://localhost/phpmyadmin`
2. Click **Import** → choose `sql/database.sql` → click **Go**
   (This creates the `bus_pass_system` database, all tables, and the default admin account.)
3. Import `sql/sample_data.sql` the same way if you want 100 ready-made test students/applications.

**Step 5 — Configure DB credentials (if needed)**
Default XAMPP MySQL has user `root` with **no password**, which already matches
`includes/db.php`. If your MySQL has a password, edit `includes/db.php`:
```php
$DB_USER = "root";
$DB_PASS = "your_password";
```

**Step 6 — Run the project**
Open your browser and go to:
```
http://localhost/CloudBusPassSystem/
```

**Step 7 — Test logins**
- **Admin:** username `admin`, password `Admin@123`
- **Sample student** (if you imported sample_data.sql): email `student1@college.edu`, password `Student@123`
- Or register a brand-new student via the Register page.

---

## 3. Core Algorithms

### Fare Calculation (O(1))
| Distance | Fare (Monthly) |
|---|---|
| 0–10 km | ₹300 |
| 11–20 km | ₹500 |
| 21–40 km | ₹700 |
| >40 km | ₹900 |
Quarterly = Monthly slab fare × 3 × 0.9 (10% bulk discount). Calculated **server-side** in
`includes/functions.php::calculateFare()` — the client-side preview in `apply_pass.php` is
cosmetic only and cannot be used to tamper with the stored fare.

### Duplicate Detection (O(1) indexed lookup)
Before an application is accepted, `hasActivePassOrPending()` checks the `buspass` and
`application` tables by `student_id`, `email`, and `phone` (all indexed columns) to block:
- A second application while one is already **Pending**
- A new application while the student holds a still-valid **Active** pass

### Pass Approval Algorithm
1. Verify the application is `Pending`
2. On **Approve**: set status → `Approved`, compute `issue_date`/`expiry_date`
   (30 days for Monthly, 90 for Quarterly), build the QR payload, insert into `buspass`
   and `payment` (as a DB transaction so it's all-or-nothing)
3. On **Reject**: set status → `Rejected`

### QR Code Generation
The server builds a plain-text payload (`buildQrPayload()` in `functions.php`) containing the
pass ID, student ID, name, and expiry date, and stores it in `buspass.qr_code_data`. The
**qrcode.js** library (loaded via CDN in `view_pass.php`) renders this text as a scannable QR
image client-side — no server-side image processing needed, which keeps the project simple
for XAMPP.

---

## 4. Database Design (Summary)

| Table | Purpose | Key Constraints |
|---|---|---|
| `admin` | Admin login | `username` UNIQUE |
| `student` | Registered students | `student_id` PK, `email`/`phone` UNIQUE |
| `application` | Pass applications | FK → student, `status` ENUM |
| `buspass` | Issued digital passes | FK → application (UNIQUE), FK → student |
| `payment` | Fare payment tracking | FK → application, student |
| `report_summary` (VIEW) | Precomputed dashboard stats | — |

Full DDL with all keys/indexes is in `sql/database.sql`.

---

## 5. Security Features Implemented

- Passwords hashed with `password_hash()` (bcrypt) — never stored in plain text
- All SQL queries use **PDO prepared statements** (SQL-injection safe)
- Server-side validation for every form (never trust client-side JS alone)
- `htmlspecialchars()` output escaping to prevent XSS
- Session-based auth with separate guards for student/admin areas
- Fare is recalculated server-side on every submission (can't be spoofed)
- Duplicate-application checks run server-side before any insert

---

## 6. Deploying to the Cloud (Free Hosting)

Once it works on localhost, "cloud-based" is satisfied by hosting it publicly:

1. **Choose a free PHP+MySQL host** — e.g. InfinityFree, 000webhost, or a free tier on
   Railway/Render (for PHP) — anything that gives you PHP 8 + MySQL.
2. **Export your database:** phpMyAdmin → Export → SQL (or just re-use `sql/database.sql`).
3. **Create a MySQL database** on the host's control panel; note the new host, DB name,
   username, and password they assign you.
4. **Update `includes/db.php`** with those new credentials.
5. **Upload all files** via the host's File Manager or FTP (e.g. FileZilla) into the
   public/`htdocs`/`public_html` folder.
6. **Import your SQL** into the new remote database via their phpMyAdmin.
7. Visit your new public URL — the same login/register/apply/approve flow now works from
   anywhere, on any device, which is what makes it "cloud-based" versus a manual/local system.

**Concepts to explain in viva:**
- *Localhost* = app runs only on your machine (127.0.0.1)
- *Cloud/shared hosting* = your app + DB run on a remote server, reachable via public internet
- *Scalability* = the host can add resources (or you can migrate to a bigger plan) as more
  students use the system, unlike a single manual ledger/counter
- *Reliability* = data lives in a managed MySQL instance with backups, instead of paper
  registers that can be lost or damaged

---

## 7. Uploading to GitHub

```bash
cd CloudBusPassSystem
git init
git add .
git commit -m "Initial commit: Cloud-Based Bus Pass Management System"
git branch -M main
git remote add origin https://github.com/<your-username>/CloudBusPassSystem.git
git push -u origin main
```
Add a `.gitignore` excluding any local config files if you later add environment secrets.

---

## 8. Suggested Next Steps / Future Scope

- Email/SMS notification on approval or rejection
- Online payment gateway integration (Razorpay/Stripe test mode) to move `payment.payment_status` to Paid automatically
- Pass renewal reminders before expiry
- Admin ability to bulk-import routes/fare slabs from a config table instead of hardcoded values
- Mobile app wrapper (WebView) for students

---

## 9. Viva-Ready Talking Points

- **Why PDO over mysqli?** PDO supports prepared statements cleanly and is DB-agnostic.
- **Why hash passwords with bcrypt?** It's adaptive (slow-by-design) and salted automatically, resisting brute-force/rainbow-table attacks — unlike MD5/SHA1.
- **Why block manual fare edits?** Prevents students from tampering with form data (e.g. via browser dev tools) to underpay.
- **How are duplicates prevented at the DB level, not just the UI?** UNIQUE constraints on `email`/`phone`/`student_id`, plus an application-layer check before insert — defense in depth.
- **What makes this "cloud-based" and not just a local PHP app?** Deploying the same LAMP-stack code to a remote host with a managed MySQL database, accessible over the internet from any device.
