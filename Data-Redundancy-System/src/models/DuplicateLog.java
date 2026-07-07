package models;

import java.sql.Timestamp;

/** DuplicateLog Model - stores duplicate detection results. */
public class DuplicateLog {
    private int       duplicateId;
    private String    recordId;
    private String    matchedWith;
    private String    reason;
    private double    similarityScore;
    private Timestamp detectedAt;

    public DuplicateLog() {}
    public DuplicateLog(String recordId, String matchedWith, String reason, double score) {
        this.recordId        = recordId;
        this.matchedWith     = matchedWith;
        this.reason          = reason;
        this.similarityScore = score;
    }

    public int       getDuplicateId()          { return duplicateId;    }
    public void      setDuplicateId(int id)    { this.duplicateId = id; }
    public String    getRecordId()             { return recordId;       }
    public void      setRecordId(String r)     { this.recordId = r;     }
    public String    getMatchedWith()          { return matchedWith;    }
    public void      setMatchedWith(String m)  { this.matchedWith = m;  }
    public String    getReason()               { return reason;         }
    public void      setReason(String r)       { this.reason = r;       }
    public double    getSimilarityScore()      { return similarityScore;}
    public void      setSimilarityScore(double s){ this.similarityScore = s; }
    public Timestamp getDetectedAt()           { return detectedAt;     }
    public void      setDetectedAt(Timestamp t){ this.detectedAt = t;   }
}
