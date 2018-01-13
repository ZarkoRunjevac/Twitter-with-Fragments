package com.codepath.apps.restclienttemplate.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.ui.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.utils.Utils;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.util.TextUtils;

public class TweetActivity extends AppCompatActivity {

    private static final String TAG = TweetActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_REPLY_T0_TWEET = 0;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvTimestamp)
    TextView tvTimestamp;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.btnReply)
    ImageButton btnReply;
    @BindView(R.id.ivEmbeddedImage)
    ImageView ivEmbeddedImage;
    @BindView(R.id.vvVideoPlayer)
    VideoView vvVideoPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);

        final Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(TweetsListFragment.TWEET));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(getApplicationContext())
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);

        tvName.setText("@" + tweet.user.screenName);
        tvUserName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvTimestamp.setText(Utils.getRelativeTimeAgo(tweet.createdAt));

        if (!TextUtils.isEmpty(tweet.embeddedVideo)){


            vvVideoPlayer.setVisibility(View.VISIBLE);
            vvVideoPlayer.setVideoPath(tweet.embeddedVideo);

            vvVideoPlayer.start();
            
        }else{
            if(!TextUtils.isEmpty(tweet.embeddedImage)){
                ivEmbeddedImage.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(tweet.embeddedImage)
                        .into(ivEmbeddedImage);
            }
        }



        btnReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReplyToTweetActivity.class);

                intent.putExtra("originalTweet", Parcels.wrap(tweet));
                startActivityForResult(intent, REQUEST_CODE_REPLY_T0_TWEET);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (REQUEST_CODE_REPLY_T0_TWEET == requestCode) {
            if (data == null) {
                return;
            }
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("replyTweet"));
//      tweets.add(0,tweet);
//      tweetAdapter.notifyDataSetChanged();
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
}
