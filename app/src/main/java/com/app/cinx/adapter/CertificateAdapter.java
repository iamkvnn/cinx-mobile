package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.model.Certificate;

import java.util.List;

/**
 * Displays a list of {@link Certificate} items as gold shimmer cards.
 *
 * Callback pattern keeps the adapter free of context-specific logic:
 * the hosting Activity/Fragment handles all actions via {@link CertificateListener}.
 */
public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.VH> {

    // ─────────────────────────────────────────────────────────────────
    // Listener interface
    // ─────────────────────────────────────────────────────────────────

    public interface CertificateListener {
        void onDownloadPdf(Certificate cert);
        void onShareLinkedIn(Certificate cert);
    }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final List<Certificate>    items;
    private final CertificateListener  listener;

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public CertificateAdapter(List<Certificate> items, CertificateListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    // ─────────────────────────────────────────────────────────────────
    // RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_certificate, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Certificate cert = items.get(position);
        Context ctx = h.itemView.getContext();

        h.tvCourseName.setText(cert.getCourseName());
        h.tvIssuedDate.setText(ctx.getString(R.string.cert_issued_prefix, cert.getIssuedDate()));
        h.tvGrade.setText(cert.getGradeLabel() + " " + cert.getScoreLabel());

        // Grade badge background: gold for Excellent, blue for Good
        int badgeBg = cert.getGrade() == Certificate.Grade.EXCELLENT
                ? R.drawable.bg_grade_excellent
                : R.drawable.bg_grade_good;
        int badgeColor = cert.getGrade() == Certificate.Grade.EXCELLENT
                ? ctx.getColor(R.color.cert_grade_excellent_text)
                : ctx.getColor(R.color.cert_grade_good_text);
        h.gradeBadge.setBackgroundResource(badgeBg);
        h.tvGrade.setTextColor(badgeColor);

        // Buttons
        h.btnDownloadPdf.setOnClickListener(v -> listener.onDownloadPdf(cert));
        h.btnLinkedIn   .setOnClickListener(v -> listener.onShareLinkedIn(cert));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        TextView     tvCourseName;
        TextView     tvIssuedDate;
        LinearLayout gradeBadge;
        TextView     tvGrade;
        LinearLayout btnDownloadPdf;
        LinearLayout btnLinkedIn;

        VH(@NonNull View v) {
            super(v);
            tvCourseName  = v.findViewById(R.id.tvCourseName);
            tvIssuedDate  = v.findViewById(R.id.tvIssuedDate);
            gradeBadge    = v.findViewById(R.id.gradeBadge);
            tvGrade       = v.findViewById(R.id.tvGrade);
            btnDownloadPdf = v.findViewById(R.id.btnDownloadPdf);
            btnLinkedIn   = v.findViewById(R.id.btnLinkedIn);
        }
    }
}
