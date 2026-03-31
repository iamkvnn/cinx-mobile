package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.EnrolledCourse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * MyLearningAdapter
 * ─────────────────
 * Single RecyclerView adapter for the "Khóa học của tôi" screen.
 * Handles three distinct card layouts via view types:
 *   · VIEW_TYPE_PROGRESS  → in-progress course card
 *   · VIEW_TYPE_COMPLETED → completed course card
 *   · VIEW_TYPE_SAVED     → saved/bookmarked course card
 *
 * Interaction callbacks are delegated through {@link OnCourseActionListener}
 * to keep the adapter UI-only with no Activity coupling beyond the interface.
 */
public class MyLearningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ── View types ────────────────────────────────────────────────────────────

    private static final int VT_PROGRESS  = 0;
    private static final int VT_COMPLETED = 1;
    private static final int VT_SAVED     = 2;

    // ── Interaction listener ──────────────────────────────────────────────────

    public interface OnCourseActionListener {
        void onContinueLearning(EnrolledCourse course);
        void onGetCertificate(EnrolledCourse course);
        void onRateCourse(EnrolledCourse course);
        void onViewSavedCourse(EnrolledCourse course);
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private List<EnrolledCourse>     courses;
    private final OnCourseActionListener listener;

    public MyLearningAdapter(List<EnrolledCourse> courses, OnCourseActionListener listener) {
        this.courses  = courses;
        this.listener = listener;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Replace the displayed list and refresh the RecyclerView. */
    public void updateList(List<EnrolledCourse> newList) {
        this.courses = newList;
        notifyDataSetChanged();
    }

    // ── Adapter overrides ─────────────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        switch (courses.get(position).getStatus()) {
            case PROGRESS:  return VT_PROGRESS;
            case COMPLETED: return VT_COMPLETED;
            default:        return VT_SAVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VT_PROGRESS:
                return new InProgressVH(
                        inf.inflate(R.layout.item_course_progress, parent, false));
            case VT_COMPLETED:
                return new CompletedVH(
                        inf.inflate(R.layout.item_course_completed, parent, false));
            default:
                return new SavedVH(
                        inf.inflate(R.layout.item_course_saved, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EnrolledCourse course = courses.get(position);
        switch (getItemViewType(position)) {
            case VT_PROGRESS:  ((InProgressVH) holder).bind(course, listener); break;
            case VT_COMPLETED: ((CompletedVH)  holder).bind(course, listener); break;
            default:           ((SavedVH)      holder).bind(course, listener); break;
        }
    }

    @Override
    public int getItemCount() {
        return courses == null ? 0 : courses.size();
    }

    // ── In-Progress ViewHolder ────────────────────────────────────────────────

    static class InProgressVH extends RecyclerView.ViewHolder {

        final ImageView  imgCourse;
        final TextView   tvTitle, tvInstructor, tvLastAccessed, tvNextLesson, tvProgress;
        final ProgressBar progressBar;
        final View       btnContinue;

        InProgressVH(@NonNull View v) {
            super(v);
            imgCourse      = v.findViewById(R.id.imgCourse);
            tvTitle        = v.findViewById(R.id.tvTitle);
            tvInstructor   = v.findViewById(R.id.tvInstructor);
            tvLastAccessed = v.findViewById(R.id.tvLastAccessed);
            tvNextLesson   = v.findViewById(R.id.tvNextLesson);
            tvProgress     = v.findViewById(R.id.tvProgress);
            progressBar    = v.findViewById(R.id.progressBar);
            btnContinue    = v.findViewById(R.id.btnContinue);
        }

        void bind(EnrolledCourse c, OnCourseActionListener l) {
            tvTitle.setText(c.getTitle());
            tvInstructor.setText("Bởi " + c.getInstructor() + "  •  " + c.getCategory());
            tvLastAccessed.setVisibility(View.GONE);
            tvNextLesson.setVisibility(View.GONE);
            tvProgress.setText(c.getProgress() + "%");
            progressBar.setProgress(c.getProgress());

            Glide.with(imgCourse.getContext())
                    .load(c.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(48)))
                    .placeholder(android.R.color.darker_gray)
                    .into(imgCourse);

            if (l != null) {
                btnContinue.setOnClickListener(v -> l.onContinueLearning(c));
            }
        }
    }

    // ── Completed ViewHolder ──────────────────────────────────────────────────

    static class CompletedVH extends RecyclerView.ViewHolder {

        final ImageView imgCourse;
        final TextView  tvTitle, tvDate, tvGrade;
        final View      btnRate, btnCertificate;

        CompletedVH(@NonNull View v) {
            super(v);
            imgCourse      = v.findViewById(R.id.imgCourse);
            tvTitle        = v.findViewById(R.id.tvTitle);
            tvDate         = v.findViewById(R.id.tvDate);
            tvGrade        = v.findViewById(R.id.tvGrade);
            btnRate        = v.findViewById(R.id.btnRate);
            btnCertificate = v.findViewById(R.id.btnCertificate);
        }

        void bind(EnrolledCourse c, OnCourseActionListener l) {
            tvTitle.setText(c.getTitle());
            tvDate.setText("Hoàn thành: " + c.getCompletionDate()
                    + "  •  " + c.getCategory());
            tvGrade.setText("Điểm: " + c.getGrade());

            Glide.with(imgCourse.getContext())
                    .load(c.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(48)))
                    .placeholder(android.R.color.darker_gray)
                    .into(imgCourse);

            if (l != null) {
                btnRate.setOnClickListener(v        -> l.onRateCourse(c));
                btnCertificate.setOnClickListener(v -> l.onGetCertificate(c));
            }
        }
    }

    // ── Saved ViewHolder ──────────────────────────────────────────────────────

    static class SavedVH extends RecyclerView.ViewHolder {

        final ImageView imgCourse;
        final TextView  tvTitle, tvInstructor, tvRating, tvPrice;
        final View      btnView;

        SavedVH(@NonNull View v) {
            super(v);
            imgCourse    = v.findViewById(R.id.imgCourse);
            tvTitle      = v.findViewById(R.id.tvTitle);
            tvInstructor = v.findViewById(R.id.tvInstructor);
            tvRating     = v.findViewById(R.id.tvRating);
            tvPrice      = v.findViewById(R.id.tvPrice);
            btnView      = v.findViewById(R.id.btnView);
        }

        void bind(EnrolledCourse c, OnCourseActionListener l) {
            tvTitle.setText(c.getTitle());
            tvInstructor.setText("Bởi " + c.getInstructor() + "  •  " + c.getCategory());
            tvRating.setText(String.format("★ %.1f", c.getRating()));
            tvPrice.setText(c.getOriginalPrice());

            Glide.with(imgCourse.getContext())
                    .load(c.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(48)))
                    .placeholder(android.R.color.darker_gray)
                    .into(imgCourse);

            if (l != null) {
                btnView.setOnClickListener(v -> l.onViewSavedCourse(c));
            }
        }
    }
}
