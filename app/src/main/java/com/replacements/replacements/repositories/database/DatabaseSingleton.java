package com.replacements.replacements.repositories.database;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by Dawid on 28.07.2017.
 */

public class DatabaseSingleton {
    private static DatabaseSingleton databaseSingleton;
    private MainDatabase mainDatabase;

    private DatabaseSingleton(Context context){
        getDatabase(context);
    }

    private void getDatabase(Context context) {
        this.mainDatabase = Room.databaseBuilder(context, MainDatabase.class, "database-name").build();
    }

    public static DatabaseSingleton getInstance(Context context) {
        if(databaseSingleton == null) {
            databaseSingleton = new DatabaseSingleton(context);
        }

        return databaseSingleton;
    }

    public MainDatabase getMainDatabase() {
        return this.mainDatabase;
    }
}
