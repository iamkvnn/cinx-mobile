package com.app.cinx.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.CartItem;
import com.app.cinx.utils.Convert;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Adapter for the Cart screen's RecyclerView.
 *
 * Features:
 *  - Checkbox selection per item (with "select all" support via the host Activity).
 *  - Bind old / sale prices; old price is rendered with a strikethrough.
 *  - Exposes {@link OnCartInteractionListener} so the host can react to
 *    check-state changes and delete requests without coupling to this adapter.
 *
 * Swipe-to-delete is wired up externally via {@code ItemTouchHelper} in
 * {@code CartActivity}, which calls {@link #removeItem(int)} directly.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // ─────────────────────────────────────────────────────────────────────
    // Interface
    // ─────────────────────────────────────────────────────────────────────

    public interface OnCartInteractionListener {
        /** Called whenever any item's selected state changes. */
        void onSelectionChanged();
        /** Called when the user explicitly taps the inline delete button. */
        void onDeleteItem(int position);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────────

    private final List<CartItem>             items;
    private final OnCartInteractionListener  listener;

    // ─────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────

    public CartAdapter(List<CartItem> items, OnCartInteractionListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter overrides
    // ─────────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public helpers called from the Activity
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Removes item at the given adapter position and notifies the list.
     * Called by the Activity's ItemTouchHelper callback and the inline delete.
     */
    public void removeItem(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        notifyItemRemoved(position);
        listener.onSelectionChanged();
    }

    /** Selects/deselects all items. */
    public void setAllSelected(boolean selected) {
        for (CartItem item : items) {
            item.setSelected(selected);
        }
        notifyItemRangeChanged(0, items.size());
        listener.onSelectionChanged();
    }

    /** Returns true when every item is selected (or the list is empty). */
    public boolean areAllSelected() {
        if (items.isEmpty()) return false;
        for (CartItem item : items) {
            if (!item.isSelected()) return false;
        }
        return true;
    }

    /** Returns the live data list (used for calculating totals). */
    public List<CartItem> getItems() {
        return items;
    }

    // ─────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────────

    class CartViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox  cbSelect;
        private final ImageView ivThumbnail;
        private final TextView  tvTitle;
        private final TextView  tvInstructor;
        private final TextView  tvOldPrice;
        private final TextView  tvSalePrice;
        private final View      btnDelete;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect     = itemView.findViewById(R.id.cbSelect);
            ivThumbnail  = itemView.findViewById(R.id.ivThumbnail);
            tvTitle      = itemView.findViewById(R.id.tvCartTitle);
            tvInstructor = itemView.findViewById(R.id.tvCartInstructor);
            tvOldPrice   = itemView.findViewById(R.id.tvOldPrice);
            tvSalePrice  = itemView.findViewById(R.id.tvSalePrice);
            btnDelete    = itemView.findViewById(R.id.btnDeleteItem);
        }

        void bind(CartItem item) {
            // Checkbox
            cbSelect.setOnCheckedChangeListener(null); // clear old listener before setting value
            cbSelect.setChecked(item.isSelected());
            cbSelect.setOnCheckedChangeListener((btn, checked) -> {
                item.setSelected(checked);
                listener.onSelectionChanged();
            });

            // Title / instructor
            tvTitle.setText(item.getTitle());
            tvInstructor.setText(
                    itemView.getContext().getString(R.string.cart_by_instructor, item.getInstructor())
            );

            // Prices
            tvOldPrice.setText(Convert.formatVnd(item.getOriginalPrice()));
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvSalePrice.setText(Convert.formatVnd(item.getSalePrice()));

            // Thumbnail
            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.bg_glass_card)
                            .error(R.drawable.bg_glass_card)
                            .transform(new RoundedCorners(16)))
                    .into(ivThumbnail);

            // Inline delete button
            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_ID) {
                    listener.onDeleteItem(pos);
                }
            });

            // Tap on card also toggles checkbox
            itemView.setOnClickListener(v -> cbSelect.performClick());
        }
    }
}
