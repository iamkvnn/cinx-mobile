package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Testimonial;
import com.bumptech.glide.Glide;

import java.util.List;

public class TestimonialAdapter extends RecyclerView.Adapter<TestimonialAdapter.TestimonialViewHolder> {

    private List<Testimonial> testimonials;

    public TestimonialAdapter(List<Testimonial> testimonials) {
        this.testimonials = testimonials;
    }

    @NonNull
    @Override
    public TestimonialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_testimonial, parent, false);
        return new TestimonialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestimonialViewHolder holder, int position) {
        if (testimonials == null || testimonials.isEmpty()) return;
        Testimonial testimonial = testimonials.get(position % testimonials.size());
        holder.bind(testimonial);
    }

    @Override
    public int getItemCount() {
        return testimonials == null || testimonials.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    static class TestimonialViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView testimonialText;

        public TestimonialViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            testimonialText = itemView.findViewById(R.id.testimonialText);
        }

        public void bind(Testimonial testimonial) {
            userName.setText(testimonial.getUserName());
            testimonialText.setText("\"" + testimonial.getText() + "\"");

            Glide.with(itemView.getContext())
                    .load(testimonial.getAvatarUrl())
                    .circleCrop()
                    .into(userAvatar);
        }
    }
}
