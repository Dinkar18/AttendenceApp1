package com.example.attendenceapp.ModelClass;

public class SubjectModal {

    private String subjectName;
    private String subjectCode;

    public SubjectModal() {
        // Empty constructor required for Firestore
    }

    public SubjectModal(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}
