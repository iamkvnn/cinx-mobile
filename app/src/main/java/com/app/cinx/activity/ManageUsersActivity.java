package com.app.cinx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.AdminUserAdapter;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.UserService;
import com.app.cinx.api.dto.PaginatedApiResponseUserDto;
import com.app.cinx.api.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private AdminUserAdapter adapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        userService = RetrofitClient.getInstance().getUserService();

        setupViews();
        setupRecyclerView();
        fetchUsers();
    }

    private void setupViews() {
        rvUsers = findViewById(R.id.rvUsers);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter(this, new ArrayList<>());
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void fetchUsers() {
        showLoading(true);

        userService.getAllUsers(1, 200).enqueue(new Callback<PaginatedApiResponseUserDto>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseUserDto> call, Response<PaginatedApiResponseUserDto> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<UserDto> allUsers = response.body().getData();
                    List<UserDto> regularUsers = new ArrayList<>();

                    for (UserDto user : allUsers) {
                        String role = user.getRole() == null ? "" : user.getRole().toUpperCase();
                        if (!"ADMIN".equals(role) && !"INSTRUCTOR".equals(role)) {
                            regularUsers.add(user);
                        }
                    }

                    adapter.updateData(regularUsers);
                    tvEmpty.setVisibility(regularUsers.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText(getString(R.string.admin_fetch_error));
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseUserDto> call, Throwable t) {
                showLoading(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(getString(R.string.admin_network_error));
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}
