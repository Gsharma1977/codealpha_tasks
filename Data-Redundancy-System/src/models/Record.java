package models;

import java.sql.Timestamp;

/**
 * Record Model - represents one row in the 'records' table.
 * A Plain Old Java Object (POJO) with getters and setters.
 */
public class Record {

    private String    recordId;
    private String    name;
    private String    email;
    private String    phone;
    private String    department;
    private String    address;
    private String    status;       // UNIQUE | DUPLICATE | POTENTIAL_DUPLICATE | FALSE_POSITIVE
    private Timestamp dateCreated;

    // ── Constructors ───────────────────────────────────────────────
    public Record() {}

    public Record(String recordId, String name, String email,
                  String phone, String department, String address) {
        this.recordId   = recordId;
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.department = department;
        this.address    = address;
        this.status     = "UNIQUE";
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public String    getRecordId()    { return recordId;    }
    public void      setRecordId(String recordId)    { this.recordId = recordId; }

    public String    getName()        { return name;        }
    public void      setName(String name)            { this.name = name; }

    public String    getEmail()       { return email;       }
    public void      setEmail(String email)          { this.email = email; }

    public String    getPhone()       { return phone;       }
    public void      setPhone(String phone)          { this.phone = phone; }

    public String    getDepartment()  { return department;  }
    public void      setDepartment(String department){ this.department = department; }

    public String    getAddress()     { return address;     }
    public void      setAddress(String address)      { this.address = address; }

    public String    getStatus()      { return status;      }
    public void      setStatus(String status)        { this.status = status; }

    public Timestamp getDateCreated() { return dateCreated; }
    public void      setDateCreated(Timestamp dateCreated){ this.dateCreated = dateCreated; }

    @Override
    public String toString() {
        return String.format("Record{id='%s', name='%s', email='%s', phone='%s', status='%s'}",
                recordId, name, email, phone, status);
    }
}
