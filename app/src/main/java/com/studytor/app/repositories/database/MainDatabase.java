package com.studytor.app.repositories.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.studytor.app.repositories.models.ReplacementRoomJson;

/**
 * Created by Dawid on 28.07.2017.
 */

@Database(entities = {ReplacementRoomJson.class}, version = 1)
public abstract class MainDatabase extends RoomDatabase {
    public abstract ReplacementDao replacementDao();
}
