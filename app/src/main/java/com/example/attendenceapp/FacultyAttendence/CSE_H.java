//package com.example.attendenceapp.FacultyAttendence;
//
//import android.app.Dialog;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.attendenceapp.AdapterClass.StudentAttendenceAdapter;
//import com.example.attendenceapp.ModelClass.AttendenceModal;
//import com.example.attendenceapp.R;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//public class CSE_H extends AppCompatActivity {
//
//    private RecyclerView stdRecyclerView;
//    private ImageView addStd, delStd;
//    private StudentAttendenceAdapter attendenceAdapter;
//    private List<AttendenceModal> adapterList;
//    private Dialog dialog;
//    private FirebaseFirestore firestore;
//    private CollectionReference subjectsCollection;
//    private TextView toolbarTV;
//    private String semester, section, subjectCode;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cse_b);
//
//        initViews();  // Initialize UI elements
//        setupFirestore();  // Initialize Firestore
//
//        fetchStudentsFromFirestore();  // Fetch and display data from Firestore
//
//        addStd.setOnClickListener(v -> showAddStudentDialog());  // Show dialog to add a new student
//    }
//
//    // Initialize UI elements
//    private void initViews() {
//        stdRecyclerView = findViewById(R.id.recylerview);
//        addStd = findViewById(R.id.add);
//        delStd = findViewById(R.id.delete);
//        toolbarTV = findViewById(R.id.semester);
//
//        semester = getIntent().getStringExtra("semester");
//        section = getIntent().getStringExtra("className");
//        subjectCode = getIntent().getStringExtra("subjectCode");
//
//        toolbarTV.setText(String.format("%s/%s/%s", semester, section, subjectCode));  // Update toolbar text
//
//        adapterList = new ArrayList<>();
//        attendenceAdapter = new StudentAttendenceAdapter(adapterList, this, subjectsCollection);
//        stdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        stdRecyclerView.setAdapter(attendenceAdapter);
//    }
//
//    // Setup Firestore references
//    private void setupFirestore() {
//        firestore = FirebaseFirestore.getInstance();
//        subjectsCollection = firestore.collection("Semesters").document(semester)
//                .collection("Sections").document(section)
//                .collection("Subjects").document(subjectCode)
//                .collection("Students");
//    }
//
//    // Show dialog for adding a student
//    private void showAddStudentDialog() {
//        dialog = new Dialog(this);
//        dialog.setContentView(R.layout.custom_dialog);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//
//        EditText studentIdInput = dialog.findViewById(R.id.studentId);
//        Button addStudentBtn = dialog.findViewById(R.id.add_dialog);
//
//        addStudentBtn.setOnClickListener(v -> {
//            String studentId = studentIdInput.getText().toString().trim();
//
//            if (!studentId.isEmpty()) {
//                checkIfStudentExists(studentId);
//            } else {
//                Toast.makeText(this, "Student ID cannot be empty", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        dialog.show();
//    }
//
//    // Check if the student is already registered
//    private void checkIfStudentExists(String studentId) {
//        subjectsCollection.whereEqualTo("studentId", studentId).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (!querySnapshot.isEmpty()) {
//                            Toast.makeText(CSE_H.this, "Student ID " + studentId + " is already registered", Toast.LENGTH_SHORT).show();
//                        } else {
//                            addStudentToFirestore(studentId);
//                        }
//                    } else {
//                        Toast.makeText(CSE_H.this, "Error checking student: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Add student to Firestore
//    private void addStudentToFirestore(String studentId) {
//        AttendenceModal student = new AttendenceModal(studentId, subjectCode, semester, getCurrentTime(), getCurrentDate(), section, false);
//
//        subjectsCollection.add(student)
//                .addOnSuccessListener(documentReference -> {
//                    adapterList.add(student);
//                    attendenceAdapter.notifyItemInserted(adapterList.size() - 1);
//                    stdRecyclerView.scrollToPosition(adapterList.size() - 1);
//
//                    Toast.makeText(CSE_H.this, "Student added successfully", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(CSE_H.this, "Failed to add student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    // Fetch students from Firestore and display in RecyclerView
//    private void fetchStudentsFromFirestore() {
//        subjectsCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        adapterList.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            AttendenceModal student = document.toObject(AttendenceModal.class);
//                            adapterList.add(student);
//                        }
//                        attendenceAdapter.notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(CSE_H.this, "Failed to fetch students: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Get current date in the required format
//    private String getCurrentDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        return dateFormat.format(Calendar.getInstance().getTime());
//    }
//
//    // Get current time in the required format
//    private String getCurrentTime() {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        return timeFormat.format(Calendar.getInstance().getTime());
//    }
//}
