package com.studytor.app.repositories.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by przemek19980102 on 31.10.2017.
 */

@Dao
public interface NewsDao {
    @Insert(onConflict = REPLACE)
    void insert(SingleNews news);

    @Insert(onConflict = REPLACE)
    void insertAll(List<SingleNews> newsList);

    @Query("SELECT * FROM singleNews WHERE id = :newsId")
    LiveData<SingleNews> load(int newsId);

    @Query("SELECT * FROM singleNews WHERE institutionId = :institutionId ORDER BY date DESC")
    LiveData<List<SingleNews>> loadByInstitution(int institutionId);

    @Query("SELECT * FROM singleNews")
    LiveData<List<SingleNews>> loadAll();

    @Update
    void updateNews(SingleNews... replacements);
}
