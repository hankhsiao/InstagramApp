package com.codepath.instagram.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPosts;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by hankxiao on 11/3/15.
 */
public class PostsService extends IntentService {

    static final String TAG = "PostsService";

    public static final String KEY_RESULTS = "KeyResults";
    public static final String KEY_RESULT_CODE = "KeyResultCode";
    public static final String ACTION = "com.codepath.instagram.postsService";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PostsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchPosts();
    }

    private void fetchPosts() {
        MainApplication.getRestClient().getMyFeed(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                InstagramPosts posts = new InstagramPosts(Utils.decodePostsFromJsonResponse(response));

                Intent in = new Intent(ACTION);
                in.putExtra(KEY_RESULT_CODE, Activity.RESULT_OK);
                in.putExtra(KEY_RESULTS, posts);
                LocalBroadcastManager.getInstance(PostsService.this).sendBroadcast(in);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
