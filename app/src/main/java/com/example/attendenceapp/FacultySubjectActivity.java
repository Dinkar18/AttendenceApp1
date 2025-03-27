package com.example.attendenceapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.AdapterClass.SubjectAdapterAtFaculty;
import com.example.attendenceapp.FacultyAttendence.CSE_A;
//import com.example.attendenceapp.FacultyAttendence.CSE_B;
//import com.example.attendenceapp.FacultyAttendence.CSE_C;
//import com.example.attendenceapp.FacultyAttendence.CSE_D;
//import com.example.attendenceapp.FacultyAttendence.CSE_E;
//import com.example.attendenceapp.FacultyAttendence.CSE_F;
//import com.example.attendenceapp.FacultyAttendence.CSE_G;
//import com.example.attendenceapp.FacultyAttendence.CSE_H;
import com.example.attendenceapp.ModelClass.SubjectModal;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FacultySubjectActivity extends AppCompatActivity {

    RecyclerView subjectRecyclerView;
    SubjectAdapterAtFaculty subjectAdapter;
    List<SubjectModal> subjectList;
    ImageView imgAdd;
    String semester, className;
    TextView sectionTV;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subject);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        subjectRecyclerView = findViewById(R.id.subjectView);
        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapterAtFaculty(subjectList, this::onSubjectClick);  // Update for click listener
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectRecyclerView.setAdapter(subjectAdapter);

        imgAdd = findViewById(R.id.addSubject);
        sectionTV = findViewById(R.id.subjectPage);

        // Pass data from previous activity
        semester = getIntent().getStringExtra("semester");
        className = getIntent().getStringExtra("className");
        Toast.makeText(this,"/"+semester+"/"+className,Toast.LENGTH_SHORT).show();
        sectionTV.setText(semester + "/" + className);

        // Load subjects from Firestore for the specific semester and class
        loadSubjectsFromFirestore(semester, className);

        // Open add subject dialog on button click
        imgAdd.setOnClickListener(v -> openAddSubjectDialog());
    }

    // Method to open custom dialog for adding subject
    private void openAddSubjectDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_subject);

        EditText etSubjectName = dialog.findViewById(R.id.et_subject_name);
        EditText etSubjectCode = dialog.findViewById(R.id.et_subject_code);
        Button btnAddSubject = dialog.findViewById(R.id.btn_addSubject);

        btnAddSubject.setOnClickListener(v -> {
            String subjectName = etSubjectName.getText().toString();
            String subjectCode = etSubjectCode.getText().toString();

            if (!subjectName.isEmpty() && !subjectCode.isEmpty()) {
                // Add subject to Firestore in the collection for the specific semester and class
                addSubjectToFirestore(subjectName, subjectCode, semester, className);
                dialog.dismiss();
            } else {
                Toast.makeText(FacultySubjectActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Method to add a subject to Firestore in a separate collection for each semester and class
    private void addSubjectToFirestore(String subjectName, String subjectCode, String semester, String className) {
        // Create a collection reference based on semester and class name
        String collectionPath = "subjects_" + semester + "_" + className;
        SubjectModal subject = new SubjectModal(subjectName, subjectCode);

        firestore.collection(collectionPath).add(subject)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FacultySubjectActivity.this, "Subject Added", Toast.LENGTH_SHORT).show();
                    loadSubjectsFromFirestore(semester, className);  // Reload the subjects after adding
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FacultySubjectActivity.this, "Error adding subject", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to load subjects from Firestore for the specific semester and class
    private void loadSubjectsFromFirestore(String semester, String className) {
        // Create a collection path based on semester and class name
        String collectionPath = "subjects_" + semester + "_" + className;

        firestore.collection(collectionPath)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(FacultySubjectActivity.this, "Error loading subjects", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        subjectList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            SubjectModal subject = document.toObject(SubjectModal.class);
                            subjectList.add(subject);
                        }
                        subjectAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Handle subject item click
    private void onSubjectClick(SubjectModal subject) {
        String subjectCode = subject.getSubjectCode();


        if (semester == null || className == null) {
            Toast.makeText(this, "Semester or Section is missing", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent;
        switch (className) {
            case "CSE A":
                intent = new Intent(FacultySubjectActivity.this, CSE_A.class);
                break;
//            case "CSE B":
//                intent = new Intent(FacultySubjectActivity.this, CSE_B.class);
//                break;
//            case "CSE C":
//                intent = new Intent(FacultySubjectActivity.this, CSE_C.class);
//                break;
//            case "CSE D":
//                intent = new Intent(FacultySubjectActivity.this, CSE_D.class);
//                break;
//            case "CSE E":
//                intent = new Intent(FacultySubjectActivity.this, CSE_E.class);
//                break;
//            case "CSE F":
//                intent = new Intent(FacultySubjectActivity.this, CSE_F.class);
//                break;
//            case "CSE G":
//                intent = new Intent(FacultySubjectActivity.this, CSE_G.class);
//                break;
//            case "CSE H":
//                intent = new Intent(FacultySubjectActivity.this, CSE_H.class);
//                break;
            default:
                Toast.makeText(FacultySubjectActivity.this, "Invalid Section", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("className", className);
        intent.putExtra("semester", semester);

        startActivity(intent);
        finish();
    }

}
