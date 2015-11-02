package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    private ArrayList<InstagramComment> comments;
    private InstagramCommentsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // New empty posts
        comments = new ArrayList<InstagramComment>();

        // Get RecyclerView Reference
        RecyclerView rvComments = (RecyclerView) findViewById(R.id.rvComments);

        // Set Divider
        rvComments.addItemDecoration(new SimpleVerticalSpacerItemDecoration(24));

        // Create Adapter
        adapter = new InstagramCommentsAdapter(this, comments);

        // Set Adapter
        rvComments.setAdapter(adapter);

        // Set Layout
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        // Fetch popular posts
        String mediaId = getIntent().getStringExtra("mediaId");

        fetchComments(mediaId);
    }

    private void fetchComments(String mediaId) {
        MainApplication.getRestClient().getComments(mediaId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                comments.clear(); // clear existing items if needed
                comments.addAll(Utils.decodeCommentsFromJsonResponse(response)); // add new items
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
