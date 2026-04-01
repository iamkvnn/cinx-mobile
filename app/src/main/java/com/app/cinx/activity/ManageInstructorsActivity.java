package com.app.cinx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.AdminInstructorAdapter;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.UserService;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.PaginatedApiResponseUserDto;
import com.app.cinx.api.dto.UserDto;
import com.app.cinx.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageInstructorsActivity extends AppCompatActivity implements AdminInstructorAdapter.OnVerifyClickListener {

    private static final int TAB_PENDING = 0;
    private static final int TAB_VERIFIED = 1;

    private RecyclerView rvInstructors;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tabPending;
    private TextView tabVerified;
    private AdminInstructorAdapter adapter;
    private UserService userService;
    private int selectedTab = TAB_PENDING;
    private final List<UserDto> pendingInstructors = new ArrayList<>();
    private final List<UserDto> verifiedInstructors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_instructors);

        userService = RetrofitClient.getInstance().getUserService();

        setupViews();
        setupRecyclerView();
        fetchInstructors();
    }

    private void setupViews() {
        rvInstructors = findViewById(R.id.rvInstructors);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        tabPending = findViewById(R.id.tabPending);
        tabVerified = findViewById(R.id.tabVerified);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tabPending.setOnClickListener(v -> selectTab(TAB_PENDING));
        tabVerified.setOnClickListener(v -> selectTab(TAB_VERIFIED));
        updateTabUi();
    }

    private void setupRecyclerView() {
        adapter = new AdminInstructorAdapter(this, new ArrayList<>(), this);
        rvInstructors.setLayoutManager(new LinearLayoutManager(this));
        rvInstructors.setAdapter(adapter);
    }

    private void fetchInstructors() {
        showLoading(true);
        pendingInstructors.clear();
        verifiedInstructors.clear();

        userService.getAllUsers(1, 200).enqueue(new Callback<PaginatedApiResponseUserDto>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseUserDto> call, Response<PaginatedApiResponseUserDto> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<UserDto> allUsers = response.body().getData();
                    List<UserDto> instructors = new ArrayList<>();

                    for (UserDto user : allUsers) {
                        String role = user.getRole() == null ? "" : user.getRole().toUpperCase();
                        if ("INSTRUCTOR".equals(role) && user.getUserId() != null) {
                            instructors.add(user);
                        }
                    }

                    splitInstructorsByVerification(instructors);
                } else {
                    showLoading(false);
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

    private void splitInstructorsByVerification(List<UserDto> instructors) {
        if (instructors.isEmpty()) {
            showLoading(false);
            adapter.updateData(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(getString(R.string.admin_instructors_empty));
            return;
        }

        AtomicInteger completed = new AtomicInteger(0);
        int total = instructors.size();

        for (UserDto instructor : instructors) {
            userService.checkInstructorVerified(instructor.getUserId()).enqueue(new Callback<ApiResponse<Boolean>>() {
                @Override
                public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                    boolean isVerified = false;
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        isVerified = Boolean.TRUE.equals(response.body().getData());
                    }

                    instructor.setInstructorVerified(isVerified);

                    if (isVerified) {
                        synchronized (verifiedInstructors) {
                            verifiedInstructors.add(instructor);
                        }
                    } else {
                        synchronized (pendingInstructors) {
                            pendingInstructors.add(instructor);
                        }
                    }

                    onCheckDone(completed.incrementAndGet(), total);
                }

                @Override
                public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                    // If status check fails, keep it in pending list so admin can still verify manually.
                    instructor.setInstructorVerified(false);
                    synchronized (pendingInstructors) {
                        pendingInstructors.add(instructor);
                    }
                    onCheckDone(completed.incrementAndGet(), total);
                }
            });
        }
    }

    private void onCheckDone(int completed, int total) {
        if (completed < total) {
            return;
        }

        showLoading(false);
        applySelectedTabData();
    }

    private void selectTab(int tab) {
        if (selectedTab == tab) {
            return;
        }
        selectedTab = tab;
        updateTabUi();
        applySelectedTabData();
    }

    private void updateTabUi() {
        boolean pendingActive = selectedTab == TAB_PENDING;

        tabPending.setBackgroundColor(pendingActive ? getColor(R.color.primary) : getColor(android.R.color.transparent));
        tabPending.setTextColor(getColor(pendingActive ? R.color.white : R.color.text_secondary));

        tabVerified.setBackgroundColor(!pendingActive ? getColor(R.color.primary) : getColor(android.R.color.transparent));
        tabVerified.setTextColor(getColor(!pendingActive ? R.color.white : R.color.text_secondary));
    }

    private void applySelectedTabData() {
        List<UserDto> displayList = selectedTab == TAB_PENDING ? pendingInstructors : verifiedInstructors;
        adapter.updateData(new ArrayList<>(displayList));

        if (displayList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(getString(selectedTab == TAB_PENDING
                    ? R.string.admin_instructors_empty_pending
                    : R.string.admin_instructors_empty_verified));
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onVerifyClick(UserDto user) {
        if (user == null || user.getUserId() == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.admin_verify_confirm_title))
                .setMessage(getString(R.string.admin_verify_confirm_message, user.getName() != null ? user.getName() : ""))
                .setNegativeButton(getString(R.string.admin_verify_confirm_cancel), null)
                .setPositiveButton(getString(R.string.admin_verify_confirm_ok), (dialog, which) -> verifyInstructor(user.getUserId()))
                .show();
    }

    private void verifyInstructor(String userId) {
        adapter.setVerifyingUserId(userId);
        userService.verifyInstructor(userId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                adapter.setVerifyingUserId(null);
                if (response.isSuccessful()) {
                    ToastUtil.showCustomToast(ManageInstructorsActivity.this, getString(R.string.admin_verify_success));
                    fetchInstructors();
                } else {
                    ToastUtil.showCustomToast(ManageInstructorsActivity.this, getString(R.string.admin_verify_error));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                adapter.setVerifyingUserId(null);
                ToastUtil.showCustomToast(ManageInstructorsActivity.this, getString(R.string.admin_network_error));
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvInstructors.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}
