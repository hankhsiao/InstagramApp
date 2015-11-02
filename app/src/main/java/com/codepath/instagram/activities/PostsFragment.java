package com.codepath.instagram.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostsFragment extends Fragment {
    private FragmentActivity listener;

    private ArrayList<InstagramPost> posts;
    private InstagramPostsAdapter adapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(int page, String title) {
        PostsFragment fragment = new PostsFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        fragment.setArguments(args);
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
        // Get RecyclerView Reference
        RecyclerView rvPosts = (RecyclerView) view.findViewById(R.id.rvPosts);

        // Set Divider
        rvPosts.addItemDecoration(new SimpleVerticalSpacerItemDecoration(24));

        // Set Adapter
        rvPosts.setAdapter(adapter);

        // Set Layout
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Fetch popular posts
        fetchPosts();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void fetchPosts() {
        MainApplication.getRestClient().getMyFeed(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                posts.clear(); // clear existing items if needed
                posts.addAll(Utils.decodePostsFromJsonResponse(response)); // add new items
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
