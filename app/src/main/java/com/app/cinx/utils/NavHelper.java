package com.app.cinx.utils;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.app.Activity;

import com.app.cinx.activity.DiscoveryActivity;
import com.app.cinx.activity.MainActivity;
import com.app.cinx.activity.ProfileActivity;
import com.app.cinx.R;
import com.app.cinx.activity.LearningScheduleActivity;

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
                Intent intent = new Intent(activity, LearningScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
        
        profileBtn.setOnClickListener(v -> {
            if (activeTabId != R.id.navProfile) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
    }

    private static void setActiveState(View view, boolean isActive) {
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            ImageView icon = null;
            android.widget.TextView text = null;

            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof ImageView) {
                    icon = (ImageView) child;
                } else if (child instanceof android.widget.TextView) {
                    text = (android.widget.TextView) child;
                }
            }

            if (icon != null && text != null) {
                if (isActive) {
                    icon.setColorFilter(icon.getContext().getResources().getColor(R.color.primary));
                    text.setVisibility(View.VISIBLE);
                    // simple float animation
                    text.setAlpha(0f);
                    text.animate().alpha(1f).setDuration(200).start();
                } else {
                    icon.setColorFilter(icon.getContext().getResources().getColor(R.color.text_secondary));
                    text.setVisibility(View.GONE);
                }
            }
        }
    }
}
