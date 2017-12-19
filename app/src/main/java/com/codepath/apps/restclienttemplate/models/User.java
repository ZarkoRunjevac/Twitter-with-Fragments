package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.models.db.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
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
@Parcel(analyze = {User.class})
public class User extends BaseModel {

    @Column
    @PrimaryKey
    public long uid;
    @Column
    public String name;
    @Column
    public String screenName;
    @Column
    public String profileImageUrl;
    @Column
    public String tagLine;
    @Column
    public int followersCount;
    @Column
    public int followingCount;


    public User() {

    }

    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        user.tagLine=json.getString("description");
        user.followersCount=json.getInt("followers_count");
        user.followingCount=json.getInt("friends_count");
        return user;
    }

}
