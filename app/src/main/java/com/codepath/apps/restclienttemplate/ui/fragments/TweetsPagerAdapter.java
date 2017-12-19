package com.codepath.apps.restclienttemplate.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by zarkorunjevac on 03/12/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String mTabTitles[]=new String[]{"Home","Mentions"};

    private WeakReference<Context> mContext;

    public TweetsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mContext=new WeakReference<Context>(context);
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public Fragment getItem(int position) {
        if(0==position){
            return new HomeTimelineFragment();
        }else if(1==position){
            return new MentionsTimelineFragment();
        }else{
            return null;
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
