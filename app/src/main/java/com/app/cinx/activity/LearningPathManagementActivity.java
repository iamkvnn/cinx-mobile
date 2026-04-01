package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.LearningPathAdapter;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.LearningPathResponse;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningPathManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerPaths;
    private LearningPathAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private ExtendedFloatingActionButton fabCreatePath;
    private ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_path_management);

        bindViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLearningPaths(); // Refresh list when returning
    }

    private void bindViews() {
        recyclerPaths = findViewById(R.id.recyclerPaths);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        fabCreatePath = findViewById(R.id.fabCreatePath);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        recyclerPaths.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LearningPathAdapter(new ArrayList<>(), item -> {
            Intent intent = new Intent(this, LearningPathDetailActivity.class);
            intent.putExtra("PATH_ID", item.getId());
            startActivity(intent);
        });
        recyclerPaths.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabCreatePath.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateLearningPathActivity.class);
            startActivity(intent);
        });
    }

    private void fetchLearningPaths() {
        progressBar.setVisibility(View.VISIBLE);
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        if (learningService == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        learningService.getLearningPaths().enqueue(new Callback<ApiResponse<List<LearningPathResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LearningPathResponse>>> call, Response<ApiResponse<List<LearningPathResponse>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<LearningPathResponse> data = response.body().getData();
                    if (data.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerPaths.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        recyclerPaths.setVisibility(View.VISIBLE);
                        adapter.updateData(data);
                    }
                } else {
                    Toast.makeText(LearningPathManagementActivity.this, "Lỗi tải danh sách lộ trình.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<LearningPathResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LearningPathManagementActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}