package com.app.cinx.adapter;

import com.app.cinx.utils.PriceUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.CourseResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseResponse> courses;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(CourseResponse course);
        void onFavoriteClick(CourseResponse course);
    }

    public CourseAdapter(List<CourseResponse> courses, OnCourseClickListener listener) {
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
        CourseResponse course = courses.get(position);
        holder.bind(course, listener);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
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

        public void bind(CourseResponse course, OnCourseClickListener listener) {
            categoryBadge.setText(course.getCategory() != null ? course.getCategory() : "Khóa học");
            courseRating.setText(String.valueOf(course.getRating() != null ? course.getRating() : 0.0));
            long students = course.getEnrollmentCount() != null ? course.getEnrollmentCount() : 0L;
            courseStudents.setText("(" + students + ")");
            courseTitle.setText(course.getTitle());
            instructorName.setText(course.getInstructor().getName() != null ? course.getInstructor().getName() : "Giảng viên"); // fallback instructor

            long price = course.getPrice() != null ? course.getPrice() : 0L;
            long discountedPriceObj = course.getDiscountedPrice() != null ? course.getDiscountedPrice() : price;
            long discountPct = course.getDiscountRate() != null ? course.getDiscountRate() : 0L;

            if (discountPct > 0) {
                coursePrice.setText(PriceUtil.formatPrice(discountedPriceObj));
                if (originalPrice != null) {
                    originalPrice.setVisibility(View.VISIBLE);
                    originalPrice.setText(PriceUtil.formatPrice(price));
                    originalPrice.setPaintFlags(originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }
                if (discountRate != null) {
                    discountRate.setVisibility(View.VISIBLE);
                    discountRate.setText("-" + discountPct + "%");
                }
            } else {
                coursePrice.setText(PriceUtil.formatPrice(price));
                if (originalPrice != null) originalPrice.setVisibility(View.GONE);
                if (discountRate != null) discountRate.setVisibility(View.GONE);
            }


            // Load course image
            Glide.with(itemView.getContext())
                    .load("https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=300&auto=format&fit=crop")
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
