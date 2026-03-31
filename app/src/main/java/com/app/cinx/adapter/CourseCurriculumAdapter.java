package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.LessonResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CourseCurriculumAdapter
 *
 * Drives the "Nội dung" RecyclerView in CourseDetailActivity.
 * Each RecyclerView item = one complete chapter card (header + expandable lessons).
 *
 * Features:
 *  - Expand / collapse per chapter (all expanded by default)
 *  - Arrow icon rotates to reflect state
 *  - "Xem trước" badge for preview lessons
 *  - Lock / play / type icons per lesson state
 *  - Lesson click callback
 */
public class CourseCurriculumAdapter
        extends RecyclerView.Adapter<CourseCurriculumAdapter.ChapterViewHolder> {

    // ── Callback ──────────────────────────────────────────────────────────
    public interface OnLessonClickListener {
        void onLessonClick(LessonResponse lesson);
    }

    // ── Fields ──────────────────────────────────────────────────────────────────────
    private final Context context;
    private final List<SectionResponse> chapters;
    private final Set<Integer> collapsedIndices = new HashSet<>(); // indices of collapsed chapters
    private String activeLessonId = null;
    private boolean isAllLocked = false;
    private Set<String> completedLessonIds = new HashSet<>();
    private OnLessonClickListener lessonClickListener;

    public CourseCurriculumAdapter(Context context, List<SectionResponse> chapters) {
        this.context = context;
        this.chapters = chapters;
        // Default: all chapters expanded (no entries in collapsedIndices)
    }

    // ── Public API ──────────────────────────────────────────────────────────────────

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

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_course_chapter, parent, false);
        return new ChapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.bind(chapters.get(position), position);
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────

    class ChapterViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout chapterHeaderRow;
        private final TextView tvChapterTitle;
        private final TextView tvChapterMeta;
        private final ImageView ivChapterArrow;
        private final LinearLayout lessonsContainer;

        ChapterViewHolder(View itemView) {
            super(itemView);
            chapterHeaderRow = itemView.findViewById(R.id.chapterHeaderRow);
            tvChapterTitle   = itemView.findViewById(R.id.tvChapterTitle);
            tvChapterMeta    = itemView.findViewById(R.id.tvChapterMeta);
            ivChapterArrow   = itemView.findViewById(R.id.ivChapterArrow);
            lessonsContainer = itemView.findViewById(R.id.lessonsContainer);
        }

        void bind(SectionResponse chapter, int index) {
            // ── Header text ──────────────────────────────────────────────────────────
            tvChapterTitle.setText(chapter.getTitle());
            
            int lessonCount = (chapter.getLessons() != null) ? chapter.getLessons().size() : 0;
            String metaText = lessonCount + " Bài học";
            
            String dur = computeDuration(chapter);
            if (!dur.isEmpty()) metaText += " • " + dur;
            tvChapterMeta.setText(metaText);

            // ── Expand / collapse state ───────────────────────────────────
            boolean isCollapsed = collapsedIndices.contains(index);
            lessonsContainer.setVisibility(isCollapsed ? View.GONE : View.VISIBLE);
            ivChapterArrow.setImageResource(
                    isCollapsed ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);

            // ── Toggle on header click ────────────────────────────────────
            chapterHeaderRow.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_ID) return;
                if (collapsedIndices.contains(pos)) {
                    collapsedIndices.remove(pos);
                } else {
                    collapsedIndices.add(pos);
                }
                notifyItemChanged(pos);
            });

            // ── Populate lesson rows ─────────────────────────────────────────────────
            rebuildLessonRows(chapter.getLessons());
        }

        /** Clears and re-inflates all lesson rows for this chapter. */
        private void rebuildLessonRows(List<LessonResponse> lessons) {
            lessonsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (lessons == null) return;

            for (int i = 0; i < lessons.size(); i++) {
                LessonResponse lesson = lessons.get(i);
                boolean isFirst = (i == 0);

                View row = inflater.inflate(R.layout.item_lesson_row, lessonsContainer, false);

                // Divider: hide for first lesson (header already provides top padding)
                View divider = row.findViewById(R.id.rowDivider);
                divider.setVisibility(isFirst ? View.GONE : View.VISIBLE);

                // Views
                ImageView ivIcon     = row.findViewById(R.id.ivLessonIcon);
                TextView tvTitle     = row.findViewById(R.id.tvLessonTitle);
                TextView tvMeta      = row.findViewById(R.id.tvLessonMeta);
                TextView tvPreview   = row.findViewById(R.id.tvPreviewBadge);
                View     rowContent  = row.findViewById(R.id.lessonRowContent);

                // ── Title with lesson number prefix ──────────────────────────────────
                String displayTitle = "Bài " + (lesson.getOrderIndex() != null ? lesson.getOrderIndex() : (i + 1))
                        + ": " + lesson.getTitle();
                tvTitle.setText(displayTitle);
                tvMeta.setText((lesson.getLessonType() != null ? lesson.getLessonType() : "Unknown") + " • " + (lesson.getDuration() != null ? (lesson.getDuration()/60) + " phút" : ""));

                // ── Preview badge ───────────────────────────────────────────────────
                tvPreview.setVisibility(View.GONE); // No preview concept in DTO yet

                // ── Icon ───────────────────────────────────────────────────────────
                boolean isActive  = lesson.getId().equals(activeLessonId);
                boolean isLocked  = isAllLocked;
                boolean isCompleted = completedLessonIds.contains(lesson.getId());

                ivIcon.setBackground(androidx.appcompat.content.res.AppCompatResources.getDrawable(context,
                        isLocked ? R.drawable.bg_icon_gray : R.drawable.bg_icon_purple));

                if (isLocked) {
                    ivIcon.setImageResource(R.drawable.ic_lock);
                    ivIcon.setColorFilter(
                            context.getResources().getColor(R.color.text_secondary, null));
                } else {
                    ivIcon.clearColorFilter();
                    String t = lesson.getLessonType() != null ? lesson.getLessonType() : "";
                    if (t.equalsIgnoreCase("VIDEO")) {
                        ivIcon.setImageResource(R.drawable.ic_play_circle);
                    } else if (t.equalsIgnoreCase("DOCUMENT") || t.equalsIgnoreCase("ARTICLE")) {
                        ivIcon.setImageResource(R.drawable.ic_document);
                    } else if (t.equalsIgnoreCase("QUIZ")) {
                        ivIcon.setImageResource(R.drawable.ic_quiz);
                    } else {
                        ivIcon.setImageResource(R.drawable.ic_document); 
                    }
                    
                    if (isCompleted) {
                        ivIcon.setImageResource(R.drawable.ic_check_circle);
                        ivIcon.setColorFilter(context.getResources().getColor(R.color.success_green, null));
                    }
                    else if (isActive) {
                        // Tint icon with primary color when active
                        ivIcon.setColorFilter(
                                context.getResources().getColor(R.color.primary, null));
                    }
                }

                // ── Dim locked rows ─────────────────────────────────────────────────
                row.setAlpha(isLocked ? 0.45f : 1f);

                // ── Click ───────────────────────────────────────────────────────────
                if (!isLocked && lessonClickListener != null) {
                    LessonResponse lessonRef = lesson;
                    rowContent.setOnClickListener(v -> lessonClickListener.onLessonClick(lessonRef));
                } else {
                    rowContent.setOnClickListener(null);
                }

                lessonsContainer.addView(row);
            }
        }
    }

    // ── Duration helper ──────────────────────────────────────────────────────────────

    /**
     * Parses each lesson's duration string ("mm:ss" or "N phút") and sums the total
     * into a human-readable string, e.g. "20 phút" or "1 giờ 30 phút".
     * Quiz durations ("N câu") are skipped.
     */
    private static String computeDuration(SectionResponse chapter) {
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
        long hours = totalMinutes / 60;
        long mins  = totalMinutes % 60;
        if (hours > 0 && mins > 0) return hours + " giờ " + mins + " phút";
        if (hours > 0)              return hours + " giờ";
        return mins + " phút";
    }
}
