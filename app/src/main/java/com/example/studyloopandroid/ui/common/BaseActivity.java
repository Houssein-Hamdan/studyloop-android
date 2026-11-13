package com.example.studyloopandroid.ui.common;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.dashboard.DashboardActivity;
import com.example.studyloopandroid.ui.learning.HomeActivity;
import com.example.studyloopandroid.ui.review.ReviewActivity;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studyloopandroid.ui.analytics.AnalyticsActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(selectedItemId);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == selectedItemId) {
                return true;
            }

            Class<?> targetActivity;

            if (itemId == R.id.nav_home) {
                targetActivity = DashboardActivity.class;
            } else if (itemId == R.id.nav_learn) {
                targetActivity = HomeActivity.class;
            } else if (itemId == R.id.nav_review) {
                targetActivity = ReviewActivity.class;
            } else if (itemId == R.id.nav_analytics) {
                targetActivity = AnalyticsActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            finish();

            return true;
        });
    }

}
