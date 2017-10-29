package com.codepath.apps.restclienttemplate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = TimelineActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_ADD_TWEET=0;

    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;

    private User mCurrentUser;

    private EndlessRecyclerViewScrollListener scrollListener;
    private Snackbar snackbar;


    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipeContainer)SwipeRefreshLayout swipeContainer;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.ivProfilePic)ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient();

        tweets = new ArrayList<>();

        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        populateTimeLine(null,false);

        getCurrentUser();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), AddNewTweetActivity.class);
//
//                intent.putExtra("user", Parcels.wrap(mCurrentUser));
//                startActivityForResult(intent,REQUEST_CODE_ADD_TWEET);

                NewTweetFragment newTweetFragment=NewTweetFragment.newInstance();
                newTweetFragment.show(getFragmentManager(),"newTweetTag");
            }
        });

        scrollListener=new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeLine(tweets.get(tweets.size()-1).uid,false);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(null,true);
            }
        });
    }

    private void getCurrentUser() {
        if(NetworkUtils.isOnline(this,rvTweets, snackbar)) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                    try {
                        mCurrentUser = User.fromJSON(response);
                        Glide.with(getApplicationContext())
                            .load(mCurrentUser.profileImageUrl)
                            .into(ivProfilePic);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            });
        }
    }


    private void populateTimeLine(Long max_id, final boolean isRefresh) {
        if(NetworkUtils.isOnline(this,rvTweets, snackbar)) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                ArrayList<Tweet> newTweets;

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                    newTweets = new ArrayList<Tweet>();
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                            tweet.user.save();
                            tweet.save();
                            newTweets.add(tweet);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if (isRefresh) {
                        tweetAdapter.clear();
                        swipeContainer.setRefreshing(false);
                    }
                    tweets.addAll(newTweets);
                    tweetAdapter.notifyDataSetChanged();

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
        }else{
            if(isRefresh){
                swipeContainer.setRefreshing(false);
                return;
            }else{
                if(tweets.isEmpty()) {
                    List<Tweet> newTweets = SQLite
                        .select()
                        .from(Tweet.class)
                        .queryList();
                    tweets.addAll(newTweets);
                    tweetAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(REQUEST_CODE_ADD_TWEET==requestCode){
            if(data==null){
                return;
            }
            Tweet tweet=(Tweet)Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tweet);
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onNewTweetAdded(Tweet tweet){
        tweets.add(0,tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    public User getUser(){
        return mCurrentUser;
    }

}
