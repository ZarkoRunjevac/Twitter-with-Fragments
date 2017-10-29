package com.codepath.apps.restclienttemplate.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by zarkorunjevac on 29/10/17.
 */

public class NewTweetFragment extends DialogFragment{



    private TwitterClient client;

    @BindView(R.id.ivDiscard)ImageView ivDiscard;
    @BindView(R.id.ivProfilePicture)ImageView ivProfilePicture;
    @BindView(R.id.etTweet)EditText etTweet;
    @BindView(R.id.btTweet)Button btTweet;
    @BindView(R.id.tvCharCounter)TextView tvCharCounter;


    private int mCharCount=140;

    private Unbinder unbinder;

    private  User mCurrentUser;



    public static NewTweetFragment newInstance(){

        return new NewTweetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TweetDialog);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_add_new_tweet,container,false);
        unbinder= ButterKnife.bind(this,view);
        Bundle bundle=getActivity().getIntent().getExtras();

        mCurrentUser=((TimelineActivity) getActivity()).getUser();
        client = TwitterApp.getRestClient();

        Glide.with(this)
                .load(mCurrentUser.profileImageUrl)
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
                dismiss();
            }
        });

        initTextWatcher();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();

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
                    //setResult(RESULT_OK,intent);
                    TimelineActivity timelineActivity=(TimelineActivity)getActivity();
                    timelineActivity.onNewTweetAdded(tweet);
                    dismiss();
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
