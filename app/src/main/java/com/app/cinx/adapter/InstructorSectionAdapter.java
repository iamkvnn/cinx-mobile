package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.SectionResponse;

import java.util.ArrayList;
import java.util.List;

public class InstructorSectionAdapter extends RecyclerView.Adapter<InstructorSectionAdapter.ViewHolder> {

    private Context context;
    private List<SectionResponse> sectionList;
    private OnSectionClickListener listener;

    public interface OnSectionClickListener {
        void onEditSection(SectionResponse section);
        void onDeleteSection(SectionResponse section);
        void onAddLesson(SectionResponse section);
        void onEditLesson(SectionResponse section, com.app.cinx.api.dto.LessonResponse lesson);
        void onDeleteLesson(SectionResponse section, com.app.cinx.api.dto.LessonResponse lesson);
        void onLessonItemClick(SectionResponse section, com.app.cinx.api.dto.LessonResponse lesson);
    }

    public InstructorSectionAdapter(Context context, List<SectionResponse> sectionList, OnSectionClickListener listener) {
        this.context = context;
        this.sectionList = sectionList != null ? sectionList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<SectionResponse> newData) {
        this.sectionList = newData != null ? newData : new ArrayList<>();
        notifyDataSetDataSetChanged();
    }
    
    private void notifyDataSetDataSetChanged() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SectionResponse section = sectionList.get(position);
        holder.tvSectionTitle.setText(section.getTitle() != null ? section.getTitle() : "Chuong " + (position + 1));

        holder.imgEditSection.setOnClickListener(v -> {
            if (listener != null) listener.onEditSection(section);
        });

        holder.imgDeleteSection.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteSection(section);
        });

        holder.btnAddLesson.setOnClickListener(v -> {
            if (listener != null) listener.onAddLesson(section);
        });

        if (section.getLessons() != null && !section.getLessons().isEmpty()) {
            InstructorLessonAdapter lessonAdapter = new InstructorLessonAdapter(context, section.getLessons(), new InstructorLessonAdapter.OnLessonClickListener() {
                @Override
                public void onEditClick(com.app.cinx.api.dto.LessonResponse lesson) {
                    if (listener != null) listener.onEditLesson(section, lesson);
                }

                @Override
                public void onLessonItemClick(com.app.cinx.api.dto.LessonResponse lesson) {
                    if (listener != null) listener.onLessonItemClick(section, lesson);
                }

                @Override
                public void onDeleteClick(com.app.cinx.api.dto.LessonResponse lesson) {
                    if (listener != null) listener.onDeleteLesson(section, lesson);
                }
            });
            holder.rvLessons.setLayoutManager(new LinearLayoutManager(context));
            holder.rvLessons.setAdapter(lessonAdapter);
            holder.rvLessons.setVisibility(View.VISIBLE);
        } else {
            holder.rvLessons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle;
        ImageView imgEditSection, imgDeleteSection;
        RecyclerView rvLessons;
        Button btnAddLesson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            imgEditSection = itemView.findViewById(R.id.imgEditSection);
            imgDeleteSection = itemView.findViewById(R.id.imgDeleteSection);
            rvLessons = itemView.findViewById(R.id.rvLessons);
            btnAddLesson = itemView.findViewById(R.id.btnAddLesson);
        }
    }
}
