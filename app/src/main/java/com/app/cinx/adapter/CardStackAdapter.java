package com.app.cinx.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.BankCard;

import java.util.List;

/**
 * Adapter for the card-stack {@link androidx.viewpager2.widget.ViewPager2}.
 *
 * Each page renders one virtual bank/credit card with a dynamic gradient
 * applied at runtime based on {@link BankCard#getColorStart()} /
 * {@link BankCard#getColorEnd()}.
 */
public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.VH> {

    private final List<BankCard> cards;

    public CardStackAdapter(List<BankCard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        BankCard card = cards.get(position);

        // Apply dynamic gradient to card root
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ card.getColorStart(), card.getColorEnd() }
        );
        bg.setCornerRadius(h.cardRoot.getContext().getResources()
                .getDimensionPixelSize(R.dimen.card_corner_radius));
        h.cardRoot.setBackground(bg);

        // Network label
        h.tvNetwork       .setText(card.getNetwork().name()); // "VISA" or "MASTERCARD"
        h.tvCardNumber    .setText(card.getMaskedNumber());
        h.tvCardholderName.setText(card.getCardholderName());
        h.tvExpiry        .setText(card.getExpiry());
    }

    @Override
    public int getItemCount() { return cards.size(); }

    public BankCard getCard(int position) { return cards.get(position); }

    // ─────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout cardRoot;
        TextView     tvNetwork;
        TextView     tvCardNumber;
        TextView     tvCardholderName;
        TextView     tvExpiry;

        VH(@NonNull View v) {
            super(v);
            cardRoot         = v.findViewById(R.id.cardRoot);
            tvNetwork        = v.findViewById(R.id.tvNetwork);
            tvCardNumber     = v.findViewById(R.id.tvCardNumber);
            tvCardholderName = v.findViewById(R.id.tvCardholderName);
            tvExpiry         = v.findViewById(R.id.tvExpiry);
        }
    }
}
