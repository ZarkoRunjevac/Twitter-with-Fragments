package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class AddNewTweetActivity extends AppCompatActivity {

    private static final String TAG = AddNewTweetActivity.class.getCanonicalName();
    private static final String TWEET_IS_ADDED="com.codepath.apps.restclienttemplate.activities.tweet_added";



    private TwitterClient client;

    @BindView(R.id.ivDiscard)ImageView ivDiscard;
    @BindView(R.id.ivProfilePicture)ImageView ivProfilePicture;
    @BindView(R.id.etTweet)EditText etTweet;
    @BindView(R.id.btTweet)Button btTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tweet);
        ButterKnife.bind(this);

        final User currentUser=(User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        client = TwitterApp.getRestClient();

        Glide.with(this)
                .load(currentUser.profileImageUrl)
                .into(ivProfilePicture);

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText=etTweet.getText().toString();
                if(!TextUtils.isEmpty(tweetText)){
                    tweet(tweetText);
                }
            }
        });

        ivDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initTextWatcher();

    }

    private void tweet(String tweetText){
        client.addNewTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
                try {
                    Tweet tweet = Tweet.fromJson(response);
                    Intent intent=new Intent();
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK,intent);
                    finish();
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
        },tweetText);
    }

    private void initTextWatcher(){
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(0==charSequence.length()) btTweet.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(etTweet.getText())) btTweet.setEnabled(true);
            }
        };

        etTweet.addTextChangedListener(textWatcher);
    }
}
