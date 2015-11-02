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
import com.codepath.instagram.adapters.SearchTagResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchTagsResultFragment extends Fragment {
    private FragmentActivity listener;

    private ArrayList<InstagramSearchTag> tags;
    private SearchTagResultsAdapter adapter;

    public SearchTagsResultFragment() {
        // Required empty public constructor
    }

    public static SearchTagsResultFragment newInstance(int page, String title) {
        SearchTagsResultFragment fragment = new SearchTagsResultFragment();
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
        tags = new ArrayList<InstagramSearchTag>();

        // Create Adapter
        adapter = new SearchTagResultsAdapter(getActivity(), tags);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_tags_result, container, false);
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
        RecyclerView rvTags = (RecyclerView) view.findViewById(R.id.rvTags);

        // Set Adapter
        rvTags.setAdapter(adapter);

        // Set Layout
        rvTags.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void search(String query) {
        MainApplication.getRestClient().searchTag(query, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                tags.clear(); // clear existing items if needed
                tags.addAll(Utils.decodeSearchTagsFromJsonResponse(response)); // add new items
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
