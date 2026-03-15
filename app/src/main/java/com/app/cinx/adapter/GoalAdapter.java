package com.app.cinx.adapter;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Goal;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.VH> {

    private List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    public void updateGoals(List<Goal> newGoals) {
        this.goals = newGoals;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Goal goal = goals.get(position);

        int iconRes;
        int iconTint;
        int bgTint;
        switch (goal.getType()) {
            case "video":
                iconRes  = R.drawable.ic_play;
                iconTint = 0xFF3B82F6;
                bgTint   = 0x1A3B82F6;
                break;
            case "quiz":
                iconRes  = R.drawable.ic_quiz;
                iconTint = 0xFFF59E0B;
                bgTint   = 0x1AF59E0B;
                break;
            case "code":
                iconRes  = R.drawable.ic_book;
                iconTint = 0xFF10B981;
                bgTint   = 0x1A10B981;
                break;
            default:
                iconRes  = R.drawable.ic_zap;
                iconTint = h.ivGoalIcon.getContext().getColor(R.color.primary);
                bgTint   = 0x1A7C3AED;
                break;
        }
        h.goalIconBg.setBackgroundTintList(ColorStateList.valueOf(bgTint));
        h.ivGoalIcon.setImageResource(iconRes);
        h.ivGoalIcon.setColorFilter(iconTint);

        h.tvGoalText.setText(goal.getText());
        if (goal.isDone()) {
            h.tvGoalText.setPaintFlags(h.tvGoalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvGoalText.setAlpha(0.45f);
        } else {
            h.tvGoalText.setPaintFlags(h.tvGoalText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvGoalText.setAlpha(1f);
        }

        h.tvGoalTime.setText(goal.getTime());

        if (goal.isDone()) {
            h.ivGoalCheck.setImageResource(R.drawable.ic_check_circle);
            h.ivGoalCheck.setColorFilter(
                    h.ivGoalCheck.getContext().getColor(R.color.success_green));
        } else {
            h.ivGoalCheck.setImageResource(R.drawable.ic_circle_outline);
            h.ivGoalCheck.setColorFilter(
                    h.ivGoalCheck.getContext().getColor(R.color.text_secondary));
        }
    }

    @Override public int getItemCount() { return goals.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final FrameLayout goalIconBg;
        final ImageView   ivGoalIcon, ivGoalCheck;
        final TextView    tvGoalText, tvGoalTime;

        VH(View v) {
            super(v);
            goalIconBg  = v.findViewById(R.id.goalIconBg);
            ivGoalIcon  = v.findViewById(R.id.ivGoalIcon);
            ivGoalCheck = v.findViewById(R.id.ivGoalCheck);
            tvGoalText  = v.findViewById(R.id.tvGoalText);
            tvGoalTime  = v.findViewById(R.id.tvGoalTime);
        }
    }
}