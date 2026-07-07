-- ================================================================
-- SAMPLE DATA – 30 records with mixed classifications
-- Run AFTER schema.sql
-- ================================================================
USE data_redundancy_db;

-- Unique records
INSERT IGNORE INTO records (record_id,name,email,phone,department,address,status) VALUES
('REC001','Aarav Sharma','aarav.sharma@gmail.com','9876543210','Computer Science','12 MG Road, Delhi','UNIQUE'),
('REC002','Priya Singh','priya.singh@yahoo.com','9812345678','Electronics','45 Park Street, Mumbai','UNIQUE'),
('REC003','Rahul Kumar','rahul.kumar@hotmail.com','9823456789','Mechanical','78 Lake View, Pune','UNIQUE'),
('REC004','Sneha Patel','sneha.patel@gmail.com','9834567890','Civil','23 Gandhi Nagar, Ahmedabad','UNIQUE'),
('REC005','Vikram Rao','vikram.rao@outlook.com','9845678901','HR','56 Banjara Hills, Hyderabad','UNIQUE'),
('REC006','Ananya Gupta','ananya.gupta@gmail.com','9856789012','Finance','89 Anna Nagar, Chennai','UNIQUE'),
('REC007','Arjun Verma','arjun.verma@rediffmail.com','9867890123','Marketing','34 Koramangala, Bangalore','UNIQUE'),
('REC008','Deepika Nair','deepika.nair@gmail.com','9878901234','Computer Science','67 Marine Drive, Kochi','UNIQUE'),
('REC009','Karan Mehta','karan.mehta@yahoo.com','9889012345','Electronics','12 Salt Lake, Kolkata','UNIQUE'),
('REC010','Meera Joshi','meera.joshi@gmail.com','9890123456','Mechanical','45 Civil Lines, Jaipur','UNIQUE'),
('REC011','Rohan Desai','rohan.desai@gmail.com','9901234567','Civil','78 Aundh, Pune','UNIQUE'),
('REC012','Pooja Iyer','pooja.iyer@outlook.com','9912345678','HR','23 T Nagar, Chennai','UNIQUE'),
('REC013','Suresh Reddy','suresh.reddy@gmail.com','9923456789','Finance','56 Jubilee Hills, Hyderabad','UNIQUE'),
('REC014','Kavya Pillai','kavya.pillai@yahoo.com','9934567890','Marketing','89 Indiranagar, Bangalore','UNIQUE'),
('REC015','Neeraj Malhotra','neeraj.malhotra@gmail.com','9945678901','Computer Science','34 Connaught Place, Delhi','UNIQUE'),

-- Potential duplicates (similar names)
('REC016','Rahul Kumer','rahulkumer99@gmail.com','9956789012','Electronics','78 Shivaji Nagar, Pune','POTENTIAL_DUPLICATE'),
('REC017','Rahul Kumar Sharma','rahul.k.sharma@gmail.com','9967890123','Mechanical','12 Deccan, Pune','POTENTIAL_DUPLICATE'),
('REC018','Jon Smith','jon.smith@gmail.com','9978901234','HR','45 Baker St, Mumbai','POTENTIAL_DUPLICATE'),
('REC019','Priya Sing','priya.sing@yahoo.com','9989012345','Finance','56 Andheri, Mumbai','POTENTIAL_DUPLICATE'),
('REC020','Aarav Sharman','aaravsharman@gmail.com','9990123456','Marketing','23 Lajpat Nagar, Delhi','POTENTIAL_DUPLICATE'),

-- False positives (looked similar but are different people)
('REC021','Vikram Rajan','vikram.rajan@gmail.com','8901234567','Computer Science','78 Velachery, Chennai','FALSE_POSITIVE'),
('REC022','Anjali Nair','anjali.nair@yahoo.com','8912345678','Electronics','34 Kakkanad, Kochi','FALSE_POSITIVE'),

-- More unique records
('REC023','Ishaan Trivedi','ishaan.trivedi@gmail.com','8923456789','Mechanical','56 Bhopal Nagar, Bhopal','UNIQUE'),
('REC024','Tanya Chopra','tanya.chopra@hotmail.com','8934567890','Civil','89 Sadar, Nagpur','UNIQUE'),
('REC025','Yash Agarwal','yash.agarwal@gmail.com','8945678901','HR','12 Sector 18, Noida','UNIQUE'),
('REC026','Riya Mishra','riya.mishra@yahoo.com','8956789012','Finance','45 Hazratganj, Lucknow','UNIQUE'),
('REC027','Harsh Pandey','harsh.pandey@gmail.com','8967890123','Marketing','78 Boring Road, Patna','UNIQUE'),
('REC028','Simran Kaur','simran.kaur@outlook.com','8978901234','Computer Science','23 Model Town, Amritsar','UNIQUE'),
('REC029','Aditya Jain','aditya.jain@gmail.com','8989012345','Electronics','56 Raja Park, Jaipur','UNIQUE'),
('REC030','Neha Tiwari','neha.tiwari@yahoo.com','8990123456','Mechanical','89 Shyam Nagar, Kanpur','UNIQUE');
