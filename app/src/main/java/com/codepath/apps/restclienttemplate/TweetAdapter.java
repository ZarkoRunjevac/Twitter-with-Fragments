package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import java.util.List;

/**
 * Created by zarko.runjevac on 10/10/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

  private List<Tweet> mTweets;
  Context mContext;

  public TweetAdapter(List<Tweet> tweets){
    mTweets=tweets;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext=parent.getContext();

    LayoutInflater inflater=LayoutInflater.from(mContext);

    View tweeetView=inflater.inflate(R.layout.item_tweet,parent,false);

    ViewHolder viewHolder=new ViewHolder(tweeetView);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Tweet tweet=mTweets.get(position);

    holder.tvUsername.setText(tweet.user.name);
    holder.tvBody.setText(tweet.body);

    Glide.with(mContext)
        .load(tweet.user.profileImageUrl)
        .into(holder.ivProfileImage);
  }

  @Override
  public int getItemCount() {
    return mTweets.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder{

    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvBody;

    public ViewHolder(View view){
      super(view);

      ivProfileImage=(ImageView) view.findViewById(R.id.ivProfileImage);
      tvUsername=(TextView) view.findViewById(R.id.tvUserName);
      tvBody=(TextView)view.findViewById(R.id.tvBody);
    }


  }

}
