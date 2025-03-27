package com.example.attendenceapp.StudentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendenceapp.AdapterClass.Student_StudentAttendanceAdapter;
import com.example.attendenceapp.ModelClass.StudentAttendanceEntryModal;
import com.example.attendenceapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StudentAttendanceActivity extends AppCompatActivity {

    private RecyclerView attendanceRecyclerView;
    private Student_StudentAttendanceAdapter adapter;
    private FirebaseFirestore firestore;
    private List<StudentAttendanceEntryModal> attendanceList;
    private ImageView selectDateButton;

    private String studentId; // To hold the student ID passed from the fragment
    private String subjectCode; // To hold the subject code passed from the fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        attendanceRecyclerView = findViewById(R.id.studentAttendance);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceList = new ArrayList<>();
        adapter = new Student_StudentAttendanceAdapter(attendanceList);
        attendanceRecyclerView.setAdapter(adapter);

        // Initialize DatePicker Button
        selectDateButton = findViewById(R.id.date);

        // Get studentId and subjectCode from Intent
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        subjectCode = intent.getStringExtra("subjectCode");

        if (studentId == null || subjectCode == null) {
            Toast.makeText(this, "Student ID or Subject Code missing. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set onClickListener for DatePicker
        selectDateButton.setOnClickListener(view -> openDatePicker());
    }

    private void openDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the selected date as "yyyy-MM-dd"
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());

            // Fetch attendance records for the selected date
            fetchAttendanceRecords(formattedDate);
        }, year, month, day).show();
    }

    private void fetchAttendanceRecords(String date) {
        // Clear previous data
        attendanceList.clear();

        // Firestore reference
        CollectionReference attendanceRef = firestore.collection("Student_Attendance1")
                .document(studentId)
                .collection("attendance_data");

        // Query Firestore for the selected date and subjectCode
        attendanceRef.whereEqualTo("dateMarked", date)
                .whereEqualTo("subCode", subjectCode)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String subjectId = document.getString("subCode");
                        String time = document.getString("timeMarked");
                        String status = document.getBoolean("isPresent") ? "Present" : "Absent";

                        // Add to the list
                        attendanceList.add(new StudentAttendanceEntryModal(date, time, subjectId, status));
                    }

                    // Notify adapter
                    adapter.notifyDataSetChanged();

                    if (attendanceList.isEmpty()) {
                        Toast.makeText(this, "No records found for " + date, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching records: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
