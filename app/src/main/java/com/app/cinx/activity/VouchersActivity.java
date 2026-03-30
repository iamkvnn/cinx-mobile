package com.app.cinx.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.VoucherProfileAdapter;
import com.app.cinx.model.ProfileVoucher;
import com.app.cinx.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VouchersActivity
 *
 * Shows the user's discount vouchers in a ticket-style list.
 * Features:
 *  - Segmented tab: "Khả dụng" / "Hết hạn / Đã dùng"
 *  - Add voucher by code
 *  - Bottom sheet with full voucher detail + copy code
 */
public class VouchersActivity extends AppCompatActivity
        implements VoucherProfileAdapter.VoucherListener {

    // ─────────────────────────────────────────────────────────────────
    // Views
    // ─────────────────────────────────────────────────────────────────

    private ImageView      btnBack;
    private TextView       tabAvailable;
    private TextView       tabExpired;
    private EditText       etVoucherCode;
    private TextView       btnSaveCode;
    private RecyclerView   rvVouchers;
    private LinearLayout   emptyState;

    // Detail bottom sheet views
    private View           sheetOverlay;
    private View           voucherDetailSheet;
    private TextView       tvSheetTitle;
    private TextView       tvSheetCode;
    private TextView       tvSheetExpiry;
    private LinearLayout   llConditions;
    private ImageView      btnCopyCode;
    private TextView       btnUnderstood;

    // ─────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────

    private VoucherProfileAdapter adapter;
    private final List<ProfileVoucher> allVouchers = buildSampleVouchers();
    private boolean showingAvailable = true;

    // ─────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers);

        bindViews();
        setupBackButton();
        setupTabs();
        setupAddCode();
        setupSheet();
        loadTab(true);
    }

    // ─────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        btnBack             = findViewById(R.id.btnBack);
        tabAvailable        = findViewById(R.id.tabAvailable);
        tabExpired          = findViewById(R.id.tabExpired);
        etVoucherCode       = findViewById(R.id.etVoucherCode);
        btnSaveCode         = findViewById(R.id.btnSaveCode);
        rvVouchers          = findViewById(R.id.rvVouchers);
        emptyState          = findViewById(R.id.emptyState);
        sheetOverlay        = findViewById(R.id.sheetOverlay);
        voucherDetailSheet  = findViewById(R.id.voucherDetailSheet);
        tvSheetTitle        = findViewById(R.id.tvSheetTitle);
        tvSheetCode         = findViewById(R.id.tvSheetCode);
        tvSheetExpiry       = findViewById(R.id.tvSheetExpiry);
        llConditions        = findViewById(R.id.llConditions);
        btnCopyCode         = findViewById(R.id.btnCopyCode);
        btnUnderstood       = findViewById(R.id.btnUnderstood);

        // Setup RecyclerView
        adapter = new VoucherProfileAdapter(new ArrayList<>(), this);
        rvVouchers.setLayoutManager(new LinearLayoutManager(this));
        rvVouchers.setAdapter(adapter);
    }

    // ─────────────────────────────────────────────────────────────────
    // Tabs
    // ─────────────────────────────────────────────────────────────────

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabAvailable.setOnClickListener(v -> {
            if (!showingAvailable) {
                showingAvailable = true;
                updateTabStyle();
                loadTab(true);
            }
        });
        tabExpired.setOnClickListener(v -> {
            if (showingAvailable) {
                showingAvailable = false;
                updateTabStyle();
                loadTab(false);
            }
        });
    }

    private void updateTabStyle() {
        if (showingAvailable) {
            tabAvailable.setBackgroundResource(R.drawable.bg_segment_active);
            tabAvailable.setTextColor(getColor(R.color.text_primary));
            tabExpired  .setBackgroundResource(android.R.color.transparent);
            tabExpired  .setTextColor(getColor(R.color.text_secondary));
        } else {
            tabExpired  .setBackgroundResource(R.drawable.bg_segment_active);
            tabExpired  .setTextColor(getColor(R.color.text_primary));
            tabAvailable.setBackgroundResource(android.R.color.transparent);
            tabAvailable.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void loadTab(boolean available) {
        List<ProfileVoucher> filtered = allVouchers.stream()
                .filter(v -> available
                        ? v.getStatus() == ProfileVoucher.Status.AVAILABLE
                        : v.getStatus() != ProfileVoucher.Status.AVAILABLE)
                .collect(Collectors.toList());

        adapter.setItems(filtered);

        boolean empty = filtered.isEmpty();
        rvVouchers.setVisibility(empty ? View.GONE  : View.VISIBLE);
        emptyState .setVisibility(empty ? View.VISIBLE : View.GONE);

        // Update tab label with count
        if (available) {
            tabAvailable.setText(getString(R.string.voucher_tab_available_count,
                    filtered.size()));
        } else {
            tabExpired.setText(getString(R.string.voucher_tab_expired));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Add code
    // ─────────────────────────────────────────────────────────────────

    private void setupAddCode() {
        btnSaveCode.setOnClickListener(v -> {
            String code = etVoucherCode.getText().toString().trim().toUpperCase();
            if (code.isEmpty()) {
                ToastUtil.showCustomToast(this, getString(R.string.voucher_enter_code));
                return;
            }
            // TODO: validate code against backend; for now show feedback
            ToastUtil.showCustomToast(this, getString(R.string.voucher_code_saved, code));
            etVoucherCode.setText("");
        });
    }

    // ─────────────────────────────────────────────────────────────────
    // Bottom sheet
    // ─────────────────────────────────────────────────────────────────

    private void setupSheet() {
        sheetOverlay.setOnClickListener(v -> hideSheet());
        btnUnderstood.setOnClickListener(v -> hideSheet());
        btnCopyCode.setOnClickListener(v -> {
            String code = tvSheetCode.getText().toString();
            android.content.ClipboardManager cm =
                    (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("voucher_code", code));
            ToastUtil.showCustomToast(this, getString(R.string.voucher_code_copied));
        });
    }

    /** Show the detail bottom sheet for a given voucher. */
    private void showSheet(ProfileVoucher voucher) {
        tvSheetTitle.setText(voucher.getTitle());
        tvSheetCode .setText(voucher.getCode());
        tvSheetExpiry.setText(getString(R.string.voucher_detail_expiry_value,
                voucher.getExpiryDate()));

        // Build conditions list
        llConditions.removeAllViews();
        for (String condition : getSampleConditions(voucher)) {
            TextView tv = new TextView(this);
            tv.setText("• " + condition);
            tv.setTextSize(14f);
            tv.setTextColor(getColor(R.color.text_primary));
            int dp6 = (int) (6 * getResources().getDisplayMetrics().density);
            tv.setPadding(0, dp6, 0, 0);
            llConditions.addView(tv);
        }

        // Show overlay + animate sheet up
        sheetOverlay.setVisibility(View.VISIBLE);
        sheetOverlay.setAlpha(0f);
        sheetOverlay.animate().alpha(1f).setDuration(250).start();

        voucherDetailSheet.post(() -> {
            float startY = voucherDetailSheet.getHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    voucherDetailSheet, "translationY", startY, 0f);
            anim.setDuration(350);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();
        });
    }

    public void hideSheet() {
        float endY = voucherDetailSheet.getHeight();
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                voucherDetailSheet, "translationY", 0f, endY);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                sheetOverlay.setVisibility(View.GONE);
            }
        });
        sheetOverlay.animate().alpha(0f).setDuration(250).start();
        anim.start();
    }

    @Override
    public void onBackPressed() {
        if (sheetOverlay.getVisibility() == View.VISIBLE) {
            hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // VoucherListener callbacks
    // ─────────────────────────────────────────────────────────────────

    @Override
    public void onUseVoucher(ProfileVoucher voucher) {
        // Navigate back with selected voucher code so the caller (Cart/Checkout) can apply it
        Intent result = new Intent();
        result.putExtra("voucher_code", voucher.getCode());
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onSeeDetail(ProfileVoucher voucher) {
        showSheet(voucher);
    }

    // ─────────────────────────────────────────────────────────────────
    // Sample data helpers
    // ─────────────────────────────────────────────────────────────────

    private List<String> getSampleConditions(ProfileVoucher v) {
        return Arrays.asList(
                "Giảm trực tiếp vào tổng giá trị đơn hàng khóa học.",
                "Mức giảm tối đa không vượt quá giới hạn của từng chương trình.",
                "Mỗi tài khoản chỉ được sử dụng voucher 1 lần duy nhất.",
                "Không áp dụng đồng thời cùng các mã giảm giá khác.",
                "Voucher không có giá trị quy đổi thành tiền mặt dưới mọi hình thức."
        );
    }

    private List<ProfileVoucher> buildSampleVouchers() {
        return Arrays.asList(
                new ProfileVoucher(
                        "v001",
                        "Ưu đãi thành viên mới",
                        "Áp dụng cho mọi khóa học trên hệ thống. Giảm 20% tổng đơn.",
                        "VOUCHER20",
                        ProfileVoucher.DiscountType.PERCENT,
                        20,
                        "30/06/2026",
                        ProfileVoucher.Status.AVAILABLE,
                        1,
                        R.drawable.bg_voucher_left_purple
                ),
                new ProfileVoucher(
                        "v002",
                        "Giảm trực tiếp 100K",
                        "Áp dụng cho danh mục Design & Code. Đơn tối thiểu 500K.",
                        "SAVE100",
                        ProfileVoucher.DiscountType.FIXED,
                        100,
                        "15/07/2026",
                        ProfileVoucher.Status.AVAILABLE,
                        0,
                        R.drawable.bg_voucher_left_teal
                ),
                new ProfileVoucher(
                        "v003",
                        "Flash Sale 30%",
                        "Voucher chương trình Flash Sale tháng 3/2026.",
                        "FLASH30",
                        ProfileVoucher.DiscountType.PERCENT,
                        30,
                        "31/03/2026",
                        ProfileVoucher.Status.EXPIRED,
                        0,
                        R.drawable.bg_voucher_left_gray
                )
        );
    }
}
