package com.codepath.instagram.networking;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by hankxiao on 10/28/15.
 */
public class InstagramClient {

    private static final String BASE_API_URL = "https://api.instagram.com/v1/media";

    private static final String CLIENT_ID = "64932776626e47c9871767539d3b76e0";

    private static final String CLIENT_ID_PARAM = "?client_id=" + CLIENT_ID;

//    private static final String POPULAR_API_URL = BASE_API_URL + "popular?client_id=" + CLIENT_ID;

    private static final String COMMENTS_PATH = "/comments";

    private static final String POPULAR_PATH = "/popular";


    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getPopularFeed(JsonHttpResponseHandler responseHandler) {
        client.get(BASE_API_URL + POPULAR_PATH + CLIENT_ID_PARAM, responseHandler);
    }

    public static void getComments(String mediaId, JsonHttpResponseHandler responseHandler) {
        client.get(BASE_API_URL + "/" + mediaId + COMMENTS_PATH + CLIENT_ID_PARAM, responseHandler);
    }

}
