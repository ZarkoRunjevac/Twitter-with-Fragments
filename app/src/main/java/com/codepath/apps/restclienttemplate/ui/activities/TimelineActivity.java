package com.codepath.apps.restclienttemplate.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.service.TwitterClient;
import com.codepath.apps.restclienttemplate.callback.TweetClickCallback;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.ui.fragments.NewTweetFragment;
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

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = TimelineActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_ADD_TWEET = 0;
    public static final String TWEET = "com.codepath.apps.restclienttemplate.adapters.TweetAdapter.TWEET";

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;

    private User mCurrentUser;

    private EndlessRecyclerViewScrollListener scrollListener;
    private Snackbar snackbar;

    ActivityTimelineBinding mBinding;

    private boolean tweetsLoaded = false;
    private boolean userLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        setLoading(true);
        setSupportActionBar(mBinding.toolbar);

        client = TwitterApp.getRestClient();

        tweets = new ArrayList<>();

        tweetAdapter = new TweetAdapter(this, mTweetClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mBinding.included.rvTweets.setLayoutManager(linearLayoutManager);
        mBinding.included.rvTweets.setAdapter(tweetAdapter);

        populateTimeLine(null, false);

        getCurrentUser();

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isOnline(TimelineActivity.this, mBinding.included.rvTweets, snackbar)) {
                    NewTweetFragment newTweetFragment = NewTweetFragment.newInstance();
                    newTweetFragment.show(getFragmentManager(), "newTweetTag");
                }else {
                    Toast.makeText(TimelineActivity.this,"No internet connection",Toast.LENGTH_LONG);
                }


            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeLine(tweets.get(tweets.size() - 1).uid, false);
            }
        };

        mBinding.included.rvTweets.addOnScrollListener(scrollListener);

        mBinding.included.swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(null, true);
            }
        });


    }

    private void getCurrentUser() {
        if (NetworkUtils.isOnline(this, mBinding.included.rvTweets, snackbar)) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: " + response.toString());
                    try {
                        mCurrentUser = User.fromJSON(response);
                        Glide.with(getApplicationContext())
                                .load(mCurrentUser.profileImageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(mBinding.includedToolbar.ivProfilePic);
                        userLoaded = true;
                        mDataLoadedListner.onDataLoaded();
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
        } else {
            userLoaded = true;
            mDataLoadedListner.onDataLoaded();
        }
    }


    private void populateTimeLine(Long max_id, final boolean isRefresh) {
        if (NetworkUtils.isOnline(this, mBinding.included.rvTweets, snackbar)) {
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
                        mBinding.included.swipeContainer.setRefreshing(false);

                    }
                    {
                        tweetsLoaded = true;
                        mDataLoadedListner.onDataLoaded();
                    }

                    tweets.addAll(newTweets);
                    tweetAdapter.setTweetList(tweets);
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
        } else {
            if (isRefresh) {
                mBinding.included.swipeContainer.setRefreshing(false);
                return;
            } else {
                if (tweets.isEmpty()) {
                    List<Tweet> newTweets = SQLite
                            .select()
                            .from(Tweet.class)
                            .queryList();
                    tweets.addAll(newTweets);
                    tweetAdapter.setTweetList(tweets);
                    tweetAdapter.notifyDataSetChanged();
                    tweetsLoaded = true;
                    mDataLoadedListner.onDataLoaded();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (REQUEST_CODE_ADD_TWEET == requestCode) {
            if (data == null) {
                return;
            }
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    public void onNewTweetAdded(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    public User getUser() {
        return mCurrentUser;
    }

    private final TweetClickCallback mTweetClickCallback = new TweetClickCallback() {
        @Override
        public void onClick(Tweet tweet) {
            Intent intent = new Intent(getApplication(), TweetActivity.class);
            intent.putExtra(TWEET, Parcels.wrap(tweet));
            startActivity(intent);
        }
    };

    private final DataLoadedListner mDataLoadedListner = new DataLoadedListner() {
        @Override
        public void onDataLoaded() {
            if (userLoaded && tweetsLoaded) {
                setLoading(false);
                userLoaded = false;
                tweetsLoaded = false;
            }
        }
    };

    private void setLoading(boolean isLoading) {
        mBinding.setIsLoading(isLoading);
        mBinding.included.setIsLoading(isLoading);
        mBinding.includedToolbar.setIsLoading(isLoading);
    }

    private interface DataLoadedListner {
        void onDataLoaded();
    }

}
