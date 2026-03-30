package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.CourseResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ContinueLearningAdapter extends RecyclerView.Adapter<ContinueLearningAdapter.ViewHolder> {

    private List<CourseResponse> courses;

    public ContinueLearningAdapter(List<CourseResponse> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_continue_learning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseResponse course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle;
        TextView chapterTitle;
        ProgressBar progressBar;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            chapterTitle = itemView.findViewById(R.id.chapterTitle);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(CourseResponse course) {
            courseTitle.setText(course.getTitle());
            chapterTitle.setText(course.getCategory() != null ? course.getCategory() : "Khóa học"); 
            // Mock progress
            progressBar.setProgress((int)(Math.random() * 100));
        }
    }
}
