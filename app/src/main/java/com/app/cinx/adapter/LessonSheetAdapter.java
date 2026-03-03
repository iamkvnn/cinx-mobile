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
import com.app.cinx.model.Chapter;
import com.app.cinx.model.Lesson;
import com.app.cinx.model.LessonType;

import java.util.ArrayList;
import java.util.List;

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

    // ── ViewType constants ────────────────────────────────────────────────
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LESSON = 1;

    // ── Flat list item sealed class ───────────────────────────────────────
    /** Flat list backing the adapter — either a header or a lesson row. */
    private static class ListItem {
        final boolean isHeader;
        final String headerTitle;   // non-null when isHeader=true
        final Lesson lesson;        // non-null when isHeader=false

        ListItem(String headerTitle) {
            this.isHeader = true;
            this.headerTitle = headerTitle;
            this.lesson = null;
        }

        ListItem(Lesson lesson) {
            this.isHeader = false;
            this.headerTitle = null;
            this.lesson = lesson;
        }
    }

    // ── Callback ──────────────────────────────────────────────────────────
    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    // ── Fields ────────────────────────────────────────────────────────────
    private final Context context;
    private final List<ListItem> items = new ArrayList<>();
    private int activeLessonId = -1;
    private OnLessonClickListener listener;

    public LessonSheetAdapter(Context context) {
        this.context = context;
    }

    // ── Public API ────────────────────────────────────────────────────────

    public void setChapters(List<Chapter> chapters) {
        items.clear();
        for (Chapter chapter : chapters) {
            // Chapter.getTitle() already contains the full label e.g. "CHƯƠNG 1: NHẬP MÔN"
            items.add(new ListItem(chapter.getTitle()));
            for (Lesson lesson : chapter.getLessons()) {
                items.add(new ListItem(lesson));
            }
        }
        notifyDataSetChanged();
    }

    public void setActiveLessonId(int lessonId) {
        this.activeLessonId = lessonId;
        notifyDataSetChanged();
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.listener = listener;
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
            ((HeaderViewHolder) holder).bind(item.headerTitle);
        } else {
            ((LessonViewHolder) holder).bind(item.lesson, activeLessonId, listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ── ViewHolders ───────────────────────────────────────────────────────

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvChapterHeader);
        }

        void bind(String title) {
            tvHeader.setText(title);
        }
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        private final View lessonRow;
        private final View viewActiveAccent;
        private final ImageView ivLessonIcon;
        private final TextView tvTitle;
        private final TextView tvMeta;
        private final ImageView ivActiveIndicator;

        LessonViewHolder(View itemView) {
            super(itemView);
            lessonRow        = itemView.findViewById(R.id.lessonRow);
            viewActiveAccent = itemView.findViewById(R.id.viewActiveAccent);
            ivLessonIcon     = itemView.findViewById(R.id.ivLessonIcon);
            tvTitle          = itemView.findViewById(R.id.tvLessonSheetTitle);
            tvMeta           = itemView.findViewById(R.id.tvLessonSheetMeta);
            ivActiveIndicator= itemView.findViewById(R.id.ivActiveIndicator);
        }

        void bind(Lesson lesson, int activeLessonId, OnLessonClickListener listener) {
            Context ctx = itemView.getContext();
            boolean isActive    = lesson.getId() == activeLessonId;
            boolean isCompleted = lesson.isCompleted();
            boolean isLocked    = lesson.isLocked();

            // ── Title & meta ──────────────────────────────────────────────
            tvTitle.setText(lesson.getTitle());
            tvMeta.setText(lesson.getTypeLabel() + " • " + lesson.getDuration());

            // ── Strikethrough for completed ───────────────────────────────
            if (isCompleted) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(ctx.getResources().getColor(R.color.text_secondary, null));
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setTextColor(ctx.getResources().getColor(
                        isActive ? R.color.primary : R.color.text_primary, null));
            }

            // ── Row background ────────────────────────────────────────────
            if (isActive) {
                lessonRow.setBackgroundResource(R.drawable.bg_lesson_active);
            } else {
                lessonRow.setBackgroundResource(R.drawable.bg_lesson_default);
            }

            // ── Left accent bar ───────────────────────────────────────────
            viewActiveAccent.setVisibility(isActive ? View.VISIBLE : View.GONE);

            // ── Icon ──────────────────────────────────────────────────────
            if (isCompleted) {
                ivLessonIcon.setImageResource(R.drawable.ic_check_circle);
                ivLessonIcon.setColorFilter(ctx.getResources().getColor(android.R.color.holo_green_dark, null));
            } else if (isLocked) {
                ivLessonIcon.setImageResource(R.drawable.ic_lock);
                ivLessonIcon.setColorFilter(ctx.getResources().getColor(R.color.text_placeholder, null));
            } else {
                ivLessonIcon.clearColorFilter();
                switch (lesson.getType()) {
                    case VIDEO:
                        ivLessonIcon.setImageResource(R.drawable.ic_play_circle);
                        if (isActive) {
                            ivLessonIcon.setColorFilter(
                                    ctx.getResources().getColor(R.color.primary, null));
                        } else {
                            ivLessonIcon.clearColorFilter();
                        }
                        break;
                    case DOCUMENT:
                        ivLessonIcon.setImageResource(R.drawable.ic_document);
                        break;
                    case QUIZ:
                        ivLessonIcon.setImageResource(R.drawable.ic_quiz);
                        break;
                }
            }

            // ── Right playing bars ────────────────────────────────────────
            ivActiveIndicator.setVisibility(isActive ? View.VISIBLE : View.GONE);

            // ── Click ──────────────────────────────────────────────────────
            if (!isLocked && listener != null) {
                itemView.setOnClickListener(v -> listener.onLessonClick(lesson));
            } else {
                itemView.setOnClickListener(null);
            }

            // ── Dim locked items ──────────────────────────────────────────
            itemView.setAlpha(isLocked ? 0.45f : 1f);
        }
    }
}
