package com.app.cinx;

import android.os.Bundle;
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

import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.model.Course;
import com.app.cinx.util.NavHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnFilter;
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
        drawerLayout = findViewById(R.id.drawerLayout);
        btnFilter = findViewById(R.id.btnFilter);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        
        // Setup Filter Button logic
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

    private void loadData() {
        // Setup Categories
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");
        categories.add("Lập trình");
        categories.add("Thiết kế");
        categories.add("Kinh doanh");
        categories.add("Marketing");
        categories.add("Ngoại ngữ");
        categoriesRecyclerView.setAdapter(new CategoryAdapter(categories));

        // Setup Courses
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<Course> courses = new ArrayList<>();
        
        // Add sample data matching HTML
        courses.add(new Course(1, "Python Data Science Pro", "Build 5 real-world projects", 4.8, "1.2k", "$49", "https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=300&auto=format&fit=crop", "Code", "22h"));
        courses.add(new Course(2, "Instagram Growth 2026", "Strategies for influencers", 4.6, "800", "$29", "https://images.unsplash.com/photo-1611162617474-5b21e879e113?q=80&w=300&auto=format&fit=crop", "Marketing", "5h"));
        courses.add(new Course(3, "Startup 101 Guide", "From idea to launch", 4.9, "2k", "Free", "https://images.unsplash.com/photo-1509062522246-3755977927d7?q=80&w=300&auto=format&fit=crop", "Business", "2h"));
        
        RecommendedAdapter adapter = new RecommendedAdapter(courses);
        coursesRecyclerView.setAdapter(adapter);
    }

    // Inner class for Chip/Category Adapter
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<String> categories;
        private int selectedPosition = 0;

        public CategoryAdapter(List<String> categories) {
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
            tv.setText(categories.get(position));
            
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
            return categories.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
