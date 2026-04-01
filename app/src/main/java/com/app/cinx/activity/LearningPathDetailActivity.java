package com.app.cinx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.LearningPathDetailAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.LearningPathItemResponse;
import com.app.cinx.api.dto.LearningPathResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningPathDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPathTitle, tvPathDescription, tvProgress, tvStatus;
    private ProgressBar progressBarPath, progressBarLoading;
    private RecyclerView recyclerPathItems;

    private LearningPathDetailAdapter adapter;
    private String pathId;
    private Map<String, String> courseNameMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_path_detail);

        pathId = getIntent().getStringExtra("PATH_ID");
        if (pathId == null || pathId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID lộ trình", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        setupRecyclerView();
        setupListeners();
        
        fetchPathDetail();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        
        tvPathTitle = findViewById(R.id.tvPathTitle);
        tvPathDescription = findViewById(R.id.tvPathDescription);
        tvProgress = findViewById(R.id.tvProgress);
        tvStatus = findViewById(R.id.tvStatus);
        progressBarPath = findViewById(R.id.progressBarPath);
        
        progressBarLoading = findViewById(R.id.progressBarLoading);
        recyclerPathItems = findViewById(R.id.recyclerPathItems);
    }

    private void setupRecyclerView() {
        adapter = new LearningPathDetailAdapter(new ArrayList<>(), courseNameMap);
        recyclerPathItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerPathItems.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchPathDetail() {
        progressBarLoading.setVisibility(View.VISIBLE);
        LearningService service = RetrofitClient.getInstance().getLearningService();
        if (service == null) return;

        service.getLearningPath(pathId).enqueue(new Callback<ApiResponse<LearningPathResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LearningPathResponse>> call, Response<ApiResponse<LearningPathResponse>> response) {
                progressBarLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    LearningPathResponse data = response.body().getData();
                    updateUI(data);
                    fetchCourseNamesForItems(data.getItems());
                } else {
                    Toast.makeText(LearningPathDetailActivity.this, "Lỗi tải chi tiết: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LearningPathResponse>> call, Throwable t) {
                progressBarLoading.setVisibility(View.GONE);
                Toast.makeText(LearningPathDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(LearningPathResponse data) {
        tvPathTitle.setText(data.getTitle() != null ? data.getTitle() : "Lộ trình học tập");
        tvPathDescription.setText(data.getDescription() != null ? data.getDescription() : "Không có mô tả.");
        
        int total = data.getTotalItems() != null ? data.getTotalItems() : 0;
        int completed = data.getCompletedItems() != null ? data.getCompletedItems() : 0;
        
        tvProgress.setText("Tiến độ: " + completed + "/" + total);
        tvStatus.setText(data.getStatus() != null ? data.getStatus() : "ACTIVE");
        
        if (total > 0) {
            progressBarPath.setMax(total);
            progressBarPath.setProgress(completed);
        } else {
            progressBarPath.setProgress(0);
        }

        if (data.getItems() != null) {
            adapter.updateData(data.getItems());
        }
    }

    private void fetchCourseNamesForItems(List<LearningPathItemResponse> items) {
        if (items == null || items.isEmpty()) return;

        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        if (courseService == null) return;

        List<String> distinctCourseIds = new ArrayList<>();
        for (LearningPathItemResponse item : items) {
            String cid = item.getCourseId();
            if (cid != null && !distinctCourseIds.contains(cid) && !courseNameMap.containsKey(cid)) {
                distinctCourseIds.add(cid);
            }
        }

        for (String cid : distinctCourseIds) {
            courseService.getCourseById(cid).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        courseNameMap.put(cid, response.body().getData().getTitle());
                        runOnUiThread(() -> adapter.updateCourseNames(courseNameMap));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                    // Ignore on failure for silent load
                }
            });
        }
    }
}