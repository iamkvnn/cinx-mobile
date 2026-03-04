package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.ProfileVoucher;

import java.util.List;

/**
 * Renders ticket-style voucher cards for the user's voucher wallet.
 *
 * Each item shows: a colored left panel (discount value), title,
 * description, expiry date, and action buttons.
 */
public class VoucherProfileAdapter extends RecyclerView.Adapter<VoucherProfileAdapter.VH> {

    // ─────────────────────────────────────────────────────────────────
    // Listener
    // ─────────────────────────────────────────────────────────────────

    public interface VoucherListener {
        void onUseVoucher(ProfileVoucher voucher);
        void onSeeDetail(ProfileVoucher voucher);
    }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final List<ProfileVoucher> items;
    private final VoucherListener      listener;

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public VoucherProfileAdapter(List<ProfileVoucher> items, VoucherListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_profile, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProfileVoucher voucher = items.get(position);
        Context ctx = h.itemView.getContext();

        // Left panel
        h.panelLeft.setBackgroundResource(voucher.getCardColorRes());
        h.tvDiscountValue.setText(voucher.getDiscountLabel());
        h.tvDiscountSub  .setText(voucher.getDiscountSubLabel());

        // Right panel
        h.tvTitle      .setText(voucher.getTitle());
        h.tvDescription.setText(voucher.getDescription());
        h.tvExpiry     .setText(ctx.getString(R.string.voucher_expiry_prefix, voucher.getExpiryDate()));

        // Quantity badge
        if (voucher.getQuantity() > 0) {
            h.tvQuantity.setVisibility(View.VISIBLE);
            h.tvQuantity.setText(ctx.getString(R.string.voucher_quantity, voucher.getQuantity()));
        } else {
            h.tvQuantity.setVisibility(View.GONE);
        }

        // Dim expired/used
        boolean active = voucher.getStatus() == ProfileVoucher.Status.AVAILABLE;
        h.itemView.setAlpha(active ? 1f : 0.55f);
        h.btnUse.setVisibility(active ? View.VISIBLE : View.GONE);

        // Clicks
        h.btnUse      .setOnClickListener(v -> listener.onUseVoucher(voucher));
        h.tvSeeDetail .setOnClickListener(v -> listener.onSeeDetail(voucher));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─────────────────────────────────────────────────────────────────
    // Data update helper
    // ─────────────────────────────────────────────────────────────────

    /** Replace the displayed list and refresh. */
    public void setItems(List<ProfileVoucher> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout panelLeft;
        TextView     tvDiscountValue;
        TextView     tvDiscountSub;
        TextView     tvTitle;
        TextView     tvDescription;
        TextView     tvSeeDetail;
        TextView     tvExpiry;
        TextView     tvQuantity;
        TextView     btnUse;

        VH(@NonNull View v) {
            super(v);
            panelLeft      = v.findViewById(R.id.panelLeft);
            tvDiscountValue = v.findViewById(R.id.tvDiscountValue);
            tvDiscountSub   = v.findViewById(R.id.tvDiscountSub);
            tvTitle         = v.findViewById(R.id.tvTitle);
            tvDescription   = v.findViewById(R.id.tvDescription);
            tvSeeDetail     = v.findViewById(R.id.tvSeeDetail);
            tvExpiry        = v.findViewById(R.id.tvExpiry);
            tvQuantity      = v.findViewById(R.id.tvQuantity);
            btnUse          = v.findViewById(R.id.btnUse);
        }
    }
}
