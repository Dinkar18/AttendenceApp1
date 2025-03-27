package com.example.attendenceapp.ModelClass;

public class StudentAttendanceEntryModal {
    private String date;
    private String time;
    private String subjectId;
    private String status;

    public StudentAttendanceEntryModal(String date, String time, String subjectId, String status) {
        this.date = date;
        this.time = time;
        this.subjectId = subjectId;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getStatus() {
        return status;
    }
}
