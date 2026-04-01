package com.app.cinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.api.dto.UserDto;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private final Context context;
    private List<UserDto> users;

    public AdminUserAdapter(Context context, List<UserDto> users) {
        this.context = context;
        this.users = users;
    }

    public void updateData(List<UserDto> newData) {
        this.users = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDto user = users.get(position);

        holder.tvName.setText(user.getName() != null ? user.getName() : "N/A");
        holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");

        String joinDate = user.getJoinDateDisplay();
        holder.tvJoinDate.setText(context.getString(R.string.admin_join_date, joinDate));

        boolean locked = user.isLockedAccount();
        boolean active = user.isActiveAccount();
        if (locked) {
            holder.tvStatus.setText(R.string.admin_status_locked);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.order_status_cancelled_text));
        } else if (active) {
            holder.tvStatus.setText(R.string.admin_status_active);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.success_green));
        } else {
            holder.tvStatus.setText(R.string.admin_status_unknown);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvEmail;
        TextView tvJoinDate;
        TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvJoinDate = itemView.findViewById(R.id.tvJoinDate);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
        }
    }
}
