package com.codepath.apps.restclienttemplate.models.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TwitterDatabase.NAME, version = TwitterDatabase.VERSION)
public class TwitterDatabase {

    public static final String NAME = "TwitterDatabase";

    public static final int VERSION = 2;
}
