package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.app.cinx.R;
import com.app.cinx.api.AuthService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword, inputRepassword;
    private ImageView iconEye, iconEyeRe;
    private boolean isPasswordVisible = false;
    private boolean isRepasswordVisible = false;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputRepassword = findViewById(R.id.input_repassword);
        rgRole = findViewById(R.id.rgRole);
        iconEye = findViewById(R.id.icon_eye);
        iconEyeRe = findViewById(R.id.icon_eye_re);
        AppCompatButton btnRegister = findViewById(R.id.btn_register);
        TextView btnSwitchLogin = findViewById(R.id.btn_switch_login);
        ImageView btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Visibility Toggles
        iconEye.setOnClickListener(v -> togglePasswordVisibility(inputPassword, iconEye));
        iconEyeRe.setOnClickListener(v -> toggleRepasswordVisibility(inputRepassword, iconEyeRe));

        // Switch to Login
        btnSwitchLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void togglePasswordVisibility(EditText input, ImageView icon) {
        if (isPasswordVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye);
            icon.setColorFilter(getColor(R.color.text_secondary));
        } else {
            input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
            icon.setColorFilter(getColor(R.color.primary));
        }
        input.setSelection(input.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void toggleRepasswordVisibility(EditText input, ImageView icon) {
        if (isRepasswordVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye);
            icon.setColorFilter(getColor(R.color.text_secondary));
        } else {
            input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
            icon.setColorFilter(getColor(R.color.primary));
        }
        input.setSelection(input.getText().length());
        isRepasswordVisible = !isRepasswordVisible;
    }

    private void handleRegister() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String pass = inputPassword.getText().toString();
        String repass = inputRepassword.getText().toString();
        boolean isValid = true;

        if (name.isEmpty()) { shakeView(inputName); isValid = false; }
        if (email.isEmpty()) { shakeView(inputEmail); isValid = false; }
        if (pass.isEmpty()) { shakeView(inputPassword); isValid = false; }
        if (repass.isEmpty()) { shakeView(inputRepassword); isValid = false; }
        if (!pass.equals(repass)) {
            inputRepassword.setError("Máº­t kháº©u khĂ´ng khá»›p");
            shakeView(inputRepassword);
            isValid = false;
        }

        if (isValid) {
            AppCompatButton btnRegister = findViewById(R.id.btn_register);
            btnRegister.setEnabled(false);
            btnRegister.setText("Äang Ä‘Äƒng kĂ½...");

            AuthService authService = RetrofitClient.getInstance().getAuthService();
            if (authService == null) {
                btnRegister.setEnabled(true);
                btnRegister.setText("ÄÄƒng kĂ½");
                return;
            }

            RegisterRequest req = new RegisterRequest();
            req.setName(name);
            req.setEmail(email);
            req.setPassword(pass);
                        // Get selected role
            int selectedId = rgRole.getCheckedRadioButtonId();
            if (selectedId == R.id.rbInstructor) {
                req.setRole("INSTRUCTOR");
            } else {
                req.setRole("USER");
            }

            authService.register(req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("ÄÄƒng kĂ½");
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "ÄÄƒng kĂ½ thĂ nh cĂ´ng!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "ÄÄƒng kĂ½ tháº¥t báº¡i", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("ÄÄƒng kĂ½");
                    Log.e("RegisterActivity", "Register error", t);
                    Toast.makeText(RegisterActivity.this, "Lá»—i káº¿t ná»‘i", Toast.LENGTH_SHORT).show();
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
}

