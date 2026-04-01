package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.app.cinx.R;
import com.app.cinx.utils.TokenManager;
import com.app.cinx.utils.UserManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout cardManageUsers;
    private LinearLayout cardManageInstructors;
    private LinearLayout btnLogout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        bindViews();
        setupActions();
    }

    private void bindViews() {
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardManageInstructors = findViewById(R.id.cardManageInstructors);
        btnLogout = findViewById(R.id.btnAdminLogout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });

        cardManageInstructors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageInstructorsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            UserManager.getInstance().logout();
            TokenManager.getInstance().clear();

            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
