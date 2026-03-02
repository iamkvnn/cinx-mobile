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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class InProgressCourseAdapter extends RecyclerView.Adapter<InProgressCourseAdapter.ViewHolder> {

    private List<CourseItem> courses;

    public InProgressCourseAdapter(List<CourseItem> courses) {
        this.courses = courses;
    }

    public void updateCourses(List<CourseItem> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_progress_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(courses.get(position));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCourse;
        TextView tvTitle, tvNextLesson, tvProgress;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourse = itemView.findViewById(R.id.imgCourse);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNextLesson = itemView.findViewById(R.id.tvNextLesson);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        void bind(CourseItem course) {
            tvTitle.setText(course.title);
            tvNextLesson.setText("Tiếp: " + course.nextLesson);
            tvProgress.setText(course.progress + "%");
            progressBar.setProgress(course.progress);

            Glide.with(imgCourse.getContext())
                    .load(course.imageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(imgCourse);
        }
    }

    public static class CourseItem {
        public String title;
        public int progress;
        public String nextLesson;
        public String imageUrl;
        public String color;

        public CourseItem(String title, int progress, String nextLesson, String imageUrl, String color) {
            this.title = title;
            this.progress = progress;
            this.nextLesson = nextLesson;
            this.imageUrl = imageUrl;
            this.color = color;
        }
    }
}
