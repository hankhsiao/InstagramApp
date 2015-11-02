package com.codepath.instagram.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramSearchTag;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";
    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
    }

    public static String formatNumberForDisplay(int number) {
        return numberFormat.format(number);
    }

    public static JSONObject loadJsonFromAsset(Context context, String fileName) throws IOException, JSONException {
        InputStream inputStream = context.getResources().getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }

        JSONObject result = new JSONObject(builder.toString());

        inputStream.close();
        bufferedReader.close();

        return result;
    }

    public static List<InstagramPost> decodePostsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramPost> posts = InstagramPost.fromJson(getDataJsonArray(jsonObject));
        return posts == null ? new ArrayList<InstagramPost>() : posts;
    }

    public static List<InstagramComment> decodeCommentsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramComment> comments = InstagramComment.fromJson(getDataJsonArray(jsonObject));
        return comments == null ? new ArrayList<InstagramComment>() : comments;
    }

    public static List<InstagramUser> decodeUsersFromJsonResponse(JSONObject jsonObject) {
        List<InstagramUser> users = InstagramUser.fromJson(getDataJsonArray(jsonObject));
        return users == null ? new ArrayList<InstagramUser>() : users;
    }

    public static List<InstagramSearchTag> decodeSearchTagsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramSearchTag> searchTags = InstagramSearchTag.fromJson(getDataJsonArray(jsonObject));
        return searchTags == null ? new ArrayList<InstagramSearchTag>() : searchTags;
    }

    public static SpannableStringBuilder concateUsername(Context context, String username, String text) {
        if (text == null) {
            return null;
        }

        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(R.color.blue_text)
        );

        SpannableStringBuilder ssb = new SpannableStringBuilder(username);
        ssb.setSpan(blueColorSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(" " + text);

        return ssb;
    }

    public static void shareBitmap(final Context context, Uri imageUri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(imageUri)
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();

        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, context);

        try {
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                public void onNewResultImpl(@Nullable Bitmap bitmap) {
                    if (bitmap == null) {
                        return;
                    }

                    shareBitmap(context, bitmap);
                }

                @Override
                public void onFailureImpl(DataSource dataSource) {
                    // No cleanup required here
                }
            }, CallerThreadExecutor.getInstance());
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }

    public static void shareBitmap(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bitmap, "Image Description", null);
        Uri bmpUri = Uri.parse(path);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    private static JSONArray getDataJsonArray(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        if (jsonObject != null) {
            jsonArray = jsonObject.optJSONArray("data");
        }
        return jsonArray;
    }
}
