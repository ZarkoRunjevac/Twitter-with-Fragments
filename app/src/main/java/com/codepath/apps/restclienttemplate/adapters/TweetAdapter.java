package com.codepath.apps.restclienttemplate.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.Callback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.callback.TweetClickCallback;
import com.codepath.apps.restclienttemplate.callback.UserClickCallback;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by zarko.runjevac on 10/10/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {



    @Nullable
    private final TweetClickCallback mTweetClickCallback;

    @Nullable
    private final UserClickCallback mUserClickCallback;

    private List<? extends Tweet> mTweets;
    final WeakReference<Activity> mContext;

    private Snackbar snackbar;



    public TweetAdapter(Activity context, @Nullable TweetClickCallback tweetClickCallback,@Nullable UserClickCallback userClickCallback) {
        mTweetClickCallback=tweetClickCallback;
        mUserClickCallback=userClickCallback;
        mContext=new WeakReference<Activity>(context);
    }

    public void setTweetList(final List<? extends Tweet> tweetList){
        if(mTweets==null){
            mTweets=tweetList;
            notifyItemRangeChanged(0,tweetList.size());
        }else{
            DiffUtil.DiffResult result=DiffUtil.calculateDiff(new Callback() {
                @Override
                public int getOldListSize() {
                    return mTweets.size();
                }

                @Override
                public int getNewListSize() {
                    return tweetList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mTweets.get(oldItemPosition).uid==tweetList.get(newItemPosition).uid;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Tweet tweet=tweetList.get(newItemPosition);
                    Tweet old=mTweets.get(oldItemPosition);
                    return tweet.uid==old.uid;
                }
            });
            mTweets=tweetList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      ItemTweetBinding binding= DataBindingUtil
          .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_tweet,
              parent,false);
        binding.setCallback(mTweetClickCallback);
        binding.setUserClickCallback(mUserClickCallback);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.binding.setTweet(tweet);

        holder.binding.vvVideoPlayer.setVisibility(View.GONE);
        holder.binding.ivEmbeddedImage.setVisibility(View.GONE);

        Glide.with(mContext.get())
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.binding.ivProfileImage);
        holder.binding.ivEmbeddedImage.setImageResource(0);

        if (!TextUtils.isEmpty(tweet.embeddedVideo)){


            holder.binding.vvVideoPlayer.setVisibility(View.VISIBLE);
            if(NetworkUtils.isInternetAvailable()&&NetworkUtils.isNetworkAvailable(mContext.get())){
            holder.binding.vvVideoPlayer.setVideoPath(tweet.embeddedVideo);

                holder.binding.vvVideoPlayer.start();
            }



        }else {
            if (!TextUtils.isEmpty(tweet.embeddedImage)) {
                holder.binding.ivEmbeddedImage.setVisibility(View.VISIBLE);
                Glide.with(mContext.get())
                        .load(tweet.embeddedImage)
                        .into(holder.binding.ivEmbeddedImage);
            }
        }
    }

    @Override
    public int getItemCount() {
       return mTweets==null ? 0 : mTweets.size();
    }

    public void clear(){
        if(mTweets!=null){
        mTweets.clear();
        notifyDataSetChanged();
        }
    }



    public  class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public final ItemTweetBinding binding;



        public ViewHolder( ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding=binding;


        }


    }

}
