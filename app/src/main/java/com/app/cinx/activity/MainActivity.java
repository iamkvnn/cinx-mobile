package com.app.cinx.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.ContinueLearningAdapter;
import com.app.cinx.adapter.CourseAdapter;
import com.app.cinx.adapter.PartnerAdapter;
import com.app.cinx.adapter.RecommendedAdapter;
import com.app.cinx.adapter.TestimonialAdapter;
import com.app.cinx.model.Course;
import com.app.cinx.model.Testimonial;
import com.app.cinx.utils.UserManager;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import com.app.cinx.utils.NavHelper;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.PaginatedApiResponseCourseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        if (UserManager.getInstance().isLoggedIn()) {
            Log.i("MainActivity", "User is logged in");
            setContentView(R.layout.activity_main_logged_in);
            initLoggedInViews();
            NavHelper.setupNavigation(this, R.id.navHome);
        } else {
            setContentView(R.layout.activity_main);
            initGuestViews();
            setupGuestRecyclerViews();
            loadGuestData();
            setupGuestClickListeners();
            // Start auto scroll only if the activity is valid
            partnersRecyclerView.post(this::startAutoScroll);
            
             // Setup StatusBar for immersive experience
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getInstance().isLoggedIn() && autoScrollHandler != null && autoScrollRunnable != null) {
            startAutoScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
    
    private void initLoggedInViews() {
        userAvatar = findViewById(R.id.userAvatar);
        continueLearningRecyclerView = findViewById(R.id.continueLearningRecyclerView);
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView);
        TextView tvUserName = findViewById(R.id.tvUserName);
        
        if (tvUserName != null && UserManager.getInstance().getUserEmail() != null) {
            tvUserName.setText(UserManager.getInstance().getUserEmail() + " 👋");
        }

        // Load Avatar
        Glide.with(this)
                .load("https://i.pravatar.cc/150?u=my_user")
                .circleCrop()
                .into(userAvatar);

        // Setup Continue Learning
        continueLearningRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Placeholder for active courses until we have API for it, for now load everything
        
        // Setup Recommended
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getAllCourses(null, null).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> courseResponses = response.body().getData();
                    
                    if (courseResponses.size() >= 2) {
                        List<CourseResponse> activeCourses = courseResponses.subList(0, 2); 
                        ContinueLearningAdapter continueAdapter = new ContinueLearningAdapter(activeCourses);
                        continueLearningRecyclerView.setAdapter(continueAdapter);
                    } else {
                        ContinueLearningAdapter continueAdapter = new ContinueLearningAdapter(courseResponses);
                        continueLearningRecyclerView.setAdapter(continueAdapter);
                    }

                    RecommendedAdapter recommendedAdapter = new RecommendedAdapter(courseResponses);
                    recommendedRecyclerView.setAdapter(recommendedAdapter);
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                Log.e("MainActivity", "Failed to load courses", t);
            }
        });
    }

    private void initGuestViews() {
        partnersRecyclerView = findViewById(R.id.partnersRecyclerView);
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        testimonialsRecyclerView1 = findViewById(R.id.testimonialsRecyclerView1);
        testimonialsRecyclerView2 = findViewById(R.id.testimonialsRecyclerView2);
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
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getAllCourses(null, null).enqueue(new Callback<PaginatedApiResponseCourseResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCourseResponse> call, Response<PaginatedApiResponseCourseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CourseResponse> courseResponses = response.body().getData();
                    CourseAdapter courseAdapter = new CourseAdapter(courseResponses, new CourseAdapter.OnCourseClickListener() {
                        @Override
                        public void onCourseClick(CourseResponse course) {
                            android.content.Intent intent = new android.content.Intent(MainActivity.this, CourseDetailActivity.class);
                            intent.putExtra("COURSE_ID", course.getId());
                            intent.putExtra("COURSE_TITLE", course.getTitle());
                            intent.putExtra("COURSE_PRICE", course.getPrice() != null ? course.getPrice() : 0L);
                            intent.putExtra("COURSE_DISCOUNTED_PRICE", course.getDiscountedPrice() != null ? course.getDiscountedPrice() : (course.getPrice() != null ? course.getPrice() : 0L));
                            startActivity(intent);
                        }

                        @Override
                        public void onFavoriteClick(CourseResponse course) {
                            Toast.makeText(MainActivity.this, "Added to favorites: " + course.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    coursesRecyclerView.setAdapter(courseAdapter);
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCourseResponse> call, Throwable t) {
                Log.e("MainActivity", "Failed to load courses", t);
            }
        });

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
        
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    private List<Course> getCoursesList() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "UI/UX Design Masterclass", "Hà Linh", 4.9, "12k", 1200000L, 599000L, 50, "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&q=80", "Design", "22h"));
        courses.add(new Course(2, "Fullstack React & Node.js", "Minh Tuấn", 4.8, "8.5k", 1200000L, 899000L, 25, "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&q=80", "Coding", "40h"));
        courses.add(new Course(3, "Digital Marketing 101", "Sarah Nguyễn", 4.7, "15k", 450000L,
                "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=800&q=80", "Business"));
        courses.add(new Course(4, "Nhiếp ảnh đường phố", "Quang Hải", 4.9, "5k", 399000L,
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
