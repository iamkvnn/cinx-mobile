package com.app.cinx.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.app.cinx.R;
import com.app.cinx.util.NavHelper;
import com.app.cinx.util.ToastUtil;
import com.app.cinx.util.UserManager;
import com.bumptech.glide.Glide;

/**
 * ProfileActivity
 *
 * Displays the user's profile:
 *  - Personal info card (avatar, name, email, membership badge, quick stats)
 *  - Grouped settings menus (iOS-style)
 *  - Floating bottom navigation bar
 */
public class ProfileActivity extends AppCompatActivity {

    // ── Profile Card ──────────────────────────────────────────────────
    private ImageView ivAvatar;
    private View btnEditProfile;
    private TextView  tvProfileName;
    private TextView  tvProfileEmail;
    private TextView  tvMembershipLabel;
    private TextView  tvStreak;
    private TextView  tvXp;
    private TextView  tvHours;

    // ── Menu Rows ─────────────────────────────────────────────────────
    private LinearLayout rowCertificates;
    private LinearLayout rowDownloads;
    private LinearLayout rowOrderHistory;
    private LinearLayout rowVouchers;
    private LinearLayout rowPaymentMethods;
    private LinearLayout rowHelpCenter;
    private LinearLayout rowLogout;

    // ── Toggles ───────────────────────────────────────────────────────
    private SwitchCompat switchNotifications;
    private SwitchCompat switchDarkMode;

    // ── Demo stat data ─────────────────────────────────────────────────
    private static final int STREAK_DAYS  = 14;
    private static final int XP_POINTS    = 2_450;
    private static final int LEARN_HOURS  = 38;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bindViews();
        populateUserData();
        setupMenuListeners();
        setupEditProfileListener();
        setupToggles();

        NavHelper.setupNavigation(this, R.id.navProfile);
    }

    // ─────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        ivAvatar          = findViewById(R.id.ivAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvProfileName     = findViewById(R.id.tvProfileName);
        tvProfileEmail    = findViewById(R.id.tvProfileEmail);
        tvMembershipLabel = findViewById(R.id.tvMembershipLabel);
        tvStreak          = findViewById(R.id.tvStreak);
        tvXp              = findViewById(R.id.tvXp);
        tvHours           = findViewById(R.id.tvHours);

        rowCertificates  = findViewById(R.id.rowCertificates);
        rowDownloads     = findViewById(R.id.rowDownloads);
        rowOrderHistory  = findViewById(R.id.rowOrderHistory);
        rowVouchers      = findViewById(R.id.rowVouchers);
        rowPaymentMethods = findViewById(R.id.rowPaymentMethods);
        rowHelpCenter    = findViewById(R.id.rowHelpCenter);
        rowLogout        = findViewById(R.id.rowLogout);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode      = findViewById(R.id.switchDarkMode);
    }

    // ─────────────────────────────────────────────────────────────────
    // Populate with real / demo data
    // ─────────────────────────────────────────────────────────────────

    private void populateUserData() {
        UserManager user = UserManager.getInstance();

        // Avatar
        Glide.with(this)
                .load("https://i.pravatar.cc/150?u=my_user")
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(ivAvatar);

        // Name & email – fall back to demo values when not available
        String email = user.getUserEmail();
        if (email != null && !email.isEmpty()) {
            tvProfileEmail.setText(email);
            // Derive a display name from the email prefix
            String namePart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            tvProfileName.setText(namePart);
        }

        // Membership badge
        boolean isPro = true; // extend UserManager to hold tier when needed
        tvMembershipLabel.setText(isPro ? R.string.profile_badge_pro : R.string.profile_badge_basic);

        // Quick stats
        tvStreak.setText(String.valueOf(STREAK_DAYS));
        tvXp.setText(String.valueOf(XP_POINTS));
        tvHours.setText(String.valueOf(LEARN_HOURS));
    }

    // ─────────────────────────────────────────────────────────────────
    // Menu click listeners
    // ─────────────────────────────────────────────────────────────────

        private void setupEditProfileListener() {
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        }
    }

    private void showEditProfileDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_profile_edit_bottom_sheet, null);
        dialog.setContentView(view);
        
        EditText inputName = view.findViewById(R.id.inputName);
        EditText inputEmail = view.findViewById(R.id.inputEmail);
        View btnSave = view.findViewById(R.id.btnSaveProfile);
        View btnClose = view.findViewById(R.id.btnClose);
        
        inputName.setText(tvProfileName.getText());
        inputEmail.setText(tvProfileEmail.getText());
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newName = inputName.getText().toString();
            String newEmail = inputEmail.getText().toString();
            if (!newName.isEmpty()) tvProfileName.setText(newName);
            if (!newEmail.isEmpty()) tvProfileEmail.setText(newEmail);
            UserManager.getInstance().setUserEmail(newEmail);
            ToastUtil.showCustomToast(this, "Đã cập nhật hồ sơ");
            dialog.dismiss();
        });
        
        dialog.show();
    }

    private void setupMenuListeners() {
        // Learning group
        rowCertificates.setOnClickListener(v -> {
            Intent intent = new Intent(this, CertificatesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowDownloads.setOnClickListener(v ->
                ToastUtil.showCustomToast(this, getString(R.string.profile_coming_soon)));

        // Transactions group
        rowOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, PurchaseHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowVouchers.setOnClickListener(v -> {
            Intent intent = new Intent(this, VouchersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowPaymentMethods.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentMethodsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        // Support group
        rowHelpCenter.setOnClickListener(v ->
                ToastUtil.showCustomToast(this, getString(R.string.profile_coming_soon)));

        rowLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    // ─────────────────────────────────────────────────────────────────
    // Toggle listeners
    // ─────────────────────────────────────────────────────────────────

    private void setupToggles() {
        switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
            String msg = isChecked
                    ? getString(R.string.profile_notifications_on)
                    : getString(R.string.profile_notifications_off);
            ToastUtil.showCustomToast(this, msg);
        });

        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            String msg = isChecked
                    ? getString(R.string.profile_dark_mode_on)
                    : getString(R.string.profile_dark_mode_off);
            ToastUtil.showCustomToast(this, msg);
            // TODO: apply AppCompatDelegate.setDefaultNightMode() when dark theme assets are ready
        });
    }

    // ─────────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────────

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_logout)
                .setMessage(R.string.profile_logout_confirm)
                .setPositiveButton(R.string.profile_logout_yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.profile_logout_cancel, null)
                .show();
    }

    private void performLogout() {
        UserManager.getInstance().logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
