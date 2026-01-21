package com.app.cinx.adapter;

import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.config.GlideApp;
import java.util.List;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder> {

    private List<String> partnerLogos;

    public PartnerAdapter(List<String> partnerLogos) {
        this.partnerLogos = partnerLogos;
    }

    @NonNull
    @Override
    public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner, parent, false);
        return new PartnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerViewHolder holder, int position) {
        // Infinite scroll effect
        String logoUrl = partnerLogos.get(position % partnerLogos.size());

        GlideApp.with(holder.itemView.getContext())
                .load(logoUrl)
                .fitCenter()
                .into(holder.partnerLogo);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Infinite
    }

    static class PartnerViewHolder extends RecyclerView.ViewHolder {
        ImageView partnerLogo;

        public PartnerViewHolder(@NonNull View itemView) {
            super(itemView);
            partnerLogo = itemView.findViewById(R.id.partnerLogo);
        }
    }
}
