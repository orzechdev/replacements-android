package com.replacements.replacements.repositories.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Dawid on 28.07.2017.
 */

@Dao
public interface ReplacementDao {
    @Insert(onConflict = REPLACE)
    void save(Replacement replacement);

    @Query("SELECT * FROM replacement WHERE id = :replacementId")
    LiveData<Replacement> load(String replacementId);

    @Query("SELECT * FROM replacement")
    LiveData<List<Replacement>> loadAll();

    @Update
    void updateReplacements(Replacement... replacements);
}
