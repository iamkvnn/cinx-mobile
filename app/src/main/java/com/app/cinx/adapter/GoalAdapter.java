package com.app.cinx.adapter;

import android.graphics.Color;
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

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    public void updateGoals(List<Goal> newGoals) {
        this.goals = newGoals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout iconContainer;
        ImageView icon;
        TextView tvGoalText;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            icon = itemView.findViewById(R.id.icon);
            tvGoalText = itemView.findViewById(R.id.tvGoalText);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(Goal goal) {
            tvGoalText.setText(goal.getText());
            tvTime.setText(goal.getTime());

            if (goal.isDone()) {
                // Green done state
                iconContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                icon.setImageResource(R.drawable.ic_check_circle);
                icon.setColorFilter(Color.parseColor("#16A34A"));

                tvGoalText.setTextColor(Color.parseColor("#94A3B8"));
                tvGoalText.setPaintFlags(tvGoalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // Violet pending state
                iconContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#F5F3FF")));
                icon.setColorFilter(Color.parseColor("#7C3AED"));

                tvGoalText.setTextColor(Color.parseColor("#334155"));
                tvGoalText.setPaintFlags(tvGoalText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                // Set icon based on type
                switch (goal.getType()) {
                    case "quiz":
                        icon.setImageResource(R.drawable.ic_check_circle);
                        break;
                    case "video":
                        icon.setImageResource(R.drawable.ic_play);
                        break;
                    case "code":
                        icon.setImageResource(R.drawable.ic_book);
                        break;
                    case "add":
                        icon.setImageResource(R.drawable.ic_zap);
                        break;
                    default:
                        icon.setImageResource(R.drawable.ic_star);
                        break;
                }
            }
        }
    }
}
