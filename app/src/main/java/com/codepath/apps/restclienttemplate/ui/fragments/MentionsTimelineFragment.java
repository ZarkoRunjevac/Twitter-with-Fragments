package com.codepath.apps.restclienttemplate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.service.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.codepath.apps.restclienttemplate.utils.TimelineType;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zarkorunjevac on 02/12/17.
 */

public class MentionsTimelineFragment extends TweetsListFragment {

    private static final String TAG = MentionsTimelineFragment.class.getCanonicalName();

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }



    @Override
    public void onResume() {
        super.onResume();
        if(tweets.size()<=0) populateTimeLine(null, false);
    }

    public void populateTimeLine(Long max_id, final boolean isRefresh) {
        if (NetworkUtils.isNetworkAvailable(getActivity())&&NetworkUtils.isInternetAvailable()) {
            client.getMentionsTimeline(new JsonHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(TAG, "onSuccess: " + response.toString());

                    addItems(response,isRefresh, TimelineType.MENTIONS);


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
            addItems(null,isRefresh,TimelineType.MENTIONS);
        }
    }
}
