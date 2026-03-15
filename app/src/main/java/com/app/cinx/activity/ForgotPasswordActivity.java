package com.app.cinx.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.app.cinx.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Views (steps)
    private LinearLayout viewForgot, viewOtp, viewReset, viewSuccess;

    // Step 1
    private EditText inputEmail;

    // Step 2
    private EditText otp1, otp2, otp3, otp4;
    private TextView tvOtpEmail, btnResendOtp;
    private CountDownTimer resendTimer;
    private int resendSeconds = 30;

    // Step 3
    private EditText inputNewPassword, inputConfirmPassword;
    private ImageView iconEyeNew;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private boolean isNewPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        setupBackButton();
        setupStep1();
        setupStep2();
        setupStep3();
        setupStep4();
    }

    private void bindViews() {
        viewForgot = findViewById(R.id.view_forgot);
        viewOtp    = findViewById(R.id.view_otp);
        viewReset  = findViewById(R.id.view_reset);
        viewSuccess = findViewById(R.id.view_success);

        inputEmail = findViewById(R.id.input_email);

        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);
        tvOtpEmail  = findViewById(R.id.tv_otp_email);
        btnResendOtp = findViewById(R.id.btn_resend_otp);

        inputNewPassword     = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        iconEyeNew   = findViewById(R.id.icon_eye_new);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthBar4 = findViewById(R.id.strength_bar_4);
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (viewOtp.getVisibility() == View.VISIBLE) {
                showView(viewForgot);
            } else if (viewReset.getVisibility() == View.VISIBLE) {
                showView(viewOtp);
            } else {
                finish();
            }
        });
    }

    // ─── STEP 1: EMAIL INPUT ────────────────────────────────────────────────
    private void setupStep1() {
        AppCompatButton btnSendOtp = findViewById(R.id.btn_send_otp);
        btnSendOtp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            if (email.isEmpty()) {
                inputEmail.setError("Vui lòng nhập email");
                shakeView(inputEmail);
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.setError("Email không hợp lệ");
                shakeView(inputEmail);
                return;
            }
            // Display masked email and move to OTP view
            tvOtpEmail.setText(maskEmail(email));
            showView(viewOtp);
            startResendTimer();
            otp1.requestFocus();
        });
    }

    // ─── STEP 2: OTP VERIFICATION ───────────────────────────────────────────
    private void setupStep2() {
        EditText[] otpFields = {otp1, otp2, otp3, otp4};
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }
            });
            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                        && event.getAction() == android.view.KeyEvent.ACTION_DOWN
                        && otpFields[index].getText().toString().isEmpty()
                        && index > 0) {
                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].setText("");
                    return true;
                }
                return false;
            });
        }

        AppCompatButton btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        btnVerifyOtp.setOnClickListener(v -> {
            String code = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();
            if (code.length() < 4) {
                shakeView(findViewById(R.id.otp_container));
                return;
            }
            // Mock: any 4-digit code is accepted
            cancelResendTimer();
            showView(viewReset);
            inputNewPassword.requestFocus();
        });

        btnResendOtp.setOnClickListener(v -> {
            if (btnResendOtp.isEnabled()) {
                // Clear OTP fields
                for (EditText f : otpFields) f.setText("");
                otp1.requestFocus();
                startResendTimer();
            }
        });
    }

    // ─── STEP 3: RESET PASSWORD ─────────────────────────────────────────────
    private void setupStep3() {
        iconEyeNew.setOnClickListener(v -> togglePasswordVisibility());

        inputNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStrengthBars(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        AppCompatButton btnUpdate = findViewById(R.id.btn_update_password);
        btnUpdate.setOnClickListener(v -> {
            String password = inputNewPassword.getText().toString();
            String confirm  = inputConfirmPassword.getText().toString();

            if (password.isEmpty()) {
                inputNewPassword.setError("Vui lòng nhập mật khẩu");
                shakeView(inputNewPassword);
                return;
            }
            if (password.length() < 6) {
                inputNewPassword.setError("Mật khẩu tối thiểu 6 ký tự");
                shakeView(inputNewPassword);
                return;
            }
            if (!password.equals(confirm)) {
                inputConfirmPassword.setError("Mật khẩu không khớp");
                shakeView(inputConfirmPassword);
                return;
            }
            // Simulate network delay, then show success
            btnUpdate.setEnabled(false);
            btnUpdate.setText("Đang cập nhật...");
            new Handler(Looper.getMainLooper()).postDelayed(() -> showView(viewSuccess), 1200);
        });
    }

    // ─── STEP 4: SUCCESS ────────────────────────────────────────────────────
    private void setupStep4() {
        AppCompatButton btnGoLogin = findViewById(R.id.btn_go_login);
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────

    private void showView(LinearLayout target) {
        viewForgot.setVisibility(View.GONE);
        viewOtp.setVisibility(View.GONE);
        viewReset.setVisibility(View.GONE);
        viewSuccess.setVisibility(View.GONE);
        target.setVisibility(View.VISIBLE);
    }

    private void togglePasswordVisibility() {
        if (isNewPasswordVisible) {
            inputNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            iconEyeNew.setImageResource(R.drawable.ic_eye);
            iconEyeNew.setColorFilter(getColor(R.color.text_secondary));
        } else {
            inputNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            iconEyeNew.setImageResource(R.drawable.ic_eye_off);
            iconEyeNew.setColorFilter(getColor(R.color.primary));
        }
        inputNewPassword.setSelection(inputNewPassword.getText().length());
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    private void updateStrengthBars(String password) {
        int strength = 0;
        if (password.length() >= 6)                               strength++;
        if (password.length() >= 10)                              strength++;
        if (password.matches(".*[A-Z].*") || password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[^a-zA-Z0-9].*"))                strength++;

        int[] colors = {0xFFEF4444, 0xFFF97316, 0xFF22C55E, 0xFF10B981};
        int activeColor = strength > 0 ? colors[Math.min(strength - 1, 3)] : 0xFFE2E8F0;
        int inactiveColor = 0xFFE2E8F0;

        strengthBar1.setBackgroundColor(strength >= 1 ? activeColor : inactiveColor);
        strengthBar2.setBackgroundColor(strength >= 2 ? activeColor : inactiveColor);
        strengthBar3.setBackgroundColor(strength >= 3 ? activeColor : inactiveColor);
        strengthBar4.setBackgroundColor(strength >= 4 ? activeColor : inactiveColor);
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return email;
        return email.substring(0, 3) + "***" + email.substring(at);
    }

    private void startResendTimer() {
        btnResendOtp.setEnabled(false);
        btnResendOtp.setTextColor(getColor(R.color.text_placeholder));
        resendSeconds = 30;
        if (resendTimer != null) resendTimer.cancel();
        resendTimer = new CountDownTimer(30_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendSeconds = (int) (millisUntilFinished / 1000);
                btnResendOtp.setText("Gửi lại (" + resendSeconds + "s)");
            }
            @Override
            public void onFinish() {
                btnResendOtp.setText("Gửi lại");
                btnResendOtp.setEnabled(true);
                btnResendOtp.setTextColor(getColor(R.color.primary));
            }
        }.start();
    }

    private void cancelResendTimer() {
        if (resendTimer != null) {
            resendTimer.cancel();
            resendTimer = null;
        }
    }

    private void shakeView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setInterpolator(new android.view.animation.CycleInterpolator(5));
        shake.setDuration(300);
        view.startAnimation(shake);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelResendTimer();
    }
}
