package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.api.dto.AddToWishlistRequest;
import com.app.cinx.api.dto.WishlistItemResponse;
import com.bumptech.glide.Glide;

import com.app.cinx.R;
import com.app.cinx.api.dto.LessonResponse;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.ReviewResponse;
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
import com.app.cinx.api.dto.CertificateRequestResponse;
import com.app.cinx.api.dto.CheckEnrollmentStatus;
import com.app.cinx.utils.PriceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity {

    // Purchase state — replace with actual API result in production
    private boolean isPurchased = false;
    private boolean isDescriptionExpanded = false;
    private boolean isWishlisted = false;
    private ImageButton btnBookmarkIcon;

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
    private TextView tvCTT;
    private TextView tvOriginalPrice;
    private CourseCurriculumAdapter curriculumAdapter;
    // ── Cart badge ──────────────────────────────────────────────────────────────────
    private FrameLayout cartBadgeFrame;
    private TextView     tvCartBadge;
    private TextView tvCurrentPrice;
    // ── Continue/Start button ─────────────────────────────────────────────
    private AppCompatButton btnStartLearning;
    private AppCompatButton btnRequestCertificate;

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
        tvCTT = findViewById(R.id.tvCourseTitle2);

        tvCourseDescription = findViewById(R.id.tvCourseDescription);
        tvExpandDescription = findViewById(R.id.tvExpandDescription);

        viewFlipper = findViewById(R.id.viewFlipper);
        tabLayout   = findViewById(R.id.tabLayout);

        rvCourseCurriculum  = findViewById(R.id.rvCourseCurriculum);
        tvCurriculumSummary = findViewById(R.id.tvCurriculumSummary);
        tvCurriculumProgress= findViewById(R.id.tvCurriculumProgress);

        btnStartLearning = findViewById(R.id.btnStartLearning);
        btnRequestCertificate = findViewById(R.id.btnRequestCertificate);

        // Cart badge
        cartBadgeFrame = findViewById(R.id.btnCartBadgeDetail);
        tvCartBadge    = findViewById(R.id.tvCartBadgeDetail);

        // Strike-through original price
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);

        courseIdStr = getIntent().getStringExtra("COURSE_ID");


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
        fetchReviews(courseId);
    }

    private void fetchReviews(String courseId) {
        RetrofitClient.getInstance().getSocialService().getReviewsByCourseId(courseId).enqueue(new Callback<ApiResponse<List<ReviewResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewResponse>>> call, Response<ApiResponse<List<ReviewResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ReviewResponse> reviews = response.body().getData();
                    updateReviewsUI(reviews);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewResponse>>> call, Throwable t) {
                Log.e("CourseDetail", "Failed to fetch reviews", t);
            }
        });
    }

    private void updateReviewsUI(List<ReviewResponse> reviews) {
        TextView tvReview1Name = findViewById(R.id.tvReview1Name);
        TextView tvReview1Date = findViewById(R.id.tvReview1Date);
        TextView tvReview1Content = findViewById(R.id.tvReview1Content);

        TextView tvReview2Name = findViewById(R.id.tvReview2Name);
        TextView tvReview2Date = findViewById(R.id.tvReview2Date);
        TextView tvReview2Content = findViewById(R.id.tvReview2Content);

        if (reviews == null || reviews.isEmpty()) {
            if (tvReview1Name != null && tvReview1Name.getParent() != null) {
                View parent1 = (View) tvReview1Name.getParent().getParent().getParent();
                if (parent1 != null && parent1 instanceof LinearLayout) {
                    parent1.setVisibility(View.GONE);
                }
            }
            if (tvReview2Name != null && tvReview2Name.getParent() != null) {
                View parent2 = (View) tvReview2Name.getParent().getParent().getParent();
                if (parent2 != null && parent2 instanceof LinearLayout) {
                    parent2.setVisibility(View.GONE);
                }
            }
            return;
        }

        if (reviews.size() > 0) {
            ReviewResponse r1 = reviews.get(0);
            if (tvReview1Name != null) {
                tvReview1Name.setText("Học viên");
            }
            if (tvReview1Content != null && r1.getContent() != null) {
                tvReview1Content.setText(r1.getContent());
            }
            // Date formatting is optional, I'll set date to empty for now if no creation time is available, or use the string if available
        }

        if (reviews.size() > 1) {
            ReviewResponse r2 = reviews.get(1);
            if (tvReview2Name != null) {
                tvReview2Name.setText("Học viên");
            }
            if (tvReview2Content != null && r2.getContent() != null) {
                tvReview2Content.setText(r2.getContent());
            }
        } else {
            if (tvReview2Name != null && tvReview2Name.getParent() != null) {
                View parent2 = (View) tvReview2Name.getParent().getParent().getParent();
                if (parent2 != null && parent2 instanceof LinearLayout) {
                    parent2.setVisibility(View.GONE);
                }
            }
        }
    }

    private void updateUIWithCourseDetail(CourseDetailResponse detail) {
        TextView tvCourseTitle = findViewById(R.id.tvCourseTitle);
        if (tvCourseTitle != null && detail.getTitle() != null) {
            tvCourseTitle.setText(detail.getTitle());
        }

        TextView titleView = findViewById(R.id.tvCourseTitle);
        titleView.setText(detail.getTitle());
        tvCTT.setText(detail.getTitle());


        tvOriginalPrice.setText(PriceUtil.formatPrice(detail.getDiscountedPrice()));
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        tvCurrentPrice.setText(PriceUtil.formatPrice(detail.getPrice()));


        ImageView ivCourseThumbnail = findViewById(R.id.ivCourseThumbnail);
        if (ivCourseThumbnail != null) {
            String imageUrl = "https://img.lovepik.com/photo/40015/9423.jpg_wh860.jpg";
            Glide.with(this).load(imageUrl).into(ivCourseThumbnail);
        }

        if (tvCourseDescription != null && detail.getDescription() != null) {
            tvCourseDescription.setText(detail.getDescription());
        }

        TextView tvCourseCategory = findViewById(R.id.tvCourseCategory);
        if (tvCourseCategory != null && detail.getCategory() != null) {
            tvCourseCategory.setText(detail.getCategory().toUpperCase());
        }

        TextView tvCourseRating = findViewById(R.id.tvCourseRating);
        if (tvCourseRating != null) {
            if (detail.getRating() != null) {
                tvCourseRating.setText(String.format(Locale.US, "%.1f", detail.getRating()));
            } else {
                tvCourseRating.setText("0.0");
            }
        }

        TextView tvCourseReviewCount = findViewById(R.id.tvCourseReviewCount);
        if (tvCourseReviewCount != null) {
            tvCourseReviewCount.setText("(0 đánh giá)");
        }

        TextView tvCourseStudents = findViewById(R.id.tvCourseStudents);
        if (tvCourseStudents != null && detail.getEnrollmentCount() != null) {
            tvCourseStudents.setText(detail.getEnrollmentCount() + "+");
        }

        TextView tvCourseDuration = findViewById(R.id.tvCourseDuration);
        if (tvCourseDuration != null && detail.getDuration() != null) {
            tvCourseDuration.setText(detail.getDuration() + " Giờ");
        }

        TextView tvCourseCertificate = findViewById(R.id.tvCourseCertificate);
        if (tvCourseCertificate != null) {
            if (detail.getHasCertificate() != null && detail.getHasCertificate()) {
                tvCourseCertificate.setText(detail.getCertificateTitle() != null ? detail.getCertificateTitle() : "Cấp sau khóa học");
            } else {
                tvCourseCertificate.setText("Không có chứng chỉ");
            }
        }

        TextView tvCourseTabRating = findViewById(R.id.tvCourseTabRating);
        if (tvCourseTabRating != null) {
            if (detail.getRating() != null) {
                tvCourseTabRating.setText(String.format(Locale.US, "%.1f", detail.getRating()));
            } else {
                tvCourseTabRating.setText("0.0");
            }
        }

        TextView tvCourseTabReviewCount = findViewById(R.id.tvCourseTabReviewCount);
        if (tvCourseTabReviewCount != null) {
            tvCourseTabReviewCount.setText("0 đánh giá");
        }

        // Removed whatYouWillLearn binding since the field isn't in DTO yet,
        // using static text in XML layout for now.

        TextView tvInstructorName = findViewById(R.id.tvInstructorName);
        TextView tvInstructorInitials = findViewById(R.id.tvInstructorInitials);
        if (detail.getInstructor() != null && detail.getInstructor().getName() != null) {
            String name = detail.getInstructor().getName();
            if (tvInstructorName != null) tvInstructorName.setText(name);
            if (tvInstructorInitials != null) {
                String initials = "";
                String[] parts = name.split(" ");
                if (parts.length > 0) initials += parts[0].substring(0, 1).toUpperCase();
                if (parts.length > 1) initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
                tvInstructorInitials.setText(initials);
            }
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

            // Sort chapters by orderIndex
            Collections.sort(chapters, new Comparator<SectionResponse>() {
                @Override
                public int compare(SectionResponse s1, SectionResponse s2) {
                    Integer o1 = s1.getOrderIndex();
                    Integer o2 = s2.getOrderIndex();
                    if (o1 == null) o1 = 0;
                    if (o2 == null) o2 = 0;
                    return o1.compareTo(o2);
                }
            });

            lessons.clear();
            for (SectionResponse sec : chapters) {
                if (sec.getLessons() != null) {
                    // Sort lessons in each chapter by orderIndex
                    Collections.sort(sec.getLessons(), new Comparator<LessonResponse>() {
                        @Override
                        public int compare(LessonResponse l1, LessonResponse l2) {
                            Integer o1 = l1.getOrderIndex();
                            Integer o2 = l2.getOrderIndex();
                            if (o1 == null) o1 = 0;
                            if (o2 == null) o2 = 0;
                            return o1.compareTo(o2);
                        }
                    });
                    lessons.addAll(sec.getLessons());
                }
            }

            checkEnrollmentAndLoadProgress();
            checkWishlistStatus();
        }
    }

    private void checkWishlistStatus() {
        RetrofitClient.getInstance().getSocialService().getWishlist().enqueue(new Callback<ApiResponse<List<WishlistItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItemResponse>>> call, Response<ApiResponse<List<WishlistItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    for (WishlistItemResponse item : response.body().getData()) {
                        if (courseIdStr != null && courseIdStr.equals(item.getCourseId())) {
                            isWishlisted = true;
                            break;
                        }
                    }
                    updateWishlistIcon();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItemResponse>>> call, Throwable t) {}
        });
    }

    private void updateWishlistIcon() {
        if (btnBookmarkIcon != null) {
            btnBookmarkIcon.setImageResource(isWishlisted ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            if (isWishlisted) {
                btnBookmarkIcon.setColorFilter(null);
            } else {
                btnBookmarkIcon.setColorFilter(getColor(R.color.white));
            }
        }
    }

    private void toggleWishlist() {
        if (!UserManager.getInstance().isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isWishlisted) {
            RetrofitClient.getInstance().getSocialService().removeFromWishlist(courseIdStr).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful()) {
                        isWishlisted = false;
                        updateWishlistIcon();
                        Toast.makeText(CourseDetailActivity.this, "Đã xoá khỏi wishlist", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {}
            });
        } else {
            AddToWishlistRequest req = new AddToWishlistRequest();
            req.setCourseId(courseIdStr);
            RetrofitClient.getInstance().getSocialService().addToWishlist(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful()) {
                        isWishlisted = true;
                        updateWishlistIcon();
                        Toast.makeText(CourseDetailActivity.this, "Đã thêm vào wishlist", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {}
            });
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

        btnBookmarkIcon = findViewById(R.id.btnBookmark);
        if (btnBookmarkIcon != null) {
            btnBookmarkIcon.setOnClickListener(v -> toggleWishlist());
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

        // Update progress bar and text in hero overlay and floating bar
        int progressPercent = totalLessons > 0 ? (int) ((completedLessons * 100.0f) / totalLessons) : 0;

        TextView tvFloatingProgress = findViewById(R.id.layoutActionPurchased).findViewById(R.id.layoutActionPurchased).findViewWithTag("tvFloatingProgress"); // Update logic later if needed
        android.widget.ProgressBar progressBarFloating = findViewById(R.id.layoutActionPurchased).findViewById(R.id.layoutActionPurchased).findViewWithTag("pbFloatingProgress");

        // This relies on ID match logic
        LinearLayout actionPurchased = findViewById(R.id.layoutActionPurchased);
        if (actionPurchased != null) {
            TextView progressTextFloating = (TextView) ((LinearLayout)((LinearLayout)actionPurchased.getChildAt(0)).getChildAt(0)).getChildAt(1);
            android.widget.ProgressBar pbFloating = (android.widget.ProgressBar) ((LinearLayout)actionPurchased.getChildAt(0)).getChildAt(1);
            if (progressTextFloating != null) progressTextFloating.setText(progressPercent + "%");
            if (pbFloating != null) pbFloating.setProgress(progressPercent);
        }

        if (layoutHeroProgress != null) {
            TextView tvHeroProgress = (TextView) layoutHeroProgress.getChildAt(0);
            android.widget.ProgressBar pbHero = (android.widget.ProgressBar) layoutHeroProgress.getChildAt(1);
            if (tvHeroProgress != null) tvHeroProgress.setText("Tiến độ khóa học: " + progressPercent + "%");
            if (pbHero != null) pbHero.setProgress(progressPercent);
        }

        if (isPurchased && totalLessons > 0 && completedLessons >= totalLessons && btnRequestCertificate != null && btnStartLearning != null) {
            btnStartLearning.setVisibility(View.GONE);
            btnRequestCertificate.setVisibility(View.VISIBLE);
            checkCertificateStatus();
        } else if (btnStartLearning != null && btnRequestCertificate != null) {
            btnStartLearning.setVisibility(View.VISIBLE);
            btnRequestCertificate.setVisibility(View.GONE);
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

    private void checkCertificateStatus() {
        if (courseIdStr == null || !UserManager.getInstance().isLoggedIn()) return;
        RetrofitClient.getInstance().getLearningService().getMyCertificate(courseIdStr)
                .enqueue(new Callback<ApiResponse<CertificateRequestResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CertificateRequestResponse>> call, Response<ApiResponse<CertificateRequestResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            CertificateRequestResponse cert = response.body().getData();
                            if ("PENDING".equalsIgnoreCase(cert.getStatus())) {
                                btnRequestCertificate.setText("Chờ xác nhận");
                                btnRequestCertificate.setEnabled(false);
                                btnRequestCertificate.setBackgroundResource(R.drawable.bg_white_card);
                            } else if ("APPROVED".equalsIgnoreCase(cert.getStatus())) {
                                btnRequestCertificate.setText("Đã cấp chứng chỉ");
                                btnRequestCertificate.setEnabled(false);
                                btnRequestCertificate.setBackgroundResource(R.drawable.bg_white_card);
                            } else {
                                setupRequestCertificateClick();
                            }
                        } else {
                            setupRequestCertificateClick();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CertificateRequestResponse>> call, Throwable t) {
                        setupRequestCertificateClick();
                    }
                });
    }

    private void setupRequestCertificateClick() {
        btnRequestCertificate.setEnabled(true);
        btnRequestCertificate.setText("Yêu cầu cấp chứng chỉ");
        btnRequestCertificate.setBackgroundResource(R.drawable.bg_gradient_button);
        btnRequestCertificate.setOnClickListener(v -> {
            btnRequestCertificate.setEnabled(false);
            btnRequestCertificate.setText("Đang yêu cầu...");
            RetrofitClient.getInstance().getLearningService().applyForCertificate(courseIdStr).enqueue(new Callback<ApiResponse<CertificateRequestResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CertificateRequestResponse>> call, Response<ApiResponse<CertificateRequestResponse>> response) {
                    if (response.isSuccessful()) {
                        ToastUtil.showCustomToast(CourseDetailActivity.this, "Đã gửi yêu cầu cấp chứng chỉ");
                        btnRequestCertificate.setText("Chờ xác nhận");
                        btnRequestCertificate.setBackgroundResource(R.drawable.bg_white_card);
                    } else {
                        ToastUtil.showCustomToast(CourseDetailActivity.this, "Yêu cầu thất bại");
                        setupRequestCertificateClick();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<CertificateRequestResponse>> call, Throwable t) {
                    ToastUtil.showCustomToast(CourseDetailActivity.this, "Lỗi kết nối");
                    setupRequestCertificateClick();
                }
            });
        });
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
