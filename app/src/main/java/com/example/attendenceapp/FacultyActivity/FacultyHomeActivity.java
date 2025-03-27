package com.example.attendenceapp.FacultyActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendenceapp.FacultyFragments.FacultyCameraFragment;
import com.example.attendenceapp.FacultyFragments.FacultyClassFragment;
import com.example.attendenceapp.FacultyFragments.FacultyHomeFragment;
import com.example.attendenceapp.FacultyFragments.FacultyNotificationFragment;
import com.example.attendenceapp.FacultyFragments.FacultyProfileFragment;
import com.example.attendenceapp.R;
import com.example.attendenceapp.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class FacultyHomeActivity extends AppCompatActivity {

    ImageView logOUT,date;
    BottomNavigationView btv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_home);


        logOUT = findViewById(R.id.ftLogoutBtn);
//        date=findViewById(R.id.date);

        logOUT.setOnClickListener(v -> {
            // Sign out the user from Firebase Authentication
            FirebaseAuth.getInstance().signOut();

            // Redirect to Sign-In Activity
            Intent intent = new Intent(FacultyHomeActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish to remove the current activity from the stack
        });

        // Set the listener for the BottomNavigationView
        btv=findViewById(R.id.bottomNavigationView);
        btv.setOnItemSelectedListener(item -> {
            int id1=item.getItemId();

            if(id1==R.id.home)
                replaceFragment( new FacultyHomeFragment());

            else if (id1==R.id.attendence) {
                replaceFragment(new FacultyClassFragment());
              }
            else if (id1==R.id.notification) {
                replaceFragment(new FacultyNotificationFragment());
               }
            else if (id1==R.id.profile)
                replaceFragment(new FacultyProfileFragment());
            else if (id1==R.id.camera) {
                replaceFragment(new FacultyCameraFragment());
            } else
                replaceFragment(new FacultyHomeFragment());

            return true;
        });

        // Set default fragment
        replaceFragment(new FacultyHomeFragment());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }
    }
