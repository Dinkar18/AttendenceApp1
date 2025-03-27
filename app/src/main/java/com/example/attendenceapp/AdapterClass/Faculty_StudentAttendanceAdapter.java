package com.example.attendenceapp.AdapterClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.ModelClass.AttendenceModal;
import com.example.attendenceapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Faculty_StudentAttendanceAdapter extends RecyclerView.Adapter<Faculty_StudentAttendanceAdapter.ViewHolder> {

    private List<AttendenceModal> studentList;
    private Context context;
    private CollectionReference subjectsCollection;
    private String selectedDate = "";
    private String selectedTime = "";

    public Faculty_StudentAttendanceAdapter(List<AttendenceModal> studentList, Context context, CollectionReference subjectsCollection) {
        this.studentList = studentList;
        this.context = context;
        this.subjectsCollection = subjectsCollection;
    }

    // Method to add a new student to the list and notify the adapter
    public void addStudent(AttendenceModal newStudent) {
        studentList.add(newStudent);
        notifyItemInserted(studentList.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_recyler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendenceModal modal = studentList.get(position);
        holder.bind(modal);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    private void updateStudentStatusInFirestore(AttendenceModal student, String date, String time) {
        subjectsCollection.whereEqualTo("studentId", student.getStudentId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentReference studentDoc = task.getResult().getDocuments().get(0).getReference();
                        studentDoc.update("isPresent", student.isPresent(),
                                "markedDate", date,
                                "markedTime", time);
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stdRoll, stdClassname, stdSem, subCode, time, date, status;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.currentDate);
            time = itemView.findViewById(R.id.currentTime);
            subCode = itemView.findViewById(R.id.subjectCode);
            stdSem = itemView.findViewById(R.id.semester);
            stdClassname = itemView.findViewById(R.id.className);
            stdRoll = itemView.findViewById(R.id.rollNo);
            status = itemView.findViewById(R.id.attendenceStatus);
            cardView = itemView.findViewById(R.id.mainLayout);
        }

        public void bind(AttendenceModal modal) {
            // Set student data
            stdRoll.setText("Student ID: " + modal.getStudentId());
            stdClassname.setText("Class: " + modal.getSection());
            stdSem.setText(modal.getSemester());
            time.setText(modal.getMarkedTime());
            date.setText(modal.getMarkedDate());
            subCode.setText("Subject: " + modal.getSubjectCode());

            // Set UI based on the current attendance status
            updateItemView(modal.isPresent());

            // Toggle attendance status and set date/time on item click
            itemView.setOnClickListener(v -> toggleAttendance(modal));
        }

        private void toggleAttendance(AttendenceModal modal) {
            boolean newStatus = !modal.isPresent();
            modal.setPresent(newStatus);

            // Get the current date and time
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            selectedDate = dateFormat.format(new Date());
            selectedTime = timeFormat.format(new Date());

            // Update modal's marked date/time
            modal.setMarkedDate(selectedDate);
            modal.setMarkedTime(selectedTime);

            // Update the UI and Firestore based on the new status
            date.setText(selectedDate);
            time.setText(selectedTime);
            updateItemView(newStatus);
            updateStudentStatusInFirestore(modal, selectedDate, selectedTime);
        }

        private void updateItemView(boolean isPresent) {
            if (isPresent) {
                status.setText("Present");
                cardView.setBackgroundColor(Color.GREEN);
            } else {
                status.setText("Absent");
                cardView.setBackgroundColor(Color.RED);
            }
        }
    }
}
