package com.codepath.instagram.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramSearchTag;

import java.util.ArrayList;

/**
 * Created by hankxiao on 11/1/15.
 */
public class SearchTagResultsAdapter extends RecyclerView.Adapter<SearchTagResultsAdapter.SearchTagViewHolder> {
    private Context context;
    private ArrayList<InstagramSearchTag> tags;

    public SearchTagResultsAdapter(Context context, ArrayList<InstagramSearchTag> tags) {
        this.context = context;
        this.tags = tags;
    }

    @Override
    public SearchTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_item_tag, parent, false);

        // Return a new holder instance
        SearchTagViewHolder viewHolder = new SearchTagViewHolder(context, contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchTagViewHolder holder, final int position) {
        InstagramSearchTag tag = tags.get(position);

        holder.tag = tag;

        holder.tvTagname.setText(tag.tag);
        holder.tvNumPosts.setText(tag.count);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class SearchTagViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public InstagramSearchTag tag;

        // User layout
        public TextView tvTagname;
        public TextView tvNumPosts;

        public SearchTagViewHolder(Context context, View layoutView) {
            super(layoutView);

            this.context = context;

            // User layout
            tvTagname = (TextView) layoutView.findViewById(R.id.tvTagname);
            tvNumPosts = (TextView) layoutView.findViewById(R.id.tvNumPosts);
        }
    }
}
