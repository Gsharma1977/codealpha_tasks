package models;

import java.sql.Timestamp;

/** ValidationLog Model - stores per-field validation results. */
public class ValidationLog {
    private int       logId;
    private String    recordId;
    private String    fieldName;
    private String    validationResult; // PASS | FAIL
    private String    message;
    private Timestamp checkedAt;

    public ValidationLog() {}
    public ValidationLog(String recordId, String fieldName, String result, String message) {
        this.recordId         = recordId;
        this.fieldName        = fieldName;
        this.validationResult = result;
        this.message          = message;
    }

    public int       getLogId()            { return logId;            }
    public void      setLogId(int logId)   { this.logId = logId;      }
    public String    getRecordId()         { return recordId;         }
    public void      setRecordId(String r) { this.recordId = r;       }
    public String    getFieldName()        { return fieldName;        }
    public void      setFieldName(String f){ this.fieldName = f;      }
    public String    getValidationResult() { return validationResult; }
    public void      setValidationResult(String v){ this.validationResult = v; }
    public String    getMessage()          { return message;          }
    public void      setMessage(String m)  { this.message = m;        }
    public Timestamp getCheckedAt()        { return checkedAt;        }
    public void      setCheckedAt(Timestamp t){ this.checkedAt = t;   }
}
