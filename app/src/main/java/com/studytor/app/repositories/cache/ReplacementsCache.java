package com.studytor.app.repositories.cache;

import android.arch.lifecycle.LiveData;

import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.SingleReplacementJson;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dawid on 11.11.2017.
 */

public class ReplacementsCache {
    private Map<String, LiveData<ReplacementsJson>> mapLiveDataReplacements = new TreeMap<>();

    public void insertOrAddReplacements(String date, LiveData<ReplacementsJson> liveDataReplacements) {
        mapLiveDataReplacements.put(date, liveDataReplacements);
    }

    public LiveData<ReplacementsJson> getReplByDate(String date) {
        return mapLiveDataReplacements.get(date);
    }
}
