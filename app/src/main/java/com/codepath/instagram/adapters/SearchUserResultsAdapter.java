package com.codepath.instagram.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by hankxiao on 11/1/15.
 */
public class SearchUserResultsAdapter extends RecyclerView.Adapter<SearchUserResultsAdapter.SearchTagViewHolder> {
    private Context context;
    private ArrayList<InstagramUser> users;

    public SearchUserResultsAdapter(Context context, ArrayList<InstagramUser> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public SearchTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_item_user, parent, false);

        // Return a new holder instance
        SearchTagViewHolder viewHolder = new SearchTagViewHolder(context, contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchTagViewHolder holder, final int position) {
        InstagramUser user = users.get(position);

        holder.user = user;

        holder.sdvAvatar.setImageURI(Uri.parse(user.profilePictureUrl));
        holder.tvUsername.setText(user.userName);
        holder.tvFullname.setText(user.fullName);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class SearchTagViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public InstagramUser user;

        // User layout
        public SimpleDraweeView sdvAvatar;
        public TextView tvUsername;
        public TextView tvFullname;

        public SearchTagViewHolder(Context context, View layoutView) {
            super(layoutView);

            this.context = context;

            // User layout
            sdvAvatar = (SimpleDraweeView) layoutView.findViewById(R.id.sdvAvatar);
            tvUsername = (TextView) layoutView.findViewById(R.id.tvUsername);
            tvFullname = (TextView) layoutView.findViewById(R.id.tvFullname);
        }
    }
}
