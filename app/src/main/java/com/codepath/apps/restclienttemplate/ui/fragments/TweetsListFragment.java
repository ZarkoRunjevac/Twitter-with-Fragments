package com.codepath.apps.restclienttemplate.ui.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.callback.DataLoadedListner;
import com.codepath.apps.restclienttemplate.callback.TweetClickCallback;
import com.codepath.apps.restclienttemplate.callback.UserClickCallback;
import com.codepath.apps.restclienttemplate.databinding.FragmentsTweetsListBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.ui.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.ui.activities.TweetActivity;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TimelineType;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zarkorunjevac on 19/11/17.
 */

public  abstract class TweetsListFragment extends Fragment {

    private static final String TAG = TweetsListFragment.class.getCanonicalName();
    public static final String TWEET = "com.codepath.apps.restclienttemplate.ui.fragments.TweetsListFragment.TWEET";

    private boolean tweetsLoaded = false;
    public FragmentsTweetsListBinding mBinding;

    TweetAdapter tweetAdapter;
    public ArrayList<Tweet> tweets;

    public EndlessRecyclerViewScrollListener scrollListener;
    private Snackbar snackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragments_tweets_list, container, false);

        setLoading(true);

        tweetAdapter = new TweetAdapter(getActivity(), mTweetClickCallback, mUserClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mBinding.included.rvTweets.setLayoutManager(linearLayoutManager);
        mBinding.included.rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                //remove last item because twitter api sends double
                Log.d(TAG, "onLoadMore: ");
                long max_id=tweets.get(tweets.size() - 1).uid;
                tweets.remove(tweets.size() - 1);
                mBinding.included.rvTweets.post(new Runnable() {
                    @Override
                    public void run() {
                        tweetAdapter.notifyDataSetChanged();
                    }
                });

                populateTimeLine(max_id, false);
            }
        };

        mBinding.included.rvTweets.addOnScrollListener(scrollListener);



        mBinding.included.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(null, true);
            }
        });

        return mBinding.getRoot();
    }


    private final TweetClickCallback mTweetClickCallback = new TweetClickCallback() {
        @Override
        public void onClick(Tweet tweet) {
            Intent intent = new Intent(getContext(), TweetActivity.class);
            intent.putExtra(TWEET, Parcels.wrap(tweet));
            startActivity(intent);
        }
    };

    private final UserClickCallback mUserClickCallback=new UserClickCallback() {
        @Override
        public void onClick(Tweet tweet) {
            Intent intent=new Intent(getContext(),ProfileActivity.class);
            intent.putExtra(TWEET, Parcels.wrap(tweet));
            startActivity(intent);
        }
    };

    public void addItems(JSONArray response, final boolean isRefresh, TimelineType timelineType) {

        if (response != null) {


            ArrayList<Tweet> newTweets = new ArrayList<Tweet>();
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

            }else
            {
                tweetsLoaded = true;
                mDataLoadedListner.onDataLoaded();
            }
            if(newTweets.size()<24){//if it has no more new tweets (page size is 25) Scroll listner is not needed
                removeScrollListener();
            }
            tweets.addAll(newTweets);
            tweetAdapter.setTweetList(tweets);
            tweetAdapter.notifyDataSetChanged();
        } else {
            if(timelineType==TimelineType.HOME){
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

    }

    private final DataLoadedListner mDataLoadedListner = new DataLoadedListner() {
        @Override
        public void onDataLoaded() {
            if (tweetsLoaded ) {
                setLoading(false);
                tweetsLoaded = false;

            }
        }
    };

    private void setLoading(boolean isLoading) {


        mBinding.included.setIsLoading(isLoading);
    }

    public void addNewTweet(Tweet tweet){
        tweets.add(0, tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    public void removeScrollListener(){
        mBinding.included.rvTweets.removeOnScrollListener(scrollListener);
    }



    public abstract void populateTimeLine(Long max_id, final boolean isRefresh) ;
}
