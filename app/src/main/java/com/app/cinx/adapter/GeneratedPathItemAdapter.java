package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.GenerateLearningPathItem;

import java.util.List;
import java.util.Map;

public class GeneratedPathItemAdapter extends RecyclerView.Adapter<GeneratedPathItemAdapter.ViewHolder> {

    private List<GenerateLearningPathItem> items;
    private Map<String, String> courseNames; // Map between courseId and courseName

    public GeneratedPathItemAdapter(List<GenerateLearningPathItem> items, Map<String, String> courseNames) {
        this.items = items;
        this.courseNames = courseNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generated_path_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenerateLearningPathItem item = items.get(position);
        holder.bind(item, courseNames);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateData(List<GenerateLearningPathItem> newItems, Map<String, String> newNames) {
        this.items = newItems;
        this.courseNames = newNames;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvReason = itemView.findViewById(R.id.tvReason);
        }

        public void bind(GenerateLearningPathItem item, Map<String, String> courseNames) {
            String cName = item.getCourseId() != null && courseNames.containsKey(item.getCourseId()) 
                    ? courseNames.get(item.getCourseId()) 
                    : "Khóa học: " + item.getCourseId();
            tvCourseName.setText(cName);
            tvReason.setText(item.getReason() != null ? item.getReason() : "");
        }
    }
}