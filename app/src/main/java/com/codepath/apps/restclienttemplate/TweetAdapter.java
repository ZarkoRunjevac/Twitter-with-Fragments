package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activities.TweetActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.Utils;

import java.util.List;
import org.parceler.Parcels;

/**
 * Created by zarko.runjevac on 10/10/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {


    public static final String TWEET="com.codepath.apps.restclienttemplate.TweetAdapter.TWEET";

    private List<Tweet> mTweets;
    Context mContext;



    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweeetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweeetView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvName.setText("@"+tweet.user.screenName);
        holder.tvTimestamp.setText(Utils.getRelativeTimeAgo(tweet.createdAt));

        Glide.with(mContext)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void clear(){
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list){
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvName;
        public TextView tvTimestamp;
        private Context context;

        public ViewHolder( View view) {
            super(view);

            this.context= view.getContext();


            ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) view.findViewById(R.id.tvUserName);
            tvBody = (TextView) view.findViewById(R.id.tvBody);
            tvName=(TextView) view.findViewById(R.id.tvName);
            tvTimestamp=(TextView) view.findViewById(R.id.tvTimestamp);
            tvBody.setOnClickListener(this);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION){
                Tweet tweet=mTweets.get(position);

                Intent intent=new Intent(mContext, TweetActivity.class);
                intent.putExtra(TweetAdapter.TWEET, Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }

}
