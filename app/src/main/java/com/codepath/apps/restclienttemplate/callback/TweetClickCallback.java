package com.codepath.apps.restclienttemplate.callback;

import com.codepath.apps.restclienttemplate.models.Tweet;

/**
 * Created by zarko.runjevac on 10/30/2017.
 */

public interface TweetClickCallback {
  void onClick(Tweet tweet);
}
