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
import com.app.cinx.model.Chapter;
import com.app.cinx.model.Lesson;

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
        void onLessonClick(Lesson lesson);
    }

    // ── Fields ────────────────────────────────────────────────────────────
    private final Context context;
    private final List<Chapter> chapters;
    private final Set<Integer> collapsedIndices = new HashSet<>(); // indices of collapsed chapters
    private int activeLessonId = -1;
    private OnLessonClickListener lessonClickListener;

    public CourseCurriculumAdapter(Context context, List<Chapter> chapters) {
        this.context = context;
        this.chapters = chapters;
        // Default: all chapters expanded (no entries in collapsedIndices)
    }

    // ── Public API ────────────────────────────────────────────────────────

    public void setActiveLessonId(int id) {
        this.activeLessonId = id;
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

        void bind(Chapter chapter, int index) {
            // ── Header text ───────────────────────────────────────────────
            tvChapterTitle.setText(chapter.getTitle());
            String metaText = chapter.getLessonCount() + " Bài học";
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

            // ── Populate lesson rows ──────────────────────────────────────
            rebuildLessonRows(chapter.getLessons());
        }

        /** Clears and re-inflates all lesson rows for this chapter. */
        private void rebuildLessonRows(List<Lesson> lessons) {
            lessonsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);

            for (int i = 0; i < lessons.size(); i++) {
                Lesson lesson = lessons.get(i);
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

                // ── Title with lesson number prefix ───────────────────────
                String displayTitle = lesson.getChapterNumber()
                        + "." + lesson.getLessonNumber()
                        + " " + lesson.getTitle();
                tvTitle.setText(displayTitle);
                tvMeta.setText(lesson.getTypeLabel() + " • " + lesson.getDuration());

                // ── Preview badge ─────────────────────────────────────────
                tvPreview.setVisibility(
                        lesson.isPreview() && !lesson.isLocked() ? View.VISIBLE : View.GONE);

                // ── Icon ──────────────────────────────────────────────────
                boolean isActive  = lesson.getId() == activeLessonId;
                boolean isLocked  = lesson.isLocked();

                ivIcon.setBackground(context.getDrawable(
                        isLocked ? R.drawable.bg_icon_gray : R.drawable.bg_icon_purple));

                if (isLocked) {
                    ivIcon.setImageResource(R.drawable.ic_lock);
                    ivIcon.setColorFilter(
                            context.getResources().getColor(R.color.text_secondary, null));
                } else {
                    ivIcon.clearColorFilter();
                    switch (lesson.getType()) {
                        case VIDEO:
                            ivIcon.setImageResource(R.drawable.ic_play_circle);
                            break;
                        case DOCUMENT:
                            ivIcon.setImageResource(R.drawable.ic_document);
                            break;
                        case QUIZ:
                            ivIcon.setImageResource(R.drawable.ic_quiz);
                            break;
                    }
                    // Tint icon with primary color when active
                    if (isActive) {
                        ivIcon.setColorFilter(
                                context.getResources().getColor(R.color.primary, null));
                    }
                }

                // ── Dim locked rows ───────────────────────────────────────
                row.setAlpha(isLocked ? 0.45f : 1f);

                // ── Click ─────────────────────────────────────────────────
                if (!isLocked && lessonClickListener != null) {
                    Lesson lessonRef = lesson;
                    rowContent.setOnClickListener(v -> lessonClickListener.onLessonClick(lessonRef));
                } else {
                    rowContent.setOnClickListener(null);
                }

                lessonsContainer.addView(row);
            }
        }
    }

    // ── Duration helper ───────────────────────────────────────────────────

    /**
     * Parses each lesson's duration string ("mm:ss" or "N phút") and sums the total
     * into a human-readable string, e.g. "20 phút" or "1 giờ 30 phút".
     * Quiz durations ("N câu") are skipped.
     */
    private static String computeDuration(Chapter chapter) {
        int totalMinutes = 0;
        for (Lesson lesson : chapter.getLessons()) {
            String dur = lesson.getDuration();
            if (dur == null || dur.isEmpty()) continue;
            if (dur.contains(":")) {
                // "mm:ss" format
                String[] parts = dur.split(":");
                try {
                    totalMinutes += Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException ignored) { /* skip */ }
            } else if (dur.contains("phút")) {
                // "N phút" format
                String num = dur.replace("phút", "").trim();
                try {
                    totalMinutes += Integer.parseInt(num);
                } catch (NumberFormatException ignored) { /* skip */ }
            }
            // "câu" (quiz question count) is not a time → skip
        }
        if (totalMinutes <= 0) return "";
        int hours = totalMinutes / 60;
        int mins  = totalMinutes % 60;
        if (hours > 0 && mins > 0) return hours + " giờ " + mins + " phút";
        if (hours > 0)              return hours + " giờ";
        return mins + " phút";
    }
}
