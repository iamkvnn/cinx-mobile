package com.app.cinx.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.NotificationAdapter;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.PaginatedApiResponseUserNotificationResponse;
import com.app.cinx.api.dto.UserNotificationResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<UserNotificationResponse> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NotificationAdapter((item, position) -> {
            if (!Boolean.TRUE.equals(item.getIsRead())) {
                markAsRead(item, position);
            }
        });
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        RetrofitClient.getInstance().getNotificationService().getNotifications(1, 50).enqueue(new Callback<PaginatedApiResponseUserNotificationResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseUserNotificationResponse> call, Response<PaginatedApiResponseUserNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    notificationList = response.body().getData();
                    adapter.submitList(notificationList);
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseUserNotificationResponse> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(UserNotificationResponse item, int position) {
        RetrofitClient.getInstance().getNotificationService().toggleRead(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    item.setIsRead(true);
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Notification", "Failed to mark as read", t);
            }
        });
    }
}
