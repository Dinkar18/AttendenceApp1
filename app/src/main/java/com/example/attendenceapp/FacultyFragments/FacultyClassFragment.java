package com.example.attendenceapp.FacultyFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendenceapp.AdapterClass.SemesterAdapter;
import com.example.attendenceapp.FacultyClassActivity;
import com.example.attendenceapp.R;
import java.util.ArrayList;
import java.util.List;

public class FacultyClassFragment extends Fragment {

    RecyclerView semesterRecyclerView;
    List<String> semesterList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_faculty_class1, container, false);

        // Initialize RecyclerView
        semesterRecyclerView = rootView.findViewById(R.id.semesterRecyclerView);
        semesterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create a list of semesters
        semesterList = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            semesterList.add("Semester " + i);
        }

        // Set adapter to RecyclerView
        SemesterAdapter semesterAdapter = new SemesterAdapter(semesterList, getContext());
        semesterRecyclerView.setAdapter(semesterAdapter);


        return rootView;
    }
}
