package com.app.cinx.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.app.cinx.R;
import com.app.cinx.api.AuthService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.SendOtpRequest;
import com.app.cinx.api.dto.VerifyEmailRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class VerifyOtpActivity extends AppCompatActivity {
    private String userEmail = "";
    private EditText inputEmail;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private AppCompatButton btnSendOtp, btnVerifyOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        userEmail = getIntent().getStringExtra("EMAIL");
        if (userEmail == null) userEmail = "";
        bindViews();
        setupActions();
    }
    private void bindViews() {
        inputEmail = findViewById(R.id.input_email);
        inputEmail.setText(userEmail);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);
        otp5 = findViewById(R.id.otp_5);
        otp6 = findViewById(R.id.otp_6);
        ImageView btnBack = findViewById(R.id.btn_back);
        if(btnBack != null) btnBack.setOnClickListener(v -> finish());
    }
    private void setupActions() {
        btnSendOtp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            if (email.isEmpty()) { inputEmail.setError("Trống"); return; }
            btnSendOtp.setEnabled(false);
            btnSendOtp.setText("Đang gửi...");
            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) return;
            SendOtpRequest req = new SendOtpRequest();
            req.setEmail(email);
            authService.resendOtp(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnSendOtp.setEnabled(true);
                    btnSendOtp.setText("Gửi lại mã OTP");
                    Toast.makeText(VerifyOtpActivity.this, "Đã gửi mã OTP!", Toast.LENGTH_SHORT).show();
                    otp1.requestFocus();
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnSendOtp.setEnabled(true);
                    btnSendOtp.setText("Gửi lại mã OTP");
                    Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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
                    if (s.length() == 1 && index < otpFields.length - 1) otpFields[index + 1].requestFocus();
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
        btnVerifyOtp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String code = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
            if (email.isEmpty()) return;
            if (code.length() < 6) return;
            btnVerifyOtp.setEnabled(false);
            btnVerifyOtp.setText("Đang xác nhận...");
            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) return;
            VerifyEmailRequest req = new VerifyEmailRequest();
            req.setEmail(email);
            req.setOtp(code);
            authService.verifyOtp(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnVerifyOtp.setEnabled(true);
                    btnVerifyOtp.setText("Xác nhận");
                    if (response.isSuccessful()) {
                        Toast.makeText(VerifyOtpActivity.this, "Thao tác thành công! Bạn có thể đăng nhập.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyOtpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnVerifyOtp.setEnabled(true);
                    btnVerifyOtp.setText("Xác nhận");
                    Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}