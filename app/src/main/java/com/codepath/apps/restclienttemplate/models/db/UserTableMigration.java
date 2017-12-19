package com.codepath.apps.restclienttemplate.models.db;

import com.codepath.apps.restclienttemplate.models.User;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by zarkorunjevac on 10/12/17.
 */
@Migration(version = TwitterDatabase.VERSION, database = TwitterDatabase.class)
public class UserTableMigration extends AlterTableMigration<User>{
    public UserTableMigration(Class<User> table){
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT,"tagLine");
        addColumn(SQLiteType.INTEGER,"followersCount");
        addColumn(SQLiteType.INTEGER,"followingCount");
        SQLite.delete(User.class).async().execute();
    }
}
