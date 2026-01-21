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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.model.Course;
import com.app.cinx.util.NavHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryActivity extends AppCompatActivity {

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
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        
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
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(40, 20, 40, 20);
            textView.setTextSize(12);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 24, 0);
            textView.setLayoutParams(params);
            
            // Set default background (generic drawable or shape created programmatically)
            
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView;
            tv.setText(categories.get(position));
            
            final int pos = position; // effectively final for lambda

            if (selectedPosition == pos) {
                tv.setBackgroundResource(R.drawable.glass_nav_bg); // Reusing glass bg, but ideally specific active chib bg
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#A78BFA"))); // Violet-ish
                tv.setTextColor(Color.WHITE);
            } else {
                tv.setBackgroundResource(R.drawable.glass_nav_bg); 
                tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#80FFFFFF"))); 
                tv.setTextColor(Color.parseColor("#475569"));
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
