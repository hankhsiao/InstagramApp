package com.codepath.instagram.networking;

import android.content.Context;

import com.codepath.instagram.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.scribe.builder.api.Api;

/**
 * Created by hankxiao on 10/28/15.
 */
public class InstagramClient extends OAuthBaseClient{
    private static final Class<? extends Api> REST_API_CLASS = InstagramApi.class;
    private static final String REST_URL = "https://api.instagram.com/v1";
    private static final String REST_CONSUMER_KEY = "64932776626e47c9871767539d3b76e0";
    private static final String REST_CONSUMER_SECRET = "c9fd10ad6b4f459da3811fc094a31f6f";
    private static final String REDIRECT_URI = Constants.REDIRECT_URI;
    private static final String SCOPE = Constants.SCOPE;

    private static final String BASE_API_URL = REST_URL + "/media";

    private static final String CLIENT_ID_PARAM = "?client_id=" + REST_CONSUMER_KEY;

    private static final String SEARCH_PARAM = "?q=";

    private static final String COMMENTS_PATH = "/comments";

    private static final String POPULAR_PATH = "/popular";

    private static final String MY_FEED_PATH = "/users/self/feed";

    private static final String USER_SEARCH_PATH = "/users/search";

    private static final String TAG_SEARCH_PATH = "/tags/search";

    private final SyncHttpClient aClient = new SyncHttpClient();

    public InstagramClient(Context context) {
        super(context, REST_API_CLASS, REST_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REDIRECT_URI, SCOPE);
    }

    private void syncFetch(String url, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("access_token", client.getAccessToken().getToken());
        aClient.get(url, params, responseHandler);
    }

    public void getPopularFeed(JsonHttpResponseHandler responseHandler) {
        client.get(BASE_API_URL + POPULAR_PATH + CLIENT_ID_PARAM, responseHandler);
    }

    public void getComments(String mediaId, JsonHttpResponseHandler responseHandler) {
        client.get(BASE_API_URL + "/" + mediaId + COMMENTS_PATH + CLIENT_ID_PARAM, responseHandler);
    }

    public void getMyFeed(JsonHttpResponseHandler responseHandler) {
        syncFetch(REST_URL + MY_FEED_PATH, responseHandler);
    }

    public void searchUser(String query, JsonHttpResponseHandler responseHandler) {
        client.get(REST_URL + USER_SEARCH_PATH + SEARCH_PARAM + query, responseHandler);
    }

    public void searchTag(String query, JsonHttpResponseHandler responseHandler) {
        client.get(REST_URL + TAG_SEARCH_PATH + SEARCH_PARAM + query, responseHandler);
    }
}
