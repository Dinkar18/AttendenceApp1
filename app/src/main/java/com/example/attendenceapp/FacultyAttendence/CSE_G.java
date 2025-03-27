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
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.attendenceapp.AdapterClass.StudentAttendenceAdapter;
//import com.example.attendenceapp.ModelClass.AttendenceModal;
//import com.example.attendenceapp.R;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
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
//public class CSE_G extends AppCompatActivity {
//
//    RecyclerView stdRecyclerView;
//    ImageView addStd, delStd;
//    StudentAttendenceAdapter attendenceAdapter;
//    List<AttendenceModal> adapterList;
//    Dialog dialog;
//    FirebaseFirestore firestore;
//    CollectionReference subjectsCollection;
//    TextView toolbarTV;
//    String semester, section, subjectCode;
//    DocumentReference subjectDoc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cse_b);
//
//        stdRecyclerView = findViewById(R.id.recylerview);
//        addStd = findViewById(R.id.add);
//        delStd = findViewById(R.id.delete);
//        toolbarTV = findViewById(R.id.semester);
//
//        // Getting data from FacultyClassActivity
//        semester = getIntent().getStringExtra("semester");
//        section = getIntent().getStringExtra("className");
//        subjectCode = getIntent().getStringExtra("subjectCode");  // Subject code passed from Intent
//
////        Toast.makeText(this,""+semester+"/"+section+"/"+subjectCode,Toast.LENGTH_SHORT).show();
//
//        toolbarTV.setText(semester+"/"+section+"/"+subjectCode);
//
//        // Initialize Firestore
//        firestore = FirebaseFirestore.getInstance();
//
//        // Access the subject document inside the section -> semester collection
//        subjectDoc = firestore.collection("Semesters").document(semester)
//                .collection("Sections").document(section)
//                .collection("Subjects").document(subjectCode);
//
//        // Access the 'Students' sub-collection inside the subject document
//        subjectsCollection = subjectDoc.collection("Students");
//
//        // Initialize the RecyclerView and Adapter
//        adapterList = new ArrayList<>();
//        attendenceAdapter = new StudentAttendenceAdapter(adapterList, this,subjectsCollection);
//        stdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        stdRecyclerView.setAdapter(attendenceAdapter);
//
//        // Fetch and display data from Firestore on RecyclerView
//        fetchStudentsFromFirestore();
//
//        // Show dialog to add a new student when 'addStd' is clicked
//        addStd.setOnClickListener(v -> showDialog());
//    }
//
//    void showDialog() {
//        dialog = new Dialog(this);
//        dialog.setContentView(R.layout.custom_dialog);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//
//        EditText studentId = dialog.findViewById(R.id.studentId);
//        Button addStudent = dialog.findViewById(R.id.add_dialog);
//
//        addStudent.setOnClickListener(v -> {
//            String id = studentId.getText().toString().trim();
//
//            if (!id.isEmpty()) {
//                // Check if the student is already registered
//                checkIfStudentExists(id);
//            }
//        });
//
//        dialog.show();  // Display the dialog
//    }
//
//    // Check if the student with the given ID already exists in Firestore
//    void checkIfStudentExists(String id) {
//        subjectsCollection.whereEqualTo("studentId", id).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (!querySnapshot.isEmpty()) {
//                            // Student already registered
//                            Toast.makeText(CSE_G.this, "Student ID " + id + " is already registered", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Student is not registered, add to Firestore
//                            addStudentToFirestore(id);
//                        }
//                    } else {
//                        // Handle error
//                        Toast.makeText(CSE_G.this, "Error checking student: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Add the student to Firestore under the specific subject
//    void addStudentToFirestore(String id) {
//        AttendenceModal student = new AttendenceModal(id, subjectCode, semester, getCurrentTime(), getCurrentDate(), section,false);
//
//        // Add student to the 'Students' sub-collection inside the subject document
//        subjectsCollection.add(student)
//                .addOnSuccessListener(documentReference -> {
//                    // Add student to the local list and update RecyclerView
//                    adapterList.add(student);
//                    attendenceAdapter.notifyItemInserted(adapterList.size() - 1);
//                    stdRecyclerView.scrollToPosition(adapterList.size() - 1); // Scroll to the new item
//
//                    Toast.makeText(CSE_G.this, "Student added successfully", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();  // Close the dialog
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure
//                    Toast.makeText(CSE_G.this, "Failed to add student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    // Fetch the existing students from Firestore and display them in the RecyclerView
//    void fetchStudentsFromFirestore() {
//        subjectsCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        adapterList.clear();  // Clear the current list
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            AttendenceModal student = document.toObject(AttendenceModal.class);
//                            adapterList.add(student);  // Add each student to the list
//                        }
//
//                        attendenceAdapter.notifyDataSetChanged();  // Update the adapter
//                    } else {
//                        // Handle error
//                        Toast.makeText(CSE_G.this, "Failed to fetch students: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Helper method to get the current date formatted
//    public static String getCurrentDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        return dateFormat.format(Calendar.getInstance().getTime());
//    }
//
//    // Helper method to get the current time formatted
//    public static String getCurrentTime() {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        return timeFormat.format(Calendar.getInstance().getTime());
//    }
//}