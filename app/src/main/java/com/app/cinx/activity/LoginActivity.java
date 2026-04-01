package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import com.app.cinx.api.dto.DeviceTokenRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.app.cinx.R;
import com.app.cinx.api.AuthService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.AuthRequestDto;
import com.app.cinx.api.dto.TokenResponseDto;
import com.app.cinx.api.dto.UserDto;
import com.app.cinx.utils.TokenManager;
import com.app.cinx.utils.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private ImageView iconEye;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        iconEye = findViewById(R.id.icon_eye);
        AppCompatButton btnLogin = findViewById(R.id.btn_login);
        TextView btnSwitchRegister = findViewById(R.id.btn_switch_register);
        TextView btnForgotPassword = findViewById(R.id.btn_forgot_password);
        ImageView btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Forgot Password
        if (btnForgotPassword != null) {
            btnForgotPassword.setOnClickListener(v -> {
                Intent forgotIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotIntent);
            });
        }

        // Toggle Password Visibility
        iconEye.setOnClickListener(v -> togglePasswordVisibility());

        // Switch to Register
        btnSwitchRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0); // No animation
            finish();
        });

        // Login Action
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            iconEye.setImageResource(R.drawable.ic_eye);
            iconEye.setColorFilter(getColor(R.color.text_secondary));
        } else {
            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            iconEye.setImageResource(R.drawable.ic_eye_off);
            iconEye.setColorFilter(getColor(R.color.primary));
        }
        inputPassword.setSelection(inputPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void handleLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        boolean isValid = true;

        if (email.isEmpty()) {
            inputEmail.setError("Cần nhập email");
            shakeView(inputEmail);
            isValid = false;
        }
        if (password.isEmpty()) {
            inputPassword.setError("Cần nhập mật khẩu");
            shakeView(inputPassword);
            isValid = false;
        }

        if (isValid) {
            AppCompatButton btnLogin = findViewById(R.id.btn_login);
            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");

            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
                return;
            }

            AuthRequestDto req = new AuthRequestDto();
            req.setEmail(email);
            req.setPassword(password);

            authService.login(req).enqueue(new Callback<ApiResponse<TokenResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<TokenResponseDto>> call, Response<ApiResponse<TokenResponseDto>> response) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        TokenResponseDto tokens = response.body().getData();
                        // Sync FCM
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                            if (!task.isSuccessful() || task.getResult() == null) return;
                            DeviceTokenRequest freq = new DeviceTokenRequest();
                            freq.setFcmToken(task.getResult());
                            RetrofitClient.getInstance().getUserService().saveDeviceToken(freq).enqueue(new retrofit2.Callback<com.app.cinx.api.dto.ApiResponse<Void>>() {
                                @Override
                                public void onResponse(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<Void>> call, retrofit2.Response<com.app.cinx.api.dto.ApiResponse<Void>> res) {}
                                @Override
                                public void onFailure(retrofit2.Call<com.app.cinx.api.dto.ApiResponse<Void>> call, Throwable th) {}
                            });
                        });

                        TokenManager.getInstance().saveTokens(
                                tokens.getAccessToken(),
                                tokens.getRefreshToken()
                        );
                        
                        // Fetch current user profile before going to MainActivity
                        fetchUserProfileAndNavigate(email);

                    } else {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Đăng nhập");

                        try {
                            if (response.errorBody() != null) {
                                String errorBodyStr = response.errorBody().string();
                                if (errorBodyStr.contains("User email is not verified")) {
                                    AuthService as = RetrofitClient.getInstance().getAuthService();
                                    if(as != null) {
                                        com.app.cinx.api.dto.SendOtpRequest sreq = new com.app.cinx.api.dto.SendOtpRequest();
                                        sreq.setEmail(email);
                                        as.resendOtp(sreq).enqueue(new Callback<ApiResponse<Object>>() {
                                            @Override
                                            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> res) {}
                                            @Override
                                            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {}
                                        });
                                    }
                                    Intent intent = new Intent(LoginActivity.this, VerifyOtpActivity.class);
                                    intent.putExtra("EMAIL", email);
                                    startActivity(intent);
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<TokenResponseDto>> call, Throwable t) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    Log.e("LoginActivity", "Login error", t);
                    Toast.makeText(LoginActivity.this, "Lỗi kết ối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void shakeView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setInterpolator(new android.view.animation.CycleInterpolator(5));
        shake.setDuration(300);
        view.startAnimation(shake);
    }

    private void fetchUserProfileAndNavigate(String fallbackEmail) {
        com.app.cinx.api.UserService userService = RetrofitClient.getInstance().getUserService();
        if (userService != null) {
            userService.getCurrentUser().enqueue(new Callback<ApiResponse<UserDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        UserDto userDto = response.body().getData();
                        UserManager.getInstance().saveUserInfo(
                                userDto.getUserId(),
                                userDto.getEmail(),
                                userDto.getName(),
                                userDto.getAvatarUrl(),
                                userDto.getRole()
                        );
                        UserManager.getInstance().setUserXp(userDto.getXp() != null ? userDto.getXp() : 0);
                    } else {
                        UserManager.getInstance().login(fallbackEmail);
                    }
                    navigateToMain();
                }

                @Override
                public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                    UserManager.getInstance().login(fallbackEmail);
                    navigateToMain();
                }
            });
        } else {
            UserManager.getInstance().login(fallbackEmail);
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        
        Intent intent;
        String role = UserManager.getInstance().getUserRole();
        if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, InstructorDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}



