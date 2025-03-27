package com.example.attendenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FacultyClassActivity extends AppCompatActivity {

    private ImageView classA, classB, classC, classD, classE, classF, classG, classH;
    TextView semesterTextView;
    String semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize views
        classA = findViewById(R.id.A);
        classB = findViewById(R.id.B);
        classC = findViewById(R.id.C);
        classD = findViewById(R.id.D);
        classE = findViewById(R.id.E);
        classF = findViewById(R.id.F);
        classG = findViewById(R.id.G);
        classH = findViewById(R.id.H);

        // Get the selected semester from the intent
        semester = getIntent().getStringExtra("semester");

        // Display the selected semester
        semesterTextView = findViewById(R.id.semester);
        semesterTextView.setText(semester);

        // Set up click listeners for each class image view, and pass class name and semester


        classA.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE A"));
        classB.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE B"));
        classC.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE C"));
        classD.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE D"));
        classE.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE E"));
        classF.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE F"));
        classG.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE G"));
        classH.setOnClickListener(v -> startNewActivity(FacultySubjectActivity.class, "CSE H"));



    }

    // Helper method to start a new activity and pass the class and semester
    private void startNewActivity(Class<?> classActivity, String className) {
        Intent intent = new Intent(FacultyClassActivity.this, classActivity);
        intent.putExtra("className", className);
        intent.putExtra("semester", semester);
        startActivity(intent);
        finish();
    }
}
