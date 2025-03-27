package com.example.attendenceapp.FacultyAttendence;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.AdapterClass.Faculty_StudentAttendanceAdapter;
import com.example.attendenceapp.ModelClass.AttendenceModal;
import com.example.attendenceapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CSE_C extends AppCompatActivity {

    RecyclerView stdRecyclerView;
    ImageView moreOpt, upload;
    Faculty_StudentAttendanceAdapter attendenceAdapter;
    List<AttendenceModal> adapterList;
    Dialog dialog;
    FirebaseFirestore firestore;
    CollectionReference subjectsCollection;
    TextView toolbarTV;
    String semester, section, subjectCode, selectedTime;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cse_c);  // Update layout for CSE_C
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize views
        stdRecyclerView = findViewById(R.id.recylerview);
        moreOpt = findViewById(R.id.moreOpt);
        toolbarTV = findViewById(R.id.semester);
        upload = findViewById(R.id.uploading);

        // Getting data passed from the previous activity
        semester = getIntent().getStringExtra("semester");
        section = "CSE_C";  // Set the section name to CSE_C
        subjectCode = getIntent().getStringExtra("subjectCode");

        // Setting toolbar text
        toolbarTV.setText(String.format("%s/%s/%s", semester, section, subjectCode));

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        subjectsCollection = firestore.collection("Semesters").document(semester)
                .collection("Sections").document(section)
                .collection("Subjects").document(subjectCode)
                .collection("Students");

        // Initialize RecyclerView and Adapter
        adapterList = new ArrayList<>();
        attendenceAdapter = new Faculty_StudentAttendanceAdapter(adapterList, this, subjectsCollection);
        stdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stdRecyclerView.setAdapter(attendenceAdapter);

        // Fetch and display data from Firestore
        fetchStudentsFromFirestore();

        // Handle 'more options' button click
        moreOpt.setOnClickListener(this::showPopupMenu);

        // Handle upload button click
        upload.setOnClickListener(v -> {
            String selectedDate = attendenceAdapter.getSelectedDate();
            selectedTime = attendenceAdapter.getSelectedTime();
            markAttendanceForStudents(selectedDate, selectedTime, subjectCode);
        });
    }

    // Fetch all students from Firestore and display them in RecyclerView
    void fetchStudentsFromFirestore() {
        progressDialog = ProgressDialog.show(this, "Loading", "Fetching students...", true);
        subjectsCollection.get().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                adapterList.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    AttendenceModal modal = documentSnapshot.toObject(AttendenceModal.class);
                    adapterList.add(modal);
                }
                attendenceAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load students: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show Popup Menu on ImageView click
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.attendence_edit, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.addStudent) {
                addStudentDialog();
                return true;
            } else if (item.getItemId() == R.id.editAttendence) {
                Toast.makeText(CSE_C.this, "Edit Attendance is Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.print) {
                Toast.makeText(CSE_C.this, "Print", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popupMenu.setGravity(Gravity.END);
        popupMenu.show();
    }

    // Show dialog to add a new student
    void addStudentDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);

        EditText studentId = dialog.findViewById(R.id.studentId);
        Button addStudent = dialog.findViewById(R.id.add_dialog);

        addStudent.setOnClickListener(v -> {
            String id = studentId.getText().toString().trim();
            if (!id.isEmpty()) {
                checkIfStudentExists(id);
            } else {
                Toast.makeText(CSE_C.this, "Please enter Student ID", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Check if the student with the given ID already exists in Firestore
    void checkIfStudentExists(String id) {
        subjectsCollection.whereEqualTo("studentId", id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(CSE_C.this, "Student ID " + id + " is already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            addStudentToFirestore(id);
                        }
                    } else {
                        Toast.makeText(CSE_C.this, "Error checking student: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Add the student to Firestore
    void addStudentToFirestore(String studentId) {
        AttendenceModal newStudent = new AttendenceModal(studentId, subjectCode, semester, getCurrentDate(), getCurrentTime(), section, false);

        subjectsCollection.add(newStudent).addOnSuccessListener(documentReference -> {
            attendenceAdapter.addStudent(newStudent);
            Toast.makeText(CSE_C.this, "Student added successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(CSE_C.this, "Failed to add student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Show time picker dialog
    private void showTimePickerDialog(EditText setTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minuteOfHour) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
            setTime.setText(selectedTime); // Set the selected time to EditText
        }, hour, minute, true);

        timePickerDialog.show();
    }

    // Method to mark attendance for all students
    private void markAttendanceForStudents(String date, String time, String subjectCode) {
        progressDialog = ProgressDialog.show(this, "Marking Attendance", "Please wait...", true);

        // Loop through all students in the adapter list
        for (AttendenceModal student : adapterList) {
            String studentId = student.getStudentId();
            boolean isPresent = student.isPresent();

            // 1. Primary Attendance Structure
            CollectionReference primaryAttendanceRef = firestore.collection("Student_Attendance")
                    .document("Semester " + semester) // Semester document
                    .collection("Sections").document(section) // Section document
                    .collection("Subjects").document(subjectCode) // Subject document
                    .collection(date); // Date collection

            Map<String, Object> primaryAttendanceData = new HashMap<>();
            primaryAttendanceData.put("studentId", studentId);
            primaryAttendanceData.put("isPresent", isPresent);
            primaryAttendanceData.put("timeMarked", time);
            primaryAttendanceData.put("dateMarked", date);
            primaryAttendanceData.put("subCode", subjectCode);
            primaryAttendanceData.put("sem", semester);
            primaryAttendanceData.put("sec", section);

            primaryAttendanceRef.document(studentId).set(primaryAttendanceData)
                    .addOnSuccessListener(aVoid -> Log.d("AttendanceDebug", "Primary attendance stored for student: " + studentId))
                    .addOnFailureListener(e -> Log.e("AttendanceDebug", "Failed to store primary attendance: ", e));
        }
    }

    // Helper method to get the current date
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    // Helper method to get the current time
    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(Calendar.getInstance().getTime());
    }
}
