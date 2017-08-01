package com.replacements.replacements.repositories.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.replacements.replacements.repositories.models.ReplacementRoomJson;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Dawid on 28.07.2017.
 */

@Dao
public interface ReplacementDao {
    @Insert(onConflict = REPLACE)
    void insert(ReplacementRoomJson replacement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReplacementRoomJson> replacements);

    @Query("SELECT * FROM replacementroomjson WHERE id = :replacementId")
    LiveData<ReplacementRoomJson> load(String replacementId);

    @Query("SELECT * FROM replacementroomjson")
    LiveData<List<ReplacementRoomJson>> loadAll();

    @Update
    void updateReplacements(ReplacementRoomJson... replacements);
}
