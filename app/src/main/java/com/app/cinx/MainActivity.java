package com.app.cinx;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.adapter.ContinueLearningAdapter;
import com.app.cinx.adapter.CourseAdapter;
import com.app.cinx.adapter.PartnerAdapter;
import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.adapter.TestimonialAdapter;
import com.app.cinx.model.Course;
import com.app.cinx.model.Testimonial;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import com.app.cinx.util.NavHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean isLoggedIn = true; // Temporary toggle

    // Guest Views
    private RecyclerView partnersRecyclerView;
    private RecyclerView coursesRecyclerView;
    private RecyclerView testimonialsRecyclerView1;
    private RecyclerView testimonialsRecyclerView2;
    
    // Logged In Views
    private RecyclerView continueLearningRecyclerView;
    private RecyclerView recommendedRecyclerView;
    private ImageView userAvatar;

    private Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLoggedIn) {
            setContentView(R.layout.activity_main_logged_in);
            initLoggedInViews();
        } else {
            setContentView(R.layout.activity_main);
            initGuestViews();
            setupGuestRecyclerViews();
            loadGuestData();
            setupGuestClickListeners();
            startAutoScroll();
        }
    }
    
    private void initLoggedInViews() {
        userAvatar = findViewById(R.id.userAvatar);
        continueLearningRecyclerView = findViewById(R.id.continueLearningRecyclerView);
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView);

        // Load Avatar
        Glide.with(this)
                .load("https://i.pravatar.cc/150?u=my_user")
                .circleCrop()
                .into(userAvatar);

        // Setup Continue Learning
        continueLearningRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Course> activeCourses = getCoursesList().subList(0, 2); // Mock some active courses
        ContinueLearningAdapter continueAdapter = new ContinueLearningAdapter(activeCourses);
        continueLearningRecyclerView.setAdapter(continueAdapter);

        // Setup Recommended
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<Course> recommendedCourses = getCoursesList();
        RecommendedAdapter recommendedAdapter = new RecommendedAdapter(recommendedCourses);
        recommendedRecyclerView.setAdapter(recommendedAdapter);
        
        // Setup Bottom Nav (Simple placeholders)
        // Note: The layout uses <include layout="@layout/bottom_nav_menu"> which has IDs like navHome, navSearch etc.
        NavHelper.setupNavigation(this, R.id.navHome);
    }

    private void initGuestViews() {
        partnersRecyclerView = findViewById(R.id.partnersRecyclerView);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        testimonialsRecyclerView1 = findViewById(R.id.testimonialsRecyclerView1);
        testimonialsRecyclerView2 = findViewById(R.id.testimonialsRecyclerView2);

        // Load profile image
        ImageView profileImage = findViewById(R.id.profileImage);
        if (profileImage != null) {
            Glide.with(this)
                    .load("https://i.pravatar.cc/150?u=8")
                    .circleCrop()
                    .into(profileImage);
        }
    }

    private void setupGuestRecyclerViews() {
        // Partners
        partnersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        // Courses
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        // Testimonials Row 1
        testimonialsRecyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        // Testimonials Row 2
        testimonialsRecyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void startAutoScroll() {
        // Scroll speed
        final int scrollSpeed = 2; 
        final int delay = 20;

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (partnersRecyclerView != null && partnersRecyclerView.getAdapter() != null) partnersRecyclerView.scrollBy(scrollSpeed - 1, 0);
                if (testimonialsRecyclerView1 != null && testimonialsRecyclerView1.getAdapter() != null) testimonialsRecyclerView1.scrollBy(scrollSpeed, 0);
                if (testimonialsRecyclerView2 != null && testimonialsRecyclerView2.getAdapter() != null) testimonialsRecyclerView2.scrollBy(-scrollSpeed, 0);
                
                autoScrollHandler.postDelayed(this, delay);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, delay);
    }

    private void loadGuestData() {
        // Partners
        List<String> partners = new ArrayList<>();
        partners.add("https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_2015_logo.svg");
        partners.add("https://upload.wikimedia.org/wikipedia/commons/9/96/Microsoft_logo_%282012%29.svg");
        partners.add("https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg");
        partners.add("https://upload.wikimedia.org/wikipedia/commons/d/d5/Slack_icon_2019.svg");
        partners.add("https://upload.wikimedia.org/wikipedia/commons/6/69/Airbnb_Logo_B%C3%A9lo.svg");
        partners.add("https://upload.wikimedia.org/wikipedia/commons/c/cc/Uber_logo_2018.png");

        PartnerAdapter partnerAdapter = new PartnerAdapter(partners);
        partnersRecyclerView.setAdapter(partnerAdapter);

        // Courses
        List<Course> courses = getCoursesList();
        CourseAdapter courseAdapter = new CourseAdapter(courses, new CourseAdapter.OnCourseClickListener() {
            @Override
            public void onCourseClick(Course course) {
                Toast.makeText(MainActivity.this, "Clicked: " + course.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteClick(Course course) {
                Toast.makeText(MainActivity.this, "Added to favorites: " + course.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        coursesRecyclerView.setAdapter(courseAdapter);

        // Testimonials
        List<Testimonial> testimonials = getTestimonialsList();
        TestimonialAdapter testimonialAdapter1 = new TestimonialAdapter(testimonials);
        testimonialsRecyclerView1.setAdapter(testimonialAdapter1);
        
        TestimonialAdapter testimonialAdapter2 = new TestimonialAdapter(testimonials);
        testimonialsRecyclerView2.setAdapter(testimonialAdapter2);
        
        // Scroll to middle for infinite effect backward scrolling
        testimonialsRecyclerView2.scrollToPosition(Integer.MAX_VALUE / 2);
    }

    private void setupGuestClickListeners() {
        MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);
        MaterialButton btnWatchDemo = findViewById(R.id.btnWatchDemo);
        
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> Toast.makeText(this, "Bắt đầu ngay!", Toast.LENGTH_SHORT).show());
        }
        if (btnWatchDemo != null) {
            btnWatchDemo.setOnClickListener(v -> Toast.makeText(this, "Xem Demo", Toast.LENGTH_SHORT).show());
        }
        
        setupSharedNavListeners();
    }
    
    private void setupSharedNavListeners() {
        // Custom nav logic - works for both layouts if IDs match
        View navHome = findViewById(R.id.navHome);
        if (navHome != null) setupNavClick((ImageView)navHome, "Home");
        
        View navSearch = findViewById(R.id.navSearch);
        if (navSearch != null) setupNavClick((ImageView)navSearch, "Search");
        
        View navCourses = findViewById(R.id.navCourses);
        if (navCourses != null) setupNavClick((ImageView)navCourses, "Courses");
        
        View navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) setupNavClick((ImageView)navProfile, "Profile");
    }
    
    private void setupNavClick(ImageView view, String name) {
        view.setOnClickListener(v -> {
            // Reset all navs visual
            resetNavs();
            // Set active visual (simple for now)
            view.setBackgroundResource(R.drawable.glass_panel_bg);
            view.setColorFilter(getColor(R.color.primary));
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        });
    }
    
    private void resetNavs() {
        int[] ids = {R.id.navHome, R.id.navSearch, R.id.navCourses, R.id.navProfile};
        for (int id : ids) {
            View v = findViewById(id);
            if (v instanceof ImageView) {
                v.setBackground(null);
                ((ImageView)v).setColorFilter(getColor(R.color.text_secondary)); 
            }
        }
    }


    private List<Course> getCoursesList() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "UI/UX Design Masterclass", "Hà Linh", 4.9, "12k", "599k",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&q=80", "Design"));
        courses.add(new Course(2, "Fullstack React & Node.js", "Minh Tuấn", 4.8, "8.5k", "899k",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&q=80", "Coding"));
        courses.add(new Course(3, "Digital Marketing 101", "Sarah Nguyễn", 4.7, "15k", "450k",
                "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=800&q=80", "Business"));
        courses.add(new Course(4, "Nhiếp ảnh đường phố", "Quang Hải", 4.9, "5k", "399k",
                "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=800&q=80", "Art"));
        return courses;
    }

    private List<Testimonial> getTestimonialsList() {
        List<Testimonial> testimonials = new ArrayList<>();
        testimonials.add(new Testimonial(1, "Khóa học thay đổi tư duy của tôi!", "An Nhiên", "https://i.pravatar.cc/150?u=1"));
        testimonials.add(new Testimonial(2, "Giao diện app quá đẹp và mượt.", "Bảo Long", "https://i.pravatar.cc/150?u=2"));
        testimonials.add(new Testimonial(3, "Kiến thức thực tế, áp dụng ngay.", "Hương Giang", "https://i.pravatar.cc/150?u=3"));
        testimonials.add(new Testimonial(4, "Support nhiệt tình 24/7.", "Đức Minh", "https://i.pravatar.cc/150?u=4"));
        testimonials.add(new Testimonial(5, "Giá cả hợp lý cho sinh viên.", "Thảo Vy", "https://i.pravatar.cc/150?u=5"));
        testimonials.add(new Testimonial(6, "Cộng đồng học tập rất sôi nổi.", "Tuấn Kiệt", "https://i.pravatar.cc/150?u=6"));
        return testimonials;
    }
}

