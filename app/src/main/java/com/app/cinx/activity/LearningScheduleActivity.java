package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.GoalAdapter;
import com.app.cinx.adapter.MyLearningAdapter;
import com.app.cinx.model.EnrolledCourse;
import com.app.cinx.model.Goal;
import com.app.cinx.util.NavHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LearningScheduleActivity — "Lịch trình"
 * -----------------------------------------
 * The "My Courses" bottom-nav tab screen.
 * Shows:
 *   · Monthly calendar with studied-day indicators
 *   · Daily goals list
 *   · In-progress courses preview (max 3) + "Xem tất cả" → MyLearningActivity
 */
public class LearningScheduleActivity extends AppCompatActivity {
    // Calendar state
    private int currentMonth;
    private int currentYear;
    private int selectedDay = -1;
    private TextView previouslySelectedDayView = null;

    // Streak data (day ranges)
    private final int[][] streakRanges = {{10, 14}, {20, 22}};
    private final Set<Integer> eventDays = new HashSet<>(Arrays.asList(5, 10, 11, 12, 13, 14, 20, 21, 22, 24));

    // Views
    private LinearLayout calendarGrid;
    private TextView tvMonthYear;
    private TextView tvGoalsTitle;
    private TextView tvGoalsProgress;
    private RecyclerView goalsRecyclerView;
    private RecyclerView coursesRecyclerView;
    private ImageView imgAvatar;

    // Adapters
    private GoalAdapter goalAdapter;
    private List<EnrolledCourse> inProgressCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning_native);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1; // 1-based
        currentYear = cal.get(Calendar.YEAR);

        initMockData();
        bindViews();
        setupGoals();
        setupCalendar();
        setupAvatar();
        setupCourses();
        setupNavigation();
    }

    private void initMockData() {
        inProgressCourses = new ArrayList<>();
        inProgressCourses.add(EnrolledCourse.progress(1,
                "UI/UX Design Masterclass: Từ Cơ Bản Đến Nâng Cao", "Hà Linh",
                "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80",
                "Design", 65, "Bài 4.2: Component & Auto Layout", "2 giờ trước"));
        inProgressCourses.add(EnrolledCourse.progress(2,
                "Fullstack React & Node.js cho người mới", "Minh Tuấn",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&q=80",
                "Coding", 15, "Bài 2.1: Cài đặt môi trường Node.js", "Hôm qua"));
        inProgressCourses.add(EnrolledCourse.progress(3,
                "Mobile App Design với Figma", "Hà Linh",
                "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=300&q=80",
                "Design", 2, "Bài 1.1: Giới thiệu khóa học", "Tuần trước"));
    }

    private void bindViews() {
        tvMonthYear         = findViewById(R.id.tvMonthYear);
        calendarGrid        = findViewById(R.id.calendarGrid);
        goalsRecyclerView   = findViewById(R.id.goalsRecyclerView);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        tvGoalsProgress     = findViewById(R.id.tvGoalsProgress);
        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvGoalsTitle = findViewById(R.id.tvGoalsTitle);
        tvGoalsProgress = findViewById(R.id.tvGoalsProgress);
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        imgAvatar = findViewById(R.id.imgAvatar);

        // Month navigation
        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> navigateMonth(-1));
        findViewById(R.id.btnNextMonth).setOnClickListener(v -> navigateMonth(1));

        TextView btnViewAll = findViewById(R.id.btnViewAllCourses);
        btnViewAll.setOnClickListener(v ->
                startActivity(new Intent(this, MyLearningActivity.class)));
    }

    private void setupAvatar() {
        Glide.with(this)
                .load("https://i.pravatar.cc/150?u=8")
                .circleCrop()
                .into(imgAvatar);
    }

    private void navigateMonth(int delta) {
        currentMonth += delta;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        } else if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        selectedDay = -1;
        previouslySelectedDayView = null;
        setupCalendar();
        // Reset goals to default
        loadGoals(-1);
    }

    // ── Calendar ────────────────────────────────────────────────────────────

    private void setupCalendar() {
        renderCalendar();
    }

    private void renderCalendar() {
        calendarGrid.removeAllViews();

        // Update month/year display
        tvMonthYear.setText("Tháng " + currentMonth + ", " + currentYear);

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth - 1, 1);

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Convert to Monday=0: Sun(1)→6, Mon(2)→0, Tue(3)→1, ...
        int startOffset = (dayOfWeek + 5) % 7;

        int totalCells = startOffset + daysInMonth;
        int rows = (int) Math.ceil(totalCells / 7.0);

        for (int row = 0; row < rows; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(52)));

            for (int col = 0; col < 7; col++) {
                int cellIndex = row * 7 + col;
                int day = cellIndex - startOffset + 1;

                FrameLayout cell = new FrameLayout(this);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                cell.setLayoutParams(cellParams);

                if (day >= 1 && day <= daysInMonth) {
                    // Add streak strip if applicable
                    String streakStatus = getStreakStatus(day);
                    if (streakStatus != null) {
                        addStreakStrip(cell, streakStatus);
                    }

                    // Day text
                    TextView dayView = createDayView(day, streakStatus);
                    cell.addView(dayView);

                    // Event dot
                    if (eventDays.contains(day)) {
                        addEventDot(cell, day == selectedDay);
                    }

                    // Auto-select today or day 24 on first load
                    Calendar today = Calendar.getInstance();
                    if (selectedDay == -1 && currentMonth == today.get(Calendar.MONTH) + 1
                            && currentYear == today.get(Calendar.YEAR)
                            && day == today.get(Calendar.DAY_OF_MONTH)) {
                        selectDay(day, dayView);
                    }
                }

                rowLayout.addView(cell);
            }

            calendarGrid.addView(rowLayout);
        }

        // If no day was selected (not current month), select day 1
        if (selectedDay == -1) {
            // Find first day view and select it
            selectFirstDay();
        }
    }

    private void selectFirstDay() {
        // Find the first day TextView in the grid
        for (int i = 0; i < calendarGrid.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) calendarGrid.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                FrameLayout cell = (FrameLayout) row.getChildAt(j);
                for (int k = 0; k < cell.getChildCount(); k++) {
                    View child = cell.getChildAt(k);
                    if (child instanceof TextView && child.getTag() != null) {
                        selectDay((int) child.getTag(), (TextView) child);
                        return;
                    }
                }
            }
        }
    }

    private TextView createDayView(int day, String streakStatus) {
        TextView dayView = new TextView(this);
        dayView.setText(String.valueOf(day));
        dayView.setGravity(Gravity.CENTER);
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        dayView.setTypeface(Typeface.DEFAULT_BOLD);
        dayView.setTag(day);

        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(
                dpToPx(44), dpToPx(44));
        tvParams.gravity = Gravity.CENTER;
        dayView.setLayoutParams(tvParams);

        if (streakStatus != null) {
            dayView.setTextColor(Color.parseColor("#7C3AED"));
            dayView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            dayView.setTextColor(Color.parseColor("#64748B"));
        }

        dayView.setOnClickListener(v -> selectDay(day, dayView));

        return dayView;
    }

    private void selectDay(int day, TextView dayView) {
        // Deselect previous
        if (previouslySelectedDayView != null) {
            previouslySelectedDayView.setBackground(null);
            // Restore text color based on streak status
            int prevDay = (int) previouslySelectedDayView.getTag();
            String prevStreak = getStreakStatus(prevDay);
            if (prevStreak != null) {
                previouslySelectedDayView.setTextColor(Color.parseColor("#7C3AED"));
            } else {
                previouslySelectedDayView.setTextColor(Color.parseColor("#64748B"));
            }
            previouslySelectedDayView.setTypeface(Typeface.DEFAULT_BOLD);
        }

        // Select new
        selectedDay = day;
        previouslySelectedDayView = dayView;
        dayView.setBackgroundResource(R.drawable.bg_calendar_selected);
        dayView.setTextColor(Color.parseColor("#5C31B3"));
        dayView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Load goals for selected day
        loadGoals(day);
    }

    private void addStreakStrip(FrameLayout cell, String status) {
        View strip = new View(this);
        FrameLayout.LayoutParams stripParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        stripParams.topMargin = dpToPx(4);
        stripParams.bottomMargin = dpToPx(4);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#268B5CF6"));

        float r = dpToPx(22);
        switch (status) {
            case "start":
                stripParams.leftMargin = dpToPx(4);
                bg.setCornerRadii(new float[]{r, r, 0, 0, 0, 0, r, r});
                break;
            case "end":
                stripParams.rightMargin = dpToPx(4);
                bg.setCornerRadii(new float[]{0, 0, r, r, r, r, 0, 0});
                break;
            case "single":
                stripParams.leftMargin = dpToPx(4);
                stripParams.rightMargin = dpToPx(4);
                bg.setCornerRadius(r);
                break;
            // "middle" — no radius
        }

        strip.setBackground(bg);
        strip.setLayoutParams(stripParams);
        cell.addView(strip);
    }

    private void addEventDot(FrameLayout cell, boolean isSelected) {
        View dot = new View(this);
        FrameLayout.LayoutParams dotParams = new FrameLayout.LayoutParams(dpToPx(5), dpToPx(5));
        dotParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        dotParams.bottomMargin = dpToPx(2);
        dot.setLayoutParams(dotParams);

        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(isSelected ? Color.parseColor("#7C3AED") : Color.parseColor("#F472B6"));
        dot.setBackground(dotBg);

        cell.addView(dot);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private String getStreakStatus(int day) {
        for (int[] range : streakRanges) {
            if (day >= range[0] && day <= range[1]) {
                if (range[0] == range[1]) return "single";
                if (day == range[0]) return "start";
                if (day == range[1]) return "end";
                return "middle";
            }
        }
        return null;
    }

    // ── Goals ────────────────────────────────────────────────────────────────

    private void setupGoals() {
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalAdapter = new GoalAdapter(getDefaultGoals());
        goalsRecyclerView.setAdapter(goalAdapter);
    }

    private void loadGoals(int day) {
        List<Goal> goals;
        Calendar today = Calendar.getInstance();
        if (day == today.get(Calendar.DAY_OF_MONTH) && currentMonth == today.get(Calendar.MONTH) + 1
                && currentYear == today.get(Calendar.YEAR)) {
            goals = new ArrayList<>();
            goals.add(new Goal(1, "Hoàn thành Quiz Module 3", true, "30m", "quiz"));
            goals.add(new Goal(2, "Xem video 'React Hooks'", true, "45m", "video"));
            goals.add(new Goal(3, "Bài tập thực hành UI", false, "60m", "code"));

            tvGoalsTitle.setText("MỤC TIÊU NGÀY " + day + "/" + currentMonth);
            tvGoalsProgress.setText("2/3 Done");
        } else {
            goals = getDefaultGoals();
            if (day > 0) {
                tvGoalsTitle.setText("MỤC TIÊU NGÀY " + day + "/" + currentMonth);
            } else {
                tvGoalsTitle.setText("MỤC TIÊU HÔM NAY");
            }
            tvGoalsProgress.setText("0/2 Done");
        }
        goalAdapter.updateGoals(goals);
    }

    private List<Goal> getDefaultGoals() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal(1, "Nghỉ ngơi hoặc ôn tập nhẹ", false, "--", "rest"));
        goals.add(new Goal(2, "Thêm mục tiêu mới?", false, "+", "add"));
        return goals;
    }

    // ── In-progress courses preview ──────────────────────────────────────────

    private void setupCourses() {
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coursesRecyclerView.setAdapter(new MyLearningAdapter(
                inProgressCourses,
                new MyLearningAdapter.OnCourseActionListener() {
                    @Override public void onContinueLearning(EnrolledCourse c) {
                        startActivity(new Intent(
                                LearningScheduleActivity.this, LessonActivity.class));
                    }
                    @Override public void onGetCertificate(EnrolledCourse c) {}
                    @Override public void onRateCourse(EnrolledCourse c)     {}
                    @Override public void onViewSavedCourse(EnrolledCourse c){}
                }));
        coursesRecyclerView.setHasFixedSize(false);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    private void setupNavigation() {
        NavHelper.setupNavigation(this, R.id.navCourses);
    }
}
