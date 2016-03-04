package com.klinker.android.twitter.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.klinker.android.twitter.R;
import com.klinker.android.twitter.adapters.GifSearchAdapter;
import com.klinker.android.twitter.utils.api_helper.GiffyHelper;
import com.lapism.arrow.ArrowDrawable;
import com.lapism.searchview.view.SearchView;

import java.util.List;

public class GiffySearch extends Activity {

    private SearchView toolbar;
    private ImageView backArrow;
    private EditText searchText;

    private RecyclerView recycler;
    private View progressSpinner;

    private GifSearchAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {

        }

        setContentView(R.layout.giffy_search_activity);

        recycler = (RecyclerView) findViewById(R.id.recycler_view);
        progressSpinner = findViewById(R.id.list_progress);
        backArrow = (ImageView) findViewById(R.id.imageView_arrow_back);
        toolbar = (SearchView) findViewById(R.id.searchView);
        searchText = (EditText) findViewById(R.id.editText_input);

        final ArrowDrawable drawable = new ArrowDrawable(this);
        drawable.animate(ArrowDrawable.STATE_ARROW);
        backArrow.setImageDrawable(drawable);

        toolbar.setOnSearchMenuListener(new SearchView.SearchMenuListener() {
            @Override
            public void onMenuClick() {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        toolbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                executeQuery(query);
                backArrow.performClick();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.releaseVideo();
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.animate(ArrowDrawable.STATE_ARROW);
                searchText.requestFocus();
            }
        }, 500);

    }

    private void executeQuery(String query) {
        progressSpinner.setVisibility(View.VISIBLE);

        GiffyHelper.search(query, new GiffyHelper.Callback() {
            @Override
            public void onResponse(List<GiffyHelper.Gif> gifs) {
                progressSpinner.setVisibility(View.GONE);

                if (adapter != null) {
                    adapter.releaseVideo();
                }

                adapter = new GifSearchAdapter(GiffySearch.this, gifs, new GifSearchAdapter.Callback() {
                    @Override
                    public void onClick(int item) {

                    }
                });

                recycler.setLayoutManager(new LinearLayoutManager(GiffySearch.this));
                recycler.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.releaseVideo();
        }
    }
}