package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.CartItemResponse;
import com.app.cinx.utils.Convert;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Read-only compact list of checked-out courses shown in the
 * Checkout screen's order-summary section.
 */
public class CheckoutCourseAdapter
        extends RecyclerView.Adapter<CheckoutCourseAdapter.ViewHolder> {

    private final List<CartItemResponse> items;

    public CheckoutCourseAdapter(@NonNull List<CartItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemResponse item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvInstructor.setText(
                holder.itemView.getContext().getString(R.string.cart_by_instructor,
                        item.getInstructor()));
        holder.tvPrice.setText(Convert.formatVnd(item.getSalePrice()));

        Glide.with(holder.ivThumb.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .centerCrop()
                .into(holder.ivThumb);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ShapeableImageView ivThumb;
        final TextView           tvTitle;
        final TextView           tvInstructor;
        final TextView           tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb      = itemView.findViewById(R.id.ivCheckoutThumb);
            tvTitle      = itemView.findViewById(R.id.tvCheckoutCourseTitle);
            tvInstructor = itemView.findViewById(R.id.tvCheckoutCourseInstructor);
            tvPrice      = itemView.findViewById(R.id.tvCheckoutCoursePrice);
        }
    }
}
