package com.codepath.instagram.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramImage;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by hankxiao on 10/27/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostsViewHolder> {
    private Context context;
    private ArrayList<InstagramPost> posts;
    private ForegroundColorSpan blueColorSpan;

    public InstagramPostsAdapter(Context context, ArrayList<InstagramPost> posts) {
        this.context = context;
        this.posts = posts;
        this.blueColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(R.color.blue_text));
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
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        InstagramPost post = posts.get(position);
        InstagramUser user = post.user;
        InstagramImage image = post.image;
        SpannableStringBuilder caption;

        holder.sdvAvatar.setImageURI(Uri.parse(user.profilePictureUrl));
        holder.tvUsername.setText(user.userName);
        holder.tvTimestamp.setText(DateUtils.getRelativeTimeSpanString(post.createdTime * 1000));
        holder.sdvImage.setImageURI(Uri.parse(image.imageUrl));
        holder.tvLikeCount.setText(Utils.formatNumberForDisplay(post.likesCount));

        // Concatenate username to caption
        caption = concateUsername(user.userName, post.caption);
        if (caption == null) {
            holder.tvCaption.setVisibility(View.GONE);
        } else {
            holder.tvCaption.setText(caption);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private SpannableStringBuilder concateUsername(String username, String caption) {
        if (caption == null) {
            return null;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(username);
        ssb.setSpan(blueColorSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(" " + caption);

        return ssb;
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView sdvAvatar;
        public TextView tvUsername;
        public TextView tvTimestamp;
        public SimpleDraweeView sdvImage;
        public TextView tvLikeCount;
        public TextView tvCaption;

        public PostsViewHolder(View layoutView) {
            super(layoutView);
            sdvAvatar = (SimpleDraweeView) layoutView.findViewById(R.id.sdvAvatar);
            tvUsername = (TextView) layoutView.findViewById(R.id.tvUsername);
            tvTimestamp = (TextView) layoutView.findViewById(R.id.tvTimestamp);
            sdvImage = (SimpleDraweeView) layoutView.findViewById(R.id.sdvImage);
            tvLikeCount = (TextView) layoutView.findViewById(R.id.tvLikeCount);
            tvCaption = (TextView) layoutView.findViewById(R.id.tvCaption);
        }
    }
}
