package com.studytor.app.repositories.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.studytor.app.models.SingleInstitution;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by przemek19980102 on 24.10.2017.
 */

@Dao
public interface InstitutionDao {
    @Insert(onConflict = REPLACE)
    void insert(SingleInstitution institution);

    @Insert(onConflict = REPLACE)
    void insertAll(List<SingleInstitution> institutions);

    @Query("SELECT * FROM singleInstitution WHERE id = :institutionId")
    LiveData<SingleInstitution> load(int institutionId);

    @Query("SELECT * FROM singleInstitution")
    LiveData<List<SingleInstitution>> loadAll();

    @Update
    void updateReplacements(SingleInstitution... replacements);
}
