package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Voucher;

import java.util.List;

/**
 * Adapter for the Voucher-picker bottom sheet.
 *
 * Only one voucher can be selected at a time; the adapter tracks the
 * currently selected ID and exposes it via {@link #getSelectedVoucher()}.
 */
public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    // ─────────────────────────────────────────────────────────────────────
    // Interface
    // ─────────────────────────────────────────────────────────────────────

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(Voucher voucher);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────────

    private final List<Voucher>             vouchers;
    private final OnVoucherSelectedListener listener;
    private       String                    selectedId;   // null = none

    // ─────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────

    public VoucherAdapter(List<Voucher> vouchers,
                          String selectedId,
                          OnVoucherSelectedListener listener) {
        this.vouchers   = vouchers;
        this.selectedId = selectedId;
        this.listener   = listener;
    }

    // ─────────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        holder.bind(vouchers.get(position));
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public helpers
    // ─────────────────────────────────────────────────────────────────────

    public Voucher getSelectedVoucher() {
        if (selectedId == null) return null;
        for (Voucher v : vouchers) {
            if (v.getId().equals(selectedId)) return v;
        }
        return null;
    }

    /** Pre-selects a specific voucher (e.g. to reflect existing applied voucher). */
    public void setSelectedId(String id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────────

    class VoucherViewHolder extends RecyclerView.ViewHolder {

        private final TextView    tvVoucherTitle;
        private final TextView    tvVoucherDesc;
        private final TextView    tvVoucherCode;
        private final RadioButton rbVoucher;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvVoucherDesc  = itemView.findViewById(R.id.tvVoucherDesc);
            tvVoucherCode  = itemView.findViewById(R.id.tvVoucherCode);
            rbVoucher      = itemView.findViewById(R.id.rbVoucher);
        }

        void bind(Voucher voucher) {
            tvVoucherTitle.setText(voucher.getTitle());
            tvVoucherDesc.setText(voucher.getDescription());
            tvVoucherCode.setText(
                    itemView.getContext().getString(R.string.cart_voucher_code_label, voucher.getCode())
            );

            boolean isSelected = voucher.getId().equals(selectedId);
            rbVoucher.setChecked(isSelected);

            // Highlight selected border
            itemView.setSelected(isSelected);

            View.OnClickListener click = v -> {
                String prevId = selectedId;
                selectedId = voucher.getId();
                // Refresh previous + current to update radio states
                notifyDataSetChanged();
                listener.onVoucherSelected(voucher);
            };

            itemView.setOnClickListener(click);
            rbVoucher.setOnClickListener(click);
        }
    }
}
