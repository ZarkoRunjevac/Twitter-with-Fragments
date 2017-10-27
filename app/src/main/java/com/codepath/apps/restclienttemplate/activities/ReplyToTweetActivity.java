package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class ReplyToTweetActivity extends AppCompatActivity {

  private static final String TAG =ReplyToTweetActivity.class.getCanonicalName();

  private TwitterClient client;

  @BindView(R.id.ivDiscard)ImageView ivDiscard;
  @BindView(R.id.ivProfilePicture)ImageView ivProfilePicture;
  @BindView(R.id.etTweet)EditText etTweet;
  @BindView(R.id.btTweet)Button btTweet;
  @BindView(R.id.tvCharCounter)TextView tvCharCounter;
  @BindView(R.id.tvUsername) TextView tvUsername;

  private int mCharCount=140;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reply_to_tweet);
    ButterKnife.bind(this);

    client = TwitterApp.getRestClient();


    final Tweet originalTweet=(Tweet)  Parcels.unwrap(getIntent().getParcelableExtra("originalTweet"));

    Glide.with(this)
        .load(originalTweet.user.profileImageUrl)
        .into(ivProfilePicture);

    btTweet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String tweetText=etTweet.getText().toString();
        if(!TextUtils.isEmpty(tweetText)){
          reply(originalTweet.uid, tweetText);
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

  private void reply(long tweetId, String tweetText){
    client.replyToTweet(new JsonHttpResponseHandler(){
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "onSuccess: " + response.toString());
        try {
          Tweet tweet = Tweet.fromJson(response);
          Intent intent=new Intent();
          intent.putExtra("replyTweet", Parcels.wrap(tweet));
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
    },tweetId,tweetText);
  }

  private void initTextWatcher(){
    TextWatcher textWatcher=new TextWatcher() {
      int charCount=0;
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(0==charSequence.length()) btTweet.setEnabled(false);
        charCount=charSequence.length();

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if(!TextUtils.isEmpty(etTweet.getText())) btTweet.setEnabled(true);
        tvCharCounter.setText(Integer.toString(mCharCount-charCount));
      }
    };

    etTweet.addTextChangedListener(textWatcher);
  }
}
