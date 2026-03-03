package com.app.cinx.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.PaymentMethod;

import java.util.List;

/**
 * Displays a list of {@link PaymentMethod} items as radio-style selectable rows.
 *
 * Selection is exclusive (single-choice). The host Activity/Fragment is notified
 * via {@link OnPaymentSelectedListener}; it should NOT attempt to manage selection
 * state itself — the adapter is the single source of truth.
 */
public class PaymentMethodAdapter
        extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

    // ─────────────────────────────────────────────────────────────────────
    // Listener
    // ─────────────────────────────────────────────────────────────────────

    public interface OnPaymentSelectedListener {
        void onPaymentSelected(@NonNull PaymentMethod method);
    }

    // ─────────────────────────────────────────────────────────────────────
    // State
    // ─────────────────────────────────────────────────────────────────────

    private final List<PaymentMethod>      methods;
    private final OnPaymentSelectedListener listener;
    private       int                       selectedIndex = 0; // default first item

    // ─────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────

    public PaymentMethodAdapter(@NonNull List<PaymentMethod> methods,
                                @NonNull OnPaymentSelectedListener listener) {
        this.methods  = methods;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethod method   = methods.get(position);
        boolean       selected = (position == selectedIndex);

        // Text
        holder.tvName.setText(method.getName());
        holder.tvDesc.setText(method.getDescription());

        // Icon
        holder.ivIcon.setImageResource(method.getIconRes());
        holder.iconWrapper.setBackgroundResource(method.getIconBgRes());

        // Radio state
        holder.rbPayment.setChecked(selected);

        // Row background — swap drawable for visual feedback
        holder.itemView.setBackground(
                ContextCompat.getDrawable(holder.itemView.getContext(),
                        selected ? R.drawable.bg_payment_method_selected
                                 : R.drawable.bg_payment_method_item));

        // Click handler
        holder.itemView.setOnClickListener(v -> {
            int prev = selectedIndex;
            selectedIndex = holder.getAdapterPosition();
            if (prev != selectedIndex) {
                notifyItemChanged(prev);
                notifyItemChanged(selectedIndex);
                animateSelection(holder.itemView);
                listener.onPaymentSelected(methods.get(selectedIndex));
            }
        });
    }

    @Override
    public int getItemCount() {
        return methods.size();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public helpers
    // ─────────────────────────────────────────────────────────────────────

    /** Returns the currently selected payment method, or {@code null} if the list is empty. */
    public PaymentMethod getSelectedMethod() {
        if (methods.isEmpty()) return null;
        return methods.get(selectedIndex);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Animation
    // ─────────────────────────────────────────────────────────────────────

    private void animateSelection(@NonNull View view) {
        // Brief scale-bounce to signal the selection
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.97f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.97f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(200);
        set.start();
    }

    // ─────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────────

    static class ViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout  iconWrapper;
        final ImageView    ivIcon;
        final TextView     tvName;
        final TextView     tvDesc;
        final RadioButton  rbPayment;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconWrapper = itemView.findViewById(R.id.iconWrapper);
            ivIcon      = itemView.findViewById(R.id.ivPaymentIcon);
            tvName      = itemView.findViewById(R.id.tvPaymentName);
            tvDesc      = itemView.findViewById(R.id.tvPaymentDesc);
            rbPayment   = itemView.findViewById(R.id.rbPayment);
        }
    }
}
