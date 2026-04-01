package com.app.cinx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.GeneratedPathItemAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.RecommendationService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.GenerateLearningPathItem;
import com.app.cinx.api.dto.GenerateLearningPathRequest;
import com.app.cinx.api.dto.GenerateLearningPathResponse;
import com.app.cinx.api.dto.LearningPathItemRequest;
import com.app.cinx.api.dto.LearningPathRequest;
import com.app.cinx.api.dto.LearningPathResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateLearningPathActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etGoal;
    private MaterialButton btnGenerate, btnSave;
    private ProgressBar progressBar;
    private TextView tvLoading, tvResultTitle, tvResultDesc;
    private LinearLayout layoutResult;
    private RecyclerView recyclerItems;

    private GeneratedPathItemAdapter adapter;
    private GenerateLearningPathResponse currentSuggestion;
    private Map<String, String> courseNameMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_learning_path);

        bindViews();
        setupRecyclerView();
        setupListeners();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        etGoal = findViewById(R.id.etGoal);
        btnGenerate = findViewById(R.id.btnGenerate);
        progressBar = findViewById(R.id.progressBar);
        tvLoading = findViewById(R.id.tvLoading);

        layoutResult = findViewById(R.id.layoutResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultDesc = findViewById(R.id.tvResultDesc);
        recyclerItems = findViewById(R.id.recyclerItems);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupRecyclerView() {
        adapter = new GeneratedPathItemAdapter(new ArrayList<>(), courseNameMap);
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnGenerate.setOnClickListener(v -> {
            String goal = etGoal.getText().toString().trim();
            if (goal.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập yêu cầu của bạn.", Toast.LENGTH_SHORT).show();
                return;
            }
            generatePath(goal);
        });

        btnSave.setOnClickListener(v -> {
            if (currentSuggestion != null) {
                saveLearningPath();
            }
        });
    }

    private void generatePath(String goal) {
        showLoading(true);
        layoutResult.setVisibility(View.GONE);

        RecommendationService recService = RetrofitClient.getInstance().getRecommendationService();
        if (recService == null) {
            showLoading(false);
            return;
        }

        GenerateLearningPathRequest req = new GenerateLearningPathRequest();
        req.setGoal(goal);

        recService.generateLearningPath(req).enqueue(new Callback<GenerateLearningPathResponse>() {
            @Override
            public void onResponse(Call<GenerateLearningPathResponse> call, Response<GenerateLearningPathResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentSuggestion = response.body();
                    fetchCourseNamesForSuggestion(currentSuggestion);
                } else {
                    showLoading(false);
                    Toast.makeText(GenerateLearningPathActivity.this, "Không thể tạo lộ trình. Thử lại sau.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenerateLearningPathResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(GenerateLearningPathActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCourseNamesForSuggestion(GenerateLearningPathResponse data) {
        if (data.getItems() == null || data.getItems().isEmpty()) {
            showLoading(false);
            displayResult();
            return;
        }

        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        if (courseService == null) {
            showLoading(false);
            displayResult();
            return;
        }

        List<String> distinctCourseIds = new ArrayList<>();
        for (GenerateLearningPathItem item : data.getItems()) {
            String cid = item.getCourseId();
            if (cid != null && !distinctCourseIds.contains(cid)) {
                distinctCourseIds.add(cid);
            }
        }

        // Keep track of how many parallel requests we make
        final int totalRequests = distinctCourseIds.size();
        if (totalRequests == 0) {
            showLoading(false);
            displayResult();
            return;
        }

        final int[] completedRequests = {0};

        for (String cid : distinctCourseIds) {
            courseService.getCourseById(cid).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                    completedRequests[0]++;
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        courseNameMap.put(cid, response.body().getData().getTitle());
                    }
                    if (completedRequests[0] == totalRequests) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            displayResult();
                        });
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                    completedRequests[0]++;
                    if (completedRequests[0] == totalRequests) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            displayResult();
                        });
                    }
                }
            });
        }
    }

    private void displayResult() {
        if (currentSuggestion == null) return;
        tvResultTitle.setText(currentSuggestion.getPathName());
        tvResultDesc.setText(currentSuggestion.getDescription());
        
        adapter.updateData(currentSuggestion.getItems(), courseNameMap);
        layoutResult.setVisibility(View.VISIBLE);
    }

    private void saveLearningPath() {
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        if (learningService == null) return;

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        LearningPathRequest request = new LearningPathRequest();
        request.setTitle(currentSuggestion.getPathName() != null ? currentSuggestion.getPathName() : "Lộ trình AI");
        request.setDescription(currentSuggestion.getDescription() != null ? currentSuggestion.getDescription() : "");
        
        List<LearningPathItemRequest> itemsReq = new ArrayList<>();
        if (currentSuggestion.getItems() != null) {
            for (int i = 0; i < currentSuggestion.getItems().size(); i++) {
                GenerateLearningPathItem gItem = currentSuggestion.getItems().get(i);
                LearningPathItemRequest itemReq = new LearningPathItemRequest();
                itemReq.setCourseId(gItem.getCourseId());
                itemReq.setLessonId(gItem.getLessonId());
                itemReq.setOrderIndex(i + 1);
                itemReq.setIsSuggested(true);
                itemsReq.add(itemReq);
            }
        }
        request.setItems(itemsReq);

        learningService.createLearningPath(request).enqueue(new Callback<ApiResponse<LearningPathResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LearningPathResponse>> call, Response<ApiResponse<LearningPathResponse>> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu lộ trình này");
                if (response.isSuccessful()) {
                    Toast.makeText(GenerateLearningPathActivity.this, "Lưu lộ trình thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen
                } else {
                    Toast.makeText(GenerateLearningPathActivity.this, "Lỗi khi lưu lộ trình", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LearningPathResponse>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu lộ trình này");
                Toast.makeText(GenerateLearningPathActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        btnGenerate.setEnabled(!isLoading);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        tvLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}