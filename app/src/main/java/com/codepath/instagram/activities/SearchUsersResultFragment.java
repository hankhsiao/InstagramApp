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
import com.codepath.instagram.adapters.SearchUserResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchUsersResultFragment extends Fragment {
    private FragmentActivity listener;

    private ArrayList<InstagramUser> users;
    private SearchUserResultsAdapter adapter;

    public SearchUsersResultFragment() {
        // Required empty public constructor
    }

    public static SearchUsersResultFragment newInstance(int page, String title) {
        SearchUsersResultFragment fragment = new SearchUsersResultFragment();
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
        users = new ArrayList<InstagramUser>();

        // Create Adapter
        adapter = new SearchUserResultsAdapter(getActivity(), users);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_users_result, container, false);
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
        RecyclerView rvUsers = (RecyclerView) getParentFragment().getView().findViewById(R.id.rvUsers);

        // Set Adapter
        rvUsers.setAdapter(adapter);

        // Set Layout
        rvUsers.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void search(String query) {
        MainApplication.getRestClient().searchUser(query, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                users.clear(); // clear existing items if needed
                users.addAll(Utils.decodeUsersFromJsonResponse(response)); // add new items
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
