package com.codepath.instagram.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by hankxiao on 10/29/15.
 */
public class InstagramCommentsAdapter extends RecyclerView.Adapter<InstagramCommentsAdapter.CommentsViewHolder> {
    private Context context;
    private ArrayList<InstagramComment> comments;

    public InstagramCommentsAdapter(Context context, ArrayList<InstagramComment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_item_comment, parent, false);

        // Return a new holder instance
        CommentsViewHolder viewHolder = new CommentsViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        InstagramComment comment = comments.get(position);
        InstagramUser user = comment.user;

        holder.sdvAvatar.setImageURI(Uri.parse(user.profilePictureUrl));
        holder.tvTextComment.setText(Utils.concateUsername(context, user.userName, comment.text));
        holder.tvCreatedTime.setText(DateUtils.getRelativeTimeSpanString(comment.createdTime * 1000));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView sdvAvatar;
        public TextView tvTextComment;
        public TextView tvCreatedTime;

        public CommentsViewHolder(View layoutView) {
            super(layoutView);
            sdvAvatar = (SimpleDraweeView) layoutView.findViewById(R.id.sdvAvatar);
            tvTextComment = (TextView) layoutView.findViewById(R.id.tvTextComment);
            tvCreatedTime = (TextView) layoutView.findViewById(R.id.tvCreatedTime);
        }
    }
}
