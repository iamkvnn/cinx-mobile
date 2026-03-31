package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.utils.NavHelper;
import com.app.cinx.utils.UserManager;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.dto.PaginatedApiResponseCourseResponse;
import com.app.cinx.api.dto.CategoryResponse;
import com.app.cinx.api.dto.CreateCourseRequest;
import com.app.cinx.api.dto.ApiResponse;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorDashboardActivity extends AppCompatActivity {

    private TextView tvInstructorName;
    private ImageView imgInstructorAvatar;
    private TextView tvTotalCourses;
    private TextView tvCreateCourse;
    private RecyclerView rvInstructorCourses;
    private ProgressBar pbLoading;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_dashboard);

        // Bind Views
        tvInstructorName = findViewById(R.id.tvInstructorName);
        imgInstructorAvatar = findViewById(R.id.imgInstructorAvatar);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);
        tvCreateCourse = findViewById(R.id.tvCreateCourse);
        rvInstructorCourses = findViewById(R.id.rvInstructorCourses);
        pbLoading = findViewById(R.id.pbLoading);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setupUserInfo();
        setupNavigation();
        setupActions();

        rvInstructorCourses.setLayoutManager(new LinearLayoutManager(this));

        // Fetch Courses
        fetchInstructorCourses();
    }

    private void setupUserInfo() {
        String name = UserManager.getInstance().getUserName();
        String avatar = UserManager.getInstance().getAvatarUrl();

        if (name != null) tvInstructorName.setText(name);
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)
                 .load(avatar)
                 .placeholder(R.drawable.ic_profile_placeholder)
                 .into(imgInstructorAvatar);
        }
    }

    private void setupNavigation() {
        NavHelper.setupInstructorNavigation(this, R.id.navInstCourses);
    }

    private void setupActions() {
        tvCreateCourse.setOnClickListener(v -> {
            showCreateCourseDialog();
        });
    }

    private void showCreateCourseDialog() {
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getAllCategories().enqueue(new Callback<ApiResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryResponse>>> call, Response<ApiResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CategoryResponse> categories = response.body().getData();
                    showCategorySelector(categories);
                } else {
                    android.widget.Toast.makeText(InstructorDashboardActivity.this, "Không thể tải danh mục", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryResponse>>> call, Throwable t) {
                android.widget.Toast.makeText(InstructorDashboardActivity.this, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategorySelector(List<CategoryResponse> categories) {
        String[] catNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            catNames[i] = categories.get(i).getName();
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chọn danh mục")
            .setItems(catNames, (dialog, which) -> {
                promptCourseTitle(categories.get(which).getId());
            })
            .show();
    }

    private void promptCourseTitle(String categoryId) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Tên khóa học");

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Nhập tên khóa học")
            .setView(input)
            .setPositiveButton("Tạo", (dialog, which) -> {
                String title = input.getText().toString().trim();
                if (!title.isEmpty()) {
                    createCourse(title, categoryId);
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void createCourse(String title, String categoryId) {
        pbLoading.setVisibility(View.VISIBLE);
        CreateCourseRequest req = new CreateCourseRequest();
        req.setTitle(title);
        req.setCategoryId(categoryId);
        req.setPrice(0L);
        req.setIsPublished(false);
        req.setSections(new ArrayList<>());
        
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.createCourse(req).enqueue(new Callback<ApiResponse<com.app.cinx.api.dto.CourseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.app.cinx.api.dto.CourseResponse>> call, Response<ApiResponse<com.app.cinx.api.dto.CourseResponse>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.app.cinx.api.dto.CourseResponse newCourse = response.body().getData();
                    android.widget.Toast.makeText(InstructorDashboardActivity.this, "Tạo thành công", android.widget.Toast.LENGTH_SHORT).show();
                    fetchInstructorCourses(); // refresh
                    Intent intent = new Intent(InstructorDashboardActivity.this, CourseOutlineActivity.class);
                    intent.putExtra("COURSE_ID", newCourse.getId());
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(InstructorDashboardActivity.this, "Lỗi tạo khóa học", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.app.cinx.api.dto.CourseResponse>> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                android.widget.Toast.makeText(InstructorDashboardActivity.this, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchInstructorCourses() {
        pbLoading.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        rvInstructorCourses.setVisibility(View.GONE);

        String instructorId = UserManager.getInstance().getUserId();
        CourseService courseService = RetrofitClient.getInstance().getCourseService();

        com.app.cinx.api.dto.PaginatedApiQuery query = new com.app.cinx.api.dto.PaginatedApiQuery();
        query.setPage(1);
        query.setSize(20);

        courseService.getAllCourses(query, null, instructorId).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> courses = response.body().getData();
                    tvTotalCourses.setText(String.valueOf(courses.size()));

                    if (courses.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        rvInstructorCourses.setVisibility(View.VISIBLE);
                        com.app.cinx.adapter.InstructorCourseAdapter adapter = new com.app.cinx.adapter.InstructorCourseAdapter(InstructorDashboardActivity.this, courses);
                        rvInstructorCourses.setAdapter(adapter);
                    }
                } else {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    tvEmptyState.setText("Không thể lấy dữ liệu. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                tvEmptyState.setText("Lỗi kết nối.");
            }
        });
    }
}
