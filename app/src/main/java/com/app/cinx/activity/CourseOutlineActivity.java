package com.app.cinx.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.InstructorSectionAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.LessonResponse;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.UpdateCourseRequest;
import com.app.cinx.api.dto.UpdateLessonRequest;
import com.app.cinx.api.dto.UpdateSectionRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseOutlineActivity extends AppCompatActivity {

    private String courseId;
    private Toolbar toolbarOutline;
    private RecyclerView rvSections;
    private Button btnAddSection;
    private InstructorSectionAdapter sectionAdapter;

    private CourseDetailResponse currentCourseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_outline);

        courseId = getIntent().getStringExtra("COURSE_ID");

        toolbarOutline = findViewById(R.id.toolbarOutline);
        rvSections = findViewById(R.id.rvSections);
        btnAddSection = findViewById(R.id.btnAddSection);

        setSupportActionBar(toolbarOutline);
        toolbarOutline.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();

        btnAddSection.setOnClickListener(v -> showAddSectionDialog());

        loadCourseData();
    }

    private void setupRecyclerView() {
        sectionAdapter = new InstructorSectionAdapter(this, new ArrayList<>(), new InstructorSectionAdapter.OnSectionClickListener() {
            @Override
            public void onEditSection(SectionResponse section) {
                showEditSectionDialog(section);
            }

            @Override
            public void onDeleteSection(SectionResponse section) {
                confirmDeleteSection(section);
            }

            @Override
            public void onAddLesson(SectionResponse section) {
                showAddLessonDialog(section);
            }

            @Override
            public void onEditLesson(SectionResponse section, LessonResponse lesson) {
                showEditLessonDialog(section, lesson);
            }

            @Override
            public void onLessonItemClick(SectionResponse section, LessonResponse lesson) {
                Intent intent = new Intent(CourseOutlineActivity.this, LessonEditorActivity.class);
                intent.putExtra("LESSON_ID", lesson.getId());
                intent.putExtra("LESSON_TYPE", lesson.getLessonType() != null ? lesson.getLessonType() : "ARTICLE");
                intent.putExtra("LESSON_TITLE", lesson.getTitle());
                startActivity(intent);
            }

            @Override
            public void onDeleteLesson(SectionResponse section, LessonResponse lesson) {
                confirmDeleteLesson(section, lesson);
            }
        });
        rvSections.setLayoutManager(new LinearLayoutManager(this));
        rvSections.setAdapter(sectionAdapter);
    }

    private void loadCourseData() {
        if (courseId == null) return;
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.getCourseById(courseId).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentCourseData = response.body().getData();
                    sectionAdapter.updateData(currentCourseData.getSections());
                } else {
                    Toast.makeText(CourseOutlineActivity.this, "L?i t?i d? li?u", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                Toast.makeText(CourseOutlineActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private UpdateCourseRequest buildUpdateReq() {
        UpdateCourseRequest req = new UpdateCourseRequest();
        if (currentCourseData != null) {
            req.setTitle(currentCourseData.getTitle());
            req.setDescription(currentCourseData.getDescription());
            req.setCategoryId(currentCourseData.getCategory() != null ? currentCourseData.getCategory() : ""); // Note: Might need better category map
            req.setPrice(currentCourseData.getPrice());
            req.setDiscountedPrice(currentCourseData.getDiscountedPrice());
            req.setIsPublished(currentCourseData.getIsPublished());
            req.setIsInSubscription(currentCourseData.getIsInSubscription());
            req.setDuration(currentCourseData.getDuration());
            req.setHasCertificate(currentCourseData.getHasCertificate());
            req.setCertificateTitle(currentCourseData.getCertificateTitle());

            List<UpdateSectionRequest> sections = new ArrayList<>();
            if (currentCourseData.getSections() != null) {
                for (SectionResponse sr : currentCourseData.getSections()) {
                    UpdateSectionRequest us = new UpdateSectionRequest();
                    us.setId(sr.getId());
                    us.setTitle(sr.getTitle());
                    us.setDescription(sr.getDescription());
                    us.setDuration(sr.getDuration());
                    us.setOrderIndex(sr.getOrderIndex());

                    List<UpdateLessonRequest> lessons = new ArrayList<>();
                    if (sr.getLessons() != null) {
                        for (LessonResponse lr : sr.getLessons()) {
                            UpdateLessonRequest ul = new UpdateLessonRequest();
                            ul.setId(lr.getId());
                            ul.setTitle(lr.getTitle());
                            ul.setDuration(lr.getDuration());
                            ul.setOrderIndex(lr.getOrderIndex());
                            lessons.add(ul);
                        }
                    }
                    us.setLessons(lessons);
                    sections.add(us);
                }
            }
            req.setSections(sections);
        }
        return req;
    }

    private void saveToServer(UpdateCourseRequest req) {
        CourseService courseService = RetrofitClient.getInstance().getCourseService();
        courseService.updateCourse(courseId, req).enqueue(new Callback<ApiResponse<com.app.cinx.api.dto.CourseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.app.cinx.api.dto.CourseResponse>> call, Response<ApiResponse<com.app.cinx.api.dto.CourseResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseOutlineActivity.this, "�? l�u", Toast.LENGTH_SHORT).show();
                    loadCourseData();
                } else {
                    Toast.makeText(CourseOutlineActivity.this, "L?i khi l�u", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.app.cinx.api.dto.CourseResponse>> call, Throwable t) {
                Toast.makeText(CourseOutlineActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddSectionDialog() {
        if (currentCourseData == null) return;
        EditText input = new EditText(this);
        input.setHint("T�n ch��ng");
        new AlertDialog.Builder(this)
                .setTitle("Them ch��ng m?i")
                .setView(input)
                .setPositiveButton("Them", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        UpdateCourseRequest req = buildUpdateReq();
                        UpdateSectionRequest newSec = new UpdateSectionRequest();
                        newSec.setTitle(title);
                        newSec.setOrderIndex(req.getSections() == null ? 0 : req.getSections().size());
                        newSec.setLessons(new ArrayList<>());
                        
                        List<UpdateSectionRequest> currentSections = req.getSections();
                        if (currentSections == null) currentSections = new ArrayList<>();
                        currentSections.add(newSec);
                        req.setSections(currentSections);
                        
                        saveToServer(req);
                    }
                })
                .setNegativeButton("H?y", null)
                .show();
    }

    private void showEditSectionDialog(SectionResponse section) {
        EditText input = new EditText(this);
        input.setText(section.getTitle());
        new AlertDialog.Builder(this)
                .setTitle("S?a t�n ch��ng")
                .setView(input)
                .setPositiveButton("L�u", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        UpdateCourseRequest req = buildUpdateReq();
                        for (UpdateSectionRequest us : req.getSections()) {
                            if (us.getId() != null && us.getId().equals(section.getId())) {
                                us.setTitle(title);
                            }
                        }
                        saveToServer(req);
                    }
                })
                .setNegativeButton("H?y", null)
                .show();
    }

    private void confirmDeleteSection(SectionResponse section) {
        new AlertDialog.Builder(this)
                .setTitle("X�a ch��ng n�y?")
                .setMessage("T?t c? b�i h?c trong ch��ng c?ng s? b? x�a.")
                .setPositiveButton("X�a", (dialog, which) -> {
                    UpdateCourseRequest req = buildUpdateReq();
                    if (req.getSections() != null) {
                        req.getSections().removeIf(s -> s.getId() != null && s.getId().equals(section.getId()));
                        saveToServer(req);
                    }
                })
                .setNegativeButton("H?y", null)
                .show();
    }

    private void showAddLessonDialog(SectionResponse section) {
        String[] types = {"Video", "Article", "Quiz", "Assignment"};
        new AlertDialog.Builder(this)
                .setTitle("Chon loai bai hoc")
                .setItems(types, (dialog1, which1) -> {
                    String selectedType = "VIDEO";
                    if(which1 == 1) selectedType = "ARTICLE";
                    if(which1 == 2) selectedType = "QUIZ";
                    if(which1 == 3) selectedType = "ASSIGNMENT";
                    String finalSelectedType = selectedType;
                    
                    EditText input = new EditText(this);
                    input.setHint("Ten bai hoc");
                    new AlertDialog.Builder(this)
                            .setTitle("Them bai hoc (" + finalSelectedType + ")")
                            .setView(input)
                            .setPositiveButton("Them", (dialog2, which2) -> {
                                String title = input.getText().toString().trim();
                                if (!title.isEmpty()) {
                                    UpdateCourseRequest req = buildUpdateReq();
                                    for (UpdateSectionRequest us : req.getSections()) {
                                        if (us.getId() != null && us.getId().equals(section.getId())) {
                                            UpdateLessonRequest newLess = new UpdateLessonRequest();
                                            newLess.setTitle(title);
                                            newLess.setLessonType(finalSelectedType);
                                            newLess.setOrderIndex(us.getLessons() == null ? 0 : us.getLessons().size());
                                            if (us.getLessons() == null) {
                                                us.setLessons(new java.util.ArrayList<>());
                                            }
                                            us.getLessons().add(newLess);
                                        }
                                    }
                                    saveToServer(req);
                                }
                            })
                            .setNegativeButton("Huy", null)
                            .show();
                }).show();
    }

    private void showEditLessonDialog(SectionResponse section, LessonResponse lesson) {
        EditText input = new EditText(this);
        input.setText(lesson.getTitle());
        new AlertDialog.Builder(this)
                .setTitle("S?a Ten bai hoc")
                .setView(input)
                .setPositiveButton("L�u", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        UpdateCourseRequest req = buildUpdateReq();
                        for (UpdateSectionRequest us : req.getSections()) {
                            if (us.getId() != null && us.getId().equals(section.getId()) && us.getLessons() != null) {
                                for (UpdateLessonRequest ul : us.getLessons()) {
                                    if (ul.getId() != null && ul.getId().equals(lesson.getId())) {
                                        ul.setTitle(title);
                                    }
                                }
                            }
                        }
                        saveToServer(req);
                    }
                })
                .setNegativeButton("H?y", null)
                .show();
    }

    private void confirmDeleteLesson(SectionResponse section, LessonResponse lesson) {
        new AlertDialog.Builder(this)
                .setTitle("X�a b�i h?c?")
                .setPositiveButton("X�a", (dialog, which) -> {
                    UpdateCourseRequest req = buildUpdateReq();
                    if (req.getSections() != null) {
                        for (UpdateSectionRequest us : req.getSections()) {
                            if (us.getId() != null && us.getId().equals(section.getId()) && us.getLessons() != null) {
                                us.getLessons().removeIf(l -> l.getId() != null && l.getId().equals(lesson.getId()));
                            }
                        }
                        saveToServer(req);
                    }
                })
                .setNegativeButton("H?y", null)
                .show();
    }
}
