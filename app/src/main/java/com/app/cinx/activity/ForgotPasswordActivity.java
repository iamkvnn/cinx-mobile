package com.app.cinx.activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.app.cinx.R;
import com.app.cinx.api.AuthService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.ResetPasswordRequest;
import com.app.cinx.api.dto.SendOtpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ForgotPasswordActivity extends AppCompatActivity {
    private LinearLayout viewForm, viewSuccess;
    private EditText inputEmail;
    private AppCompatButton btnSendOtp;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private EditText inputNewPassword, inputConfirmPassword;
    private ImageView iconEyeNew;
    private AppCompatButton btnUpdatePassword;
    private boolean isNewPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bindViews();
        setupActions();
    }
    private void bindViews() {
        viewForm = findViewById(R.id.view_form);
        viewSuccess = findViewById(R.id.view_success);
        inputEmail = findViewById(R.id.input_email);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);
        otp5 = findViewById(R.id.otp_5);
        otp6 = findViewById(R.id.otp_6);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        iconEyeNew = findViewById(R.id.icon_eye_new);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        ImageView btnBack = findViewById(R.id.btn_back);
        if(btnBack != null) btnBack.setOnClickListener(v -> finish());
        AppCompatButton btnGoLogin = findViewById(R.id.btn_go_login);
        if(btnGoLogin != null) {
            btnGoLogin.setOnClickListener(v -> {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
    private void setupActions() {
        btnSendOtp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            if (email.isEmpty()) { inputEmail.setError("Vui lòng nhập email"); shakeView(inputEmail); return; }
            btnSendOtp.setEnabled(false);
            btnSendOtp.setText("Đang gửi...");
            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) return;
            SendOtpRequest req = new SendOtpRequest();
            req.setEmail(email);
            authService.sendChangePasswordOtp(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnSendOtp.setEnabled(true);
                    btnSendOtp.setText("Gửi lại mã OTP");
                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đã gửi mã OTP", Toast.LENGTH_SHORT).show();
                        otp1.requestFocus();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Không thể gửi OTP", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnSendOtp.setEnabled(true);
                    btnSendOtp.setText("Gửi mã OTP");
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
        EditText[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};
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
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.getAction() == android.view.KeyEvent.ACTION_DOWN && otpFields[index].getText().toString().isEmpty() && index > 0) {
                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].setText("");
                    return true;
                }
                return false;
            });
        }
        iconEyeNew.setOnClickListener(v -> {
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
        });
        btnUpdatePassword.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String code = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
            String password = inputNewPassword.getText().toString();
            String confirm  = inputConfirmPassword.getText().toString();
            if (email.isEmpty()) { inputEmail.setError("Email rỗng"); return; }
            if (code.length() < 6) { shakeView(findViewById(R.id.otp_container)); return; }
            if (password.isEmpty()) { inputNewPassword.setError("Mật khẩu rỗng"); return; }
            if (!password.equals(confirm)) { inputConfirmPassword.setError("Không khớp"); return; }
            btnUpdatePassword.setEnabled(false);
            btnUpdatePassword.setText("Đang cập nhật...");
            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) return;
            ResetPasswordRequest req = new ResetPasswordRequest();
            req.setEmail(email);
            req.setOtp(code);
            req.setNewPassword(password);
            authService.resetPassword(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnUpdatePassword.setEnabled(true);
                    btnUpdatePassword.setText("Cập nhật mật khẩu");
                    if (response.isSuccessful()) {
                        viewForm.setVisibility(View.GONE);
                        viewSuccess.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnUpdatePassword.setEnabled(true);
                    btnUpdatePassword.setText("Cập nhật mật khẩu");
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void shakeView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setInterpolator(new android.view.animation.CycleInterpolator(5));
        shake.setDuration(300);
        view.startAnimation(shake);
    }
}