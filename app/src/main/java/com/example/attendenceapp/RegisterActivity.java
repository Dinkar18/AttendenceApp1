package com.example.attendenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendenceapp.FacultyActivity.FacultyHomeActivity;
import com.example.attendenceapp.StudentActivity.StudentHomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Spinner roleSpinner;
    private LinearLayout formContainer;
    private View studentForm, facultyForm;
    private TextView toLogin;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    // Input fields for both roles
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone;
    // Student-specific fields
    private TextInputEditText etStudentID, etDepartment, etYear;
    // Faculty-specific fields
    private TextInputEditText etFacultyID;

    // Store userId
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize UI components
        roleSpinner = findViewById(R.id.spinnerRole);
        formContainer = findViewById(R.id.formContainer);
        toLogin = findViewById(R.id.toLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        // Inflate student and faculty forms
        studentForm = LayoutInflater.from(this).inflate(R.layout.student_registration, null);
        facultyForm = LayoutInflater.from(this).inflate(R.layout.faculty_registration, null);

        // Set up role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Handle role selection
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formContainer.removeAllViews();
                if (position == 1) {
                    // Show student form
                    formContainer.addView(studentForm);
                    setupStudentForm();
                } else if (position == 2) {
                    // Show faculty form
                    formContainer.addView(facultyForm);
                    setupFacultyForm();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                formContainer.removeAllViews();
            }
        });

        // Handle registration button click
        findViewById(R.id.btnRegister).setOnClickListener(v -> handleRegistration());

        // Navigate to sign-in page from registration
        toLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void setupStudentForm() {
        etFullName = studentForm.findViewById(R.id.etFullName);
        etEmail = studentForm.findViewById(R.id.etEmail);
        etPassword = studentForm.findViewById(R.id.etPassword);
        etConfirmPassword = studentForm.findViewById(R.id.etConfirmPassword);
        etStudentID = studentForm.findViewById(R.id.etStudentID);
        etDepartment = studentForm.findViewById(R.id.etDepartment);
        etYear = studentForm.findViewById(R.id.etYear);
        etPhone = studentForm.findViewById(R.id.etPhone);
    }

    private void setupFacultyForm() {
        etFullName = facultyForm.findViewById(R.id.etFullName);
        etEmail = facultyForm.findViewById(R.id.etEmail);
        etPassword = facultyForm.findViewById(R.id.etPassword);
        etConfirmPassword = facultyForm.findViewById(R.id.etConfirmPassword);
        etFacultyID = facultyForm.findViewById(R.id.etFacultyID);
        etPhone = facultyForm.findViewById(R.id.etPhone);
    }

    private void handleRegistration() {
        int selectedRole = roleSpinner.getSelectedItemPosition();
        if (selectedRole == 1) {
            registerUser(true);  // Register student
        } else if (selectedRole == 2) {
            registerUser(false);  // Register faculty
        } else {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(boolean isStudent) {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(etFullName, etEmail, etPassword, etConfirmPassword, etPhone)) return;

        // Firebase registration
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (isStudent) {
                    registerStudent(firebaseUser);
                } else {
                    registerFaculty(firebaseUser);
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Function for student registration
    private void registerStudent(FirebaseUser firebaseUser) {
        // Collect student-specific data
        String studentID = etStudentID.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        // Store student data in Firestore
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("fullName", etFullName.getText().toString().trim());
        studentData.put("email", etEmail.getText().toString().trim());
        studentData.put("studentID", studentID);
        studentData.put("department", department);
        studentData.put("year", year);
        studentData.put("phone", etPhone.getText().toString().trim());
        studentData.put("St_role", roleSpinner.getSelectedItem().toString());

        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference docRef = fStore.collection("Registered Student").document(userId);

        docRef.set(studentData).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Student Registration Completed Successfully", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(RegisterActivity.this, "Failed! Student Registration: " + e.toString(), Toast.LENGTH_SHORT).show()
        );

        startActivity(new Intent(RegisterActivity.this, StudentHomeActivity.class));
        finish();
    }

    // Function for faculty registration
    private void registerFaculty(FirebaseUser firebaseUser) {
        // Set userId for faculty (same as student)
        userId = firebaseAuth.getCurrentUser().getUid();

        // Collect faculty-specific data
        String facultyID = etFacultyID.getText().toString().trim();

        // Store faculty data in Firestore
        Map<String, Object> facultyData = new HashMap<>();
        facultyData.put("fullName", etFullName.getText().toString().trim());
        facultyData.put("email", etEmail.getText().toString().trim());
        facultyData.put("facultyID", facultyID);
        facultyData.put("phone", etPhone.getText().toString().trim());
        facultyData.put("Fa_role", roleSpinner.getSelectedItem().toString());

        DocumentReference docRef = fStore.collection("Registered Faculty").document(userId);

        docRef.set(facultyData).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Faculty Registration Completed Successfully", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(RegisterActivity.this, "Failed! Faculty Registration: " + e.toString(), Toast.LENGTH_SHORT).show()
        );

        startActivity(new Intent(RegisterActivity.this, FacultyHomeActivity.class));
        finish();
    }

    private boolean validateInput(TextInputEditText... inputs) {
        for (TextInputEditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}