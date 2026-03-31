package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.QuizOptionResponse;

import java.util.List;

/**
 * QuizOptionAdapter
 * Drives the options list inside view_lesson_quiz.xml.
 *
 * Three display states (set after answer submission):
 *  - DEFAULT   — not answered yet; radio outline only
 *  - SELECTED  — user tapped this option (purple border), awaiting reveal
 *  - CORRECT   — this was the right answer (green border + check icon)
 *  - WRONG     — user selected this but it was wrong (red border + X icon)
 */
public class QuizOptionAdapter extends RecyclerView.Adapter<QuizOptionAdapter.OptionViewHolder> {

    /** Display state for a single option row. */
    public enum OptionState { DEFAULT, SELECTED, CORRECT, WRONG }

    /** Callback fired when user taps an option (only if quiz is not yet answered). */
    public interface OnOptionSelectedListener {
        void onOptionSelected(int position, QuizOptionResponse option);
    }

    // ── Fields ──────────────────────────────────────────────────────────────────────
    private final Context context;
    private final List<QuizOptionResponse> options;
    private final OptionState[] states;
    private boolean answered = false;
    private OnOptionSelectedListener listener;

    public QuizOptionAdapter(Context context, List<QuizOptionResponse> options) {
        this.context = context;
        this.options = options;
        this.states = new OptionState[options.size()];
        for (int i = 0; i < states.length; i++) states[i] = OptionState.DEFAULT;
    }

    // ── Public API ──────────────────────────────────────────────────────────────────

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Mark an option as selected (before answering).
     * @param selectedIndex index tapped by user
     */
    public void selectOption(int selectedIndex) {
        if (answered) return;
        for (int i = 0; i < options.size(); i++) {
            states[i] = (i == selectedIndex) ? OptionState.SELECTED : OptionState.DEFAULT;
        }
        notifyDataSetChanged();
    }

    /**
     * Reveal the results after the user has submitted an answer.
     * @param selectedIndex index tapped by user
     */
    public void revealAnswer(int selectedIndex) {
        answered = true;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getIsCorrect() != null && options.get(i).getIsCorrect()) {
                states[i] = OptionState.CORRECT;
            } else if (i == selectedIndex) {
                states[i] = OptionState.WRONG;
            } else {
                states[i] = OptionState.DEFAULT;
            }
        }
        notifyDataSetChanged();
    }

    /** Reset all options to default (for moving to next question). */
    public void reset() {
        answered = false;
        for (int i = 0; i < states.length; i++) states[i] = OptionState.DEFAULT;
        notifyDataSetChanged();
    }

    public boolean isAnswered() { return answered; }

    // ── RecyclerView.Adapter ──────────────────────────────────────────────

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_quiz_option, parent, false);
        return new OptionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        QuizOptionResponse option = options.get(position);
        holder.bind(option, states[position], position);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────

    class OptionViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout optionContainer;
        private final View viewRadioDefault;
        private final ImageView ivRadioSelected;
        private final ImageView ivOptionResult;
        private final TextView tvOptionText;
        private final TextView tvOptionLabel;

        OptionViewHolder(View itemView) {
            super(itemView);
            optionContainer = itemView.findViewById(R.id.optionContainer);
            viewRadioDefault= itemView.findViewById(R.id.viewRadioDefault);
            ivRadioSelected = itemView.findViewById(R.id.ivRadioSelected);
            ivOptionResult  = itemView.findViewById(R.id.ivOptionResult);
            tvOptionText    = itemView.findViewById(R.id.tvOptionText);
            tvOptionLabel   = itemView.findViewById(R.id.tvOptionLabel);
        }

        void bind(QuizOptionResponse option, OptionState state, int position) {
            tvOptionText.setText(option.getOptionText() != null ? option.getOptionText() : "");

            switch (state) {
                case DEFAULT:
                    applyDefault();
                    break;
                case SELECTED:
                    applySelected();
                    break;
                case CORRECT:
                    applyCorrect();
                    break;
                case WRONG:
                    applyWrong();
                    break;
            }

            // Click — only allowed before answer is revealed
            if (!answered) {
                optionContainer.setOnClickListener(v -> {
                    if (listener != null) listener.onOptionSelected(position, option);
                });
            } else {
                optionContainer.setOnClickListener(null);
            }
        }

        private void applyDefault() {
            optionContainer.setBackgroundResource(R.drawable.bg_quiz_option);
            viewRadioDefault.setVisibility(View.VISIBLE);
            ivRadioSelected.setVisibility(View.GONE);
            ivOptionResult.setVisibility(View.GONE);
            tvOptionText.setTextColor(context.getResources().getColor(R.color.text_primary, null));
            tvOptionLabel.setVisibility(View.GONE);
        }

        private void applySelected() {
            optionContainer.setBackgroundResource(R.drawable.bg_quiz_option_selected);
            viewRadioDefault.setVisibility(View.GONE);
            ivRadioSelected.setVisibility(View.VISIBLE);
            ivOptionResult.setVisibility(View.GONE);
            tvOptionText.setTextColor(context.getResources().getColor(R.color.primary, null));
            tvOptionLabel.setVisibility(View.GONE);
        }

        private void applyCorrect() {
            optionContainer.setBackgroundResource(R.drawable.bg_quiz_option_correct);
            viewRadioDefault.setVisibility(View.GONE);
            ivRadioSelected.setVisibility(View.GONE);
            ivOptionResult.setVisibility(View.VISIBLE);
            ivOptionResult.setImageResource(R.drawable.ic_check_circle);
            ivOptionResult.setColorFilter(
                    context.getResources().getColor(android.R.color.holo_green_dark, null));
            tvOptionText.setTextColor(
                    context.getResources().getColor(android.R.color.holo_green_dark, null));
            tvOptionLabel.setVisibility(View.VISIBLE);
            tvOptionLabel.setText("Chính xác!");
            tvOptionLabel.setTextColor(
                    context.getResources().getColor(android.R.color.holo_green_dark, null));
        }

        private void applyWrong() {
            optionContainer.setBackgroundResource(R.drawable.bg_quiz_option_wrong);
            viewRadioDefault.setVisibility(View.GONE);
            ivRadioSelected.setVisibility(View.GONE);
            ivOptionResult.setVisibility(View.VISIBLE);
            ivOptionResult.setImageResource(R.drawable.ic_close);
            ivOptionResult.setColorFilter(
                    context.getResources().getColor(R.color.error, null));
            tvOptionText.setTextColor(
                    context.getResources().getColor(R.color.error, null));
            tvOptionLabel.setVisibility(View.VISIBLE);
            tvOptionLabel.setText("Chưa đúng");
            tvOptionLabel.setTextColor(
                    context.getResources().getColor(R.color.error, null));
        }
    }
}
