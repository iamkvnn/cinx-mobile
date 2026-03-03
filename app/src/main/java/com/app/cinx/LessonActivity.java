package com.app.cinx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.adapter.LessonSheetAdapter;
import com.app.cinx.adapter.QuizOptionAdapter;
import com.app.cinx.data.SampleCourseData;
import com.app.cinx.model.Chapter;
import com.app.cinx.model.Lesson;
import com.app.cinx.model.LessonType;
import com.app.cinx.model.QuizQuestion;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

/**
 * LessonActivity
 * ─────────────────────────────────────────────────────────────────────────
 * Hosts all three lesson types (VIDEO, DOCUMENT, QUIZ) in a single screen.
 * The appropriate view section is shown/hidden based on the lesson's type.
 *
 * Navigation:
 *  • Prev / Next buttons navigate through the flat lesson list
 *  • Curriculum bottom sheet lets the user jump to any unlocked lesson
 *
 * Architecture notes:
 *  • Data is read from SampleCourseData (swap with Repository in production)
 *  • Each lesson type is self-contained in its own bind method
 *  • Extend by overriding bindVideo / bindDocument / bindQuiz
 */
public class LessonActivity extends AppCompatActivity {

    // ── Intent extras ─────────────────────────────────────────────────────
    public static final String EXTRA_LESSON_ID = "extra_lesson_id";

    /** Convenience factory method; always prefer this over manual Intent construction. */
    public static Intent newIntent(Context context, int lessonId) {
        Intent intent = new Intent(context, LessonActivity.class);
        intent.putExtra(EXTRA_LESSON_ID, lessonId);
        return intent;
    }

    // ── Data ──────────────────────────────────────────────────────────────
    private List<Lesson> lessons;
    private List<Chapter> chapters;
    private int currentIndex = 0;   // index into the flat lessons list

    // ── Header views ──────────────────────────────────────────────────────
    private TextView tvChapterLabel;
    private TextView tvLessonTitle;
    private MaterialButton btnClose;
    private MaterialButton btnCurriculum;

    // ── Content containers (toggled by lesson type) ───────────────────────
    private View viewVideo;
    private View viewDocument;
    private View viewQuiz;

    // ── VIDEO views ───────────────────────────────────────────────────────
    private ImageView ivVideoThumbnail;
    private View btnVideoPlay;
    private ImageView ivPlayPauseIcon;
    private TextView tvVideoDuration;
    private TextView tvVideoMeta;
    private TextView tvVideoTitle;
    private TextView tvVideoChapter;
    private ProgressBar videoProgressBar;
    private boolean isVideoPlaying = false;

    // ── DOCUMENT views ────────────────────────────────────────────────────
    private TextView tvDocMeta;
    private WebView webViewDocument;

    // ── QUIZ views ────────────────────────────────────────────────────────
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
    private int selectedOptionIndex = -1;
    private CountDownTimer quizCountDownTimer;
    private static final long QUIZ_TIME_MILLIS = 15 * 60 * 1000L; // 15 minutes

    // ── Bottom action bar ─────────────────────────────────────────────────
    private MaterialButton btnPrevLesson;
    private MaterialButton btnPrimaryAction;

    // ── Bottom sheet ──────────────────────────────────────────────────────
    private BottomSheetBehavior<View> sheetBehavior;
    private View sheetOverlay;
    private LessonSheetAdapter sheetAdapter;
    private TextView tvSheetSubtitle;
    private TextView tvSheetProgress;

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        loadData();
        initViews();
        setupBottomSheet();
        setupBottomBar();

        // Determine which lesson to open
        int lessonId = getIntent().getIntExtra(EXTRA_LESSON_ID, -1);
        int startIndex = findLessonIndex(lessonId);
        loadLesson(startIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelQuizTimer();
        if (webViewDocument != null) {
            webViewDocument.destroy();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────────

    private void loadData() {
        chapters = SampleCourseData.getChapters();
        lessons  = SampleCourseData.getLessons();
    }

    private int findLessonIndex(int lessonId) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getId() == lessonId) return i;
        }
        return 0; // default to first lesson
    }

    // ─────────────────────────────────────────────────────────────────────
    // View initialisation
    // ─────────────────────────────────────────────────────────────────────

    private void initViews() {
        // Header
        tvChapterLabel  = findViewById(R.id.tvChapterLabel);
        tvLessonTitle   = findViewById(R.id.tvLessonTitle);
        btnClose        = findViewById(R.id.btnClose);
        btnCurriculum   = findViewById(R.id.btnCurriculum);

        btnClose.setOnClickListener(v -> onBackPressed());
        btnCurriculum.setOnClickListener(v -> toggleBottomSheet());

        // Content containers
        viewVideo    = findViewById(R.id.viewVideo);
        viewDocument = findViewById(R.id.viewDocument);
        viewQuiz     = findViewById(R.id.viewQuiz);

        // Video sub-views
        ivVideoThumbnail  = findViewById(R.id.ivVideoThumbnail);
        btnVideoPlay      = findViewById(R.id.btnVideoPlay);
        ivPlayPauseIcon   = findViewById(R.id.ivPlayPauseIcon);
        tvVideoDuration   = findViewById(R.id.tvVideoDuration);
        tvVideoMeta       = findViewById(R.id.tvVideoMeta);
        tvVideoTitle      = findViewById(R.id.tvVideoTitle);
        tvVideoChapter    = findViewById(R.id.tvVideoChapter);
        videoProgressBar  = findViewById(R.id.videoProgressBar);

        btnVideoPlay.setOnClickListener(v -> toggleVideoPlayback());

        // Document sub-views
        tvDocMeta        = findViewById(R.id.tvDocMeta);
        webViewDocument  = findViewById(R.id.webViewDocument);

        // Quiz sub-views
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

        // Bottom action bar
        btnPrevLesson   = findViewById(R.id.btnPrevLesson);
        btnPrimaryAction= findViewById(R.id.btnPrimaryAction);

        // Sheet overlay
        sheetOverlay = findViewById(R.id.sheetOverlay);
        sheetOverlay.setOnClickListener(v -> collapseBottomSheet());

        // Sheet header
        tvSheetSubtitle = findViewById(R.id.tvSheetSubtitle);
        tvSheetProgress = findViewById(R.id.tvSheetProgress);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Load lesson
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Central "controller" — called any time the active lesson changes.
     * Binds all data, shows/hides appropriate content views.
     */
    private void loadLesson(int index) {
        currentIndex = index;
        Lesson lesson = lessons.get(index);

        // Mark as active in data layer
        for (Lesson l : lessons) l.setActive(false);
        lesson.setActive(true);

        // Header
        tvChapterLabel.setText("Chương " + lesson.getChapterNumber());
        tvLessonTitle.setText(lesson.getChapterTitle());

        // Hide all content sections, then show the matching one
        viewVideo.setVisibility(View.GONE);
        viewDocument.setVisibility(View.GONE);
        viewQuiz.setVisibility(View.GONE);

        cancelQuizTimer(); // stop any running timer

        switch (lesson.getType()) {
            case VIDEO:
                viewVideo.setVisibility(View.VISIBLE);
                bindVideo(lesson);
                break;
            case DOCUMENT:
                viewDocument.setVisibility(View.VISIBLE);
                bindDocument(lesson);
                break;
            case QUIZ:
                viewQuiz.setVisibility(View.VISIBLE);
                bindQuiz(lesson);
                break;
        }

        updateBottomBarState(lesson);
        refreshSheetAdapter();
    }

    // ─────────────────────────────────────────────────────────────────────
    // VIDEO
    // ─────────────────────────────────────────────────────────────────────

    private void bindVideo(Lesson lesson) {
        isVideoPlaying = false;
        ivPlayPauseIcon.setImageResource(R.drawable.ic_play);
        videoProgressBar.setProgress(0);

        tvVideoTitle.setText(lesson.getTitle());
        tvVideoChapter.setText(lesson.getChapterTitle());
        tvVideoDuration.setText(lesson.getDuration());
        tvVideoMeta.setText(lesson.getDuration());

        if (lesson.getVideoThumbnailUrl() != null && !lesson.getVideoThumbnailUrl().isEmpty()) {
            Glide.with(this)
                    .load(lesson.getVideoThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_play_circle)
                    .into(ivVideoThumbnail);
        } else {
            ivVideoThumbnail.setImageResource(R.drawable.ic_play_circle);
        }
    }

    private void toggleVideoPlayback() {
        isVideoPlaying = !isVideoPlaying;
        if (isVideoPlaying) {
            ivPlayPauseIcon.setImageResource(R.drawable.ic_play); // swap to pause icon when available
        } else {
            ivPlayPauseIcon.setImageResource(R.drawable.ic_play);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // DOCUMENT
    // ─────────────────────────────────────────────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    private void bindDocument(Lesson lesson) {
        tvDocMeta.setText("Bài đọc • " + lesson.getDuration());

        WebSettings settings = webViewDocument.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        webViewDocument.setWebViewClient(new WebViewClient());
        webViewDocument.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        String html = buildStyledHtml(lesson.getDocumentHtml());
        webViewDocument.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    /** Wraps raw HTML content with app-matching typography styles. */
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

    // ─────────────────────────────────────────────────────────────────────
    // QUIZ
    // ─────────────────────────────────────────────────────────────────────

    private void bindQuiz(Lesson lesson) {
        currentQuestionIndex = 0;
        selectedOptionIndex = -1;

        List<QuizQuestion> questions = lesson.getQuestions();
        if (questions == null || questions.isEmpty()) return;

        updateSheetProgress();
        showQuestion(questions, currentQuestionIndex);
        startQuizTimer(QUIZ_TIME_MILLIS);

        // Bottom bar primary button becomes "Kiểm tra"
        btnPrimaryAction.setText("Kiểm tra đáp án");
    }

    private void showQuestion(List<QuizQuestion> questions, int index) {
        QuizQuestion question = questions.get(index);

        // Counter
        tvQuestionCounter.setText(
                String.format(Locale.getDefault(), "CÂU HỎI %d/%d", index + 1, questions.size()));

        // Progress bar
        int pct = (int) ((index + 1) / (float) questions.size() * 100);
        quizProgressBar.setProgress(pct);

        // Question text
        tvQuestionText.setText(question.getQuestionText());

        // Options adapter
        selectedOptionIndex = -1;
        layoutQuizFeedback.setVisibility(View.GONE);
        quizOptionAdapter = new QuizOptionAdapter(this, question.getOptions());
        quizOptionAdapter.setOnOptionSelectedListener((pos, option) -> {
            selectedOptionIndex = pos;
            // Visual "selected" state — highlight only (don't reveal yet)
            notifyOptionSelected(pos, question.getOptions().size());
        });
        rvQuizOptions.setAdapter(quizOptionAdapter);

        // Reset primary button label
        btnPrimaryAction.setText("Kiểm tra đáp án");
    }

    /** Apply selected visual state to the tapped option (pending reveal). */
    private void notifyOptionSelected(int selectedPos, int total) {
        // Re-bind with SELECTED state for the picked item only
        // QuizOptionAdapter handles state via revealAnswer; for "selected only" we
        // need a lightweight refresh. We'll mark the state directly.
        for (int i = 0; i < total; i++) {
            // Nothing visible yet — handled on "check" button
        }
    }

    private void checkQuizAnswer() {
        if (selectedOptionIndex < 0) {
            com.app.cinx.util.ToastUtil.showCustomToast(this, "Vui lòng chọn một đáp án!");
            return;
        }
        quizOptionAdapter.revealAnswer(selectedOptionIndex);

        // Show feedback
        boolean isCorrect = lessons.get(currentIndex)
                .getQuestions().get(currentQuestionIndex)
                .getOptions().get(selectedOptionIndex).isCorrect();

        layoutQuizFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            layoutQuizFeedback.setBackgroundResource(R.drawable.bg_quiz_option_correct);
            ivFeedbackIcon.setImageResource(R.drawable.ic_check_circle);
            ivFeedbackIcon.setColorFilter(getResources().getColor(android.R.color.holo_green_dark, null));
            tvFeedbackTitle.setText("Chính xác! 🎉");
            tvFeedbackTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
        } else {
            layoutQuizFeedback.setBackgroundResource(R.drawable.bg_quiz_option_wrong);
            ivFeedbackIcon.setImageResource(R.drawable.ic_close);
            ivFeedbackIcon.setColorFilter(getResources().getColor(R.color.error, null));
            tvFeedbackTitle.setText("Chưa đúng!");
            tvFeedbackTitle.setTextColor(getResources().getColor(R.color.error, null));
            // Show the correct answer text
            tvFeedbackDetail.setVisibility(View.VISIBLE);
            List<com.app.cinx.model.QuizOption> opts =
                    lessons.get(currentIndex).getQuestions().get(currentQuestionIndex).getOptions();
            for (com.app.cinx.model.QuizOption o : opts) {
                if (o.isCorrect()) {
                    tvFeedbackDetail.setText("Đáp án đúng: " + o.getText());
                    break;
                }
            }
        }

        // Switch button to "Câu tiếp theo"
        List<QuizQuestion> qs = lessons.get(currentIndex).getQuestions();
        boolean isLastQuestion = currentQuestionIndex >= qs.size() - 1;
        btnPrimaryAction.setText(isLastQuestion ? "Hoàn thành Quiz ✓" : "Câu tiếp theo →");
    }

    private void advanceToNextQuestion() {
        List<QuizQuestion> questions = lessons.get(currentIndex).getQuestions();
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showQuestion(questions, currentQuestionIndex);
        } else {
            // Quiz finished — mark lesson complete and go to next lesson
            lessons.get(currentIndex).setCompleted(true);
            navigateToNextLesson();
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
            }
        }.start();
    }

    private void cancelQuizTimer() {
        if (quizCountDownTimer != null) {
            quizCountDownTimer.cancel();
            quizCountDownTimer = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Bottom action bar
    // ─────────────────────────────────────────────────────────────────────

    private void setupBottomBar() {
        btnPrevLesson.setOnClickListener(v -> navigateToPrevLesson());
        btnPrimaryAction.setOnClickListener(v -> handlePrimaryAction());
    }

    private void handlePrimaryAction() {
        Lesson lesson = lessons.get(currentIndex);
        switch (lesson.getType()) {
            case VIDEO:
                lesson.setCompleted(true);
                navigateToNextLesson();
                break;
            case DOCUMENT:
                lesson.setCompleted(true);
                navigateToNextLesson();
                break;
            case QUIZ:
                if (quizOptionAdapter != null && quizOptionAdapter.isAnswered()) {
                    advanceToNextQuestion();
                } else {
                    checkQuizAnswer();
                }
                break;
        }
    }

    private void updateBottomBarState(Lesson lesson) {
        boolean hasPrev = currentIndex > 0;
        btnPrevLesson.setEnabled(hasPrev);
        btnPrevLesson.setAlpha(hasPrev ? 1f : 0.4f);

        // Button label for non-quiz types
        if (lesson.getType() != LessonType.QUIZ) {
            btnPrimaryAction.setText("Hoàn thành & Tiếp tục");
        }
    }

    private void navigateToPrevLesson() {
        if (currentIndex > 0) loadLesson(currentIndex - 1);
    }

    private void navigateToNextLesson() {
        int next = currentIndex + 1;
        if (next < lessons.size() && !lessons.get(next).isLocked()) {
            loadLesson(next);
        } else {
            // End of course or next is locked
            com.app.cinx.util.ToastUtil.showCustomToast(this, "Bạn đã hoàn thành bài học! 🎉");
            finish();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Curriculum bottom sheet
    // ─────────────────────────────────────────────────────────────────────

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

        // RecyclerView setup
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
        if (sheetAdapter == null) return;
        int activeLessonId = lessons.get(currentIndex).getId();
        sheetAdapter.setActiveLessonId(activeLessonId);
        updateSheetProgress();
    }

    private void updateSheetProgress() {
        int total = lessons.size();
        int completed = 0;
        for (Lesson l : lessons) if (l.isCompleted()) completed++;

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
