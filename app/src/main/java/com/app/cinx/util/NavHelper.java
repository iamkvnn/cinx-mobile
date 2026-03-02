package com.app.cinx.util;

import static com.app.cinx.util.Convert.dpToPx;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.app.Activity;

import androidx.core.content.res.ResourcesCompat;

import com.app.cinx.DiscoveryActivity;
import com.app.cinx.MainActivity;
import com.app.cinx.R;

public class NavHelper {

    public static void setupNavigation(Activity activity, int activeTabId) {
        View homeBtn = activity.findViewById(R.id.navHome);
        View searchBtn = activity.findViewById(R.id.navSearch);
        View coursesBtn = activity.findViewById(R.id.navCourses);
        View profileBtn = activity.findViewById(R.id.navProfile);

        // Set active state visually
        setActiveState(homeBtn, activeTabId == R.id.navHome);
        setActiveState(searchBtn, activeTabId == R.id.navSearch);
        setActiveState(coursesBtn, activeTabId == R.id.navCourses);
        setActiveState(profileBtn, activeTabId == R.id.navProfile);

        // Actions
        homeBtn.setOnClickListener(v -> {
            if (activeTabId != R.id.navHome) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(v -> {
            if (activeTabId != R.id.navSearch) {
                Intent intent = new Intent(activity, DiscoveryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });

        coursesBtn.setOnClickListener(v -> {
            if (activeTabId != R.id.navCourses) {
                Intent intent = new Intent(activity, com.app.cinx.MyLearningActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
        
        // Add others as needed
    }

    private static void setActiveState(View view, boolean isActive) {
        if (view instanceof ImageView) {
             ImageView icon = (ImageView) view;
             if (isActive) {
                 icon.setColorFilter(icon.getContext().getResources().getColor(R.color.primary));
                 icon.setBackground(ResourcesCompat.getDrawable(icon.getResources(), R.drawable.glass_panel_bg, null));
                 ViewGroup.LayoutParams params = view.getLayoutParams();
                 params.width = dpToPx(icon.getContext(), 76);
                 view.setLayoutParams(params);
             } else {
                 icon.setColorFilter(icon.getContext().getResources().getColor(R.color.text_secondary)); // Should define text_secondary or generic grey
                 icon.setScaleX(1.0f);
                 icon.setScaleY(1.0f);
             }
        }
    }
}
