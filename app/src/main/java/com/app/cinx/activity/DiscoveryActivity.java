package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.data.CartRepository;
import com.app.cinx.model.Course;
import com.app.cinx.utils.NavHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CategoryResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.PaginatedApiQuery;
import com.app.cinx.api.dto.PaginatedApiResponseCourseResponse;

public class DiscoveryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView    btnFilter;
    private FrameLayout  btnCartBadge;
    private TextView     tvCartBadge;
    private RecyclerView coursesRecyclerView;
    private RecyclerView categoriesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        initViews();
        setupNavigation();
        loadData();
    }

    private void initViews() {
        drawerLayout         = findViewById(R.id.drawerLayout);
        btnFilter            = findViewById(R.id.btnFilter);
        btnCartBadge         = findViewById(R.id.btnCartBadgeDiscovery);
        tvCartBadge          = findViewById(R.id.tvCartBadgeDiscovery);
        coursesRecyclerView  = findViewById(R.id.coursesRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);

        // Cart button
        if (btnCartBadge != null) {
            btnCartBadge.setOnClickListener(v ->
                    startActivity(new Intent(this, CartActivity.class)));
        }

        // Filter button
        btnFilter.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // Setup Drawer Buttons (Clear, Apply)
        findViewById(R.id.btnClear).setOnClickListener(v -> {
             // Logic to clear filters
             if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.END);
        });
        
        findViewById(R.id.btnApply).setOnClickListener(v -> {
             // Logic to apply filters
             if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.END);
        });
        
        // Setup Featured Image
        ImageView featuredImage = findViewById(R.id.featuredImage);
        Glide.with(this)
             .load("https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?q=80&w=600&auto=format&fit=crop")
             .centerCrop()
             .into(featuredImage);
    }

    private void setupNavigation() {
        NavHelper.setupNavigation(this, R.id.navSearch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartBadge();
    }

    private void refreshCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartRepository.getInstance().getItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        // Setup Categories
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getAllCategories().enqueue(new Callback<ApiResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryResponse>>> call, Response<ApiResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CategoryResponse> cats = new ArrayList<>();
                    CategoryResponse allCat = new CategoryResponse();
                    allCat.setId(null);
                    allCat.setName("Tất cả");
                    cats.add(allCat);
                    cats.addAll(response.body().getData());
                    categoriesRecyclerView.setAdapter(new CategoryAdapter(cats));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryResponse>>> call, Throwable t) {
                // handle error
            }
        });

        // Setup Courses
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        courseService.getAllCourses(null, null).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> courseResponses = response.body().getData();
                    RecommendedAdapter adapter = new RecommendedAdapter(courseResponses);
                    coursesRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                // handle error
            }
        });
    }

    // Inner class for Chip/Category Adapter
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<CategoryResponse> categories;
        private int selectedPosition = 0;

        public CategoryAdapter(List<CategoryResponse> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView;
            tv.setText(categories.get(position).getName());
            
            final int pos = position; // effectively final for lambda

            if (selectedPosition == pos) {
                // Active state: Gradient or solid bright color
                tv.setBackgroundResource(R.drawable.glass_card_bg); // Reusing glass, but ideally specific active bg
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#A78BFA"))); // Violet-ish
                tv.setTextColor(Color.WHITE);
                tv.setElevation(8f);
            } else {
                // Inactive state: Glassy white
                tv.setBackgroundResource(R.drawable.glass_card_bg);
                tv.setBackgroundTintList(null); // Clear tint to show original drawable
                tv.setTextColor(Color.parseColor("#64748B"));
                tv.setElevation(0f);
            }

            tv.setOnClickListener(v -> {
                int old = selectedPosition;
                selectedPosition = pos;
                notifyItemChanged(old);
                notifyItemChanged(selectedPosition);
            });
        }

        @Override
        public int getItemCount() {
            return categories != null ? categories.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
