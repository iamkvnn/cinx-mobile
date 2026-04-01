package com.app.cinx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CertificateRequestAdapter;
import com.app.cinx.api.LearningService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CertificateRequestResponse;
import com.app.cinx.api.dto.PaginatedApiResponseCertificateRequestResponse;
import com.app.cinx.utils.NavHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificateRequestsActivity extends AppCompatActivity implements CertificateRequestAdapter.OnRequestActionListener {

    private RecyclerView rvRequests;
    private ProgressBar pbLoading;
    private CertificateRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_requests);

        Toolbar toolbar = findViewById(R.id.toolbarCertificateRequests);
        setSupportActionBar(toolbar);


        rvRequests = findViewById(R.id.rvRequests);
        pbLoading = findViewById(R.id.pbLoading);

        adapter = new CertificateRequestAdapter(this);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);

        fetchRequests();
        
        NavHelper.setupInstructorNavigation(this, R.id.navInstCert);
    }

    private void fetchRequests() {
        pbLoading.setVisibility(View.VISIBLE);
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        // Fetching all certificate requests using the overloaded method without courseId
        learningService.getRequestsByCourse(null, 1, 100).enqueue(new Callback<PaginatedApiResponseCertificateRequestResponse>() {
            @Override
            public void onResponse(Call<PaginatedApiResponseCertificateRequestResponse> call, Response<PaginatedApiResponseCertificateRequestResponse> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    adapter.setItems(response.body().getData());
                } else {
                    Toast.makeText(CertificateRequestsActivity.this, "Không thể tải danh sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaginatedApiResponseCertificateRequestResponse> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(CertificateRequestsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(CertificateRequestResponse request) {
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        learningService.approveCertificate(request.getId()).enqueue(new Callback<ApiResponse<CertificateRequestResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CertificateRequestResponse>> call, Response<ApiResponse<CertificateRequestResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CertificateRequestsActivity.this, "Đã phê duyệt", Toast.LENGTH_SHORT).show();
                    // Refresh data
                    fetchRequests();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<CertificateRequestResponse>> call, Throwable t) {}
        });
    }

    @Override
    public void onReject(CertificateRequestResponse request) {
        LearningService learningService = RetrofitClient.getInstance().getLearningService();
        learningService.rejectCertificate(request.getId()).enqueue(new Callback<ApiResponse<CertificateRequestResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CertificateRequestResponse>> call, Response<ApiResponse<CertificateRequestResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CertificateRequestsActivity.this, "Đã từ chối", Toast.LENGTH_SHORT).show();
                    // Refresh data
                    fetchRequests();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<CertificateRequestResponse>> call, Throwable t) {}
        });
    }
}
