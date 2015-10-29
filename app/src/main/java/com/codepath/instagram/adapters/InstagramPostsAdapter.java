package com.codepath.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramImage;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * Created by hankxiao on 10/27/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostsViewHolder> {
    private Context context;
    private ArrayList<InstagramPost> posts;

    public InstagramPostsAdapter(Context context, ArrayList<InstagramPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_item_post, parent, false);

        // Return a new holder instance
        PostsViewHolder viewHolder = new PostsViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostsViewHolder holder, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final InstagramPost post = posts.get(position);
        InstagramUser user = post.user;
        InstagramImage image = post.image;
        SpannableStringBuilder caption;
        ArrayList<InstagramComment> comments = (ArrayList<InstagramComment>)post.comments;
        InstagramComment comment;
        int commentCount = post.commentsCount;

        holder.sdvAvatar.setImageURI(Uri.parse(user.profilePictureUrl));
        holder.tvUsername.setText(user.userName);
        holder.tvTimestamp.setText(DateUtils.getRelativeTimeSpanString(post.createdTime * 1000));
        holder.sdvImage.setImageURI(Uri.parse(image.imageUrl));

        // Social layout
        holder.ibtnMoreDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialPopup(v, position);
            }
        });

        holder.tvLikeCount.setText(Utils.formatNumberForDisplay(post.likesCount));

        // Concatenate username to caption
        caption = Utils.concateUsername(context, user.userName, post.caption);
        if (caption == null) {
            holder.tvCaption.setVisibility(View.GONE);
        } else {
            holder.tvCaption.setText(caption);
        }

        if (commentCount > 2) {
            holder.btnViewComments.setVisibility(View.VISIBLE);
            holder.btnViewComments.setText("View all " + commentCount + " comments");
            holder.btnViewComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CommentsActivity.class);
                    i.putExtra("mediaId", post.mediaId);
                    context.startActivity(i);
                }
            });
        } else {
            holder.btnViewComments.setVisibility(View.GONE);
        }

        // Show at most 2 comments
        holder.llComments.removeAllViews();
        for (int i = 0; i < commentCount && i < 2; i++) {
            TextView commentView = (TextView) inflater.inflate(R.layout.layout_item_text_comment, null);

            comment = comments.get(i);
            commentView.setText(Utils.concateUsername(context, comment.user.userName, comment.text));

            holder.llComments.addView(commentView);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void showSocialPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(context, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_social, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuShare:
                        String imageUrl = posts.get(position).image.imageUrl;
                        shareBitmap(Uri.parse(imageUrl));
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the menu
        popup.show();
    }

    private void shareBitmap(Uri imageUri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(imageUri)
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();

        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, this);

        try {
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                public void onNewResultImpl(@Nullable Bitmap bitmap) {
                    if (bitmap == null) {
                        return;
                    }

                    shareBitmap(bitmap);
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

    private void shareBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bitmap, "Image Description", null);
        Uri bmpUri = Uri.parse(path);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        // User layout
        public SimpleDraweeView sdvAvatar;
        public TextView tvUsername;
        public TextView tvTimestamp;
        public SimpleDraweeView sdvImage;

        // Social layout
        public ImageButton ibtnHeart;
        public ImageButton ibtnComment;
        public ImageButton ibtnMoreDots;

        public TextView tvLikeCount;
        public TextView tvCaption;
        public Button btnViewComments;
        public LinearLayout llComments;

        public PostsViewHolder(View layoutView) {
            super(layoutView);
            // User layout
            sdvAvatar = (SimpleDraweeView) layoutView.findViewById(R.id.sdvAvatar);
            tvUsername = (TextView) layoutView.findViewById(R.id.tvUsername);
            tvTimestamp = (TextView) layoutView.findViewById(R.id.tvTimestamp);
            sdvImage = (SimpleDraweeView) layoutView.findViewById(R.id.sdvImage);

            // Social layout
            ibtnHeart = (ImageButton) layoutView.findViewById(R.id.ibtnHeart);
            ibtnComment = (ImageButton) layoutView.findViewById(R.id.ibtnComment);
            ibtnMoreDots = (ImageButton) layoutView.findViewById(R.id.ibtnMoreDots);

            tvLikeCount = (TextView) layoutView.findViewById(R.id.tvLikeCount);
            tvCaption = (TextView) layoutView.findViewById(R.id.tvCaption);
            btnViewComments = (Button) layoutView.findViewById(R.id.btnViewComments);
            llComments = (LinearLayout) layoutView.findViewById(R.id.llComments);
        }
    }
}
