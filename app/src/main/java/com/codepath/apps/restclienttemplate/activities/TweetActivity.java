package com.codepath.apps.restclienttemplate.activities;

import static com.codepath.apps.restclienttemplate.R.id.ivProfilePic;
import static com.codepath.apps.restclienttemplate.R.id.tvBody;
import static com.codepath.apps.restclienttemplate.R.id.tvName;
import static com.codepath.apps.restclienttemplate.R.id.tvTimestamp;
import static com.codepath.apps.restclienttemplate.R.id.tvUserName;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.Utils;
import org.parceler.Parcels;

public class TweetActivity extends AppCompatActivity {

  @BindView(R.id.ivProfileImage)ImageView ivProfileImage;
  @BindView(R.id.tvName)TextView tvName;
  @BindView(R.id.tvUserName)TextView tvUserName;
  @BindView(R.id.tvTimestamp)TextView tvTimestamp;
  @BindView(R.id.tvBody)TextView tvBody;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tweet);
    ButterKnife.bind(this);

    final Tweet tweet=(Tweet) Parcels.unwrap(getIntent().getParcelableExtra(TweetAdapter.TWEET));

    Glide.with(getApplicationContext())
        .load(tweet.user.profileImageUrl)
        .into(ivProfileImage);

    tvName.setText("@"+tweet.user.screenName);
    tvUserName.setText(tweet.user.name);
    tvBody.setText(tweet.body);
    tvTimestamp.setText(Utils.getRelativeTimeAgo(tweet.createdAt));
  }
}
