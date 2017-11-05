package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.models.db.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
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

    @Column
    public String embeddedImage="";

    @Column
    public String embeddedVideo="";

    public Tweet() {

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        JSONObject entities=jsonObject.getJSONObject("entities");
        if(entities!=null && entities.has("media")){

            JSONArray media=entities.getJSONArray("media");
            if(media!=null){
                for(int i=0;i<media.length();++i){
                    JSONObject object=media.getJSONObject(i);
                    String type=object.getString("type");
                    if(type.equals("photo")){
                        tweet.embeddedImage=object.getString("media_url");
                    }

                }
            }

        }
        if(jsonObject.has("extended_entities")){
            JSONObject extended_entities=jsonObject.getJSONObject("extended_entities");
            if(extended_entities!=null && extended_entities.has("media")){

                JSONArray media=extended_entities.getJSONArray("media");
                if(media!=null){
                    for(int i=0;i<media.length();++i){
                        JSONObject object=media.getJSONObject(i);
                        String type=object.getString("type");

                        if(type.equals("video")){
                            tweet.embeddedVideo=object.getString("media_url");
                            JSONObject video_info=object.getJSONObject("video_info");
                            if(video_info!=null && video_info.has("variants")){
                                JSONArray variants=video_info.getJSONArray("variants");
                                if(variants!=null&&variants.length()>0){
                                    JSONObject video=variants.getJSONObject(0);
                                    tweet.embeddedVideo=video.getString("url");
                                }
                            }
                        }
                    }
                }

            }
        }



        return tweet;
    }

}
