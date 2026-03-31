package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.LessonResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LessonSheetAdapter
 * Drives the curriculum bottom-sheet RecyclerView.
 *
 * Supports two view types:
 *  - TYPE_HEADER : chapter section header label
 *  - TYPE_LESSON : individual lesson row
 *
 * Extend by adding new ViewTypes or new states without touching other layers.
 */
public class LessonSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LESSON = 1;

    /**
     * Flattened data structure mixing headers and child lessons.
     * Simplifies adapter logic (just one linear list).
     */
    private static class ListItem {
        final boolean isHeader;

        // Used if isHeader = true
        final SectionResponse chapter;   // non-null when isHeader=true
        
        // Used if isHeader = false
        final LessonResponse lesson;        // non-null when isHeader=false

        ListItem(SectionResponse chapter) {
            this.isHeader = true;
            this.chapter  = chapter;
            this.lesson   = null;
        }

        ListItem(LessonResponse lesson) {
            this.isHeader = false;
            this.chapter  = null;
            this.lesson   = lesson;
        }
    }

    private final List<ListItem> items = new ArrayList<>();
    private final Context context;

    public interface OnLessonClickListener {
        void onLessonClick(LessonResponse lesson);
    }
    private OnLessonClickListener lessonClickListener;

    /** ID of the currently active lesson, to highlight it in the list */
    private String activeLessonId = null;
    private boolean isAllLocked = false;
    private Set<String> completedLessonIds = new HashSet<>();

    public LessonSheetAdapter(Context context) {
        this.context = context;
    }

    public void setChapters(List<SectionResponse> chapters) {
        items.clear();
        for (SectionResponse c : chapters) {
            items.add(new ListItem(c));
            for (LessonResponse l : c.getLessons()) {
                items.add(new ListItem(l));
            }
        }
        notifyDataSetChanged();
    }

    public void setActiveLessonId(String id) {
        this.activeLessonId = id;
        notifyDataSetChanged();
    }
    
    public void setLockState(boolean isAllLocked) {
        this.isAllLocked = isAllLocked;
        notifyDataSetChanged();
    }
    
    public void setCompletedLessons(Set<String> completedLessonIds) {
        this.completedLessonIds = completedLessonIds;
        notifyDataSetChanged();
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.lessonClickListener = listener;
    }

    // ── RecyclerView.Adapter ──────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader ? TYPE_HEADER : TYPE_LESSON;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_chapter_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_lesson_sheet, parent, false);
            return new LessonViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.chapter);
        } else {
            ((LessonViewHolder) holder).bind(item.lesson, activeLessonId, lessonClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ── ViewHolders ───────────────────────────────────────────────────────

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvChapterTitle;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterHeader);
        }

        void bind(SectionResponse chapter) {
            tvChapterTitle.setText(chapter.getTitle());
        }

        private String computeDuration(SectionResponse chapter) {
            long totalSeconds = 0;
            if (chapter.getLessons() != null) {
                for (LessonResponse lesson : chapter.getLessons()) {
                    if (lesson.getDuration() != null) {
                        totalSeconds += lesson.getDuration();
                    }
                }
            }
            long totalMinutes = totalSeconds / 60;
            if (totalMinutes <= 0) return "";
            return totalMinutes + " phút";
        }
    }

    // ── Lesson ViewHolder ────────────────────────────────────────────────────────────

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private final View rowContent;
        private final ImageView ivLessonIcon;
        private final TextView tvLessonTitle;
        private final TextView tvLessonDuration;

        LessonViewHolder(View itemView) {
            super(itemView);
            rowContent       = itemView.findViewById(R.id.lessonRow);
            ivLessonIcon     = itemView.findViewById(R.id.ivLessonIcon);
            tvLessonTitle    = itemView.findViewById(R.id.tvLessonSheetTitle);
            tvLessonDuration = itemView.findViewById(R.id.tvLessonSheetMeta);
        }

        void bind(LessonResponse lesson, String activeLessonId, OnLessonClickListener listener) {
            boolean isActive = activeLessonId != null && lesson.getId().equals(activeLessonId);
            boolean isLocked = isAllLocked;
            boolean isCompleted = completedLessonIds.contains(lesson.getId());

            String displayTitle = "BĂi " + (lesson.getOrderIndex() != null ? lesson.getOrderIndex() : "") + ". " + lesson.getTitle();
            tvLessonTitle.setText(displayTitle);
            tvLessonDuration.setText(lesson.getDuration() != null ? (lesson.getDuration() / 60) + " phút" : "0 phút");

            // Row styling
            rowContent.setBackgroundResource(isActive ? R.drawable.bg_lesson_active : R.drawable.bg_lesson_default);
            rowContent.setAlpha(isLocked ? 0.45f : 1f);

            // Icon styling
            String lessonTypeStr = lesson.getLessonType() != null ? lesson.getLessonType() : "";
            if (isLocked) {
                ivLessonIcon.setImageResource(R.drawable.ic_lock);
                ivLessonIcon.setColorFilter(context.getResources().getColor(R.color.text_secondary, null));
            } else if (isCompleted) {
                ivLessonIcon.setImageResource(R.drawable.ic_check_circle);
                ivLessonIcon.setColorFilter(context.getResources().getColor(R.color.success_green, null));
            } else {
                ivLessonIcon.clearColorFilter();
                if (lessonTypeStr.equalsIgnoreCase("VIDEO")) {
                    ivLessonIcon.setImageResource(R.drawable.ic_play_circle);
                } else if (lessonTypeStr.equalsIgnoreCase("DOCUMENT") || lessonTypeStr.equalsIgnoreCase("ARTICLE")) {
                    ivLessonIcon.setImageResource(R.drawable.ic_document);
                } else if (lessonTypeStr.equalsIgnoreCase("QUIZ")) {
                    ivLessonIcon.setImageResource(R.drawable.ic_quiz);
                } else {
                    ivLessonIcon.setImageResource(R.drawable.ic_document);
                }
                
                if (isActive) {
                    ivLessonIcon.setColorFilter(context.getResources().getColor(R.color.primary, null));
                }
            }

            // Click handling
            if (!isLocked && listener != null) {
                rowContent.setOnClickListener(v -> listener.onLessonClick(lesson));
            } else {
                rowContent.setOnClickListener(null);
            }
        }
    }
}
