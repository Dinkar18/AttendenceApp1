package com.example.attendenceapp.StudentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendenceapp.R;
import com.example.attendenceapp.SigninActivity;
import com.example.attendenceapp.StudentFragments.StudentHomeFragment;
import com.example.attendenceapp.StudentFragments.StudentNotificationFragment;
import com.example.attendenceapp.StudentFragments.StudentProfileFragment;
import com.example.attendenceapp.StudentFragments.StudentAttendanceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentHomeActivity extends AppCompatActivity {

    private ImageView logOUT, date;
    private BottomNavigationView btv;
    private TextView rollID, studentName;  // Added studentName TextView reference

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btv = findViewById(R.id.bottomNavigationView);
        date = findViewById(R.id.calendar);
        logOUT = findViewById(R.id.stLogoutBtn);
        rollID = findViewById(R.id.studentData);  //stores name and roll in toolbar

        // Logout button click listener
        logOUT.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(StudentHomeActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        });

        // Fetch and display student data
        fetchStudentDataBasedOnUID();

        // Set listener for BottomNavigationView
        btv.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Switch between fragments based on the selected menu item
            if (id == R.id.home) {
                replaceFragment(new StudentHomeFragment());
            } else if (id == R.id.attendence) {
                replaceFragment(new StudentAttendanceFragment());
            } else if (id == R.id.notification) {
                replaceFragment(new StudentNotificationFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new StudentProfileFragment());
            } else {
                replaceFragment(new StudentHomeFragment());
            }

            return true;
        });

        // Set default fragment as StudentHomeFragment
        replaceFragment(new StudentHomeFragment());
    }

    private void fetchStudentDataBasedOnUID() {
        // Get the UID of the currently logged-in user
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "User not logged in or invalid UID!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore document path using UID
        String documentPath = "students/" + uid;

        // Fetch the document from Firestore
        firestore.document(documentPath)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract data
                        String name = documentSnapshot.getString("fullName");
                        String roll = documentSnapshot.getString("studentID");

                        // Update UI with student data
                        if (roll != null ) {
                            rollID.setText("Roll No: " + roll+"/"+name);
                        } else {
                            rollID.setText("Roll No: Not available");
                        }
                    } else {
                        Log.e("StudentHomeActivity", "No document found for UID: " + uid);
                        Toast.makeText(this, "Student data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("StudentHomeActivity", "Failed to fetch student data", e);
                    Toast.makeText(this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }
}
