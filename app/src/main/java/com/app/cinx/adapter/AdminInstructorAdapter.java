package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.UserDto;

import java.util.List;

public class AdminInstructorAdapter extends RecyclerView.Adapter<AdminInstructorAdapter.ViewHolder> {

    public interface OnVerifyClickListener {
        void onVerifyClick(UserDto user);
    }

    private final Context context;
    private List<UserDto> instructors;
    private final OnVerifyClickListener listener;
    private String verifyingUserId;

    public AdminInstructorAdapter(Context context, List<UserDto> instructors, OnVerifyClickListener listener) {
        this.context = context;
        this.instructors = instructors;
        this.listener = listener;
    }

    public void updateData(List<UserDto> newData) {
        this.instructors = newData;
        notifyDataSetChanged();
    }

    public void setVerifyingUserId(String verifyingUserId) {
        this.verifyingUserId = verifyingUserId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_instructor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDto instructor = instructors.get(position);
        holder.tvName.setText(instructor.getName() != null ? instructor.getName() : "N/A");
        holder.tvEmail.setText(instructor.getEmail() != null ? instructor.getEmail() : "N/A");

        boolean verified = instructor.isInstructorApproved();
        holder.tvStatus.setText(verified ? R.string.admin_status_verified : R.string.admin_status_pending);
        holder.tvStatus.setTextColor(context.getResources().getColor(
                verified ? R.color.success_green : R.color.order_status_pending_text
        ));

        if (verified) {
            holder.btnVerify.setVisibility(View.GONE);
        } else {
            holder.btnVerify.setVisibility(View.VISIBLE);
            boolean isVerifying = instructor.getUserId() != null && instructor.getUserId().equals(verifyingUserId);
            holder.btnVerify.setEnabled(!isVerifying);
            holder.btnVerify.setText(isVerifying
                    ? context.getString(R.string.admin_verify_loading)
                    : context.getString(R.string.admin_verify_action));
            holder.btnVerify.setOnClickListener(v -> listener.onVerifyClick(instructor));
        }
    }

    @Override
    public int getItemCount() {
        return instructors == null ? 0 : instructors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvEmail;
        TextView tvStatus;
        Button btnVerify;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvInstructorName);
            tvEmail = itemView.findViewById(R.id.tvInstructorEmail);
            tvStatus = itemView.findViewById(R.id.tvInstructorStatus);
            btnVerify = itemView.findViewById(R.id.btnVerify);
        }
    }
}
