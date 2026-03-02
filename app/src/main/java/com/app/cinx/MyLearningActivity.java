package com.app.cinx;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.adapter.CompletedCourseAdapter;
import com.app.cinx.adapter.GoalAdapter;
import com.app.cinx.adapter.InProgressCourseAdapter;
import com.app.cinx.model.Goal;
import com.app.cinx.util.NavHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyLearningActivity extends AppCompatActivity {

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
    private FrameLayout segmentedControl;
    private View tabIndicator;
    private TextView tabInProgress;
    private TextView tabCompleted;
    private ImageView imgAvatar;

    // Adapters
    private GoalAdapter goalAdapter;
    private InProgressCourseAdapter inProgressAdapter;
    private CompletedCourseAdapter completedAdapter;

    // Tab state
    private int currentTab = 0; // 0 = in-progress, 1 = completed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning_native);

        // Use current date for initial month
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1; // 1-based
        currentYear = cal.get(Calendar.YEAR);

        initViews();
        setupAvatar();
        setupGoals();
        setupCalendar();
        setupCourses();
        setupTabControl();
        setupNavigation();
    }

    private void initViews() {
        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvGoalsTitle = findViewById(R.id.tvGoalsTitle);
        tvGoalsProgress = findViewById(R.id.tvGoalsProgress);
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        segmentedControl = findViewById(R.id.segmentedControl);
        tabIndicator = findViewById(R.id.tabIndicator);
        tabInProgress = findViewById(R.id.tabInProgress);
        tabCompleted = findViewById(R.id.tabCompleted);
        imgAvatar = findViewById(R.id.imgAvatar);

        // Month navigation
        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> navigateMonth(-1));
        findViewById(R.id.btnNextMonth).setOnClickListener(v -> navigateMonth(1));
    }

    private void setupAvatar() {
        Glide.with(this)
                .load("https://i.pravatar.cc/150?u=8")
                .circleCrop()
                .into(imgAvatar);
    }

    // ==================== CALENDAR ====================

    private void setupCalendar() {
        buildCalendar();
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
        buildCalendar();
        // Reset goals to default
        loadGoals(-1);
    }

    private void buildCalendar() {
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

    // ==================== GOALS ====================

    private void setupGoals() {
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalAdapter = new GoalAdapter(getDefaultGoals());
        goalsRecyclerView.setAdapter(goalAdapter);
    }

    private void loadGoals(int day) {
        List<Goal> goals;
        if (day == 24) {
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

    // ==================== COURSES ====================

    private void setupCourses() {
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create adapters
        inProgressAdapter = new InProgressCourseAdapter(getInProgressCourses());
        completedAdapter = new CompletedCourseAdapter(getCompletedCourses());

        // Default: show in-progress
        coursesRecyclerView.setAdapter(inProgressAdapter);
    }

    private void setupTabControl() {
        tabInProgress.setOnClickListener(v -> switchTab(0));
        tabCompleted.setOnClickListener(v -> switchTab(1));

        // Setup indicator width after layout
        segmentedControl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        segmentedControl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int padding = dpToPx(4);
                        int totalWidth = segmentedControl.getWidth() - 2 * padding;
                        int tabWidth = totalWidth / 2;

                        android.view.ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
                        lp.width = tabWidth;
                        tabIndicator.setLayoutParams(lp);
                    }
                });
    }

    private void switchTab(int index) {
        if (index == currentTab) return;
        currentTab = index;

        int padding = dpToPx(4);
        int totalWidth = segmentedControl.getWidth() - 2 * padding;
        int tabWidth = totalWidth / 2;

        // Animate indicator
        tabIndicator.animate()
                .translationX(index * tabWidth)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Update text colors
        tabInProgress.setTextColor(index == 0
                ? Color.parseColor("#1E293B") : Color.parseColor("#64748B"));
        tabCompleted.setTextColor(index == 1
                ? Color.parseColor("#1E293B") : Color.parseColor("#64748B"));

        // Swap adapter
        if (index == 0) {
            coursesRecyclerView.setAdapter(inProgressAdapter);
        } else {
            coursesRecyclerView.setAdapter(completedAdapter);
        }
    }

    private List<InProgressCourseAdapter.CourseItem> getInProgressCourses() {
        List<InProgressCourseAdapter.CourseItem> courses = new ArrayList<>();
        courses.add(new InProgressCourseAdapter.CourseItem(
                "Advanced React Patterns", 75,
                "Custom Hooks Deep Dive",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&q=80",
                "violet"));
        courses.add(new InProgressCourseAdapter.CourseItem(
                "UI Design Fundamentals", 30,
                "Color Theory & Emotion",
                "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80",
                "pink"));
        courses.add(new InProgressCourseAdapter.CourseItem(
                "Business English B2", 50,
                "Negotiation Skills",
                "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=300&q=80",
                "indigo"));
        return courses;
    }

    private List<CompletedCourseAdapter.CourseItem> getCompletedCourses() {
        List<CompletedCourseAdapter.CourseItem> courses = new ArrayList<>();
        courses.add(new CompletedCourseAdapter.CourseItem(
                "HTML & CSS Basics", "98/100", "12/04/2026",
                "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=300&q=80"));
        courses.add(new CompletedCourseAdapter.CourseItem(
                "Intro to Python", "95/100", "05/03/2026",
                "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=300&q=80"));
        return courses;
    }

    // ==================== NAVIGATION ====================

    private void setupNavigation() {
        NavHelper.setupNavigation(this, R.id.navCourses);
    }

    // ==================== UTILS ====================

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
