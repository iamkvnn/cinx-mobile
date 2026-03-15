package com.app.cinx.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CertificateAdapter;
import com.app.cinx.model.Certificate;
import com.app.cinx.util.ToastUtil;

import java.util.Arrays;
import java.util.List;

/**
 * CertificatesActivity
 *
 * Displays the user's earned certificates as gold shimmer cards.
 * Each card provides:
 *  - Course name, issue date, grade badge
 *  - "Download PDF" action (simulated; hook into real backend later)
 *  - "Add to LinkedIn" action (opens LinkedIn share flow)
 */
public class CertificatesActivity extends AppCompatActivity
        implements CertificateAdapter.CertificateListener {

    // ─────────────────────────────────────────────────────────────────
    // Views
    // ─────────────────────────────────────────────────────────────────

    private RecyclerView   rvCertificates;
    private LinearLayout   emptyState;
    private ImageView      btnBack;

    // ─────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificates);

        bindViews();
        setupBackButton();
        loadCertificates();
    }

    // ─────────────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        rvCertificates = findViewById(R.id.rvCertificates);
        emptyState     = findViewById(R.id.emptyState);
        btnBack        = findViewById(R.id.btnBack);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCertificates() {
        List<Certificate> certs = buildSampleCertificates();

        if (certs.isEmpty()) {
            rvCertificates.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        CertificateAdapter adapter = new CertificateAdapter(certs, this);
        rvCertificates.setLayoutManager(new LinearLayoutManager(this));
        rvCertificates.setAdapter(adapter);
    }

    // ─────────────────────────────────────────────────────────────────
    // CertificateListener callbacks
    // ─────────────────────────────────────────────────────────────────

    @Override
    public void onDownloadPdf(Certificate cert) {
        // TODO: replace with real DownloadManager / backend PDF link
        ToastUtil.showCustomToast(this, getString(R.string.cert_download_toast, cert.getCourseName()));
    }

    @Override
    public void onShareLinkedIn(Certificate cert) {
        // Try opening the LinkedIn app first (certAdd deep-link),
        // then fall back to the LinkedIn website.
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("linkedin://share?text=" + Uri.encode(buildLinkedInText(cert))));
        appIntent.setPackage("com.linkedin.android");

        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else {
            // Fallback: open LinkedIn add-profile-section in browser
            String url = "https://www.linkedin.com/sharing/share-offsite/?url=" +
                    Uri.encode("https://edufuture.app/cert/" + cert.getId());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────

    /** Constructed share text sent to LinkedIn. */
    private String buildLinkedInText(Certificate cert) {
        return "Tôi vừa hoàn thành khóa học \"" + cert.getCourseName() +
               "\" trên EduFuture với kết quả " + cert.getGradeLabel() +
               " (" + cert.getScore() + "/100). #EduFuture #Learning";
    }

    /**
     * Sample certificates – replace with a real repository call
     * (e.g. CertificateRepository.getForUser(userId)) when backend is ready.
     */
    private List<Certificate> buildSampleCertificates() {
        return Arrays.asList(
                new Certificate(
                        "cert_001",
                        "UI/UX Design Masterclass",
                        "20/05/2026",
                        98,
                        Certificate.Grade.EXCELLENT,
                        "https://edufuture.app/cert/cert_001.pdf"
                ),
                new Certificate(
                        "cert_002",
                        "Lập trình Web Frontend Cơ bản",
                        "15/02/2026",
                        85,
                        Certificate.Grade.GOOD,
                        "https://edufuture.app/cert/cert_002.pdf"
                ),
                new Certificate(
                        "cert_003",
                        "Python cho Người Mới Bắt Đầu",
                        "10/11/2025",
                        76,
                        Certificate.Grade.AVERAGE,
                        "https://edufuture.app/cert/cert_003.pdf"
                )
        );
    }
}
