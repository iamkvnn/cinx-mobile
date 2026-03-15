package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
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

    private Calendar      displayedMonth;
    private TextView      tvMonthYear;
    private LinearLayout  calendarGrid;
    private RecyclerView  goalsRecyclerView;
    private RecyclerView  coursesRecyclerView;
    private TextView      tvGoalsProgress;

    private List<Goal>          goals;
    private List<EnrolledCourse> inProgressCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning_native);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        displayedMonth = Calendar.getInstance();

        initMockData();
        bindViews();
        setupCalendar();
        setupGoals();
        setupCourses();
        setupNavigation();
    }

    private void initMockData() {
        goals = new ArrayList<>();
        goals.add(new Goal(1, "Xem bài giảng: Component & Auto Layout", true,  "09:00", "video"));
        goals.add(new Goal(2, "Làm bài quiz chương 4",                   true,  "10:30", "quiz"));
        goals.add(new Goal(3, "Code thực hành: Figma Prototyping",       false, "14:00", "code"));

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

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            renderCalendar();
        });
        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            renderCalendar();
        });

        TextView btnViewAll = findViewById(R.id.btnViewAllCourses);
        btnViewAll.setOnClickListener(v ->
                startActivity(new Intent(this, MyLearningActivity.class)));
    }

    // ── Calendar ────────────────────────────────────────────────────────────

    private void setupCalendar() {
        renderCalendar();
    }

    private void renderCalendar() {
        int month = displayedMonth.get(Calendar.MONTH) + 1;
        int year  = displayedMonth.get(Calendar.YEAR);
        tvMonthYear.setText(String.format("Tháng %d, %d", month, year));

        // Mock studied days
        Set<Integer> studiedDays = new HashSet<>();
        for (int d = 1;  d <= 5;  d++) studiedDays.add(d);
        for (int d = 8;  d <= 12; d++) studiedDays.add(d);
        studiedDays.add(15); studiedDays.add(17);
        for (int d = 20; d <= 22; d++) studiedDays.add(d);

        // Today's day number (only if same month/year is showing)
        Calendar today  = Calendar.getInstance();
        int todayDay = (today.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH)
                     && today.get(Calendar.YEAR)  == displayedMonth.get(Calendar.YEAR))
                     ? today.get(Calendar.DAY_OF_MONTH) : -1;

        // First weekday offset: Mon=0 … Sun=6
        Calendar first = (Calendar) displayedMonth.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int rawDow      = first.get(Calendar.DAY_OF_WEEK);
        int startOffset = (rawDow == Calendar.SUNDAY) ? 6 : rawDow - Calendar.MONDAY;
        int daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendarGrid.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        int cell = 0;
        LinearLayout row = null;

        // Leading empty cells
        for (int pad = 0; pad < startOffset; pad++) {
            if (cell % 7 == 0) { row = newCalendarRow(); calendarGrid.addView(row); }
            View empty = inflater.inflate(R.layout.item_calendar_day, row, false);
            empty.setVisibility(View.INVISIBLE);
            row.addView(empty);
            cell++;
        }

        // Day cells
        for (int day = 1; day <= daysInMonth; day++) {
            if (cell % 7 == 0) { row = newCalendarRow(); calendarGrid.addView(row); }
            View  cv    = inflater.inflate(R.layout.item_calendar_day, row, false);
            TextView tvDay   = cv.findViewById(R.id.tvDay);
            View     todayBg = cv.findViewById(R.id.todayBg);
            View     dot     = cv.findViewById(R.id.studiedDot);

            tvDay.setText(String.valueOf(day));
            if (day == todayDay)          todayBg.setVisibility(View.VISIBLE);
            if (studiedDays.contains(day)) dot.setVisibility(View.VISIBLE);

            row.addView(cv);
            cell++;
        }

        // Trailing filler to complete final row
        while (cell % 7 != 0) {
            View empty = inflater.inflate(R.layout.item_calendar_day, row, false);
            empty.setVisibility(View.INVISIBLE);
            row.addView(empty);
            cell++;
        }
    }

    private LinearLayout newCalendarRow() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;
    }

    // ── Goals ────────────────────────────────────────────────────────────────

    private void setupGoals() {
        long done = 0;
        for (Goal g : goals) if (g.isDone()) done++;
        tvGoalsProgress.setText(done + "/" + goals.size() + " Done");

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsRecyclerView.setAdapter(new GoalAdapter(goals));
        goalsRecyclerView.setHasFixedSize(false);
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
