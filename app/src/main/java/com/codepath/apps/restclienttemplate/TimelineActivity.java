package com.codepath.apps.restclienttemplate;

import static com.codepath.apps.restclienttemplate.models.Tweet.fromJson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import java.sql.Time;
import java.util.ArrayList;
import java.util.jar.JarException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimelineActivity extends AppCompatActivity {
  
  private static final String TAG= TimelineActivity.class.getCanonicalName();

  private TwitterClient client;
  TweetAdapter tweetAdapter;
  ArrayList<Tweet> tweets;
  RecyclerView rvTweets;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);

    client=TwitterApp.getRestClient();

    rvTweets=(RecyclerView)findViewById(R.id.rvTweets);

    tweets=new ArrayList<>();

    tweetAdapter=new TweetAdapter(tweets);

    rvTweets.setLayoutManager(new LinearLayoutManager(this));
    rvTweets.setAdapter(tweetAdapter);

    populateTimeLine();
  }

  private void populateTimeLine(){
    client.getHomeTimeline(new JsonHttpResponseHandler(){
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG, "onSuccess: "+response.toString());
      }

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG, "onSuccess: "+response.toString());
        for(int i=0;i<response.length();i++){
          try {
            Tweet tweet= Tweet.fromJson(response.getJSONObject(i));
            tweets.add(tweet);
            tweetAdapter.notifyItemInserted(tweets.size()-1);
          }catch (JSONException e){
            e.printStackTrace();
          }

        }
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable,
          JSONObject errorResponse) {
        Log.d(TAG, "onSuccess: "+errorResponse.toString());
        throwable.printStackTrace();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable,
          JSONArray errorResponse) {
        Log.d(TAG, "onSuccess: "+errorResponse.toString());
        throwable.printStackTrace();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString,
          Throwable throwable) {
        Log.d(TAG, "onSuccess: "+responseString.toString());
        throwable.printStackTrace();
      }
    });
  }
}
