package com.example.attendenceapp.StudentFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.AdapterClass.SubjectAdapterStudent;
import com.example.attendenceapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceFragment extends Fragment {

    private RecyclerView recyclerView;
    private SubjectAdapterStudent subjectAdapter;
    private List<String> subjectList;
    private FirebaseFirestore firestore;
    private String studentId; // Pass this from your activity or auth

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_subjects, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        subjectList = new ArrayList<>();
        // Pass studentId while creating the adapter
        subjectAdapter = new SubjectAdapterStudent(subjectList, studentId);
        recyclerView.setAdapter(subjectAdapter);

        firestore = FirebaseFirestore.getInstance();
        fetchSubjects();

        return view;
    }

    private void fetchSubjects() {
        firestore.collection("Attendance_Summary")
                .document(studentId)
                .collection("attendance")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    subjectList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        subjectList.add(doc.getId());
                    }
                    subjectAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("StudentSubjectFragment", "Failed to fetch subjects", e);
                });
    }
}
