package com.codepath.instagram.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.codepath.instagram.services.PostsService;

import java.util.ArrayList;

public class PostsFragment extends Fragment {
    private FragmentActivity listener;

    private ArrayList<InstagramPost> posts;
    private InstagramPostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(int page, String title) {
        PostsFragment fragment = new PostsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // New empty posts
        posts = new ArrayList<InstagramPost>();

        // Create Adapter
        adapter = new InstagramPostsAdapter(getActivity(), posts);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (FragmentActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Set Swiper
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPosts();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Get RecyclerView Reference
        RecyclerView rvPosts = (RecyclerView) view.findViewById(R.id.rvPosts);

        // Set Divider
        rvPosts.addItemDecoration(new SimpleVerticalSpacerItemDecoration(24));

        // Set Adapter
        rvPosts.setAdapter(adapter);

        // Set Layout
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(PostsService.ACTION);
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(testReceiver, filter);
        fetchPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(testReceiver);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra(PostsService.KEY_RESULT_CODE, Activity.RESULT_CANCELED);
            if (resultCode == Activity.RESULT_OK) {
                InstagramPosts sPosts = (InstagramPosts)intent.getSerializableExtra(PostsService.KEY_RESULTS);
                posts.clear(); // clear existing items if needed
                posts.addAll(sPosts.posts); // add new items
                adapter.notifyDataSetChanged();

                InstagramClientDatabase db = InstagramClientDatabase.getInstance(PostsFragment.this.getActivity());
                db.emptyAllTables();
                db.addInstagramPosts(posts);
            }
            swipeContainer.setRefreshing(false);
        }
    };

    private void fetchPosts() {
        if (Utils.isNetworkAvailable(this.getActivity())) {
            Intent i = new Intent(getActivity().getApplication(), PostsService.class);
            getActivity().getApplication().startService(i);
        } else {
            InstagramClientDatabase db = InstagramClientDatabase.getInstance(PostsFragment.this.getActivity());
            posts.clear();
            posts.addAll(db.getAllInstagramPosts());
            adapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        }
    }
}
