package com.studytor.app.repositories.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.studytor.app.repositories.models.UserReplacementRoomJson;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Dawid on 28.07.2017.
 */

@Dao
public interface ReplacementDao {
    @Insert(onConflict = REPLACE)
    void insert(UserReplacementRoomJson replacement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserReplacementRoomJson> replacements);

    @Query("SELECT * FROM UserReplacementRoomJson WHERE id = :replacementId")
    LiveData<UserReplacementRoomJson> load(String replacementId);

    @Query("SELECT * FROM UserReplacementRoomJson")
    LiveData<List<UserReplacementRoomJson>> loadAll();

    @Update
    void updateReplacements(UserReplacementRoomJson... replacements);
}
