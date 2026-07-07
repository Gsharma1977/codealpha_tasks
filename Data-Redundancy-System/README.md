# 🛡️ Data Redundancy Removal and Validation System
### Internship Project | Java + JDBC + MySQL | 3rd Year B.Tech

---

## 📁 Project Structure

```
Data-Redundancy-System/
│
├── src/
│   ├── database/
│   │   └── DatabaseConnection.java     ← JDBC singleton connection
│   ├── models/
│   │   ├── Record.java                 ← Record POJO
│   │   ├── Admin.java                  ← Admin POJO
│   │   ├── ValidationLog.java          ← Validation log POJO
│   │   └── DuplicateLog.java           ← Duplicate log POJO
│   ├── dao/
│   │   ├── RecordDAO.java              ← SQL for records table
│   │   ├── AdminDAO.java               ← SQL for admin table
│   │   ├── ValidationLogDAO.java       ← SQL for validation_log
│   │   └── DuplicateLogDAO.java        ← SQL for duplicate_log
│   ├── service/
│   │   ├── ValidationService.java      ← Field validation logic
│   │   ├── DuplicateDetectionService.java ← 3-level detection
│   │   └── ReportService.java          ← Report generation
│   ├── utility/
│   │   └── LevenshteinDistanceUtil.java ← String similarity
│   └── ui/
│       ├── Main.java                   ← Entry point
│       └── SimpleHttpServer.java       ← Built-in HTTP server
│
├── web/
│   ├── login.html                      ← Login page
│   ├── dashboard.html                  ← Dashboard
│   ├── add-record.html                 ← Data entry form
│   ├── records.html                    ← View all records
│   └── reports.html                    ← Reports viewer
│
├── sql/
│   ├── schema.sql                      ← Table creation scripts
│   └── sample_data.sql                 ← 30 sample records
│
├── lib/                                ← Put MySQL connector JAR here
├── out/                                ← Compiled .class files (auto-created)
├── compile.sh / compile.bat            ← Compilation scripts
├── run.sh / run.bat                    ← Run scripts
└── README.md
```

---

## 🚀 HOW TO RUN ON YOUR SYSTEM (Step by Step)

### STEP 1: Install Prerequisites

1. **Java JDK 11+**
   - Download: https://adoptium.net
   - Verify: open CMD/Terminal → type `java -version`

2. **MySQL 8.0**
   - Download: https://dev.mysql.com/downloads/mysql/
   - During install, set root password (remember it!)
   - Verify: `mysql --version`

3. **MySQL Workbench** (GUI for database)
   - Download: https://dev.mysql.com/downloads/workbench/

4. **MySQL JDBC Connector JAR**
   - Download: https://dev.mysql.com/downloads/connector/j/
   - Choose "Platform Independent" → download ZIP
   - Extract and find `mysql-connector-j-8.x.x.jar`

---

### STEP 2: Setup the Database

**Option A – Using MySQL Workbench:**
1. Open MySQL Workbench
2. Connect to localhost (root / your-password)
3. File → Open SQL Script → select `sql/schema.sql` → Run (⚡)
4. File → Open SQL Script → select `sql/sample_data.sql` → Run (⚡)

**Option B – Using Command Line:**
```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/sample_data.sql
```

---

### STEP 3: Configure Database Password

Open `src/database/DatabaseConnection.java` and change line:
```java
private static final String PASSWORD = "root";  // ← your MySQL password
```

---

### STEP 4: Add JDBC Driver

1. Create a folder named `lib` inside the project root
2. Copy `mysql-connector-j-8.x.x.jar` into the `lib/` folder
3. Rename it if needed in compile.bat/compile.sh to match exact filename

---

### STEP 5: Compile the Project

**On Windows (CMD):**
```
cd Data-Redundancy-System
compile.bat
```

**On Mac/Linux (Terminal):**
```bash
cd Data-Redundancy-System
chmod +x compile.sh
bash compile.sh
```

You should see: `✅ Compilation successful!`

---

### STEP 6: Run the Server

**Windows:**
```
run.bat
```

**Mac/Linux:**
```bash
bash run.sh
```

Output:
```
=================================================
 Data Redundancy System Server Started
 Open in browser: http://localhost:8080/web/login.html
=================================================
```

---

### STEP 7: Open in Browser

Go to: **http://localhost:8080/web/login.html**

- Username: `admin`
- Password: `admin123`

---

## 🔧 Using IntelliJ IDEA (Recommended IDE)

1. Open IntelliJ IDEA
2. File → Open → select `Data-Redundancy-System` folder
3. Right-click `src` folder → Mark Directory as → Sources Root
4. File → Project Structure → Modules → Dependencies → Add JAR → select `lib/mysql-connector-j-8.x.x.jar`
5. Find `Main.java` → right-click → Run 'Main.main()'

---

## 🔧 Using Eclipse

1. File → New → Java Project → Project name: `Data-Redundancy-System`
2. Right-click project → Build Path → Add External Archives → select the JAR
3. Copy all `src/` files maintaining package structure
4. Right-click `Main.java` → Run As → Java Application

---

## 🌐 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/login | Admin login |
| GET | /api/records | Get all records |
| GET | /api/records?status=DUPLICATE | Filter by status |
| POST | /api/records | Add new record |
| DELETE | /api/records/{id} | Delete record |
| PUT | /api/records/{id}/false-positive | Mark as false positive |
| GET | /api/dashboard | Dashboard stats |
| GET | /api/reports/summary | Summary report |
| GET | /api/reports/duplicate | Duplicate report |
| GET | /api/reports/validation | Validation report |

---

## 🧠 Algorithm Summary

### Levenshtein Distance
- Computes minimum edits (insert/delete/replace) between two strings
- "Rahul Kumar" vs "Rahul Kumer" → 1 edit → 91% similar
- Threshold: 70% similarity = POTENTIAL_DUPLICATE
- Time Complexity: O(m × n)

### Detection Levels
1. **LEVEL 1 – Exact**: Same email/phone/ID → DUPLICATE (blocked)
2. **LEVEL 2 – Partial**: Name contains existing name + same phone → POTENTIAL_DUPLICATE
3. **LEVEL 3 – Similar**: Levenshtein ≥ 70% → POTENTIAL_DUPLICATE

---

## 📊 Record Status Meanings

| Status | Meaning |
|--------|---------|
| UNIQUE | New, verified record. Saved to DB. |
| DUPLICATE | Exact match found. BLOCKED from DB. |
| POTENTIAL_DUPLICATE | Suspicious match. Saved but flagged for admin review. |
| FALSE_POSITIVE | Admin confirmed it's actually a different person. |

---

## 👨‍💻 Tech Stack

- Java 11+ (Core Java, no frameworks)
- JDBC (database connectivity)
- MySQL 8.0
- HTML5 + CSS3 + JavaScript
- Bootstrap 5.3
- com.sun.net.httpserver (built-in Java HTTP server)

