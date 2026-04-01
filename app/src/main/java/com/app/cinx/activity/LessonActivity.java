package com.app.cinx.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.LessonSheetAdapter;
import com.app.cinx.adapter.QuizOptionAdapter;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.ChooseQuizAnswerRequest;
import com.app.cinx.api.dto.CourseDetailResponse;
import com.app.cinx.api.dto.LessonResponse;
import com.app.cinx.api.dto.QuizOptionResponse;
import com.app.cinx.api.dto.QuizQuestionResponse;
import com.app.cinx.api.dto.QuizSessionResponse;
import com.app.cinx.api.dto.SectionResponse;
import com.app.cinx.api.dto.SubmitQuizSessionRequest;
import com.app.cinx.api.dto.TrackingVideoLessonRequest;
import com.app.cinx.api.dto.VideoLessonResponse;
import com.app.cinx.api.dto.ArticleLessonResponse;
import com.app.cinx.api.dto.PaginatedApiResponseQuizSessionQuestionResponse;
import com.app.cinx.api.dto.QuizSessionQuestionResponse;
import com.app.cinx.api.dto.LearningItemProgressResponse;
import com.app.cinx.api.dto.CheckEnrollmentStatus;
import com.app.cinx.utils.ToastUtil;
import com.app.cinx.utils.UserManager;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_LESSON_ID = "extra_lesson_id";

    public static Intent newIntent(Context context, String courseId, String lessonId) {
        Intent intent = new Intent(context, LessonActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_LESSON_ID, lessonId);
        return intent;
    }

    private String courseIdStr;
    private String currentLessonId;

    private List<LessonResponse> lessons = new ArrayList<>();
    private List<SectionResponse> chapters = new ArrayList<>();
    private int currentIndex = 0;

    private TextView tvChapterLabel;
    private TextView tvLessonTitle;
    private MaterialButton btnClose;
    private MaterialButton btnCurriculum;

    private View viewVideo;
    private View viewDocument;
    private View viewQuiz;

    private ImageView ivVideoThumbnail;
    private View btnVideoPlay;
    private ImageView ivPlayPauseIcon;
    private TextView tvVideoDuration;
    private TextView tvVideoTitle;
    private TextView tvVideoChapter;
    private ProgressBar videoProgressBar;
    private boolean isVideoPlaying = false;

    private android.os.Handler trackingHandler = new android.os.Handler();
    private Runnable trackingRunnable;
    private int currentVideoPositionSeconds = 0;

    private TextView tvDocMeta;
    private TextView tvDocumentTitle;
    private WebView webViewDocument;

    private TextView tvQuestionCounter;
    private TextView tvQuizTimer;
    private ProgressBar quizProgressBar;
    private TextView tvQuestionText;
    private RecyclerView rvQuizOptions;
    private View layoutQuizFeedback;
    private ImageView ivFeedbackIcon;
    private TextView tvFeedbackTitle;
    private TextView tvFeedbackDetail;

    private QuizOptionAdapter quizOptionAdapter;
    private int currentQuestionIndex = 0;
    private QuizOptionResponse selectedOption = null;
    private CountDownTimer quizCountDownTimer;
    private static final long QUIZ_TIME_MILLIS = 15 * 60 * 1000L;
    
    private String currentQuizSessionId = null;
    private List<QuizQuestionResponse> apiQuestions = new ArrayList<>();

    private MaterialButton btnPrevLesson;
    private MaterialButton btnPrimaryAction;

    private BottomSheetBehavior<View> sheetBehavior;
    private View sheetOverlay;
    private LessonSheetAdapter sheetAdapter;
    private TextView tvSheetSubtitle;
    private TextView tvSheetProgress;

    private CourseService courseService;
    private LearningService learningService;



    private String activeLessonId;
    private Set<String> completedLessonIds = new HashSet<>();
    private boolean isAllLocked = false;

    private android.widget.VideoView videoView;
    private View videoOverlay;
    private String currentVideoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        courseService = RetrofitClient.getInstance().getCourseService();
        learningService = RetrofitClient.getInstance().getLearningService();

        courseIdStr = getIntent().getStringExtra(EXTRA_COURSE_ID);
        currentLessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);

        initViews();
        setupBottomSheet();
        setupBottomBar();

        if (courseIdStr != null) {
            loadCourseData();
        } else {
            ToastUtil.showCustomToast(this, "Không tìm thấy thông tin khóa học!");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseVideo();
        cancelQuizTimer();
        if (webViewDocument != null) {
            webViewDocument.destroy();
        }
    }

    private void loadCourseData() {
        courseService.getCourseById(courseIdStr).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CourseDetailResponse detail = response.body().getData();
                    if (detail.getSections() != null) {
                        chapters = detail.getSections();
                        lessons.clear();
                        for (SectionResponse sec : chapters) {
                            if (sec.getLessons() != null) {
                                lessons.addAll(sec.getLessons());
                            }
                        }
                        
                        checkEnrollmentAndLoadProgress();
                    }
                } else {
                    ToastUtil.showCustomToast(LessonActivity.this, "Lỗi tải thông tin khóa học");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                ToastUtil.showCustomToast(LessonActivity.this, "Lỗi kết nối");
            }
        });
    }

    private void checkEnrollmentAndLoadProgress() {
        if (!UserManager.getInstance().isLoggedIn() || courseIdStr == null) {
            isAllLocked = true;
            setupLessonInitial();
            return;
        }

        List<String> ids = new ArrayList<>();
        ids.add(courseIdStr);

        RetrofitClient.getInstance().getEnrollmentService().checkEnrollmentStatus(ids).enqueue(new Callback<ApiResponse<List<CheckEnrollmentStatus>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CheckEnrollmentStatus>>> call, Response<ApiResponse<List<CheckEnrollmentStatus>>> response) {
                boolean enrolled = false;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<CheckEnrollmentStatus> statusList = response.body().getData();
                    if (!statusList.isEmpty()) {
                        Boolean st = statusList.get(0).getIsEnrolled();
                        enrolled = (st != null && st);
                    }
                }
                
                isAllLocked = !enrolled;
                if (!isAllLocked) {
                    fetchLearningProgress();
                } else {
                    setupLessonInitial();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CheckEnrollmentStatus>>> call, Throwable t) {
                isAllLocked = true;
                setupLessonInitial();
            }
        });
    }

    private void fetchLearningProgress() {
        learningService.getLearningItemProgressByCourseId(courseIdStr)
                .enqueue(new Callback<ApiResponse<List<LearningItemProgressResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<LearningItemProgressResponse>>> call, Response<ApiResponse<List<LearningItemProgressResponse>>> response) {
                        completedLessonIds.clear();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            for (LearningItemProgressResponse item : response.body().getData()) {
                                if (item.getIsCompleted() != null && item.getIsCompleted()) {
                                    completedLessonIds.add(item.getItemId());
                                }
                            }
                        }
                        setupLessonInitial();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<LearningItemProgressResponse>>> call, Throwable t) {
                        setupLessonInitial();
                    }
                });
    }

    private void setupLessonInitial() {
        if (isAllLocked) {
            ToastUtil.showCustomToast(this, "Bài học chưa mở khóa (chưa mua/chưa đăng nhập)");
            finish();
            return;
        }
        
        currentIndex = findLessonIndex(currentLessonId);
        if (currentIndex >= 0 && currentIndex < lessons.size()) {
            loadLesson(currentIndex);
        }
        
        if (sheetAdapter != null) {
            sheetAdapter.setChapters(chapters);
            refreshSheetAdapter();
        }
    }

    private int findLessonIndex(String id) {
        if (id == null) return 0;
        for (int i = 0; i < lessons.size(); i++) {
            if (id.equals(lessons.get(i).getId())) return i;
        }
        return 0;
    }

    private void initViews() {
        tvChapterLabel  = findViewById(R.id.tvChapterLabel);
        tvLessonTitle   = findViewById(R.id.tvLessonTitle);
        btnClose        = findViewById(R.id.btnClose);
        btnCurriculum   = findViewById(R.id.btnCurriculum);

        btnClose.setOnClickListener(v -> onBackPressed());
        btnCurriculum.setOnClickListener(v -> toggleBottomSheet());

        viewVideo    = findViewById(R.id.viewVideo);
        viewDocument = findViewById(R.id.viewDocument);
        viewQuiz     = findViewById(R.id.viewQuiz);

        ivVideoThumbnail  = findViewById(R.id.ivVideoThumbnail);
        btnVideoPlay      = findViewById(R.id.btnVideoPlay);
        ivPlayPauseIcon   = findViewById(R.id.ivPlayPauseIcon);
        tvVideoDuration   = findViewById(R.id.tvVideoDuration);
        tvVideoTitle      = findViewById(R.id.tvVideoTitle);
        tvVideoChapter    = findViewById(R.id.tvVideoChapter);
        videoProgressBar  = findViewById(R.id.videoProgressBar);
        
        videoView         = findViewById(R.id.videoView);
        videoOverlay      = findViewById(R.id.ivVideoThumbnail); // Optional, we can just cast it or find dark overlay

        btnVideoPlay.setOnClickListener(v -> toggleVideoPlayback());

        tvDocMeta        = findViewById(R.id.tvDocMeta);
        tvDocumentTitle  = findViewById(R.id.tvDocumentTitle);
        webViewDocument  = findViewById(R.id.webViewDocument);

        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        tvQuizTimer       = findViewById(R.id.tvQuizTimer);
        quizProgressBar   = findViewById(R.id.quizProgressBar);
        tvQuestionText    = findViewById(R.id.tvQuestionText);
        rvQuizOptions     = findViewById(R.id.rvQuizOptions);
        layoutQuizFeedback= findViewById(R.id.layoutQuizFeedback);
        ivFeedbackIcon    = findViewById(R.id.ivFeedbackIcon);
        tvFeedbackTitle   = findViewById(R.id.tvFeedbackTitle);
        tvFeedbackDetail  = findViewById(R.id.tvFeedbackDetail);

        rvQuizOptions.setLayoutManager(new LinearLayoutManager(this));
        rvQuizOptions.setNestedScrollingEnabled(false);

        btnPrevLesson   = findViewById(R.id.btnPrevLesson);
        btnPrimaryAction= findViewById(R.id.btnPrimaryAction);

        sheetOverlay = findViewById(R.id.sheetOverlay);
        sheetOverlay.setOnClickListener(v -> collapseBottomSheet());

        tvSheetSubtitle = findViewById(R.id.tvSheetSubtitle);
        tvSheetProgress = findViewById(R.id.tvSheetProgress);
    }

    private void loadLesson(int index) {
        pauseVideo();
        currentIndex = index;
        LessonResponse lesson = lessons.get(index);
        currentLessonId = lesson.getId();

        activeLessonId = lesson.getId();

        tvChapterLabel.setText("Bài học:");
        tvLessonTitle.setText(lesson.getTitle());

        viewVideo.setVisibility(View.GONE);
        viewDocument.setVisibility(View.GONE);
        viewQuiz.setVisibility(View.GONE);

        cancelQuizTimer();

        String typeStr = lesson.getLessonType() != null ? lesson.getLessonType() : "";
        if (typeStr.equalsIgnoreCase("VIDEO")) {
            viewVideo.setVisibility(View.VISIBLE);
            bindVideo(lesson);
        } else if (typeStr.equalsIgnoreCase("DOCUMENT") || typeStr.equalsIgnoreCase("ARTICLE")) {
            viewDocument.setVisibility(View.VISIBLE);
            bindDocument(lesson);
        } else if (typeStr.equalsIgnoreCase("QUIZ")) {
            viewQuiz.setVisibility(View.VISIBLE);
            bindQuiz(lesson);
        } else {
            viewDocument.setVisibility(View.VISIBLE);
            bindDocument(lesson);
        }

        updateBottomBarState(lesson);
        refreshSheetAdapter();
    }

    private void bindVideo(LessonResponse lesson) {
        isVideoPlaying = false;
        currentVideoUrl = null;
        videoView.setVisibility(View.GONE);
        ivVideoThumbnail.setVisibility(View.VISIBLE);
        
        ivPlayPauseIcon.setImageResource(R.drawable.ic_play);
        videoProgressBar.setProgress(0);
        currentVideoPositionSeconds = 0;

        tvVideoTitle.setText(lesson.getTitle());
        tvVideoChapter.setText("");
        tvVideoDuration.setText(lesson.getDuration() != null ? (lesson.getDuration() / 60) + " phút" : "0 phút");

        ivVideoThumbnail.setImageResource(R.drawable.ic_play_circle);
        
        courseService.getVideoByLessonId(lesson.getId()).enqueue(new Callback<ApiResponse<VideoLessonResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<VideoLessonResponse>> call, Response<ApiResponse<VideoLessonResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    VideoLessonResponse videoData = response.body().getData();
                    currentVideoUrl = videoData.getVideoUrl();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<VideoLessonResponse>> call, Throwable t) {}
        });
    }

    private void toggleVideoPlayback() {
        if (currentVideoUrl == null) {
            ToastUtil.showCustomToast(this, "Video đang tải hoặc không có sẵn");
            return;
        }

        if (isVideoPlaying) {
            pauseVideo();
        } else {
            playVideo();
        }
    }

    private void playVideo() {
        isVideoPlaying = true;
        ivPlayPauseIcon.setImageResource(R.drawable.ic_pause);
        ivVideoThumbnail.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        if (!videoView.isPlaying()) {
            if (currentVideoPositionSeconds == 0) {
                videoView.setVideoPath(currentVideoUrl);
                android.widget.MediaController mediaController = new android.widget.MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.setOnPreparedListener(mp -> {
                    mp.start();
                    videoProgressBar.setMax(mp.getDuration() / 1000);
                });
                videoView.setOnCompletionListener(mp -> {
                    pauseVideo();
                    completedLessonIds.add(currentLessonId);
                    handlePrimaryAction();
                });
            } else {
                videoView.start();
            }
        }
        
        if (trackingRunnable == null) {
            trackingRunnable = new Runnable() {
                @Override
                public void run() {
                    currentVideoPositionSeconds += 10;
                    if (videoView.isPlaying()) {
                        videoProgressBar.setProgress(videoView.getCurrentPosition() / 1000);
                        currentVideoPositionSeconds = videoView.getCurrentPosition() / 1000;
                    }
                    
                    TrackingVideoLessonRequest req = new TrackingVideoLessonRequest();
                    req.setVideoLessonId(currentLessonId); // Need video id really, fallback to lesson id
                    req.setCurrentPosition(currentVideoPositionSeconds);
                    
                    learningService.trackVideoProgress(req).enqueue(new Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {}
                        @Override
                        public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {}
                    });
                    
                    trackingHandler.postDelayed(this, 10000);
                }
            };
        }
        trackingHandler.postDelayed(trackingRunnable, 10000);
    }
    
    private void pauseVideo() {
        isVideoPlaying = false;
        if (ivPlayPauseIcon != null) {
            ivPlayPauseIcon.setImageResource(R.drawable.ic_play);
        }
        
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        
        if (trackingHandler != null && trackingRunnable != null) {
            trackingHandler.removeCallbacks(trackingRunnable);
            trackingRunnable = null;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void bindDocument(LessonResponse lesson) {
        tvDocMeta.setText("Bài đọc • " + (lesson.getDuration() != null ? (lesson.getDuration()/60)+" phút" : ""));
        tvDocumentTitle.setText(lesson.getTitle());

        WebSettings settings = webViewDocument.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        webViewDocument.setWebViewClient(new WebViewClient());
        webViewDocument.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        
        courseService.getArticleByLessonId(lesson.getId()).enqueue(new Callback<ApiResponse<ArticleLessonResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArticleLessonResponse>> call, Response<ApiResponse<ArticleLessonResponse>> response) {
                String content = "";
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    content = response.body().getData().getContent();
                }
                if (content == null) content = "<p>Nội dung trống</p>";
                String html = buildStyledHtml(content);
                webViewDocument.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
            @Override
            public void onFailure(Call<ApiResponse<ArticleLessonResponse>> call, Throwable t) {
                String html = buildStyledHtml("<p>Lỗi kết nối</p>");
                webViewDocument.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
        });
    }

    private String buildStyledHtml(String bodyContent) {
        return "<!DOCTYPE html><html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<style>"
                + "body{font-family:-apple-system,sans-serif;font-size:15px;color:#1E293B;"
                + "line-height:1.7;padding:0;margin:0;background:transparent;}"
                + "h1{font-size:22px;font-weight:800;margin-top:8px;margin-bottom:12px;color:#0F172A;}"
                + "h2{font-size:17px;font-weight:700;margin-top:24px;margin-bottom:8px;color:#1E293B;"
                + "border-left:4px solid #7C3AED;padding-left:10px;}"
                + "p{margin-bottom:12px;color:#374151;}"
                + "ul{padding-left:20px;margin-bottom:12px;}"
                + "li{margin-bottom:6px;color:#374151;}"
                + "b{color:#1E293B;}"
                + "blockquote{background:#F5F3FF;border-left:4px solid #7C3AED;"
                + "margin:16px 0;padding:12px 16px;border-radius:0 12px 12px 0;color:#374151;}"
                + "</style></head><body>"
                + bodyContent
                + "</body></html>";
    }

    private void bindQuiz(LessonResponse lesson) {
        currentQuestionIndex = 0;
        selectedOption = null;
        layoutQuizFeedback.setVisibility(View.GONE);
        tvQuestionText.setText("Đang tải...");
        rvQuizOptions.setAdapter(null);
        btnPrimaryAction.setText("Câu tiếp theo");
        btnPrimaryAction.setEnabled(false);
        
        learningService.createQuizSession(lesson.getId()).enqueue(new Callback<ApiResponse<QuizSessionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<QuizSessionResponse>> call, Response<ApiResponse<QuizSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentQuizSessionId = response.body().getData().getId();
                    loadQuizQuestions();
                } else {
                    ToastUtil.showCustomToast(LessonActivity.this, "Lỗi tạo session quiz");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<QuizSessionResponse>> call, Throwable t) {
                ToastUtil.showCustomToast(LessonActivity.this, "Lỗi kết nối tạo session");
            }
        });
        
        updateSheetProgress();
    }
    
    private void loadQuizQuestions() {
        learningService.getQuizSessionQuestions(currentQuizSessionId, null, 100).enqueue(new Callback<PaginatedApiResponseQuizSessionQuestionResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseQuizSessionQuestionResponse> call, Response<PaginatedApiResponseQuizSessionQuestionResponse> response) {
                // Because QuizSessionQuestionResponse doesn't have the text of options / titles
                // Let's call CourseService to get actual info
                courseService.getQuizByLessonId(currentLessonId).enqueue(new Callback<ApiResponse<com.app.cinx.api.dto.QuizLessonResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<com.app.cinx.api.dto.QuizLessonResponse>> call, Response<ApiResponse<com.app.cinx.api.dto.QuizLessonResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            apiQuestions = response.body().getData().getQuestions();
                            if (apiQuestions != null && !apiQuestions.isEmpty()) {
                                showQuestion(0);
                                btnPrimaryAction.setEnabled(true);
                                startQuizTimer(QUIZ_TIME_MILLIS);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<com.app.cinx.api.dto.QuizLessonResponse>> call, Throwable t) {}
                });
            }
            @Override
            public void onFailure(Call<PaginatedApiResponseQuizSessionQuestionResponse> call, Throwable t) {}
        });
    }

    private void showQuestion(int index) {
        if (apiQuestions == null || index >= apiQuestions.size()) return;
        
        QuizQuestionResponse question = apiQuestions.get(index);

        tvQuestionCounter.setText(String.format(Locale.getDefault(), "CÂU HỎI %d/%d", index + 1, apiQuestions.size()));

        int pct = (int) ((index + 1) / (float) apiQuestions.size() * 100);
        quizProgressBar.setProgress(pct);

        tvQuestionText.setText(question.getQuestionText() != null ? question.getQuestionText() : "");

        selectedOption = null;
        layoutQuizFeedback.setVisibility(View.GONE);
        
        List<QuizOptionResponse> opts = question.getOptions() != null ? question.getOptions() : new ArrayList<>();
        quizOptionAdapter = new QuizOptionAdapter(this, opts);
        quizOptionAdapter.setOnOptionSelectedListener((pos, option) -> {
            selectedOption = option;
            quizOptionAdapter.selectOption(pos);
            btnPrimaryAction.setText(currentQuestionIndex == apiQuestions.size() - 1 ? "Nộp bài" : "Câu tiếp theo");
        });
        rvQuizOptions.setAdapter(quizOptionAdapter);

        btnPrimaryAction.setText(index == apiQuestions.size() - 1 ? "Nộp bài" : "Câu tiếp theo");
    }

    private void handlePrimaryAction() {
        LessonResponse lesson = lessons.get(currentIndex);
        String t = lesson.getLessonType() != null ? lesson.getLessonType() : "";
        if (t.equalsIgnoreCase("VIDEO") || t.equalsIgnoreCase("DOCUMENT") || t.equalsIgnoreCase("ARTICLE")) {
            completedLessonIds.add(lesson.getId());
            navigateToNextLesson();
        } else if (t.equalsIgnoreCase("QUIZ")) {
            if (selectedOption != null) {
                ChooseQuizAnswerRequest req = new ChooseQuizAnswerRequest();
                req.setQuestionId(apiQuestions.get(currentQuestionIndex).getId());
                req.setUserAnswer(selectedOption.getOptionText());
                learningService.chooseQuizSessionQuestion(currentQuizSessionId, req).enqueue(new Callback<ApiResponse<Object>>() {
                   @Override
                   public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {}
                   @Override
                   public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {}
                });
            }
            
            if (currentQuestionIndex < apiQuestions.size() - 1) {
                currentQuestionIndex++;
                showQuestion(currentQuestionIndex);
            } else {
                SubmitQuizSessionRequest submitReq = new SubmitQuizSessionRequest();
                submitReq.setAnswers(new ArrayList<>());
                learningService.submitQuizSession(currentQuizSessionId, submitReq).enqueue(new Callback<ApiResponse<QuizSessionResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<QuizSessionResponse>> call, Response<ApiResponse<QuizSessionResponse>> response) {
                        layoutQuizFeedback.setVisibility(View.VISIBLE);
                        layoutQuizFeedback.setBackgroundResource(R.drawable.bg_quiz_option_correct);
                        tvFeedbackTitle.setText("Hoàn thành! Đang chuyển bài...");
                        
                        btnPrimaryAction.setText("Bài tiếp theo");
                        btnPrimaryAction.setOnClickListener(v -> navigateToNextLesson());
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<QuizSessionResponse>> call, Throwable t) {
                        ToastUtil.showCustomToast(LessonActivity.this, "Lỗi nộp bài");
                    }
                });
            }
        }
    }

    private void startQuizTimer(long millisInFuture) {
        cancelQuizTimer();
        quizCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvQuizTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }
            @Override public void onFinish() {
                tvQuizTimer.setText("00:00");
                handlePrimaryAction(); 
            }
        }.start();
    }

    private void cancelQuizTimer() {
        if (quizCountDownTimer != null) {
            quizCountDownTimer.cancel();
            quizCountDownTimer = null;
        }
    }

    private void setupBottomBar() {
        btnPrevLesson.setOnClickListener(v -> navigateToPrevLesson());
        btnPrimaryAction.setOnClickListener(v -> handlePrimaryAction());
    }

    private void updateBottomBarState(LessonResponse lesson) {
        boolean hasPrev = currentIndex > 0;
        btnPrevLesson.setEnabled(hasPrev);
        btnPrevLesson.setAlpha(hasPrev ? 1f : 0.4f);

        String t = lesson.getLessonType() != null ? lesson.getLessonType() : "";
        if (!t.equalsIgnoreCase("QUIZ")) {
            btnPrimaryAction.setText("Hoàn thành & Tiếp tục");
        }
    }

    private void navigateToPrevLesson() {
        if (currentIndex > 0) loadLesson(currentIndex - 1);
    }

    private void navigateToNextLesson() {
        int next = currentIndex + 1;
        if (next < lessons.size() && !isAllLocked) {
            loadLesson(next);
        } else {
            ToastUtil.showCustomToast(this, "Bạn đã hoàn thành bài học! 🎉");
            finish();
        }
    }

    private void setupBottomSheet() {
        View bottomSheet = findViewById(R.id.bottomSheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetBehavior.setSkipCollapsed(true);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(@androidx.annotation.NonNull View view, int newState) {
                boolean visible = newState != BottomSheetBehavior.STATE_HIDDEN;
                sheetOverlay.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
            @Override public void onSlide(@androidx.annotation.NonNull View view, float offset) {}
        });

        RecyclerView rvCurriculum = findViewById(R.id.rvCurriculum);
        sheetAdapter = new LessonSheetAdapter(this);
        sheetAdapter.setChapters(chapters);
        sheetAdapter.setOnLessonClickListener(lesson -> {
            int idx = findLessonIndex(lesson.getId());
            loadLesson(idx);
            collapseBottomSheet();
        });
        rvCurriculum.setLayoutManager(new LinearLayoutManager(this));
        rvCurriculum.setAdapter(sheetAdapter);

        updateSheetProgress();
    }

    private void refreshSheetAdapter() {
        if (sheetAdapter == null || currentIndex >= lessons.size()) return;
        String activeLessonId = lessons.get(currentIndex).getId();
        sheetAdapter.setActiveLessonId(activeLessonId);
        updateSheetProgress();
    }

    private void updateSheetProgress() {
        int total = lessons.size();
        if (total == 0) return;
        int completed = 0;
        for (LessonResponse l : lessons) if (completedLessonIds.contains(l.getId())) completed++;

        if (tvSheetSubtitle != null) {
            tvSheetSubtitle.setText(total + " bài học • Hoàn thành "
                    + (int)(completed * 100f / total) + "%");
        }
        if (tvSheetProgress != null) {
            tvSheetProgress.setText(completed + "/" + total);
        }
    }

    private void toggleBottomSheet() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            collapseBottomSheet();
        }
    }

    private void collapseBottomSheet() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
