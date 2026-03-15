package com.app.cinx.adapter;

import com.app.cinx.util.PriceUtil;
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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
        void onFavoriteClick(Course course);
    }

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course, listener);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView categoryBadge;
        TextView courseRating;
        TextView courseStudents;
        TextView courseTitle;
        ImageView instructorAvatar;
        TextView instructorName;
        TextView coursePrice;
        TextView originalPrice;
        TextView discountRate;
        View favButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            courseRating = itemView.findViewById(R.id.courseRating);
            courseStudents = itemView.findViewById(R.id.courseStudents);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            instructorAvatar = itemView.findViewById(R.id.instructorAvatar);
            instructorName = itemView.findViewById(R.id.instructorName);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountRate = itemView.findViewById(R.id.discountRate);
            favButton = itemView.findViewById(R.id.favButton);
        }

        public void bind(Course course, OnCourseClickListener listener) {
            categoryBadge.setText(course.getCategory());
            courseRating.setText(String.valueOf(course.getRating()));
            courseStudents.setText("(" + course.getStudents() + ")");
            courseTitle.setText(course.getTitle());
            instructorName.setText(course.getInstructor());
            
            if (course.getDiscountRate() > 0) {
                coursePrice.setText(PriceUtil.formatPrice(course.getDiscountedPrice()));
                if (originalPrice != null) {
                    originalPrice.setVisibility(View.VISIBLE);
                    originalPrice.setText(PriceUtil.formatPrice(course.getPrice()));
                    originalPrice.setPaintFlags(originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }
                if (discountRate != null) {
                    discountRate.setVisibility(View.VISIBLE);
                    discountRate.setText("-" + course.getDiscountRate() + "%");
                }
            } else {
                coursePrice.setText(PriceUtil.formatPrice(course.getPrice()));
                if (originalPrice != null) originalPrice.setVisibility(View.GONE);
                if (discountRate != null) discountRate.setVisibility(View.GONE);
            }


            // Load course image
            Glide.with(itemView.getContext())
                    .load(course.getImageUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(40)))
                    .into(courseImage);

            // Load instructor avatar
            String avatarUrl = "https://i.pravatar.cc/150?u=1";
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .circleCrop()
                    .into(instructorAvatar);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseClick(course);
                }
            });

            favButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(course);
                }
            });
        }
    }
}
