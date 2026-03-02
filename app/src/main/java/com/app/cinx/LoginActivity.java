package com.app.cinx;

import android.content.Intent;
import android.os.Bundle;
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
        ImageView btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
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
            // Mock Login Success
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            
            // Save user to memory
            UserManager.getInstance().login(email); // Mock name

            // Navigate to Main Activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void shakeView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setInterpolator(new android.view.animation.CycleInterpolator(5));
        shake.setDuration(300);
        view.startAnimation(shake);
    }
}
