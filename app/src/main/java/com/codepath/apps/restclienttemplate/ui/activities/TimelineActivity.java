package com.codepath.apps.restclienttemplate.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.callback.DataLoadedListner;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.service.TwitterClient;
import com.codepath.apps.restclienttemplate.ui.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.ui.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = TimelineActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_ADD_TWEET = 0;


    private TwitterClient client;


    private User mCurrentUser;


    TweetsListFragment mFragmentTweetsList;
    ActivityTimelineBinding mBinding;


    private boolean userLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        setLoading(true);
        setSupportActionBar(mBinding.toolbar);

        client = TwitterApp.getRestClient();

        mFragmentTweetsList=(TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);



        getCurrentUser();

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkAvailable(TimelineActivity.this)&&NetworkUtils.isInternetAvailable()) {
                    NewTweetFragment newTweetFragment = NewTweetFragment.newInstance();
                    newTweetFragment.show(getFragmentManager(), "newTweetTag");
                }else {
                    Toast.makeText(TimelineActivity.this,"No internet connection",Toast.LENGTH_LONG);
                }


            }
        });




    }

    private void getCurrentUser() {
        if (NetworkUtils.isNetworkAvailable(this)&&NetworkUtils.isInternetAvailable()) {
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
            mFragmentTweetsList.addNewTweet(tweet);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    public void onNewTweetAdded(Tweet tweet) {
        mFragmentTweetsList.addNewTweet(tweet);
    }

    public User getUser() {
        return mCurrentUser;
    }



    private final DataLoadedListner mDataLoadedListner = new DataLoadedListner() {
        @Override
        public void onDataLoaded() {
            if (userLoaded ) {
                setLoading(false);
                userLoaded = false;

            }
        }
    };

    private void setLoading(boolean isLoading) {
        mBinding.setIsLoading(isLoading);

        mBinding.includedToolbar.setIsLoading(isLoading);
    }


}
