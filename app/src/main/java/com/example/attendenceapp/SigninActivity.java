package com.example.attendenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.attendenceapp.FacultyActivity.FacultyHomeActivity;
import com.example.attendenceapp.StudentActivity.StudentHomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {

    // Declare UI components
    Spinner spinner;
    TextInputEditText emailId, passwd;
    TextView forgotPswd;
    AppCompatButton loginButton;
    TextView toSignUP;

    // Firebase instances for authentication and Firestore database
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Instantiate views
        spinner = findViewById(R.id.spinnerLoginRole);  // Spinner for selecting role (student/faculty)
        emailId = findViewById(R.id.username);          // Input field for email
        passwd = findViewById(R.id.password);           // Input field for password
        forgotPswd = findViewById(R.id.forgotPassword); // TextView for forgot password
        loginButton = findViewById(R.id.login);         // Button for login action
        toSignUP = findViewById(R.id.toRegister);       // TextView for sign-up action

        // Firebase setup
        firebaseAuth = FirebaseAuth.getInstance();             // Initialize Firebase authentication
        db = FirebaseFirestore.getInstance();           // Initialize Firestore database

        // If user doesn't have credentials, redirect to the registration screen
        toSignUP.setOnClickListener(v -> {
            startActivity(new Intent(SigninActivity.this, RegisterActivity.class));  // Go to RegisterActivity
            finish();  // Close the current activity
        });

        // Initialize spinner with roles (student/faculty) using an array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Login button click listener to handle login action
        loginButton.setOnClickListener(v -> {
            String email = emailId.getText().toString().trim();    // Get the email input
            String password = passwd.getText().toString().trim();  // Get the password input
            String selectedRole = spinner.getSelectedItem().toString().toLowerCase();  // Get selected role

            // Validate credentials before proceeding
            if (validateSignInCredentials(email, password, selectedRole)) {
                // If valid, sign in the user and check their role
                signInUser(email, password, selectedRole);
            } else {
                Toast.makeText(SigninActivity.this, "Failed to sign in", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Function to sign in user using FirebaseAuth and validate role in Firestore
    private void signInUser(String email, String password, String selectedRole) {
        // Firebase authentication using email and password
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If authentication succeeds, fetch user data based on the selected role (student or faculty)
                if (selectedRole.equals("Registered Student")) {
                    checkUserRole(email, "Registered Student", StudentHomeActivity.class);  // Check if user exists in "students" collection
                } else if (selectedRole.equals("Registered Faculty")) {
                    checkUserRole(email, "Registered Faculty", FacultyHomeActivity.class);  // Check if user exists in "faculty" collection
                }
            } else {
                Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();  // Show error if authentication fails
            }
        });
    }

    // Function to check user role in Firestore and redirect to the appropriate home activity
    private void checkUserRole(String email, String collection, Class<?> homeActivity) {
        // Fetch the user document from the Firestore collection (either students or faculty) based on UID
        db.collection(collection).document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();  // Get the user document
                        if (document.exists()) {
                            // If user exists in the specified collection, proceed to respective home activity
                            startActivity(new Intent(SigninActivity.this, homeActivity));
                            finish();  // Close the login activity
                        } else {
                            // If user doesn't exist in the role's collection, sign out and show error
                            Toast.makeText(SigninActivity.this, "User not found in " + collection + " role", Toast.LENGTH_LONG).show();
                            firebaseAuth.signOut();  // Sign out the user
                        }
                    }
                    else {
                        // Show error if unable to fetch user data
                        Toast.makeText(SigninActivity.this, "Failed to fetch user data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Function to validate sign-in credentials (email, password, and role selection)
    private boolean validateSignInCredentials(String email, String password, String selectedRole) {
        // Reset previous error messages
        emailId.setError(null);
        passwd.setError(null);

        // Validate email field
        if (email.isEmpty()) {
            emailId.setError("Email is required");  // Error if email is empty
            return false;
        }

        // Check if email matches the correct pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailId.setError("Invalid email address");  // Error if email format is invalid
            return false;
        }

        // Validate password field
        if (password.isEmpty()) {
            passwd.setError("Password is required");  // Error if password is empty
            return false;
        }

        // Ensure the password length is at least 6 characters
        if (password.length() < 6) {
            passwd.setError("Password must be at least 6 characters");  // Error if password is too short
            return false;
        }

        // Validate role selection to ensure either "student" or "faculty" is selected
        if (selectedRole == null || (!selectedRole.equals("student") && !selectedRole.equals("faculty"))) {
            Toast.makeText(this, "Please select a valid role: student or faculty", Toast.LENGTH_SHORT).show();  // Show error if role is invalid
            return false;
        }

        return true;  // All validations passed
    }
}