package com.example.attendenceapp.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.R;
import com.example.attendenceapp.StudentActivity.StudentAttendanceActivity;

import java.util.List;

public class SubjectAdapterStudent extends RecyclerView.Adapter<SubjectAdapterStudent.SubjectViewHolder> {

    private List<String> subjectList;
    private String studentId; // Pass this from the fragment

    public SubjectAdapterStudent(List<String> subjectList, String studentId) {
        this.subjectList = subjectList;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        String subjectCode = subjectList.get(position);
        holder.subjectTextView.setText(subjectCode);

        // Add click listener
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, StudentAttendanceActivity.class);
            intent.putExtra("studentId", studentId);
            intent.putExtra("subjectCode", subjectCode);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView); // Update with your TextView ID
        }
    }
}
