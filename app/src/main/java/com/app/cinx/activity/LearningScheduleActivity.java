package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.MyLearningAdapter;
import com.app.cinx.model.EnrolledCourse;
import com.app.cinx.utils.NavHelper;
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
    private final Set<Integer> eventDays = new HashSet<>();
    private Integer streakStartDay = null;
    private Integer streakEndDay = null;

    // Views
    private LinearLayout calendarGrid;
    private TextView tvMonthYear;
    private TextView tvGoalsTitle;
    private TextView tvDailyGoalText;
    private android.widget.ProgressBar pbDailyGoal;
    private TextView tvDailyGoalXp;
    private android.widget.Button btnManageGoal;
    private RecyclerView coursesRecyclerView;
    private ImageView imgAvatar;
    private TextView tvStreakDays;
    private TextView tvXP;

    // Daily Goal state
    private com.app.cinx.api.dto.DailyGoalResponse currentDailyGoal;
    private List<EnrolledCourse> inProgressCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning_native);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1; // 1-based
        currentYear = cal.get(Calendar.YEAR);

        fetchInProgressCourses();
        bindViews();
        fetchMyStreak();
        setupCalendarAndGoals();
        setupAvatar();
        setupCourses();
        setupNavigation();
    }

    private void fetchInProgressCourses() {
        inProgressCourses = new ArrayList<>();
        com.app.cinx.api.EnrollmentService enrollmentService = com.app.cinx.api.RetrofitClient.getInstance().getEnrollmentService();
        com.app.cinx.api.LearningService learningService = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        
        enrollmentService.getEnrolledCourses(1, 100).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.PaginatedApiResponseCourseResponse> call, retrofit2.Response<com.app.cinx.api.dto.PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.app.cinx.api.dto.CourseResponse> enrollments = response.body().getData();
                    if (enrollments.isEmpty()) return;
                    
                    List<String> courseIds = new ArrayList<>();
                    for (com.app.cinx.api.dto.CourseResponse cr : enrollments) {
                        if (cr != null && cr.getId() != null) courseIds.add(cr.getId());
                    }
                    if (courseIds.isEmpty()) return;

                    learningService.getCourseProgressByCourseIds(courseIds).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> pCall, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> pResp) {
                            if (pResp.isSuccessful() && pResp.body() != null && pResp.body().getData() != null) {
                                List<com.app.cinx.api.dto.CourseProgressResponse> progresses = pResp.body().getData();
                                
                                int count = 0;
                                for (com.app.cinx.api.dto.CourseResponse cr : enrollments) {
                                    if (count >= 3) break; // max 3 items
                                    if (cr == null || cr.getId() == null) continue;
                                    
                                    com.app.cinx.api.dto.CourseProgressResponse match = null;
                                    for (com.app.cinx.api.dto.CourseProgressResponse p : progresses) {
                                        if (cr.getId().equals(p.getCourseId())) {
                                            match = p;
                                            break;
                                        }
                                    }
                                    
                                    boolean isCompleted = match != null && Boolean.TRUE.equals(match.getIsCompleted());
                                    if (!isCompleted) {
                                        int id = cr.getId().hashCode();
                                        String title = cr.getTitle();
                                        String instructor = (cr.getInstructor() != null && cr.getInstructor().getName() != null) ? cr.getInstructor().getName() : "Unknown";
                                        String thumbnail = (cr.getImages() != null && !cr.getImages().isEmpty()) ? cr.getImages().get(0).getImageUrl() : "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80";
                                        String category = cr.getCategory();
                                        
                                        int progressPercent = 0;
                                        if (match != null && match.getTotalItems() != null && match.getTotalItems() > 0 && match.getCompletedItems() != null) {
                                            progressPercent = (int) (((double) match.getCompletedItems() / match.getTotalItems()) * 100);
                                        }
                                        inProgressCourses.add(EnrolledCourse.progress(id, title, instructor, thumbnail, category, progressPercent));
                                        count++;
                                    }
                                }
                                runOnUiThread(() -> {
                                    if (coursesRecyclerView.getAdapter() != null) {
                                        coursesRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.CourseProgressResponse>>> pCall, Throwable t) {
                            android.util.Log.e("LearningSchedule", "Failed to fetch course progress", t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.PaginatedApiResponseCourseResponse> call, Throwable t) {
                android.util.Log.e("LearningSchedule", "Failed to fetch enrollments", t);
            }
        });
    }

    private void bindViews() {
        tvMonthYear         = findViewById(R.id.tvMonthYear);
        calendarGrid        = findViewById(R.id.calendarGrid);
        tvDailyGoalText     = findViewById(R.id.tvDailyGoalText);
        pbDailyGoal         = findViewById(R.id.pbDailyGoal);
        tvDailyGoalXp       = findViewById(R.id.tvDailyGoalXp);
        btnManageGoal       = findViewById(R.id.btnManageGoal);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        tvStreakDays        = findViewById(R.id.tvStreakDays);
        tvXP                = findViewById(R.id.tvXP);
        if (tvXP != null) {
            tvXP.setText(String.format("%,d", com.app.cinx.utils.UserManager.getInstance().getUserXp()));
        }
        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvGoalsTitle = findViewById(R.id.tvGoalsTitle);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        imgAvatar = findViewById(R.id.imgAvatar);

        // Month navigation
        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> navigateMonth(-1));
        findViewById(R.id.btnNextMonth).setOnClickListener(v -> navigateMonth(1));
        
        btnManageGoal.setOnClickListener(v -> showManageGoalDialog());

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

    private void fetchMyStreak() {
        com.app.cinx.api.LearningService ls = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        ls.getMyStreak().enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.UserStreakResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.UserStreakResponse>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.UserStreakResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.app.cinx.api.dto.UserStreakResponse streak = response.body().getData();
                    runOnUiThread(() -> {
                        int currentStreak = streak.getCurrentStreak() != null ? streak.getCurrentStreak() : 0;
                        tvStreakDays.setText(currentStreak + " Ngày");
                        
                        // Set streak ranges on Calendar
                        if (currentStreak > 0) {
                            Calendar today = Calendar.getInstance();
                            if (currentMonth == today.get(Calendar.MONTH) + 1 && currentYear == today.get(Calendar.YEAR)) {
                                streakEndDay = today.get(Calendar.DAY_OF_MONTH);
                                streakStartDay = Math.max(1, streakEndDay - currentStreak + 1);
                            } else {
                                streakStartDay = null;
                                streakEndDay = null;
                            }
                        } else {
                            streakStartDay = null;
                            streakEndDay = null;
                        }
                        
                        // We will update Calendar streak lines next time it renders or right now
                        renderCalendar();
                    });
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.UserStreakResponse>> call, Throwable t) {
                android.util.Log.e("LearningSchedule", "fetchMyStreak failed", t);
            }
        });
    }

    private void fetchGoalsForMonth(int year, int month) {
        com.app.cinx.api.LearningService ls = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        ls.getDailyGoalsInMonth(year, month).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.DailyGoalResponse>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.DailyGoalResponse>>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.DailyGoalResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    eventDays.clear();
                    for (com.app.cinx.api.dto.DailyGoalResponse goal : response.body().getData()) {
                        String date = goal.getGoalDate(); // assuming "YYYY-MM-DD"
                        if (date != null && date.length() >= 10) {
                            try {
                                int dYear = Integer.parseInt(date.substring(0, 4));
                                int dMonth = Integer.parseInt(date.substring(5, 7));
                                int dDay = Integer.parseInt(date.substring(8, 10));
                                if (dYear == year && dMonth == month) {
                                    eventDays.add(dDay);
                                }
                            } catch (Exception e) {}
                        }
                    }
                    runOnUiThread(() -> renderCalendar());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<List<com.app.cinx.api.dto.DailyGoalResponse>>> call, Throwable t) {
                android.util.Log.e("LearningSchedule", "fetchGoalsForMonth failed", t);
            }
        });
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
        
        fetchMyStreak();
        fetchGoalsForMonth(currentYear, currentMonth);
        renderCalendar();
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
                }

                rowLayout.addView(cell);
            }

            calendarGrid.addView(rowLayout);
        }

        // Wait for user to select a day. No default selection.
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
        if (streakStartDay != null && streakEndDay != null) {
            if (day >= streakStartDay && day <= streakEndDay) {
                if (streakStartDay.equals(streakEndDay)) return "single";
                if (day == streakStartDay) return "start";
                if (day == streakEndDay) return "end";
                return "middle";
            }
        }
        return null;
    }

    // ── Goals ────────────────────────────────────────────────────────────────
    
    private void setupCalendarAndGoals() {
        fetchGoalsForMonth(currentYear, currentMonth);
        renderCalendar();
    }

    private void loadGoals(int day) {
        if (day <= 0) {
            Calendar today = Calendar.getInstance();
            if (currentMonth == today.get(Calendar.MONTH) + 1 && currentYear == today.get(Calendar.YEAR)) {
                day = today.get(Calendar.DAY_OF_MONTH);
            } else {
                day = 1;
            }
        }
        tvGoalsTitle.setText(String.format("MỤC TIÊU NGÀY %02d/%02d/%d", day, currentMonth, currentYear));
        
        String dateStr = String.format("%04d-%02d-%02d", currentYear, currentMonth, day);
        fetchDailyGoal(dateStr);
    }
    
    private void fetchDailyGoal(String dateStr) {
        com.app.cinx.api.LearningService ls = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        ls.getDailyGoal(dateStr).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentDailyGoal = response.body().getData();
                    updateDailyGoalUI(currentDailyGoal);
                } else {
                    currentDailyGoal = null;
                    updateDailyGoalUI(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> call, Throwable t) {
                currentDailyGoal = null;
                updateDailyGoalUI(null);
            }
        });
    }

    private void updateDailyGoalUI(com.app.cinx.api.dto.DailyGoalResponse goal) {
        if (goal == null) {
            tvDailyGoalText.setText("Bạn chưa thiết lập mục tiêu XP cho ngày này.");
            pbDailyGoal.setProgress(0);
            tvDailyGoalXp.setText("0 / 0 XP");
            btnManageGoal.setText("Thiết lập Mục tiêu");
        } else {
            int current = goal.getCurrentXp() != null ? goal.getCurrentXp() : 0;
            int target = goal.getTargetXp() != null ? goal.getTargetXp() : 0;
            tvDailyGoalText.setText(current >= target ? "Chúc mừng! Bạn đã hoàn thành mục tiêu ngày." : "Hãy tiếp tục học để đạt mục tiêu!");
            
            pbDailyGoal.setMax(target > 0 ? target : 100);
            pbDailyGoal.setProgress(current);
            tvDailyGoalXp.setText(current + " / " + target + " XP");
            btnManageGoal.setText("Chỉnh sửa Mục tiêu");
        }
    }
    
    private void showManageGoalDialog() {
        if (selectedDay < 1) return;
        String dateStr = String.format("%04d-%02d-%02d", currentYear, currentMonth, selectedDay);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Mục tiêu XP");
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentDailyGoal != null && currentDailyGoal.getTargetXp() != null) {
            input.setText(String.valueOf(currentDailyGoal.getTargetXp()));
        } else {
            input.setText("100");
        }
        builder.setView(input);
        
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String val = input.getText().toString();
            if(!val.isEmpty()) {
                saveDailyGoal(dateStr, Integer.parseInt(val));
            }
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        
        if (currentDailyGoal != null) {
            builder.setNeutralButton("Xóa", (dialog, which) -> deleteDailyGoal(dateStr));
        }
        
        builder.show();
    }
    
    private void saveDailyGoal(String dateStr, int targetXp) {
        com.app.cinx.api.dto.SetDailyGoalRequest req = new com.app.cinx.api.dto.SetDailyGoalRequest();
        req.setTargetXp(targetXp);
        req.setGoalDate(dateStr);
        
        com.app.cinx.api.LearningService ls = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> call;
        
        if (currentDailyGoal == null) {
            call = ls.setDailyGoal(req);
        } else {
            call = ls.editDailyGoal(req);
        }
        
        call.enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> response) {
                fetchDailyGoal(dateStr);
                renderCalendar(); // Refresh calendar dots
            }

            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<com.app.cinx.api.dto.DailyGoalResponse>> call, Throwable t) {
                com.app.cinx.utils.ToastUtil.showCustomToast(LearningScheduleActivity.this, "Lỗi lưu mục tiêu");
            }
        });
    }
    
    private void deleteDailyGoal(String dateStr) {
        com.app.cinx.api.LearningService ls = com.app.cinx.api.RetrofitClient.getInstance().getLearningService();
        ls.deleteDailyGoal(dateStr).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<Void>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<Void>> response) {
                fetchDailyGoal(dateStr);
                renderCalendar();
            }

            @Override
            public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<Void>> call, Throwable t) {
                com.app.cinx.utils.ToastUtil.showCustomToast(LearningScheduleActivity.this, "Lỗi xóa mục tiêu");
            }
        });
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
