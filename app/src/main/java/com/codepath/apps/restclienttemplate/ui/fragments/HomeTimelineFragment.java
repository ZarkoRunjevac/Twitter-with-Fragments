package com.codepath.apps.restclienttemplate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.service.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zarkorunjevac on 19/11/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {

    private static final String TAG = HomeTimelineFragment.class.getCanonicalName();

    private TwitterClient client;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateTimeLine(null, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scrollListener = new EndlessRecyclerViewScrollListener(new LinearLayoutManager(getContext())) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeLine(tweets.get(tweets.size() - 1).uid, false);
            }
        };

        mBinding.included.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(null, true);
            }
        });
    }



    public void populateTimeLine(Long max_id, final boolean isRefresh) {
        if (NetworkUtils.isNetworkAvailable(getActivity())&&NetworkUtils.isInternetAvailable()) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                ArrayList<Tweet> newTweets;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(TAG, "onSuccess: " + response.toString());

                    addItems(response,isRefresh);


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    Log.d(TAG, "onFailure: " + errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONArray errorResponse) {
                    Log.d(TAG, "onFailure: " + errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                                      Throwable throwable) {
                    Log.d(TAG, "onFailure: " + responseString.toString());
                    throwable.printStackTrace();
                }
            }, max_id);
        } else {
            addItems(null,isRefresh);
        }
    }
}
