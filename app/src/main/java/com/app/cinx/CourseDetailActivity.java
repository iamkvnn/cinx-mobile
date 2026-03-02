package com.app.cinx;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class CourseDetailActivity extends AppCompatActivity {

    // Trang thai mua hang - chi quan ly trong code, khong hien thi tren UI
    private boolean isPurchased = false;
    private boolean isDescriptionExpanded = false;

    // Tham chieu view
    private LinearLayout layoutActionUnpurchased;
    private LinearLayout layoutActionPurchased;
    private LinearLayout layoutHeroProgress;

    // Bai hoc (Tab 2 - Noi dung)
    private ImageView ivLesson1Icon;
    private TextView tvPreview1;

    // Mo ta kho hoc - xem them / thu gon (Tab 1 - Tong quan)
    private TextView tvCourseDescription;
    private TextView tvExpandDescription;

    private ViewFlipper viewFlipper;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        initViews();
        setupNavButtons();
        setupTabs();
        setupDescriptionExpand();

        // Khoi tao UI voi trang thai mac dinh: chua mua
        updatePurchaseState(isPurchased);
    }

    private void initViews() {
        layoutActionUnpurchased = findViewById(R.id.layoutActionUnpurchased);
        layoutActionPurchased = findViewById(R.id.layoutActionPurchased);
        layoutHeroProgress = findViewById(R.id.layoutHeroProgress);

        ivLesson1Icon = findViewById(R.id.ivLesson1Icon);
        tvPreview1 = findViewById(R.id.tvPreview1);

        tvCourseDescription = findViewById(R.id.tvCourseDescription);
        tvExpandDescription = findViewById(R.id.tvExpandDescription);

        viewFlipper = findViewById(R.id.viewFlipper);
        tabLayout = findViewById(R.id.tabLayout);

        // Ap dung gach ngang cho gia goc
        TextView tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        if (tvOriginalPrice != null) {
            tvOriginalPrice.setPaintFlags(
                tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void setupNavButtons() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        ImageButton btnBookmark = findViewById(R.id.btnBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v -> {
                // Logic luu bookmark - ket noi backend sau
                btnBookmark.setImageResource(R.drawable.ic_bookmark);
            });
        }
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewFlipper.setDisplayedChild(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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

    /**
     * Cap nhat giao dien dua tren trang thai da mua hay chua mua.
     * Goi ham nay sau khi nhan ket qua tu API thuc te.
     *
     * @param purchased true neu da so huu khoa hoc, false neu la khach.
     */
    public void updatePurchaseState(boolean purchased) {
        this.isPurchased = purchased;

        // 1. Bottom Action Bar
        layoutActionUnpurchased.setVisibility(purchased ? View.GONE : View.VISIBLE);
        layoutActionPurchased.setVisibility(purchased ? View.VISIBLE : View.GONE);

        // 2. Hero Progress Overlay
        if (layoutHeroProgress != null) {
            layoutHeroProgress.setVisibility(purchased ? View.VISIBLE : View.GONE);
        }

        // 3. Curriculum Lessons UI
        if (ivLesson1Icon != null && tvPreview1 != null) {
            if (purchased) {
                ivLesson1Icon.setImageResource(R.drawable.ic_play);
                tvPreview1.setVisibility(View.GONE);
            } else {
                ivLesson1Icon.setImageResource(R.drawable.ic_play);
                tvPreview1.setVisibility(View.VISIBLE);
            }
        }
    }
}
