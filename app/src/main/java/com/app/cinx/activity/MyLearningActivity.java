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
import com.app.cinx.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        initMockData();
        bindViews();
        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupFilterDialog();
        renderCourses();
    }

    private void initMockData() {
        allCourses.add(EnrolledCourse.progress(1,
                "UI/UX Design Masterclass: Từ Cơ Bản Đến Nâng Cao", "Hà Linh",
                "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80",
                "Design", 65, "Bài 4.2: Component & Auto Layout", "2 giờ trước"));
        allCourses.add(EnrolledCourse.progress(2,
                "Fullstack React & Node.js cho người mới", "Minh Tuấn",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&q=80",
                "Coding", 15, "Bài 2.1: Cài đặt môi trường Node.js", "Hôm qua"));
        allCourses.add(EnrolledCourse.progress(3,
                "Mobile App Design với Figma", "Hà Linh",
                "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=300&q=80",
                "Design", 2, "Bài 1.1: Giới thiệu khóa học", "Tuần trước"));

        String[][] completed = {
                {"Digital Marketing 101: SEO & Ads", "Sarah Nguyễn",
                 "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=300&q=80",
                 "Business", "15/04/2026", "9.5/10"},
                {"Python cho Data Science", "Minh Tuấn",
                 "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=300&q=80",
                 "Coding", "10/03/2026", "8.7/10"},
                {"Thiết kế Logo chuyên nghiệp", "Hà Linh",
                 "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=300&q=80",
                 "Design", "01/02/2026", "9.0/10"},
                {"Google Ads: Từ A đến Z", "Sarah Nguyễn",
                 "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=300&q=80",
                 "Business", "20/01/2026", "8.5/10"},
                {"JavaScript ES6+ nâng cao", "Minh Tuấn",
                 "https://images.unsplash.com/photo-1579468118864-1b9ea3c0db4a?w=300&q=80",
                 "Coding", "15/01/2026", "9.2/10"},
                {"Adobe Illustrator cơ bản", "Hà Linh",
                 "https://images.unsplash.com/photo-1609921212029-bb5a28e60960?w=300&q=80",
                 "Design", "05/01/2026", "8.0/10"},
                {"Excel cho dân văn phòng", "Sarah Nguyễn",
                 "https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?w=300&q=80",
                 "Business", "20/12/2025", "9.8/10"},
                {"Vue.js 3 căn bản", "Minh Tuấn",
                 "https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=300&q=80",
                 "Coding", "10/12/2025", "8.9/10"},
                {"Photoshop retouching", "Hà Linh",
                 "https://images.unsplash.com/photo-1542744095-fcf48d80b0fd?w=300&q=80",
                 "Design", "01/12/2025", "9.1/10"},
                {"Email Marketing pro", "Sarah Nguyễn",
                 "https://images.unsplash.com/photo-1563986768494-4dee2763ff3f?w=300&q=80",
                 "Business", "15/11/2025", "8.3/10"},
                {"Flutter đa nền tảng", "Minh Tuấn",
                 "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&q=80",
                 "Coding", "01/11/2025", "9.4/10"},
                {"Brand Identity Design", "Hà Linh",
                 "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300&q=80",
                 "Design", "20/10/2025", "8.6/10"},
        };
        int id = 4;
        for (String[] d : completed) {
            allCourses.add(EnrolledCourse.completed(id++, d[0], d[1], d[2], d[3], d[4], d[5]));
        }

        String[][] saved = {
                {"Advanced iOS Development", "Lê Tuấn",
                 "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=300&q=80",
                 "Coding", "1.299.000đ", "4.8"},
                {"Web Animation với GSAP", "Hà Linh",
                 "https://images.unsplash.com/photo-1547082299-de196ea013d6?w=300&q=80",
                 "Design", "899.000đ", "4.7"},
                {"Facebook Ads 2026", "Sarah Nguyễn",
                 "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=300&q=80",
                 "Business", "699.000đ", "4.6"},
                {"TypeScript masterclass", "Minh Tuấn",
                 "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&q=80",
                 "Coding", "999.000đ", "4.9"},
                {"3D modeling với Blender", "Hoàng Long",
                 "https://images.unsplash.com/photo-1617854818583-09e7f077a156?w=300&q=80",
                 "Design", "1.099.000đ", "4.5"},
        };
        for (String[] d : saved) {
            allCourses.add(EnrolledCourse.saved(id++, d[0], d[1], d[2], d[3], d[4],
                    Double.parseDouble(d[5])));
        }
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
