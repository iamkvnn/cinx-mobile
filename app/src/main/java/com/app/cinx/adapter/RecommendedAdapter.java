package com.app.cinx.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.activity.CourseDetailActivity;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.utils.PriceUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private List<CourseResponse> courses;

    public RecommendedAdapter(List<CourseResponse> courses) {
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
        CourseResponse course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle;
        TextView courseDesc;
        TextView courseCategory;
        TextView coursePrice;
        TextView courseDuration;
        TextView courseRating;
        TextView originalPrice;
        TextView discountRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDesc = itemView.findViewById(R.id.courseDesc);
            courseCategory = itemView.findViewById(R.id.courseCategory);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            courseDuration = itemView.findViewById(R.id.courseDuration);
            courseRating = itemView.findViewById(R.id.courseRating);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountRate = itemView.findViewById(R.id.discountRate);
        }

        public void bind(CourseResponse course) {
            courseTitle.setText(course.getTitle());
            courseDesc.setText(course.getDescription() != null ? course.getDescription() : "Giảng viên"); // fallback instructor or description

            if (courseCategory != null) {
                courseCategory.setText(course.getCategory() != null ? course.getCategory() : "Khóa học");
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
            
            long price = course.getPrice() != null ? course.getPrice() : 0L;
            long discountedPriceObj = course.getDiscountedPrice() != null ? course.getDiscountedPrice() : price;
            long discountPct = course.getDiscountRate() != null ? course.getDiscountRate() : 0L;

            if (discountPct > 0) {
                if (coursePrice != null) coursePrice.setText(PriceUtil.formatPrice(discountedPriceObj));
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
                if (coursePrice != null) {
                     coursePrice.setText(PriceUtil.formatPrice(price));
                     coursePrice.setTextColor(android.graphics.Color.parseColor("#1E293B"));
                }
                if (originalPrice != null) originalPrice.setVisibility(View.GONE);
                if (discountRate != null) discountRate.setVisibility(View.GONE);
            }
            if (courseDuration != null) {
                long duration = course.getDuration() != null ? course.getDuration() : 0L;
                courseDuration.setText(duration + "h");
            }
            if (courseRating != null) {
                double rating = course.getRating() != null ? course.getRating() : 0.0;
                courseRating.setText(String.valueOf(rating));
            }

            Glide.with(itemView.getContext())
                    .load("https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=800&q=80")
                    .apply(new RequestOptions().transform(new RoundedCorners(24)))
                    .into(courseImage);

            // Add click listener to navigate to CourseDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CourseDetailActivity.class);
                intent.putExtra("COURSE_ID", course.getId());
                intent.putExtra("COURSE_TITLE", course.getTitle());
                intent.putExtra("COURSE_PRICE", price);
                intent.putExtra("COURSE_DISCOUNTED_PRICE", discountedPriceObj);
                v.getContext().startActivity(intent);
            });
        }
    }
}
