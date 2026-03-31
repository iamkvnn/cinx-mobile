package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.LessonResponse;

import java.util.List;

public class InstructorLessonAdapter extends RecyclerView.Adapter<InstructorLessonAdapter.ViewHolder> {

    private Context context;
    private List<LessonResponse> lessonList;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onEditClick(LessonResponse lesson);
        void onDeleteClick(LessonResponse lesson);`n        void onLessonItemClick(LessonResponse lesson);
    }

    public InstructorLessonAdapter(Context context, List<LessonResponse> lessonList, OnLessonClickListener listener) {
        this.context = context;
        this.lessonList = lessonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonResponse lesson = lessonList.get(position);
        holder.tvLessonTitle.setText(lesson.getTitle() != null ? lesson.getTitle() : "Chua co ten");
        
        if ("VIDEO".equalsIgnoreCase(lesson.getLessonType())) {
            holder.imgLessonType.setImageResource(R.drawable.ic_play_circle);
        } else if ("ARTICLE".equalsIgnoreCase(lesson.getLessonType())) {
            holder.imgLessonType.setImageResource(R.drawable.ic_document);
        } else if ("QUIZ".equalsIgnoreCase(lesson.getLessonType())) {
            holder.imgLessonType.setImageResource(R.drawable.ic_quiz);
        } else {
            holder.imgLessonType.setImageResource(R.drawable.ic_document);
        }

        holder.imgEditLesson.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(lesson);
        });

        holder.itemView.setOnClickListener(v -> {`n            if (listener != null) listener.onLessonItemClick(lesson);`n        });`n`n        holder.imgDeleteLesson.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(lesson);
        });
    }

    @Override
    public int getItemCount() {
        return lessonList != null ? lessonList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLessonType, imgEditLesson, imgDeleteLesson;
        TextView tvLessonTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLessonType = itemView.findViewById(R.id.imgLessonType);
            imgEditLesson = itemView.findViewById(R.id.imgEditLesson);
            imgDeleteLesson = itemView.findViewById(R.id.imgDeleteLesson);
            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
        }
    }
}
