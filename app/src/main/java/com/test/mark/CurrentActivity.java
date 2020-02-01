package com.test.mark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CurrentActivity extends AppCompatActivity {

    private RecyclerView activitiesRecyclerView;
    private TextView textView;

    private FrameLayout toolbarContainer;
    private Toolbar toolbar;

    private VerticalScrollTrendingAdapter horizontalScrollTrendingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        activitiesRecyclerView = findViewById(R.id.trending_activities_recycler_view);
        toolbarContainer = (FrameLayout) findViewById(R.id.toolbar_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparent
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes status bar covers toolbar issue
            toolbarContainer.setPadding(0, getStatusBarHeight(), 0, 0);
            //toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Trending Recycler view

        LinearLayoutManager trendingLayoutManager = new LinearLayoutManager(this);
        trendingLayoutManager.setOrientation(RecyclerView.VERTICAL);
        activitiesRecyclerView.setLayoutManager(trendingLayoutManager);

        final List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList = new ArrayList<HorizontalScrollTrendingModel>();
        horizontalScrollTrendingAdapter =  new VerticalScrollTrendingAdapter(horizontalScrollTrendingModelList);

        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));

        activitiesRecyclerView.setAdapter(horizontalScrollTrendingAdapter);
        horizontalScrollTrendingAdapter.notifyDataSetChanged();
    }


    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
