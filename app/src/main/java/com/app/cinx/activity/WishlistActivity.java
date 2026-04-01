package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CourseAdapter;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.WishlistItemResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity {
    private RecyclerView rvWishlist;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        rvWishlist = findViewById(R.id.rvWishlist);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new CourseAdapter(new ArrayList<>(), new CourseAdapter.OnCourseClickListener() {
            @Override
            public void onCourseClick(CourseResponse course) {
                Intent intent = new Intent(WishlistActivity.this, CourseDetailActivity.class);
                intent.putExtra("COURSE_ID", course.getId());
                startActivity(intent);
            }
            @Override
            public void onFavoriteClick(CourseResponse course) {
                // handle removing from wishlist
            }
        });
        rvWishlist.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist();
    }

    private void loadWishlist() {
        RetrofitClient.getInstance().getSocialService().getWishlist().enqueue(new Callback<ApiResponse<List<WishlistItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItemResponse>>> call, Response<ApiResponse<List<WishlistItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<String> ids = new ArrayList<>();
                    for (WishlistItemResponse item : response.body().getData()) {
                        ids.add(item.getCourseId());
                    }
                    if (!ids.isEmpty()) {
                        fetchCoursesAndDisplay(ids);
                    } else {
                        adapter.updateData(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItemResponse>>> call, Throwable t) {}
        });
    }

    private void fetchCoursesAndDisplay(List<String> ids) {
        RetrofitClient.getInstance().getCourseService().getCourseById_1(ids).enqueue(new Callback<ApiResponse<List<CourseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CourseResponse>>> call, Response<ApiResponse<List<CourseResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    adapter.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<CourseResponse>>> call, Throwable t) {}
        });
    }
}
