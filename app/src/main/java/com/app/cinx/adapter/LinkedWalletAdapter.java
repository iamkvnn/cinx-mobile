package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.LinkedWallet;

import java.util.List;

/**
 * Displays linked e-wallets (MoMo, VNPAY, etc.) with an "Unlink" action.
 */
public class LinkedWalletAdapter extends RecyclerView.Adapter<LinkedWalletAdapter.VH> {

    // ─────────────────────────────────────────────────────────────────
    // Listener
    // ─────────────────────────────────────────────────────────────────

    public interface WalletListener {
        void onUnlink(LinkedWallet wallet);
    }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final List<LinkedWallet> wallets;
    private final WalletListener     listener;

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public LinkedWalletAdapter(List<LinkedWallet> wallets, WalletListener listener) {
        this.wallets  = wallets;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_linked_wallet, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        LinkedWallet wallet = wallets.get(position);

        // Icon
        h.ivWalletIcon.setImageResource(wallet.getIconRes());
        h.iconContainer.getBackground().setTint(wallet.getIconBgColor());

        // Text
        h.tvWalletName .setText(wallet.getDisplayName());
        h.tvMaskedPhone.setText(wallet.getMaskedPhone());

        // Unlink
        h.btnUnlink.setOnClickListener(v -> listener.onUnlink(wallet));
    }

    @Override
    public int getItemCount() { return wallets.size(); }

    // ─────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        FrameLayout iconContainer;
        ImageView   ivWalletIcon;
        TextView    tvWalletName;
        TextView    tvMaskedPhone;
        TextView    btnUnlink;

        VH(@NonNull View v) {
            super(v);
            iconContainer = v.findViewById(R.id.iconContainer);
            ivWalletIcon  = v.findViewById(R.id.ivWalletIcon);
            tvWalletName  = v.findViewById(R.id.tvWalletName);
            tvMaskedPhone = v.findViewById(R.id.tvMaskedPhone);
            btnUnlink     = v.findViewById(R.id.btnUnlink);
        }
    }
}
