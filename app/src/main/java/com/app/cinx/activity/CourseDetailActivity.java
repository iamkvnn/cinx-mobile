package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.util.UserManager;
import android.widget.Toast;
import com.app.cinx.adapter.CourseCurriculumAdapter;
import com.app.cinx.data.SampleCourseData;
import com.app.cinx.data.CartRepository;
import com.app.cinx.model.Chapter;
import com.app.cinx.model.Lesson;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    // Purchase state — replace with actual API result in production
    private boolean isPurchased = false;
    private boolean isDescriptionExpanded = false;

    // ── Layout views ──────────────────────────────────────────────────────
    private LinearLayout layoutActionUnpurchased;
    private LinearLayout layoutActionPurchased;
    private LinearLayout layoutHeroProgress;

    // ── Description expand ────────────────────────────────────────────────
    private TextView tvCourseDescription;
    private TextView tvExpandDescription;

    // ── Tabs ──────────────────────────────────────────────────────────────
    private ViewFlipper viewFlipper;
    private TabLayout tabLayout;

    // ── Curriculum tab (Tab 2) ────────────────────────────────────────────
    private RecyclerView rvCourseCurriculum;
    private TextView tvCurriculumSummary;
    private TextView tvCurriculumProgress;
    private CourseCurriculumAdapter curriculumAdapter;
    // ── Cart badge ──────────────────────────────────────────────────────────────────
    private FrameLayout cartBadgeFrame;
    private TextView     tvCartBadge;
    // ── Continue/Start button ─────────────────────────────────────────────
    private AppCompatButton btnStartLearning;

    // ── Data ──────────────────────────────────────────────────────────────
    private List<Chapter> chapters;
    private List<Lesson> lessons;

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        loadData();
        initViews();
        setupNavButtons();
        setupTabs();
        setupDescriptionExpand();
        setupCurriculum();
        setupStartButton();

        updatePurchaseState(isPurchased);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────────

    private void loadData() {
        chapters = SampleCourseData.getChapters();
        lessons  = SampleCourseData.getLessons();
    }

    // ─────────────────────────────────────────────────────────────────────
    // View initialisation
    // ─────────────────────────────────────────────────────────────────────

    private void initViews() {
        layoutActionUnpurchased = findViewById(R.id.layoutActionUnpurchased);
        layoutActionPurchased   = findViewById(R.id.layoutActionPurchased);
        layoutHeroProgress      = findViewById(R.id.layoutHeroProgress);

        tvCourseDescription = findViewById(R.id.tvCourseDescription);
        tvExpandDescription = findViewById(R.id.tvExpandDescription);

        viewFlipper = findViewById(R.id.viewFlipper);
        tabLayout   = findViewById(R.id.tabLayout);

        rvCourseCurriculum  = findViewById(R.id.rvCourseCurriculum);
        tvCurriculumSummary = findViewById(R.id.tvCurriculumSummary);
        tvCurriculumProgress= findViewById(R.id.tvCurriculumProgress);

        btnStartLearning = findViewById(R.id.btnStartLearning);

        // Cart badge
        cartBadgeFrame = findViewById(R.id.btnCartBadgeDetail);
        tvCartBadge    = findViewById(R.id.tvCartBadgeDetail);

        // Strike-through original price
        TextView tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        TextView tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        int courseId = getIntent().getIntExtra("COURSE_ID", -1);
        if (courseId == 1 && UserManager.getInstance().isLoggedIn()) {
            isPurchased = true; // Simulate course already purchased for course ID 1
        }
        long originalPriceVal = getIntent().getLongExtra("COURSE_PRICE", 1200000L);
        long currentPriceVal = getIntent().getLongExtra("COURSE_DISCOUNTED_PRICE", 599000L);
        String courseTitle = getIntent().getStringExtra("COURSE_TITLE");
        
        if (courseTitle != null) {
            TextView titleView = findViewById(R.id.tvCourseTitle); // Assuming there's a title view, if not it's fine
            if (titleView != null) titleView.setText(courseTitle);
        }

        if (tvOriginalPrice != null) {
            tvOriginalPrice.setText(com.app.cinx.util.PriceUtil.formatPrice(originalPriceVal));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (tvCurrentPrice != null) {
            tvCurrentPrice.setText(com.app.cinx.util.PriceUtil.formatPrice(currentPriceVal));
        }

        View btnAddCart = findViewById(R.id.btnAddCart);
        View btnBuyNow = findViewById(R.id.btnBuyNow);

        View.OnClickListener buyAction = v -> {
            boolean isLoggedIn = UserManager.getInstance().isLoggedIn();
            if (!isLoggedIn) {
                Intent loginIntent = new Intent(CourseDetailActivity.this, com.app.cinx.activity.LoginActivity.class);
                startActivity(loginIntent);
            } else {
                Toast.makeText(CourseDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        };

        if (btnAddCart != null) btnAddCart.setOnClickListener(buyAction);
        if (btnBuyNow != null) btnBuyNow.setOnClickListener(buyAction);
    }

    private void setupNavButtons() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        // Bookmark is now an overlay on the hero image; same id, same toggle logic
        ImageButton btnBookmark = findViewById(R.id.btnBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v ->
                    btnBookmark.setImageResource(R.drawable.ic_bookmark));
        }

        // Cart button — navigate to CartActivity
        if (cartBadgeFrame != null) {
            cartBadgeFrame.setOnClickListener(v -> {
                if (!UserManager.getInstance().isLoggedIn()) {
                    startActivity(new Intent(this, com.app.cinx.activity.LoginActivity.class));
                } else {
                    startActivity(new Intent(this, CartActivity.class));
                }
            });
        }
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewFlipper.setDisplayedChild(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupDescriptionExpand() {
        if (tvExpandDescription == null || tvCourseDescription == null) return;
        tvExpandDescription.setOnClickListener(v -> {
            isDescriptionExpanded = !isDescriptionExpanded;
            if (isDescriptionExpanded) {
                tvCourseDescription.setMaxLines(Integer.MAX_VALUE);
                tvCourseDescription.setEllipsize(null);
            } else {
                tvCourseDescription.setMaxLines(4);
                tvCourseDescription.setEllipsize(android.text.TextUtils.TruncateAt.END);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────
    // Curriculum tab
    // ─────────────────────────────────────────────────────────────────────

    private void setupCurriculum() {
        // Summary counts
        int totalLessons = lessons.size();
        int completedLessons = 0;
        for (Lesson l : lessons) if (l.isCompleted()) completedLessons++;

        if (tvCurriculumSummary != null) {
            tvCurriculumSummary.setText(
                    totalLessons + " bài học • " + chapters.size() + " chương");
        }
        if (tvCurriculumProgress != null) {
            tvCurriculumProgress.setText(completedLessons + "/" + totalLessons + " hoàn thành");
        }

        // Build adapter
        curriculumAdapter = new CourseCurriculumAdapter(this, chapters);

        // Highlight the first active (or first unlocked incomplete) lesson
        int activeLessonId = -1;
        for (Lesson l : lessons) {
            if (l.isActive()) { activeLessonId = l.getId(); break; }
        }
        if (activeLessonId < 0) {
            for (Lesson l : lessons) {
                if (!l.isLocked() && !l.isCompleted()) {
                    activeLessonId = l.getId();
                    break;
                }
            }
        }
        curriculumAdapter.setActiveLessonId(activeLessonId);
        curriculumAdapter.setOnLessonClickListener(lesson -> openLesson(lesson.getId()));

        rvCourseCurriculum.setLayoutManager(new LinearLayoutManager(this));
        rvCourseCurriculum.setAdapter(curriculumAdapter);
        rvCourseCurriculum.setNestedScrollingEnabled(false);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Start / Continue button
    // ─────────────────────────────────────────────────────────────────────

    private void setupStartButton() {
        if (btnStartLearning == null) return;
        btnStartLearning.setOnClickListener(v -> {
            // Resume at first active lesson, else first incomplete unlocked lesson
            for (Lesson l : lessons) {
                if (l.isActive() && !l.isLocked()) { openLesson(l.getId()); return; }
            }
            for (Lesson l : lessons) {
                if (!l.isLocked() && !l.isCompleted()) { openLesson(l.getId()); return; }
            }
            // All done or all locked — restart from lesson 1
            if (!lessons.isEmpty()) openLesson(lessons.get(0).getId());
        });
    }

    /** Launches LessonActivity for the given lesson ID (only if unlocked). */
    private void openLesson(int lessonId) {
        for (Lesson l : lessons) {
            if (l.getId() == lessonId) {
                if (l.isLocked()) {
                    com.app.cinx.util.ToastUtil.showCustomToast(this, "Bài học chưa mở khoá!");
                    return;
                }
                startActivity(LessonActivity.newIntent(this, lessonId));
                return;
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────    // Cart badge refresh
    // ───────────────────────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartBadge();
    }

    private void refreshCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartRepository.getInstance().getItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    // ───────────────────────────────────────────────────────────────────    // Purchase state
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Toggle UI between purchased and non-purchased states.
     * Call this after receiving the real purchase status from backend.
     */
    public void updatePurchaseState(boolean purchased) {
        this.isPurchased = purchased;

        layoutActionUnpurchased.setVisibility(purchased ? View.GONE : View.VISIBLE);
        layoutActionPurchased.setVisibility(purchased ? View.VISIBLE : View.GONE);

        if (layoutHeroProgress != null) {
            layoutHeroProgress.setVisibility(purchased ? View.VISIBLE : View.GONE);
        }
    }
}
