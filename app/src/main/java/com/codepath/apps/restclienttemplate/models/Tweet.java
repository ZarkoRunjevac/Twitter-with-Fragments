package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by zarko.runjevac on 10/10/2017.
 */
@Table(database = TwitterDatabase.class)
@Parcel(analyze = {Tweet.class})
public class Tweet extends BaseModel {

    @Column
    @PrimaryKey
    public long uid;
    @Column
    public String createdAt;
    @Column
    @ForeignKey
    public User user;
    @Column
    public String body;

    public Tweet() {

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }

}
