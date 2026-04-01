package com.app.cinx.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.app.cinx.R;
import com.app.cinx.utils.ToastUtil;

public class PaymentSuccessActivity extends AppCompatActivity {

    // ── Intent extras ─────────────────────────────────────────────────────
    public static final String EXTRA_ORDER_CODE = "extra_order_code";
    public static final String EXTRA_EMAIL      = "extra_email";
    public static final String EXTRA_AMOUNT     = "extra_amount";

    // ── Animation delay: start confetti slightly after check starts ───────
    private static final long CONFETTI_DELAY_MS = 200L;

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        String orderCode = getIntent().getStringExtra(EXTRA_ORDER_CODE);
        String email     = getIntent().getStringExtra(EXTRA_EMAIL);

        if (orderCode == null) orderCode = "EDUF-DEMO";
        if (email     == null) email     = "user@example.com";

        bindViews(orderCode, email);
        startAnimations();
    }

    /** Pressing Back from success → go straight to MainActivity. */
    @Override
    public void onBackPressed() {
        navigateHome();
    }

    // ─────────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────────

    private void bindViews(String orderCode, String email) {
        // Description text
        TextView tvDesc = findViewById(R.id.tvSuccessDesc);
        tvDesc.setText(getString(R.string.success_desc, email));

        // Order code
        TextView tvCode = findViewById(R.id.tvOrderCode);
        tvCode.setText(orderCode);

        // Copy button
        String finalOrderCode = orderCode;
        TextView btnCopy = findViewById(R.id.btnCopyCode);
        btnCopy.setOnClickListener(v -> copyToClipboard(finalOrderCode));

        // Go-learn button → MyLearningActivity
        AppCompatButton btnGoLearn = findViewById(R.id.btnGoLearn);
        btnGoLearn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyLearningActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Go-home button → MainActivity
        AppCompatButton btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(v -> navigateHome());
    }

    // ─────────────────────────────────────────────────────────────────────
    // Lottie animations
    // ─────────────────────────────────────────────────────────────────────

    private void startAnimations() {
        LottieAnimationView lottieCheck    = findViewById(R.id.lottieCheck);
        LottieAnimationView lottieConfetti = findViewById(R.id.lottieConfetti);

        // Play the success check immediately
        lottieCheck.playAnimation();

        // Fire confetti after a short delay so it feels like a "burst" reaction
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                lottieConfetti.playAnimation(), CONFETTI_DELAY_MS);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────

    private void copyToClipboard(String text) {
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("order_code", text));
        }
        ToastUtil.showCustomToast(this, getString(R.string.success_copied));
    }

    private void navigateHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
