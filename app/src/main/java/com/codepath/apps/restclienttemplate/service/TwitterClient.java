package com.codepath.apps.restclienttemplate.service;

import android.content.Context;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "q9SOpFcdl0GXiZ0VeMTGjIYew";       // Change this
    public static final String REST_CONSUMER_SECRET = "JcBEWoaTjlX6fvgsKYWS1haCtkovgUQCIdJJJ8sTKnoBtecekq"; // Change this

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getHomeTimeline(AsyncHttpResponseHandler handler,Long max_id) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", 100);
        if(max_id==null){
            params.put("since_id", 1);
        }else{
            params.put("max_id",max_id);
        }

        client.get(apiUrl, params, handler);
    }

    public void addNewTweet(AsyncHttpResponseHandler handler, String tweet) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();

        params.put("status", tweet);
        client.post(apiUrl, params, handler);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");

        client.get(apiUrl,null,handler);
    }

    public void getUser(AsyncHttpResponseHandler handler,long userId){
        String apiUrl = getApiUrl("users/lookup.json");
        RequestParams params = new RequestParams();

        params.put("user_id",userId);
        client.get(apiUrl,params,handler);

    }

    public void replyToTweet(AsyncHttpResponseHandler handler,long tweetId, String reply){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();

        params.put("status", reply);
        params.put("in_reply_to_status_id",tweetId);
        client.post(apiUrl, params, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler,Long max_id) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", 100);
        if(max_id==null){
            params.put("since_id", 1);
        }else{
            params.put("max_id",max_id);
        }

        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(AsyncHttpResponseHandler handler,Long max_id, String screenName) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("screen_name",screenName);
        params.put("count", 100);
        if(max_id==null){
            params.put("since_id", 1);
        }else{
            params.put("max_id",max_id);
        }

        client.get(apiUrl, params, handler);
    }


}
