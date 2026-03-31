package com.app.cinx.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.cinx.R;
import com.app.cinx.api.CourseService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.ArticleLessonResponse;
import com.app.cinx.api.dto.CreateArticleLessonRequest;
import com.app.cinx.api.dto.CreateVideoLessonRequest;
import com.app.cinx.api.dto.PresignedUrlResponse;
import com.app.cinx.api.dto.VideoLessonResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonEditorActivity extends AppCompatActivity {

    private String lessonId;
    private String lessonType;
    private String lessonTitle;

    private LinearLayout llArticleEditor, llVideoEditor;
    private EditText etArticleContent;
    private TextView tvCurrentVideo, tvUploadStatus;
    private Button btnPickVideo, btnSaveContent;
    private ProgressBar pbVideoUpload;

    private Uri selectedVideoUri = null;
    private String selectedFileName = "";
    private long selectedFileSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_editor);

        lessonId = getIntent().getStringExtra("LESSON_ID");
        lessonType = getIntent().getStringExtra("LESSON_TYPE");
        lessonTitle = getIntent().getStringExtra("LESSON_TITLE");

        Toolbar toolbar = findViewById(R.id.toolbarLessonEditor);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvEditorTitle);
        if (lessonTitle != null) { tvTitle.setText(lessonTitle); } else { tvTitle.setText("Lesson Editor"); }

        llArticleEditor = findViewById(R.id.llArticleEditor);
        llVideoEditor = findViewById(R.id.llVideoEditor);
        etArticleContent = findViewById(R.id.etArticleContent);
        tvCurrentVideo = findViewById(R.id.tvCurrentVideo);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnSaveContent = findViewById(R.id.btnSaveContent);
        pbVideoUpload = findViewById(R.id.pbVideoUpload);

        setupEditor();
        btnSaveContent.setOnClickListener(v -> saveContent());
    }

    private void setupEditor() {
        if ("ARTICLE".equalsIgnoreCase(lessonType)) {
            llArticleEditor.setVisibility(View.VISIBLE);
            loadArticleData();
        } else if ("VIDEO".equalsIgnoreCase(lessonType)) {
            llVideoEditor.setVisibility(View.VISIBLE);
            loadVideoData();
            btnPickVideo.setOnClickListener(v -> pickVideo());
        } else {
            Toast.makeText(this, "Type not supported yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadArticleData() {
        CourseService service = RetrofitClient.getInstance().create(CourseService.class);
        service.getArticleByLessonId(lessonId).enqueue(new Callback<ApiResponse<ArticleLessonResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArticleLessonResponse>> call, Response<ApiResponse<ArticleLessonResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    etArticleContent.setText(response.body().getData().getContent());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArticleLessonResponse>> call, Throwable t) {}
        });
    }

    private void loadVideoData() {
        CourseService service = RetrofitClient.getInstance().create(CourseService.class);
        service.getVideoByLessonId(lessonId).enqueue(new Callback<ApiResponse<VideoLessonResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<VideoLessonResponse>> call, Response<ApiResponse<VideoLessonResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    tvCurrentVideo.setText("Video hien tai: " + response.body().getData().getFileName());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<VideoLessonResponse>> call, Throwable t) {}
        });
    }

    private final ActivityResultLauncher<Intent> pickVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedVideoUri = result.getData().getData();
                    extractFileInfo(selectedVideoUri);
                    tvCurrentVideo.setText("Da chon: " + selectedFileName);
                }
            });

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        pickVideoLauncher.launch(intent);
    }

    private void extractFileInfo(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            selectedFileName = cursor.getString(nameIndex);
            selectedFileSize = cursor.getLong(sizeIndex);
            cursor.close();
        }
    }

    private void saveContent() {
        if ("ARTICLE".equalsIgnoreCase(lessonType)) {
            CreateArticleLessonRequest req = new CreateArticleLessonRequest();
            req.setContent(etArticleContent.getText().toString());
            CourseService service = RetrofitClient.getInstance().create(CourseService.class);
            service.createArticleLesson(lessonId, req).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LessonEditorActivity.this, "Luu thanh cong", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LessonEditorActivity.this, "Loi luu", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    Toast.makeText(LessonEditorActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else if ("VIDEO".equalsIgnoreCase(lessonType)) {
            if (selectedVideoUri == null) {
                Toast.makeText(this, "Chua chon video moi", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadVideoToPresignedUrl();
        }
    }

    private void uploadVideoToPresignedUrl() {
        btnSaveContent.setEnabled(false);
        pbVideoUpload.setVisibility(View.VISIBLE);
        tvUploadStatus.setVisibility(View.VISIBLE);
        tvUploadStatus.setText("Dang lay link upload...");

        CourseService service = RetrofitClient.getInstance().create(CourseService.class);
        service.getPresignedUrl(selectedFileName, "video/mp4").enqueue(new Callback<ApiResponse<PresignedUrlResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PresignedUrlResponse>> call, Response<ApiResponse<PresignedUrlResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PresignedUrlResponse pUrl = response.body().getData();
                    doActualS3Upload(pUrl, service);
                } else {
                    Toast.makeText(LessonEditorActivity.this, "Loi lay presigned url", Toast.LENGTH_SHORT).show();
                    resetUploadState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PresignedUrlResponse>> call, Throwable t) {
                resetUploadState();
            }
        });
    }

    private void doActualS3Upload(PresignedUrlResponse pUrl, CourseService service) {
        tvUploadStatus.setText("Dang tai len file...");
        try {
            InputStream is = getContentResolver().openInputStream(selectedVideoUri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] fileBytes = buffer.toByteArray();

            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("video/mp4");
                }
                @Override
                public void writeTo(okio.BufferedSink sink) throws java.io.IOException {
                    sink.write(fileBytes);
                }
            };

            service.uploadFileToPresignedUrl(pUrl.getPresignedUrl(), requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        saveVideoLessonData(pUrl.getFileKey(), service);
                    } else {
                        Toast.makeText(LessonEditorActivity.this, "Upload that bai", Toast.LENGTH_SHORT).show();
                        resetUploadState();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    resetUploadState();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            resetUploadState();
        }
    }

    private void saveVideoLessonData(String fileKey, CourseService service) {
        tvUploadStatus.setText("Dang luu du lieu...");
        CreateVideoLessonRequest req = new CreateVideoLessonRequest();
        req.setFileKey(fileKey);
        req.setFileName(selectedFileName);
        req.setFileType("video/mp4");
        req.setFileSize(selectedFileSize);
        req.setDuration(0); // Optional placeholder

        service.createVideoLesson(lessonId, req).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                Toast.makeText(LessonEditorActivity.this, "Luu video thanh cong", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                resetUploadState();
            }
        });
    }

    private void resetUploadState() {
        btnSaveContent.setEnabled(true);
        pbVideoUpload.setVisibility(View.GONE);
        tvUploadStatus.setText("Loi.");
    }
}
