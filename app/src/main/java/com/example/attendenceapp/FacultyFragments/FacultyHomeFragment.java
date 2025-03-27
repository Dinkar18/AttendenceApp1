package com.example.attendenceapp.FacultyFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.attendenceapp.R;
import com.example.attendenceapp.StudentActivity.AddStudentActivity;

public class FacultyHomeFragment extends Fragment {

    ImageView addStudent,markAttendence;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.faculty_fragment_home, container, false);

        // Find the button by its ID
        addStudent = view.findViewById(R.id.addUser);  // Replace with actual ID of the button
        markAttendence=view.findViewById(R.id.attendenceButton);

        // Set OnClickListener to navigate to AddStudentActivity
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the AddStudentActivity
                Intent intent = new Intent(getActivity(), AddStudentActivity.class);
                startActivity(intent);
            }
        });

        //mark attendence


        return view;
    }
}
