package com.app.cinx.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.activity.CourseDetailActivity;
// import com.app.cinx.activity.CourseOutlineActivity;
import com.app.cinx.api.dto.CourseResponse;
import com.bumptech.glide.Glide;

import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.CourseViewHolder> {

    private final Context context;
    private final List<CourseResponse> courses;

    public InstructorCourseAdapter(Context context, List<CourseResponse> courses) {
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseResponse course = courses.get(position);
        
        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvCoursePrice.setText(String.format("%,dđ", course.getPrice()));
        
        holder.tvCourseStatus.setText(Boolean.TRUE.equals(course.getIsPublished()) ? "Đã xuất bản" : "Bản nháp");
        holder.tvCourseStatus.setTextColor(Boolean.TRUE.equals(course.getIsPublished()) 
                ? context.getResources().getColor(R.color.primary) 
                : 0xFFF59E0B); // Amber color for draft

        if (course.getImages() != null && !course.getImages().isEmpty()) {
            String imageUrl = course.getImages().get(0).getImageUrl();
            if (imageUrl != null) {
                Glide.with(context)
                     .load(imageUrl)
                     .placeholder(R.drawable.ic_profile_placeholder)
                     .into(holder.imgCourse);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.app.cinx.activity.CourseEditActivity.class);
            intent.putExtra("COURSE_ID", course.getId());
            context.startActivity(intent);
            android.widget.Toast.makeText(context, "Mở quản lý khóa " + course.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return courses == null ? 0 : courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCourse;
        TextView tvCourseTitle;
        TextView tvCourseStatus;
        TextView tvCoursePrice;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourse = itemView.findViewById(R.id.imgCourse);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseStatus = itemView.findViewById(R.id.tvCourseStatus);
            tvCoursePrice = itemView.findViewById(R.id.tvCoursePrice);
        }
    }
}
