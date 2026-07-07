# 🎓 Viva Preparation – Data Redundancy System

## 30 Important Viva Q&A

**Q1. What is data redundancy?**
Data redundancy means the same data is stored more than once. It wastes storage, creates inconsistencies, and reduces data quality.

**Q2. How does your system detect duplicates?**
Three levels: Level 1 checks exact matches (email/phone/ID). Level 2 checks name containment. Level 3 uses Levenshtein Distance for name similarity ≥ 70%.

**Q3. What is the Levenshtein Distance algorithm?**
Minimum single-character edits (insert/delete/replace) to convert one string to another. "Rahul Kumar" → "Rahul Kumer" = 1 edit = 91% similar. Time complexity: O(m×n).

**Q4. What is JDBC?**
Java Database Connectivity — API for Java to talk to databases. Uses Connection, PreparedStatement, ResultSet classes.

**Q5. Why PreparedStatement over Statement?**
Prevents SQL Injection. Pre-compiled for better performance. Parameters passed separately from query.

**Q6. What design patterns did you use?**
Singleton (DatabaseConnection), DAO (RecordDAO, AdminDAO), Service Layer (ValidationService), MVC (Model-View-Controller separation).

**Q7. What are the 4 classifications?**
UNIQUE (saved), DUPLICATE (blocked), POTENTIAL_DUPLICATE (saved, flagged), FALSE_POSITIVE (admin-reviewed).

**Q8. What validations do you perform?**
Required fields, email format regex, 10-digit phone regex, duplicate email/phone/ID check.

**Q9. What is redundancy percentage?**
((Duplicates + Potential Duplicates) / Total) × 100

**Q10. How does your HTTP server work?**
Uses Java's built-in com.sun.net.httpserver.HttpServer — no Spring Boot needed. Routes mapped to handler classes.

**Q11. Why not save exact DUPLICATE records?**
Exact duplicates (same email/phone/ID) are definitely duplicates — saving them defeats the system's purpose.

**Q12. What is a Singleton pattern?**
Ensures only one instance of a class exists. Used for DatabaseConnection to avoid multiple DB connections.

**Q13. What is DAO pattern?**
Separates SQL logic from business logic. Service classes don't know how data is stored — they call DAO methods.

**Q14. Explain your database tables.**
admin (login), records (main data), validation_log (per-field validation results), duplicate_log (detection events).

**Q15. What constraints did you use in MySQL?**
PRIMARY KEY, UNIQUE (email, phone), FOREIGN KEY (duplicate_log.matched_with → records), INDEX (name, status).

**Q16. How does the frontend talk to the backend?**
JavaScript fetch() API sends HTTP requests to Java server. Server returns JSON. No page refresh needed.

**Q17. What is the difference between DELETE and FALSE_POSITIVE?**
DELETE removes permanently. FALSE_POSITIVE keeps record for audit trail — admin confirmed it's a different person.

**Q18. How would you improve this system?**
BCrypt password hashing, connection pooling (HikariCP), PDF report export, Spring Boot API, email notifications.

**Q19. What is an INDEX in MySQL?**
Speeds up SELECT queries. Added on frequently searched columns: name, status.

**Q20. What happens if two users submit the same record?**
MySQL UNIQUE constraint rejects the second insert at DB level, throwing SQLException which I handle gracefully.

---

## Resume Description

**ATS-friendly:**
• Built Data Redundancy Removal System using Core Java, JDBC, MySQL, Bootstrap 5
• Implemented 3-level duplicate detection: exact match, partial match, Levenshtein Distance (70% threshold)
• Created RESTful API with Java HttpServer — no external frameworks
• Designed normalized MySQL schema with 4 tables, constraints, and indexes
• Tech Stack: Java 11, JDBC, MySQL 8.0, HTML5, CSS3, JavaScript, Bootstrap 5, Git
