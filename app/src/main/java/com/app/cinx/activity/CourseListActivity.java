package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CourseAdapter;
import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CategoryResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.PaginatedApiQuery;
import com.app.cinx.api.dto.PaginatedApiResponseCourseResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private EditText etSearch;
    private RecyclerView rvCourses;
    private TextView tvResultCount;
    private ImageView btnFilter, btnBack;

    // Filter Panel views
    private RadioGroup rgSort;
    private ChipGroup cgCategories;
    
    private String currentQuery = null;
    private String currentCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        initViews();
        handleIntent(getIntent());
        
        loadCategories();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        etSearch = findViewById(R.id.etSearch);
        rvCourses = findViewById(R.id.rvCourses);
        tvResultCount = findViewById(R.id.tvResultCount);
        btnFilter = findViewById(R.id.btnFilter);
        btnBack = findViewById(R.id.btnBack);
        
        rgSort = findViewById(R.id.rgSort);
        cgCategories = findViewById(R.id.cgCategories);

        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());
        
        btnFilter.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentQuery = etSearch.getText().toString();
                fetchCourses();
                return true;
            }
            return false;
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            rgSort.check(R.id.rbSortPopular);
            cgCategories.clearCheck();
            currentCategoryId = null;
            drawerLayout.closeDrawer(GravityCompat.END);
            fetchCourses();
        });

        findViewById(R.id.btnApply).setOnClickListener(v -> {
            int selectedChipId = cgCategories.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip chip = findViewById(selectedChipId);
                currentCategoryId = (String) chip.getTag();
            } else {
                currentCategoryId = null;
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            fetchCourses();
        });
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("SEARCH_QUERY")) {
                currentQuery = intent.getStringExtra("SEARCH_QUERY");
                etSearch.setText(currentQuery);
            }
            if (intent.hasExtra("CATEGORY_ID")) {
                currentCategoryId = intent.getStringExtra("CATEGORY_ID");
            }
        }
        fetchCourses();
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getCourseService().getAllCategories().enqueue(new Callback<ApiResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryResponse>>> call, Response<ApiResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CategoryResponse> categories = response.body().getData();
                    cgCategories.removeAllViews();
                    for (CategoryResponse cat : categories) {
                        Chip chip = new Chip(CourseListActivity.this);
                        chip.setText(cat.getName());
                        chip.setCheckable(true);
                        chip.setTag(cat.getId());
                        
                        if (cat.getId().equals(currentCategoryId)) {
                            chip.setChecked(true);
                        }
                        
                        cgCategories.addView(chip);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryResponse>>> call, Throwable t) {
                // ignore
            }
        });
    }

    private void fetchCourses() {
        PaginatedApiQuery query = new PaginatedApiQuery();
        
        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            query.setQuery(currentQuery.trim());
        }
        
        int sortId = rgSort.getCheckedRadioButtonId();
        if (sortId == R.id.rbSortPopular) {
            query.setSort("{\"enrollmentCount\":\"desc\"}");
        } else if (sortId == R.id.rbSortNewest) {
            query.setSort("{\"createdAt\":\"desc\"}");
        } else if (sortId == R.id.rbSortPriceAsc) {
            query.setSort("{\"discountedPrice\":\"asc\"}");
        } else if (sortId == R.id.rbSortPriceDesc) {
            query.setSort("{\"discountedPrice\":\"desc\"}");
        }

        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getAllCourses(1, 30, query.getQuery(), query.getSort(), currentCategoryId, null).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> courses = response.body().getData();
                    
                    tvResultCount.setText(String.format("%d kết quả", courses.size()));
                    
                    RecommendedAdapter adapter = new RecommendedAdapter(courses);
                    rvCourses.setAdapter(adapter);
                } else {
                    tvResultCount.setText("0 kết quả");
                    rvCourses.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                Toast.makeText(CourseListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
