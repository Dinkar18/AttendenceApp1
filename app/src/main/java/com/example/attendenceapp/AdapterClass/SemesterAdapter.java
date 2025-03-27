package com.example.attendenceapp.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendenceapp.FacultyClassActivity;
import com.example.attendenceapp.R;

import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private List<String> semesterList;
    private Context context;

    public SemesterAdapter(List<String> semesterList, Context context) {
        this.semesterList = semesterList;
        this.context = context;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        String semester = semesterList.get(position);
        holder.semesterTextView.setText(semester);

        // Handle click on each semester item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FacultyClassActivity.class); // This is your target activity now
            intent.putExtra("semester", semester);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }

    static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView semesterTextView;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            semesterTextView = itemView.findViewById(R.id.semesterTextView);
        }
    }
}
