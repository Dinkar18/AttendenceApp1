package com.example.attendenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.attendenceapp.FacultyActivity.FacultyHomeActivity;
import com.example.attendenceapp.StudentActivity.StudentHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntroActivity extends AppCompatActivity {

    AppCompatButton next;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        next = findViewById(R.id.start);
        next.setOnClickListener(v -> {
            // Check if the user is already logged in
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, display a toast and redirect to the appropriate home activity
                Toast.makeText(IntroActivity.this, "You are already logged in", Toast.LENGTH_SHORT).show();
                redirectUser(currentUser);
            }
            else {
                // No user is logged in, redirect to the registration page
                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void redirectUser(FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        // You might want to fetch the user role from Firestore to decide where to navigate
        fStore.collection("students").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() || task.getResult() != null && task.getResult().exists()) {
                startActivity(new Intent(IntroActivity.this, StudentHomeActivity.class));
            } else {
                // If not a student, check faculty
                fStore.collection("faculty").document(userId).get().addOnCompleteListener(facultyTask -> {
                    if (facultyTask.isSuccessful() || facultyTask.getResult() != null && facultyTask.getResult().exists()) {
                        startActivity(new Intent(IntroActivity.this, FacultyHomeActivity.class));
                    }
                    else {
                        // Handle the case where the user is neither a student nor faculty
                        Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
