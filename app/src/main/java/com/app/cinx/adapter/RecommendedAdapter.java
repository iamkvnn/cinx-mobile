package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Course;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private List<Course> courses;

    public RecommendedAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle;
        TextView courseDesc;
        TextView courseCategory;
        TextView coursePrice;
        TextView courseDuration;
        TextView courseRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDesc = itemView.findViewById(R.id.courseDesc);
            courseCategory = itemView.findViewById(R.id.courseCategory);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            courseDuration = itemView.findViewById(R.id.courseDuration);
            courseRating = itemView.findViewById(R.id.courseRating);
        }

        public void bind(Course course) {
            courseTitle.setText(course.getTitle());
            courseDesc.setText(course.getInstructor());
            
            if (courseCategory != null) {
                courseCategory.setText(course.getCategory());
                // Simple logic to change color based on category if needed
                if ("Code".equalsIgnoreCase(course.getCategory())) {
                    courseCategory.setTextColor(android.graphics.Color.parseColor("#0EA5E9")); // Sky 500
                    courseCategory.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0F9FF"))); // Sky 50
                } else if ("Design".equalsIgnoreCase(course.getCategory())) {
                    courseCategory.setTextColor(android.graphics.Color.parseColor("#D946EF")); // Fucshia 500
                    courseCategory.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FDF4FF"))); // Fucshia 50
                } else {
                     courseCategory.setTextColor(android.graphics.Color.parseColor("#64748B"));
                     courseCategory.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F1F5F9")));
                }
            }
            
            if (coursePrice != null) coursePrice.setText(course.getPrice());
            if (courseDuration != null) courseDuration.setText(course.getDuration());
            if (courseRating != null) courseRating.setText(String.valueOf(course.getRating()));

            Glide.with(itemView.getContext())
                    .load(course.getImageUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(24)))
                    .into(courseImage);
        }
    }
}
