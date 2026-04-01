package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.LearningPathResponse;

import java.util.List;

public class LearningPathAdapter extends RecyclerView.Adapter<LearningPathAdapter.ViewHolder> {

    private List<LearningPathResponse> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LearningPathResponse item);
    }

    public LearningPathAdapter(List<LearningPathResponse> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_learning_path_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningPathResponse item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateData(List<LearningPathResponse> newItems) {
        this.items = newItems;
        notifyDataSetDataSetChanged();
    }
    
    private void notifyDataSetDataSetChanged() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPathTitle, tvPathDescription, tvProgress, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPathTitle = itemView.findViewById(R.id.tvPathTitle);
            tvPathDescription = itemView.findViewById(R.id.tvPathDescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(LearningPathResponse item, OnItemClickListener listener) {
            tvPathTitle.setText(item.getTitle());
            tvPathDescription.setText(item.getDescription());
            tvProgress.setText("Tiến độ: " + item.getCompletedItems() + "/" + item.getTotalItems());
            tvStatus.setText(item.getStatus() != null ? item.getStatus() : "ACTIVE");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}