package com.app.cinx.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Order;
import com.app.cinx.model.OrderItem;
import com.app.cinx.utils.Convert;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * RecyclerView adapter for the Purchase History screen.
 *
 * Each card shows at most {@link #MAX_VISIBLE_COURSES} course rows
 * inline; any extra courses are summarised by a "See N more" label so
 * we avoid nested RecyclerViews and unlimited card height.
 *
 * Interaction events are surfaced via {@link OnOrderActionListener} so
 * the host Activity stays decoupled from adapter internals.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    // ─────────────────────────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────────────────────────

    /** Maximum number of course rows shown inline per card. */
    private static final int MAX_VISIBLE_COURSES = 2;

    // ─────────────────────────────────────────────────────────────────
    // Listener interface
    // ─────────────────────────────────────────────────────────────────

    public interface OnOrderActionListener {
        void onLeftAction(Order order);   // Đánh giá | Hủy đơn | Xem chi tiết
        void onRightAction(Order order);  // Vào học ngay | Thanh toán ngay | Mua lại
    }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private List<Order>             orders;
    private final OnOrderActionListener listener;

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public OrderAdapter(List<Order> orders, OnOrderActionListener listener) {
        this.orders   = orders;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────
    // Data update
    // ─────────────────────────────────────────────────────────────────

    /**
     * Replaces the data set and refreshes the list with a full rebind.
     * For production consider DiffUtil when the data source is live.
     */
    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter overrides
    // ─────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    // ─────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView     tvOrderId;
        private final TextView     tvOrderStatus;
        private final LinearLayout llOrderCourses;
        private final TextView     tvMoreItems;
        private final TextView     tvOrderTotal;
        private final TextView     btnLeft;
        private final TextView     btnRight;

        /** Tracks whether all course rows are currently shown. */
        private boolean isExpanded = false;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId      = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus  = itemView.findViewById(R.id.tvOrderStatus);
            llOrderCourses = itemView.findViewById(R.id.llOrderCourses);
            tvMoreItems    = itemView.findViewById(R.id.tvOrderMoreItems);
            tvOrderTotal   = itemView.findViewById(R.id.tvOrderTotal);
            btnLeft        = itemView.findViewById(R.id.btnOrderActionLeft);
            btnRight       = itemView.findViewById(R.id.btnOrderActionRight);
        }

        void bind(Order order) {
            Context ctx = itemView.getContext();

            // ── Order ID ──────────────────────────────────────────
            tvOrderId.setText(order.getOrderId());

            // ── Status badge ──────────────────────────────────────
            applyStatusStyle(ctx, tvOrderStatus, order.getStatus());

            // ── Course rows ───────────────────────────────────────
            isExpanded = false; // reset on each rebind
            llOrderCourses.removeAllViews();
            List<OrderItem> items = order.getItems();
            int visibleCount = Math.min(items.size(), MAX_VISIBLE_COURSES);

            LayoutInflater inflater = LayoutInflater.from(ctx);
            for (int i = 0; i < visibleCount; i++) {
                bindCourseRow(inflater, llOrderCourses, items.get(i));
            }

            int hiddenCount = items.size() - visibleCount;
            if (hiddenCount > 0) {
                tvMoreItems.setVisibility(View.VISIBLE);
                tvMoreItems.setText(ctx.getString(R.string.order_more_items, hiddenCount));
                tvMoreItems.setOnClickListener(v -> {
                    if (!isExpanded) {
                        // Expand: add hidden rows
                        for (int i = visibleCount; i < items.size(); i++) {
                            bindCourseRow(inflater, llOrderCourses, items.get(i));
                        }
                        tvMoreItems.setText(ctx.getString(R.string.order_collapse));
                        isExpanded = true;
                    } else {
                        // Collapse: remove all and re-add only visible
                        llOrderCourses.removeAllViews();
                        for (int i = 0; i < visibleCount; i++) {
                            bindCourseRow(inflater, llOrderCourses, items.get(i));
                        }
                        tvMoreItems.setText(ctx.getString(R.string.order_more_items, hiddenCount));
                        isExpanded = false;
                    }
                });
            } else {
                tvMoreItems.setVisibility(View.GONE);
                tvMoreItems.setOnClickListener(null);
            }

            // ── Total ─────────────────────────────────────────────
            tvOrderTotal.setText(Convert.formatVnd(order.getTotalAmount()));

            // ── Action buttons ────────────────────────────────────
            applyActionButtons(ctx, order);
        }

        // ── Status badge styling ──────────────────────────────────

        private void applyStatusStyle(Context ctx, TextView badge, Order.Status status) {
            switch (status) {
                case COMPLETED:
                    badge.setText(ctx.getString(R.string.order_status_completed));
                    badge.setBackgroundResource(R.drawable.bg_order_status_completed);
                    badge.setTextColor(ctx.getColor(R.color.order_status_completed_text));
                    break;
                case PENDING:
                    badge.setText(ctx.getString(R.string.order_status_pending));
                    badge.setBackgroundResource(R.drawable.bg_order_status_pending);
                    badge.setTextColor(ctx.getColor(R.color.order_status_pending_text));
                    break;
                case CANCELLED:
                    badge.setText(ctx.getString(R.string.order_status_cancelled));
                    badge.setBackgroundResource(R.drawable.bg_order_status_cancelled);
                    badge.setTextColor(ctx.getColor(R.color.order_status_cancelled_text));
                    break;
            }
        }

        // ── Inflate + bind a single course row ───────────────────

        private void bindCourseRow(LayoutInflater inflater, LinearLayout container,
                                   OrderItem item) {
            View row = inflater.inflate(R.layout.item_order_course, container, false);

            ImageView ivImage    = row.findViewById(R.id.ivOrderCourseImage);
            TextView  tvTitle    = row.findViewById(R.id.tvOrderCourseTitle);
            TextView  tvInstr    = row.findViewById(R.id.tvOrderCourseInstructor);
            TextView  tvOriginal = row.findViewById(R.id.tvOrderCourseOriginalPrice);
            TextView  tvSale     = row.findViewById(R.id.tvOrderCourseSalePrice);

            Glide.with(ivImage.getContext())
                    .load(item.getImageUrl())
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(Convert.dpToPx(row.getContext(), 8)))
                            .placeholder(R.drawable.bg_glass_card)
                            .error(R.drawable.bg_glass_card))
                    .into(ivImage);

            tvTitle.setText(item.getTitle());
            tvInstr.setText(row.getContext()
                    .getString(R.string.order_by_instructor, item.getInstructor()));

            // Strikethrough original price
            tvOriginal.setText(Convert.formatVnd(item.getOriginalPrice()));
            tvOriginal.setPaintFlags(tvOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvSale.setText(Convert.formatVnd(item.getSalePrice()));

            container.addView(row);
        }

        // ── Contextual action buttons ─────────────────────────────

        private void applyActionButtons(Context ctx, Order order) {
            switch (order.getStatus()) {
                case COMPLETED:
                    btnLeft.setText(ctx.getString(R.string.order_btn_review));
                    btnLeft.setBackgroundResource(R.drawable.bg_btn_outline);
                    btnLeft.setTextColor(ctx.getColor(R.color.text_secondary));

                    btnRight.setText(ctx.getString(R.string.order_btn_go_learn));
                    btnRight.setBackgroundResource(R.drawable.bg_gradient_button);
                    btnRight.setTextColor(ctx.getColor(R.color.white));
                    break;

                case PENDING:
                    btnLeft.setText(ctx.getString(R.string.order_btn_cancel));
                    btnLeft.setBackgroundResource(R.drawable.bg_btn_outline);
                    btnLeft.setTextColor(ctx.getColor(R.color.text_secondary));

                    btnRight.setText(ctx.getString(R.string.order_btn_pay_now));
                    btnRight.setBackgroundResource(R.drawable.bg_btn_dark_full);
                    btnRight.setTextColor(ctx.getColor(R.color.white));
                    break;

                case CANCELLED:
                    btnLeft.setText(ctx.getString(R.string.order_btn_view_detail));
                    btnLeft.setBackgroundResource(R.drawable.bg_btn_outline);
                    btnLeft.setTextColor(ctx.getColor(R.color.text_secondary));

                    btnRight.setText(ctx.getString(R.string.order_btn_repurchase));
                    btnRight.setBackgroundResource(R.drawable.bg_btn_repurchase);
                    btnRight.setTextColor(ctx.getColor(R.color.order_tab_active_text));
                    break;
            }

            btnLeft.setOnClickListener(v -> {
                if (listener != null) listener.onLeftAction(order);
            });
            btnRight.setOnClickListener(v -> {
                if (listener != null) listener.onRightAction(order);
            });
        }
    }
}
