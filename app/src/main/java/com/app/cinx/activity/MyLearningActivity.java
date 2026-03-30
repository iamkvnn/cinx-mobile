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
import com.app.cinx.model.EnrolledCourse;
import com.app.cinx.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.app.cinx.api.EnrollmentService;
import com.app.cinx.api.SocialService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.WishlistItemResponse;
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
        enrollmentService.getEnrolledCourses(0, 100).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> enrollments = response.body().getData();
                    for (CourseResponse cr : enrollments) {
                        if (cr == null) continue;
                        
                        String idStr = cr.getId();
                        int id = (idStr != null) ? idStr.hashCode() : 0;
                        String title = cr.getTitle();
                        String instructor = cr.getDescription(); // fallback
                        String thumbnail = "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80";
                        String category = cr.getCategory();
                        
                        // Fake progress since its not in CourseResponse 
                        boolean isCompleted = false;
                        if (isCompleted) {
                            allCourses.add(EnrolledCourse.completed(id, title, instructor, thumbnail, category, "N/A", "N/A"));
                        } else {
                            allCourses.add(EnrolledCourse.progress(id, title, instructor, thumbnail, category, 0, "N/A", "N/A"));
                        }
                    }
                    runOnUiThread(() -> {
                        setupTabs();
                        renderCourses();
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
                                    String instructor = course.getDescription(); // fallback
                                    String thumbnail = "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80";
                                    String category = course.getCategory();
                                    double rating = course.getRating() != null ? course.getRating() : 0.0;
                                    long price = course.getDiscountedPrice() != null ? course.getDiscountedPrice() : (course.getPrice() != null ? course.getPrice() : 0L);
                                    
                                    allCourses.add(EnrolledCourse.saved(id, title, instructor, thumbnail, category, com.app.cinx.utils.PriceUtil.formatPrice(price), rating));
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
        startActivity(new Intent(this, LessonActivity.class));
    }

    @Override public void onGetCertificate(EnrolledCourse course) {
        startActivity(new Intent(this, CertificatesActivity.class));
    }

    @Override public void onRateCourse(EnrolledCourse course) {
        ToastUtil.showCustomToast(this, "Cảm ơn bạn đã đánh giá \"" + course.getTitle() + "\"!");
    }

    @Override public void onViewSavedCourse(EnrolledCourse course) {
        startActivity(new Intent(this, CourseDetailActivity.class));
    }

}
