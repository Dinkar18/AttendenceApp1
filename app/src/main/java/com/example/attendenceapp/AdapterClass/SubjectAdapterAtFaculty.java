package com.example.attendenceapp.AdapterClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendenceapp.ModelClass.SubjectModal;
import com.example.attendenceapp.R;

import java.util.List;

public class SubjectAdapterAtFaculty extends RecyclerView.Adapter<SubjectAdapterAtFaculty.ViewHolder> {

    private final List<SubjectModal> subjectList;
    private final OnSubjectClickListener onSubjectClickListener;

    public SubjectAdapterAtFaculty(List<SubjectModal> subjectList, OnSubjectClickListener onSubjectClickListener) {
        this.subjectList = subjectList;
        this.onSubjectClickListener = onSubjectClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_subject_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectModal subject = subjectList.get(position);
        holder.subjectName.setText(subject.getSubjectName());
        holder.subjectCode.setText(subject.getSubjectCode());

        holder.itemView.setOnClickListener(v -> onSubjectClickListener.onSubjectClick(subject));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public interface OnSubjectClickListener {
        void onSubjectClick(SubjectModal subject);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, subjectCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.SubjectName);
            subjectCode = itemView.findViewById(R.id.subjectCode);
        }
    }
}
