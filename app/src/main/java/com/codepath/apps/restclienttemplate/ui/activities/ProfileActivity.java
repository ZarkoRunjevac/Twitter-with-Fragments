package com.codepath.apps.restclienttemplate.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.service.TwitterClient;
import com.codepath.apps.restclienttemplate.ui.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.ui.fragments.UserTimeLineFragment;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getCanonicalName();

    ActivityProfileBinding mBinding;

    User mCurrentUser;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        client = TwitterApp.getRestClient();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        final Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(TweetsListFragment.TWEET));

        if(mCurrentUser==null){
            getUser(tweet);
        }else{
            setUpActivity();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUser(Tweet tweet){

            if (NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isInternetAvailable()) {
                client.getUser(new JsonHttpResponseHandler() {



                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d(TAG, "onSuccess: " + response.toString());
                        try {
                            mCurrentUser = User.fromJSON(response.getJSONObject(0));
                            setUpActivity();

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
                },tweet.user.uid);
            }
        }

        private void setUpActivity(){
            getSupportActionBar().setTitle(mCurrentUser.name);

            UserTimeLineFragment userTimeLineFragment=UserTimeLineFragment.newInstance(mCurrentUser.screenName);

            android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.flContainer,userTimeLineFragment).commit();
            Glide.with(this)
                    .load(mCurrentUser.profileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mBinding.ivProfileImage);

            mBinding.setUser(mCurrentUser);
        }
    }


