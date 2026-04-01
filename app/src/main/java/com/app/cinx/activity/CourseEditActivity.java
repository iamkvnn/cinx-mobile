package com.app.cinx.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.EditSectionAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CategoryResponse;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.CreateCourseRequest;
import com.app.cinx.api.dto.CreateLessonRequest;
import com.app.cinx.api.dto.CreateSectionRequest;
import com.app.cinx.api.dto.PresignedUrlResponse;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.LessonResponse;
import com.app.cinx.api.dto.UpdateCourseRequest;
import com.app.cinx.api.dto.UpdateLessonRequest;
import com.app.cinx.api.dto.UpdateSectionRequest;
import com.app.cinx.model.EditableLesson;
import com.app.cinx.model.EditableSection;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class CourseEditActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private View llInformationTab, llCurriculumTab;

    private TextInputEditText etTitle, etDescription, etPrice, etDiscountedPrice;
    private Switch switchPublish;
    private Spinner spinnerCategory;
    private ImageView imgCourseCover;
    private Button btnChangeCover, btnSaveInfo, btnAddSection;
    private ProgressBar pbImageUpload;
    private RecyclerView rvSections;

    private List<CategoryResponse> categoryList = new ArrayList<>();
    private String courseId;
    private CourseService courseService;
    private CourseDetailResponse currentCourse;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    private List<EditableSection> editableSections = new ArrayList<>();
    private EditSectionAdapter sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        courseId = getIntent().getStringExtra("COURSE_ID");
        courseService = RetrofitClient.getInstance().getCourseService();

        tabLayout = findViewById(R.id.tabLayout);
        llInformationTab = findViewById(R.id.llInformationTab);
        llCurriculumTab = findViewById(R.id.llCurriculumTab);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etDiscountedPrice = findViewById(R.id.etDiscountedPrice);
        switchPublish = findViewById(R.id.switchPublish);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imgCourseCover = findViewById(R.id.imgCourseCover);
        btnChangeCover = findViewById(R.id.btnChangeCover);
        btnSaveInfo = findViewById(R.id.btnSaveInfo);
        pbImageUpload = findViewById(R.id.pbImageUpload);
        rvSections = findViewById(R.id.rvSections);
        btnAddSection = findViewById(R.id.btnAddSection);

        findViewById(R.id.toolbarCourseEdit).setOnClickListener(v -> finish());

        setupTabs();
        setupCurriculumRv();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                imgCourseCover.setImageURI(uri);
            }
        });

        btnChangeCover.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveInfo.setOnClickListener(v -> saveCourseData());
        btnAddSection.setOnClickListener(v -> showAddSectionDialog());

        loadCategories();
        if (courseId != null) {
            loadCourseDetails();
        }
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    llInformationTab.setVisibility(View.VISIBLE);
                    llCurriculumTab.setVisibility(View.GONE);
                } else {
                    llInformationTab.setVisibility(View.GONE);
                    llCurriculumTab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupCurriculumRv() {
        rvSections.setLayoutManager(new LinearLayoutManager(this));
        sectionAdapter = new EditSectionAdapter(this, editableSections);
        rvSections.setAdapter(sectionAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                sectionAdapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvSections);
        sectionAdapter.setItemTouchHelper(touchHelper);
    }

    private void showAddSectionDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Thêm chương");
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Tên chương");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String title = input.getText().toString();
            if (!title.isEmpty()) {
                EditableSection sec = new EditableSection();
                sec.title = title;
                editableSections.add(sec);
                sectionAdapter.notifyItemInserted(editableSections.size() - 1);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadCategories() {
        courseService.getAllCategories().enqueue(new Callback<ApiResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryResponse>>> call, retrofit2.Response<ApiResponse<List<CategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryResponse cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CourseEditActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    if (currentCourse != null) {
                        populateUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryResponse>>> call, Throwable t) {
                Toast.makeText(CourseEditActivity.this, "Lỗi tải categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCourseDetails() {
        courseService.getCourseById(courseId).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, retrofit2.Response<ApiResponse<CourseDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentCourse = response.body().getData();
                    populateUI();
                    populateCurriculum();
                } else {
                    Toast.makeText(CourseEditActivity.this, "Lỗi tải thông tin khóa học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                Toast.makeText(CourseEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI() {
        if (currentCourse == null) return;

        if (currentCourse.getTitle() != null) etTitle.setText(currentCourse.getTitle());
        if (currentCourse.getDescription() != null) etDescription.setText(currentCourse.getDescription());
        
        if (currentCourse.getPrice() != null) {
            etPrice.setText(String.valueOf(currentCourse.getPrice()));
        }
        if (currentCourse.getDiscountedPrice() != null) {
            etDiscountedPrice.setText(String.valueOf(currentCourse.getDiscountedPrice()));
        }
        
        if (currentCourse.getCategory() != null && !categoryList.isEmpty()) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (currentCourse.getCategory().equals(categoryList.get(i).getName()) || currentCourse.getCategory().equals(categoryList.get(i).getId())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }

        if (currentCourse.getIsPublished() != null) {
            switchPublish.setChecked(currentCourse.getIsPublished());
        }

        if (currentCourse.getImages() != null && !currentCourse.getImages().isEmpty()) {
            com.app.cinx.api.dto.CourseImageResponse img = currentCourse.getImages().get(0);
            if (img.getImageUrl() != null) {
                Glide.with(this).load(img.getImageUrl()).into(imgCourseCover);
            }
        }
    }

    private void populateCurriculum() {
        editableSections.clear();
        if (currentCourse != null && currentCourse.getSections() != null) {
            for (SectionResponse secRes : currentCourse.getSections()) {
                EditableSection sec = new EditableSection();
                sec.id = secRes.getId();
                sec.title = secRes.getTitle();
                sec.description = secRes.getDescription();
                sec.duration = secRes.getDuration();
                
                if (secRes.getLessons() != null) {
                    for (LessonResponse lessRes : secRes.getLessons()) {
                        EditableLesson less = new EditableLesson();
                        less.id = lessRes.getId();
                        less.title = lessRes.getTitle();
                        less.duration = lessRes.getDuration();
                        less.lessonType = (lessRes.getLessonType() != null) ? lessRes.getLessonType().toUpperCase() : "VIDEO";
                        sec.lessons.add(less);
                    }
                }
                editableSections.add(sec);
            }
        }
        sectionAdapter.notifyDataSetChanged();
    }

    private void saveCourseData() {
        btnSaveInfo.setEnabled(false);
        Toast.makeText(this, "Đang lưu...", Toast.LENGTH_SHORT).show();
        
        UpdateCourseRequest updateReq = new UpdateCourseRequest();
        CreateCourseRequest createReq = new CreateCourseRequest();
        
        String title = etTitle.getText().toString();
        String desc = etDescription.getText().toString();
        Long price = 0L;
        Long discounted = 0L;
        try {
            if (etPrice.getText() != null && !etPrice.getText().toString().isEmpty()) price = Long.parseLong(etPrice.getText().toString());
            if (etDiscountedPrice.getText() != null && !etDiscountedPrice.getText().toString().isEmpty()) discounted = Long.parseLong(etDiscountedPrice.getText().toString());
        } catch (Exception e) { e.printStackTrace(); }

        String categoryId = null;
        if (!categoryList.isEmpty() && spinnerCategory.getSelectedItemPosition() >= 0) {
            categoryId = categoryList.get(spinnerCategory.getSelectedItemPosition()).getId();
        }

        boolean isPublished = switchPublish.isChecked();

        updateReq.setTitle(title);
        updateReq.setDescription(desc);
        updateReq.setPrice(price);
        updateReq.setDiscountedPrice(discounted);
        updateReq.setCategoryId(categoryId);
        updateReq.setIsPublished(isPublished);

        createReq.setTitle(title);
        createReq.setDescription(desc);
        createReq.setPrice(price);
        createReq.setDiscountedPrice(discounted);
        createReq.setCategoryId(categoryId);
        createReq.setIsPublished(isPublished);

        List<UpdateSectionRequest> updateSections = new ArrayList<>();
        List<CreateSectionRequest> createSections = new ArrayList<>();

        for (int i = 0; i < editableSections.size(); i++) {
            EditableSection sec = editableSections.get(i);
            
            // For Update
            UpdateSectionRequest usr = new UpdateSectionRequest();
            usr.setId(sec.id); // null if new
            usr.setTitle(sec.title);
            usr.setDescription(sec.description);
            usr.setDuration(sec.duration);
            usr.setOrderIndex(i);
            
            List<UpdateLessonRequest> ulrList = new ArrayList<>();
            for (int j = 0; j < sec.lessons.size(); j++) {
                EditableLesson less = sec.lessons.get(j);
                UpdateLessonRequest ulr = new UpdateLessonRequest();
                ulr.setId(less.id); // null if new
                ulr.setTitle(less.title);
                ulr.setDuration(less.duration);
                ulr.setOrderIndex(j);
                ulrList.add(ulr);
            }
            usr.setLessons(ulrList);
            updateSections.add(usr);
            
            // For Create
            CreateSectionRequest csr = new CreateSectionRequest();
            csr.setTitle(sec.title);
            csr.setDescription(sec.description);
            csr.setDuration(sec.duration);
            csr.setOrderIndex(i);
            List<CreateLessonRequest> clrList = new ArrayList<>();
            for (int j = 0; j < sec.lessons.size(); j++) {
                EditableLesson less = sec.lessons.get(j);
                CreateLessonRequest clr = new CreateLessonRequest();
                clr.setTitle(less.title);
                clr.setDuration(less.duration);
                clr.setLessonType(less.lessonType);
                clr.setOrderIndex(j);
                clrList.add(clr);
            }
            csr.setLessons(clrList);
            createSections.add(csr);
        }

        updateReq.setSections(updateSections);
        createReq.setSections(createSections);

        if (courseId != null) {
            // Update mode
            courseService.updateCourse(courseId, updateReq).enqueue(new Callback<ApiResponse<CourseResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CourseResponse>> call, retrofit2.Response<ApiResponse<CourseResponse>> response) {
                    btnSaveInfo.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(CourseEditActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CourseEditActivity.this, "Lỗi khi cập nhật.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<CourseResponse>> call, Throwable t) {
                    btnSaveInfo.setEnabled(true);
                    Toast.makeText(CourseEditActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create mode
            courseService.createCourse(createReq).enqueue(new Callback<ApiResponse<CourseResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CourseResponse>> call, retrofit2.Response<ApiResponse<CourseResponse>> response) {
                    btnSaveInfo.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(CourseEditActivity.this, "Tạo khóa học thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CourseEditActivity.this, "Lỗi khi tạo.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<CourseResponse>> call, Throwable t) {
                    btnSaveInfo.setEnabled(true);
                    Toast.makeText(CourseEditActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
