package com.example.attendenceapp.AdapterClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendenceapp.R;
import com.example.attendenceapp.ModelClass.StudentAttendanceEntryModal;
import java.util.List;

public class Student_StudentAttendanceAdapter extends RecyclerView.Adapter<Student_StudentAttendanceAdapter.AttendanceViewHolder> {
    private final List<StudentAttendanceEntryModal> attendanceList;

    public Student_StudentAttendanceAdapter(List<StudentAttendanceEntryModal> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each attendance item row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_attendance_recyclerview, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StudentAttendanceEntryModal record = attendanceList.get(position);
        holder.subjectCodeTextView.setText(record.getSubjectId());
        holder.dateTextView.setText(record.getDate());
        holder.timeTextView.setText(record.getTime());
        holder.statusTextView.setText(record.getStatus());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView subjectCodeTextView, statusTextView, timeTextView, dateTextView;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectCodeTextView = itemView.findViewById(R.id.subjectCode);
            statusTextView = itemView.findViewById(R.id.status);
            timeTextView = itemView.findViewById(R.id.time);
            dateTextView = itemView.findViewById(R.id.date);
        }
    }
}
