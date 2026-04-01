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
                                        String thumbnail = (cr.getImages() != null && !cr.getImages().isEmpty()) ? cr.getImages().get(0).getImageUrl() : "data:image/jpeg;base64,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";
                                        String category = cr.getCategory();
                                        
                                        int progressPercent = 0;
                                        if (match != null && match.getTotalItems() != null && match.getTotalItems() > 0 && match.getCompletedItems() != null) {
                                            progressPercent = (int) (((double) match.getCompletedItems() / match.getTotalItems()) * 100);
                                        }
                                        inProgressCourses.add(EnrolledCourse.progress(id, cr.getId(), title, instructor, thumbnail, category, progressPercent));
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
                        startActivity(LessonActivity.newIntent(LearningScheduleActivity.this, c.getCourseId(), null));
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
