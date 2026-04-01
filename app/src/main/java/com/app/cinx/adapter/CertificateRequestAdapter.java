package com.app.cinx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.CertificateRequestResponse;

import java.util.ArrayList;
import java.util.List;

public class CertificateRequestAdapter extends RecyclerView.Adapter<CertificateRequestAdapter.ViewHolder> {

    private List<CertificateRequestResponse> items = new ArrayList<>();
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(CertificateRequestResponse request);
        void onReject(CertificateRequestResponse request);
    }

    public CertificateRequestAdapter(OnRequestActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CertificateRequestResponse> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificateRequestResponse item = items.get(position);
        holder.tvUserId.setText("User ID: " + item.getUserId());
        holder.tvCourseId.setText("Course ID: " + item.getCourseId());
        holder.tvStatus.setText("Trạng thái: " + item.getStatus());

        if ("PENDING".equalsIgnoreCase(item.getStatus())) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(item);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvCourseId, tvStatus;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvCourseId = itemView.findViewById(R.id.tvCourseId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
