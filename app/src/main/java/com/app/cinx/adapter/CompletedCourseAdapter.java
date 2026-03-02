package com.app.cinx.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CompletedCourseAdapter extends RecyclerView.Adapter<CompletedCourseAdapter.ViewHolder> {

    private List<CourseItem> courses;

    public CompletedCourseAdapter(List<CourseItem> courses) {
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
                .inflate(R.layout.item_completed_course, parent, false);
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
        TextView tvTitle, tvDate, tvGrade;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourse = itemView.findViewById(R.id.imgCourse);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvGrade = itemView.findViewById(R.id.tvGrade);
        }

        void bind(CourseItem course) {
            tvTitle.setText(course.title);
            tvDate.setText("Hoàn thành: " + course.date);
            tvGrade.setText("Điểm: " + course.grade);

            Glide.with(imgCourse.getContext())
                    .load(course.imageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(imgCourse);

            // Apply grayscale filter
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            imgCourse.setColorFilter(new ColorMatrixColorFilter(matrix));
        }
    }

    public static class CourseItem {
        public String title;
        public String grade;
        public String date;
        public String imageUrl;

        public CourseItem(String title, String grade, String date, String imageUrl) {
            this.title = title;
            this.grade = grade;
            this.date = date;
            this.imageUrl = imageUrl;
        }
    }
}
