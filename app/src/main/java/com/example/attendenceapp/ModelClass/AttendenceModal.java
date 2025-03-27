package com.example.attendenceapp.ModelClass;

public class AttendenceModal {
    private String studentId;
    private String subjectCode;
    private String semester;
    private String markedDate;
    private String markedTime;
    private String section;
    private boolean isPresent;  // false by default (Absent)

    // Empty constructor for Firestore
    public AttendenceModal() {}

    // Constructor with all required fields
    public AttendenceModal(String studentId, String subjectCode, String semester, String markedDate, String markedTime, String section, boolean isPresent) {
        this.studentId = studentId;
        this.subjectCode = subjectCode;
        this.semester = semester;
        this.markedDate = markedDate;
        this.markedTime = markedTime;
        this.section = section;
        this.isPresent = isPresent;
    }

    // Getters and setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getMarkedDate() {
        return markedDate;
    }

    public void setMarkedDate(String markedDate) {
        this.markedDate = markedDate;
    }

    public String getMarkedTime() {
        return markedTime;
    }

    public void setMarkedTime(String markedTime) {
        this.markedTime = markedTime;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public boolean isPresent() {
        return isPresent;
    }


    public void setPresent(boolean present) {
        this.isPresent = present;
    }
}
