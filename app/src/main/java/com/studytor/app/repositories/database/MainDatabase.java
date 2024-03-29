package com.studytor.app.repositories.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.UserReplacementRoomJson;
import com.studytor.app.repositories.models.SingleNews;

/**
 * Created by Dawid on 28.07.2017.
 */

@Database(entities = {UserReplacementRoomJson.class, SingleInstitution.class, SingleNews.class}, version = 7)
public abstract class MainDatabase extends RoomDatabase {
    public abstract ReplacementDao replacementDao();
    public abstract InstitutionDao institutionDao();
    public abstract NewsDao newsDao();
}
