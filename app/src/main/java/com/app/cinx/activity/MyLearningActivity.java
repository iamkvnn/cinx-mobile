package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.MyLearningAdapter;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.dto.CertificateRequestResponse;
import com.app.cinx.model.EnrolledCourse;
import com.app.cinx.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.app.cinx.api.EnrollmentService;
import com.app.cinx.api.SocialService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.WishlistItemResponse;
import com.app.cinx.api.dto.CertificateRequestResponse;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.dto.PaginatedApiResponseCourseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * MyLearningActivity — "Khóa học của tôi"
 * -----------------------------------------
 * Displays the current user's enrolled courses in three switchable tabs:
 *   · Đang học   – in-progress courses with a progress bar and "Continue" CTA
 *   · Hoàn thành – completed courses with grade + certificate / review actions
 *   · Đã lưu     – bookmarked courses with price + "View Course" CTA
 *
 * A filter bottom-sheet supports sorting by recency or progress, and
 * filtering by course category (Design / Coding / Business).
 * A search bar further narrows the visible list in real-time.
 */
public class MyLearningActivity extends AppCompatActivity
        implements MyLearningAdapter.OnCourseActionListener {

    // Tab enum
    private enum Tab { PROGRESS, COMPLETED, SAVED }

    // State
    private Tab    currentTab    = Tab.PROGRESS;
    private String searchQuery   = "";
    private String activeSort     = "recent";
    private String activeCategory = "all";
    private String tempSort     = "recent";
    private String tempCategory = "all";

    // Data
    private final List<EnrolledCourse> allCourses = new ArrayList<>();

    // Adapter
    private MyLearningAdapter adapter;

    // Views
    private RecyclerView  rvCourses;
    private LinearLayout  layoutEmpty;
    private View          filterActiveDot;
    private EditText      etSearch;
    private TextView      tabProgress, tabCompleted, tabSaved;

    // Filter dialog + chip refs
    private BottomSheetDialog filterDialog;
    private TextView chipSortRecent, chipSortHigh, chipSortLow;
    private TextView chipCatAll, chipCatDesign, chipCatCoding, chipCatBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        allCourses.clear();
        bindViews();
        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupFilterDialog();
        
        fetchData();
    }

    private void fetchData() {
        fetchEnrolledCourses();
        fetchWishlist();
    }
    
    private void fetchEnrolledCourses() {
        EnrollmentService enrollmentService = RetrofitClient.getInstance().getEnrollmentService();
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        enrollmentService.getEnrolledCourses(1, 100).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> enrollments = response.body().getData();
                    if (enrollments.isEmpty()) {
                        runOnUiThread(() -> {
                            setupTabs();
                            renderCourses();
                        });
                        return;
                    }
                    
                    List<String> courseIds = new ArrayList<>();
                    for (CourseResponse cr : enrollments) {
                        if (cr != null && cr.getId() != null) {
                            courseIds.add(cr.getId());
                        }
                    }
                    
                    if (courseIds.isEmpty()) return;

                    learningService.getCourseProgressByCourseIds(courseIds).enqueue(new Callback<ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> progressCall, Response<ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> progressResponse) {
                            if (progressResponse.isSuccessful() && progressResponse.body() != null && progressResponse.body().getData() != null) {
                                List<com.app.cinx.api.dto.CourseProgressResponse> progresses = progressResponse.body().getData();
                                
                                for (CourseResponse cr : enrollments) {
                                    if (cr == null || cr.getId() == null) continue;
                                    
                                    com.app.cinx.api.dto.CourseProgressResponse match = null;
                                    for (com.app.cinx.api.dto.CourseProgressResponse p : progresses) {
                                        if (cr.getId().equals(p.getCourseId())) {
                                            match = p;
                                            break;
                                        }
                                    }
                                    
                                    int id = cr.getId().hashCode();
                                    String title = cr.getTitle();
                                    String instructor = (cr.getInstructor() != null && cr.getInstructor().getName() != null) ? cr.getInstructor().getName() : "Unknown";
                                    String thumbnail = (cr.getImages() != null && !cr.getImages().isEmpty()) ? cr.getImages().get(0).getImageUrl() : "data:image/jpeg;base64,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";
                                    String category = cr.getCategory();
                                    
                                    boolean isCompleted = match != null && Boolean.TRUE.equals(match.getIsCompleted());
                                    if (isCompleted) {
                                        String date = match.getCompletionTime() != null ? match.getCompletionTime() : "N/A";
                                        String grade = match.getAvgScore() != null ? String.valueOf(match.getAvgScore()) : "N/A";
                                        allCourses.add(EnrolledCourse.completed(id, cr.getId(), title, instructor, thumbnail, category, date, grade));
                                    } else {
                                        int progressPercent = 0;
                                        if (match != null && match.getTotalItems() != null && match.getTotalItems() > 0 && match.getCompletedItems() != null) {
                                            progressPercent = (int) (((double) match.getCompletedItems() / match.getTotalItems()) * 100);
                                        }
                                        allCourses.add(EnrolledCourse.progress(id, cr.getId(), title, instructor, thumbnail, category, progressPercent));
                                    }
                                }
                                runOnUiThread(() -> {
                                    setupTabs();
                                    renderCourses();
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> progressCall, Throwable t) {
                            Log.e("MyLearning", "Failed to fetch course progress", t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                Log.e("MyLearning", "Failed to fetch enrollments", t);
            }
        });
    }

    private void fetchWishlist() {
        SocialService socialService = RetrofitClient.getInstance().getSocialService();
        if (socialService == null) return; // if not initialized
        socialService.getWishlist().enqueue(new Callback<ApiResponse<List<WishlistItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItemResponse>>> call, Response<ApiResponse<List<WishlistItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<WishlistItemResponse> wishlist = response.body().getData();
                    CourseService courseService = RetrofitClient.getInstance().getCourseService();
                    if (wishlist.isEmpty()) {
                        runOnUiThread(() -> {
                            setupTabs();
                            renderCourses();
                        });
                        return;
                    }
                    
                    List<String> ids = new ArrayList<>();
                    for (WishlistItemResponse item : wishlist) {
                        ids.add(item.getCourseId());
                    }
                    
                    courseService.getCourseById_1(ids).enqueue(new Callback<ApiResponse<List<CourseResponse>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<CourseResponse>>> cc, Response<ApiResponse<List<CourseResponse>>> cr) {
                            if (cr.isSuccessful() && cr.body() != null && cr.body().getData() != null) {
                                for (CourseResponse course : cr.body().getData()) {
                                    int id = course.getId() != null ? course.getId().hashCode() : 0;
                                    String title = course.getTitle();
                                    String instructor = (course.getInstructor() != null && course.getInstructor().getName() != null) ? course.getInstructor().getName() : "Unknown";
                                    String thumbnail = (course.getImages() != null && !course.getImages().isEmpty()) ? course.getImages().get(0).getImageUrl() : "https://bizweb.dktcdn.net/100/021/721/articles/anh-chup-man-hinh-2023-07-27-luc-12-23-53.jpg?v=1690435505903";
                                    String category = course.getCategory();
                                    double rating = course.getRating() != null ? course.getRating() : 0.0;
                                    long price = course.getDiscountedPrice() != null ? course.getDiscountedPrice() : (course.getPrice() != null ? course.getPrice() : 0L);
                                    
                                    allCourses.add(EnrolledCourse.saved(id, course.getId(), title, instructor, thumbnail, category, com.app.cinx.utils.PriceUtil.formatPrice(price), rating));
                                }
                                runOnUiThread(() -> {
                                    setupTabs();
                                    renderCourses();
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<List<CourseResponse>>> cc, Throwable t) {
                            Log.e("MyLearning", "Failed to fetch wishlist courses", t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItemResponse>>> call, Throwable t) {
                Log.e("MyLearning", "Failed to fetch wishlist", t);
            }
        });
    }

    private void bindViews() {
        rvCourses       = findViewById(R.id.rvCourses);
        layoutEmpty     = findViewById(R.id.layoutEmpty);
        filterActiveDot = findViewById(R.id.filterActiveDot);
        etSearch        = findViewById(R.id.etSearch);
        tabProgress     = findViewById(R.id.tabProgress);
        tabCompleted    = findViewById(R.id.tabCompleted);
        tabSaved        = findViewById(R.id.tabSaved);

        ImageButton btnBack   = findViewById(R.id.btnBack);
        View        btnFilter = findViewById(R.id.btnFilter);
        btnBack.setOnClickListener(v   -> finish());
        btnFilter.setOnClickListener(v -> openFilterSheet());
    }

    private void setupRecyclerView() {
        adapter = new MyLearningAdapter(new ArrayList<>(), this);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);
        rvCourses.setHasFixedSize(false);
    }

    private void setupTabs() {
        int nP = 0, nC = 0, nS = 0;
        for (EnrolledCourse c : allCourses) {
            switch (c.getStatus()) {
                case PROGRESS:  nP++; break;
                case COMPLETED: nC++; break;
                default:        nS++; break;
            }
        }
        tabProgress.setText(String.format("Đang học (%d)", nP));
        tabCompleted.setText(String.format("Hoàn thành (%d)", nC));
        tabSaved.setText(String.format("Đã lưu (%d)", nS));

        tabProgress.setOnClickListener(v  -> selectTab(Tab.PROGRESS));
        tabCompleted.setOnClickListener(v -> selectTab(Tab.COMPLETED));
        tabSaved.setOnClickListener(v     -> selectTab(Tab.SAVED));

        applyTabStyle(currentTab);
    }

    private void selectTab(Tab tab) {
        currentTab = tab;
        applyTabStyle(tab);
        renderCourses();
    }

    private void applyTabStyle(Tab active) {
        int activeBg   = R.drawable.bg_order_tab_active;
        int inactiveBg = R.drawable.bg_order_tab_inactive;
        int activeClr  = getColor(R.color.primary);
        int inactiveClr = getColor(R.color.text_secondary);

        tabProgress.setBackgroundResource( active == Tab.PROGRESS  ? activeBg : inactiveBg);
        tabProgress.setTextColor(          active == Tab.PROGRESS  ? activeClr : inactiveClr);
        tabCompleted.setBackgroundResource(active == Tab.COMPLETED ? activeBg : inactiveBg);
        tabCompleted.setTextColor(         active == Tab.COMPLETED ? activeClr : inactiveClr);
        tabSaved.setBackgroundResource(    active == Tab.SAVED     ? activeBg : inactiveBg);
        tabSaved.setTextColor(             active == Tab.SAVED     ? activeClr : inactiveClr);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                searchQuery = s.toString();
                renderCourses();
            }
        });
    }

    private void setupFilterDialog() {
        filterDialog = new BottomSheetDialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.layout_my_learning_filter, null);
        filterDialog.setContentView(v);

        chipSortRecent  = v.findViewById(R.id.chipSortRecent);
        chipSortHigh    = v.findViewById(R.id.chipSortHigh);
        chipSortLow     = v.findViewById(R.id.chipSortLow);
        chipCatAll      = v.findViewById(R.id.chipCatAll);
        chipCatDesign   = v.findViewById(R.id.chipCatDesign);
        chipCatCoding   = v.findViewById(R.id.chipCatCoding);
        chipCatBusiness = v.findViewById(R.id.chipCatBusiness);

        chipSortRecent.setOnClickListener(x  -> setTempSort("recent"));
        chipSortHigh.setOnClickListener(x    -> setTempSort("progress_high"));
        chipSortLow.setOnClickListener(x     -> setTempSort("progress_low"));
        chipCatAll.setOnClickListener(x      -> setTempCategory("all"));
        chipCatDesign.setOnClickListener(x   -> setTempCategory("Design"));
        chipCatCoding.setOnClickListener(x   -> setTempCategory("Coding"));
        chipCatBusiness.setOnClickListener(x -> setTempCategory("Business"));

        v.findViewById(R.id.btnReset).setOnClickListener(x -> resetTempFilters());
        v.findViewById(R.id.btnApply).setOnClickListener(x -> applyFilters());
        v.findViewById(R.id.dragHandle).setOnClickListener(x -> filterDialog.dismiss());

        filterDialog.setOnDismissListener(d -> {
            tempSort     = activeSort;
            tempCategory = activeCategory;
        });
    }

    private void openFilterSheet() {
        tempSort     = activeSort;
        tempCategory = activeCategory;
        refreshChipUI();
        filterDialog.show();
    }

    private void setTempSort(String sort) {
        tempSort = sort;
        refreshSortChips();
    }

    private void setTempCategory(String category) {
        tempCategory = category;
        refreshCategoryChips();
    }

    private void resetTempFilters() {
        tempSort     = "recent";
        tempCategory = "all";
        refreshChipUI();
    }

    private void applyFilters() {
        activeSort     = tempSort;
        activeCategory = tempCategory;
        boolean nonDefault = !"recent".equals(activeSort) || !"all".equals(activeCategory);
        filterActiveDot.setVisibility(nonDefault ? View.VISIBLE : View.GONE);
        renderCourses();
        filterDialog.dismiss();
    }

    private void refreshChipUI() {
        refreshSortChips();
        refreshCategoryChips();
    }

    private void refreshSortChips() {
        setChipActive(chipSortRecent, "recent".equals(tempSort));
        setChipActive(chipSortHigh,   "progress_high".equals(tempSort));
        setChipActive(chipSortLow,    "progress_low".equals(tempSort));
    }

    private void refreshCategoryChips() {
        setChipActive(chipCatAll,      "all".equals(tempCategory));
        setChipActive(chipCatDesign,   "Design".equals(tempCategory));
        setChipActive(chipCatCoding,   "Coding".equals(tempCategory));
        setChipActive(chipCatBusiness, "Business".equals(tempCategory));
    }

    private void setChipActive(TextView chip, boolean active) {
        chip.setBackgroundResource(active
                ? R.drawable.bg_filter_chip_active : R.drawable.bg_filter_chip);
        chip.setTextColor(active
                ? getColor(android.R.color.white) : getColor(R.color.text_secondary));
    }

    private void renderCourses() {
        EnrolledCourse.Status target = statusFor(currentTab);

        List<EnrolledCourse> result = new ArrayList<>();
        for (EnrolledCourse c : allCourses) {
            if (c.getStatus() == target) result.add(c);
        }

        if (!searchQuery.trim().isEmpty()) {
            String q = searchQuery.toLowerCase();
            List<EnrolledCourse> filtered = new ArrayList<>();
            for (EnrolledCourse c : result) {
                if (c.getTitle().toLowerCase().contains(q)
                        || c.getInstructor().toLowerCase().contains(q)) {
                    filtered.add(c);
                }
            }
            result = filtered;
        }

        if (!"all".equals(activeCategory)) {
            List<EnrolledCourse> catFiltered = new ArrayList<>();
            for (EnrolledCourse c : result) {
                if (c.getCategory().equals(activeCategory)) catFiltered.add(c);
            }
            result = catFiltered;
        }

        switch (activeSort) {
            case "progress_high":
                result.sort((a, b) -> b.getProgress() - a.getProgress()); break;
            case "progress_low":
                result.sort((a, b) -> a.getProgress() - b.getProgress()); break;
            default:
                result.sort((a, b) -> Integer.compare(a.getId(), b.getId())); break;
        }

        boolean empty = result.isEmpty();
        rvCourses.setVisibility(  empty ? View.GONE    : View.VISIBLE);
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        adapter.updateList(result);
    }

    private EnrolledCourse.Status statusFor(Tab tab) {
        switch (tab) {
            case COMPLETED: return EnrolledCourse.Status.COMPLETED;
            case SAVED:     return EnrolledCourse.Status.SAVED;
            default:        return EnrolledCourse.Status.PROGRESS;
        }
    }

    @Override public void onContinueLearning(EnrolledCourse course) {
        Intent intent = new Intent(MyLearningActivity.this, LessonActivity.class);
        intent.putExtra("courseId", course.getId());
        startActivity(intent);
    }

    @Override public void onGetCertificate(EnrolledCourse course) {
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        learningService.applyForCertificate(course.getCourseId()).enqueue(new Callback<ApiResponse<CertificateRequestResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CertificateRequestResponse>> call, Response<ApiResponse<CertificateRequestResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CertificateRequestResponse req = response.body().getData();
                    ToastUtil.showCustomToast(MyLearningActivity.this, "Đã gửi yêu cầu nhận chứng chỉ! Trạng thái: " + req.getStatus());
                } else {
                    ToastUtil.showCustomToast(MyLearningActivity.this, "Yêu cầu đã tồn tại hoặc có lỗi xảy ra");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CertificateRequestResponse>> call, Throwable t) {
                ToastUtil.showCustomToast(MyLearningActivity.this, "Lỗi kết nối khi xin chứng chỉ");
            }
        });
    }

    @Override public void onRateCourse(EnrolledCourse course) {
        ToastUtil.showCustomToast(this, "Cảm ơn bạn đã đánh giá \"" + course.getTitle() + "\"!");
    }

    @Override public void onViewSavedCourse(EnrolledCourse course) {
        startActivity(new Intent(this, CourseDetailActivity.class));
    }

}
