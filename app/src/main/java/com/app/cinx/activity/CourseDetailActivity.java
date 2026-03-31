package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.app.cinx.api.dto.LessonResponse;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.utils.UserManager;
import android.widget.Toast;
import com.app.cinx.adapter.CourseCurriculumAdapter;
import com.app.cinx.data.CartRepository;
import com.app.cinx.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;

import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.AddToCartRequest;
import com.app.cinx.api.dto.LearningItemProgressResponse;
import com.app.cinx.api.dto.CheckEnrollmentStatus;
import com.app.cinx.utils.PriceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    // ── Data ────────────────────────────────────────────────────────────────────────
    private List<SectionResponse> chapters = new ArrayList<>();
    private List<LessonResponse> lessons = new ArrayList<>();
    private String courseIdStr;
    private Set<String> completedLessonIds = new HashSet<>();
    private boolean isAllLocked = true;

    // ──────────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        initViews();
        setupNavButtons();
        setupTabs();
        setupDescriptionExpand();
        setupStartButton();

        updatePurchaseState(isPurchased);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // View initialisation
    // ──────────────────────────────────────────────────────────────────────────────

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
        
        courseIdStr = getIntent().getStringExtra("COURSE_ID");

        long originalPriceVal = getIntent().getLongExtra("COURSE_PRICE", 1200000L);
        long currentPriceVal = getIntent().getLongExtra("COURSE_DISCOUNTED_PRICE", 599000L);
        String courseTitle = getIntent().getStringExtra("COURSE_TITLE");
        
        if (courseTitle != null) {
            TextView titleView = findViewById(R.id.tvCourseTitle);
            if (titleView != null) titleView.setText(courseTitle);
        }

        if (tvOriginalPrice != null) {
            tvOriginalPrice.setText(PriceUtil.formatPrice(originalPriceVal));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (tvCurrentPrice != null) {
            tvCurrentPrice.setText(PriceUtil.formatPrice(currentPriceVal));
        }

        if (courseIdStr != null && !courseIdStr.isEmpty()) {
            fetchCourseDetails(courseIdStr);
        }

        View btnAddCart = findViewById(R.id.btnAddCart);
        View btnBuyNow = findViewById(R.id.btnBuyNow);

        View.OnClickListener buyAction = v -> {
            boolean isLoggedIn = UserManager.getInstance().isLoggedIn();
            if (!isLoggedIn) {
                Intent loginIntent = new Intent(CourseDetailActivity.this, com.app.cinx.activity.LoginActivity.class);
                startActivity(loginIntent);
            } else {
                if (courseIdStr != null) {
                    AddToCartRequest req = new AddToCartRequest();
                    req.setCourseId(courseIdStr);
                    RetrofitClient.getInstance().getCartService().addToCart(req).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(CourseDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                refreshCartBadge(); // update badge async if we change it to fetch from API, for now it relies on local state
                            } else {
                                Toast.makeText(CourseDetailActivity.this, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(CourseDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        if (btnAddCart != null) btnAddCart.setOnClickListener(buyAction);
        if (btnBuyNow != null) btnBuyNow.setOnClickListener(buyAction);
    }

    private void fetchCourseDetails(String courseId) {
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getCourseById(courseId).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CourseDetailResponse detail = response.body().getData();
                    updateUIWithCourseDetail(detail);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                Log.e("CourseDetail", "Failed to fetch details", t);
            }
        });
    }

    private void updateUIWithCourseDetail(CourseDetailResponse detail) {
        TextView tvCourseTitle = findViewById(R.id.tvCourseTitle);
        if (tvCourseTitle != null && detail.getTitle() != null) {
            tvCourseTitle.setText(detail.getTitle());
        }

        if (tvCourseDescription != null && detail.getDescription() != null) {
            tvCourseDescription.setText(detail.getDescription());
        }

        TextView tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        TextView tvCurrentPrice = findViewById(R.id.tvCurrentPrice);

        long price = detail.getPrice() != null ? detail.getPrice() : 0L;
        long discountedPrice = detail.getDiscountedPrice() != null ? detail.getDiscountedPrice() : price;

        if (tvCurrentPrice != null) {
            tvCurrentPrice.setText(PriceUtil.formatPrice(discountedPrice));
        }

        if (tvOriginalPrice != null && detail.getDiscountRate() != null && detail.getDiscountRate() > 0) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(PriceUtil.formatPrice(price));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else if (tvOriginalPrice != null) {
            tvOriginalPrice.setVisibility(View.GONE);
        }

        // Set initial purchase state (will be refined by checkEnrollment)
        if (detail.getIsInSubscription() != null && detail.getIsInSubscription()) {
            isPurchased = true;
            updatePurchaseState(isPurchased);
        }
        
        // Update curriculum
        if (detail.getSections() != null) {
            chapters = detail.getSections();
            lessons.clear();
            for (SectionResponse sec : chapters) {
                if (sec.getLessons() != null) {
                    lessons.addAll(sec.getLessons());
                }
            }
            
            checkEnrollmentAndLoadProgress();
        }
    }

    private void checkEnrollmentAndLoadProgress() {
        if (!UserManager.getInstance().isLoggedIn() || courseIdStr == null) {
            isAllLocked = true;
            isPurchased = false;
            updatePurchaseState(isPurchased);
            setupCurriculum();
            return;
        }

        List<String> ids = new ArrayList<>();
        ids.add(courseIdStr);

        RetrofitClient.getInstance().getEnrollmentService().checkEnrollmentStatus(ids).enqueue(new Callback<ApiResponse<List<CheckEnrollmentStatus>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CheckEnrollmentStatus>>> call, Response<ApiResponse<List<CheckEnrollmentStatus>>> response) {
                boolean enrolled = false;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CheckEnrollmentStatus> statusList = response.body().getData();
                    if (!statusList.isEmpty()) {
                        Boolean st = statusList.get(0).getIsEnrolled();
                        enrolled = (st != null && st);
                    }
                }
                
                isPurchased = enrolled;
                isAllLocked = !enrolled;
                updatePurchaseState(isPurchased);
                
                if (!isAllLocked) {
                    fetchLearningProgress(courseIdStr);
                } else {
                    setupCurriculum();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CheckEnrollmentStatus>>> call, Throwable t) {
                Log.e("CourseDetail", "Check enroll failed", t);
                isAllLocked = true;
                isPurchased = false;
                updatePurchaseState(isPurchased);
                setupCurriculum();
            }
        });
    }

    private void fetchLearningProgress(String courseId) {
        RetrofitClient.getInstance().getLearningService().getLearningItemProgressByCourseId(courseId)
                .enqueue(new Callback<ApiResponse<List<LearningItemProgressResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<LearningItemProgressResponse>>> call, Response<ApiResponse<List<LearningItemProgressResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<LearningItemProgressResponse> list = response.body().getData();
                            completedLessonIds.clear();
                            for (LearningItemProgressResponse item : list) {
                                if (item.getIsCompleted() != null && item.getIsCompleted()) {
                                    completedLessonIds.add(item.getItemId());
                                }
                            }
                        }
                        setupCurriculum();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<LearningItemProgressResponse>>> call, Throwable t) {
                        Log.e("CourseDetail", "Failed to fetch learning progress", t);
                        setupCurriculum();
                    }
                });
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

    // ─────────────────────────────────────────────────────────────────
    // Curriculum tab
    // ─────────────────────────────────────────────────────────────────

    private void setupCurriculum() {
        if (chapters == null || chapters.isEmpty()) return;
        
        // Summary counts
        int totalLessons = lessons.size();
        int completedLessons = completedLessonIds.size();

        if (tvCurriculumSummary != null) {
            tvCurriculumSummary.setText(
                    totalLessons + " bài học • " + chapters.size() + " chương");
        }
        if (tvCurriculumProgress != null) {
            tvCurriculumProgress.setText(completedLessons + "/" + totalLessons + " hoàn thành");
        }

        // Build adapter
        curriculumAdapter = new CourseCurriculumAdapter(this, chapters);
        curriculumAdapter.setLockState(isAllLocked);
        curriculumAdapter.setCompletedLessons(completedLessonIds);

        // Highlight the first active (or first unlocked incomplete) lesson
        String activeLessonId = null;
        for (LessonResponse l : lessons) {
            if (!isAllLocked && !completedLessonIds.contains(l.getId())) {
                activeLessonId = l.getId();
                break;
            }
        }
        if (activeLessonId != null) {
            curriculumAdapter.setActiveLessonId(activeLessonId);
        } else if (!lessons.isEmpty() && !isAllLocked) {
             activeLessonId = lessons.get(0).getId();
             curriculumAdapter.setActiveLessonId(activeLessonId);
        }
        curriculumAdapter.setOnLessonClickListener(lesson -> openLesson(lesson.getId()));

        rvCourseCurriculum.setLayoutManager(new LinearLayoutManager(this));
        rvCourseCurriculum.setAdapter(curriculumAdapter);
        rvCourseCurriculum.setNestedScrollingEnabled(false);
    }

    // ─────────────────────────────────────────────────────────────────
    // Start / Continue button
    // ─────────────────────────────────────────────────────────────────

    private void setupStartButton() {
        if (btnStartLearning == null) return;
        btnStartLearning.setOnClickListener(v -> {
            if (isAllLocked) {
                ToastUtil.showCustomToast(this, "Vui lòng đăng nhập để học");
                return;
            }
            
            // Resume at first incomplete unlocked lesson
            for (LessonResponse l : lessons) {
                if (!completedLessonIds.contains(l.getId())) { 
                    openLesson(l.getId()); 
                    return; 
                }
            }
            // All done or all locked — restart from lesson 1
            if (!lessons.isEmpty()) openLesson(lessons.get(0).getId());
        });
    }

    /** Launches LessonActivity for the given lesson ID (only if unlocked). */
    private void openLesson(String lessonId) {
        for (LessonResponse l : lessons) {
            if (l.getId().equals(lessonId)) {
                if (isAllLocked) {
                    ToastUtil.showCustomToast(this, "Bài học chưa mở khóa!");
                    return;
                }
                startActivity(LessonActivity.newIntent(this, courseIdStr, lessonId));
                return;
            }
        }
    }

    // ── Cart badge refresh ───────────────────────────────────────────────────────────

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
