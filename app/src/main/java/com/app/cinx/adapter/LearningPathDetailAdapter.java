package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.LearningPathItemResponse;

import java.util.List;
import java.util.Map;

public class LearningPathDetailAdapter extends RecyclerView.Adapter<LearningPathDetailAdapter.ViewHolder> {

    private List<LearningPathItemResponse> items;
    private final Map<String, String> courseNameMap;

    public LearningPathDetailAdapter(List<LearningPathItemResponse> items, Map<String, String> courseNameMap) {
        this.items = items;
        this.courseNameMap = courseNameMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_learning_path_detail_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningPathItemResponse item = items.get(position);
        holder.bind(item, courseNameMap);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateData(List<LearningPathItemResponse> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void updateCourseNames(Map<String, String> newMap) {
        this.courseNameMap.putAll(newMap);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderIndex, tvCourseName, tvLessonInfo, tvSuggested, tvCompletedStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderIndex = itemView.findViewById(R.id.tvOrderIndex);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvLessonInfo = itemView.findViewById(R.id.tvLessonInfo);
            tvSuggested = itemView.findViewById(R.id.tvSuggested);
            tvCompletedStatus = itemView.findViewById(R.id.tvCompletedStatus);
        }

        public void bind(LearningPathItemResponse item, Map<String, String> courseNameMap) {
            // Order
            tvOrderIndex.setText(item.getOrderIndex() != null ? String.valueOf(item.getOrderIndex()) : "-");

            // Course Name lookup
            String cid = item.getCourseId();
            if (cid != null && courseNameMap.containsKey(cid)) {
                tvCourseName.setText(courseNameMap.get(cid));
            } else {
                tvCourseName.setText("Khóa học ID: " + (cid != null && cid.length() > 8 ? cid.substring(0, 8) + "..." : cid));
            }

            // Lesson Info
            String lid = item.getLessonId();
            tvLessonInfo.setText("Bài học ID: " + (lid != null && lid.length() > 8 ? lid.substring(0, 8) + "..." : lid));

            // Flags
            if (Boolean.TRUE.equals(item.getIsSuggested())) {
                tvSuggested.setVisibility(View.VISIBLE);
            } else {
                tvSuggested.setVisibility(View.GONE);
            }

            if (Boolean.TRUE.equals(item.getIsCompleted())) {
                tvCompletedStatus.setVisibility(View.VISIBLE);
            } else {
                tvCompletedStatus.setVisibility(View.GONE);
            }
        }
    }
}