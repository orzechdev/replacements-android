package com.replacements.replacements.repositories.cache;

import android.arch.lifecycle.LiveData;

import com.replacements.replacements.repositories.database.Replacement;
import com.replacements.replacements.repositories.models.ReplacementRoomJson;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentReplacementsCache {

//    private Map<String, ReplacementCache> jsonReplMapToday = new TreeMap<>();
//    private Map<String, ReplacementCache> jsonReplMapTomorrow = new TreeMap<>();
//    private Map<String, ReplacementCache> jsonReplMapOther = new TreeMap<>();
    private Map<String, ReplacementCache> replsMapToday = new TreeMap<>();
    private Map<String, ReplacementCache> replsMapTomorrow = new TreeMap<>();
    private Map<String, ReplacementCache> replsMapOther = new TreeMap<>();

    private String today = "";
    private String tomorrow = "";
    private String other = "";

    //public boolean putRepl(MutableLiveData<ReplacementsJson> jsonReplacements, String institutionId, String ver, String date) {
    public boolean putRepl(LiveData<List<ReplacementRoomJson>> listLiveDataReplacement, String institutionId, String ver, String date) {
        if(date.equals(""))
            return false;
        if (date.equals(today))
            return putReplToday(listLiveDataReplacement, institutionId, ver);
        else if (date.equals(tomorrow))
            return putReplTomorrow(listLiveDataReplacement, institutionId, ver);
        this.other = date;
        return putReplOther(listLiveDataReplacement, institutionId, ver);
    }

    //public boolean putReplToday(MutableLiveData<ReplacementsJson> jsonReplacements, String institutionId, String ver) {
    public boolean putReplToday(LiveData<List<ReplacementRoomJson>> listLiveDataReplacement, String institutionId, String ver) {
        replsMapToday.put(institutionId, new ReplacementCache(listLiveDataReplacement, ver));
        return true;
    }
    //public boolean putReplTomorrow(MutableLiveData<ReplacementsJson> jsonReplacements, String institutionId, String ver) {
    public boolean putReplTomorrow(LiveData<List<ReplacementRoomJson>> listLiveDataReplacement, String institutionId, String ver) {
        replsMapTomorrow.put(institutionId, new ReplacementCache(listLiveDataReplacement, ver));
        return true;
    }
    //public boolean putReplOther(MutableLiveData<ReplacementsJson> jsonReplacements, String institutionId, String ver) {
    public boolean putReplOther(LiveData<List<ReplacementRoomJson>> listLiveDataReplacement, String institutionId, String ver) {
        replsMapOther.put(institutionId, new ReplacementCache(listLiveDataReplacement, ver));
        return true;
    }

    public ReplacementCache getRepl(String institutionId, String date) {
        if(date.equals(""))
            return null;
        if (date.equals(today))
            return getReplToday(institutionId);
        else if (date.equals(tomorrow))
            return getReplTomorrow(institutionId);
        else if (date.equals(other))
            return getReplOther(institutionId);
        return null;
    }

    public ReplacementCache getReplToday(String institutionId) {
        return replsMapToday.get(institutionId);
    }

    public ReplacementCache getReplTomorrow(String institutionId) {
        return replsMapTomorrow.get(institutionId);
    }

    public ReplacementCache getReplOther(String institutionId) {
        return replsMapOther.get(institutionId);
    }

    public String getToday() {
        return this.today;
    }

    public boolean setTodayAndTomorrow(String today, String tomorrow) {
        return refreshDays(today, tomorrow);
    }

    public boolean refreshDays(String today, String tomorrow) {
        //TODO
        if (!today.equals(this.today)) {
            // delete today repls
            if(today.equals(this.tomorrow)) {
                // take tomorrow repls into today repls
                // delete tomorrow repls
            }
            if(today.equals(this.other)) {
                // take other repls into today repls
                // delete other repls
            }
            // set this.today as today
        }
        if (!tomorrow.equals(this.tomorrow)) {
            // delete tomorrow repls
            if(tomorrow.equals(this.today)) {
                // take tomorrow repls into today repls
                // delete tomorrow repls
            }
            if(tomorrow.equals(this.other)) {
                // take other repls into today repls
                // delete other repls
            }
            // set this.tomorrow as tomorrow
        }
        return false;
    }

    public class ReplacementCache {
        //private MutableLiveData<ReplacementsJson> jsonReplMap;
        //private MutableLiveData<Replacement> replacementsModel;
        private LiveData<List<ReplacementRoomJson>> listLiveDataReplacement;
        private String ver;

        //public ReplacementCache(MutableLiveData<ReplacementsJson> jsonReplMap, String ver) {
        public ReplacementCache(LiveData<List<ReplacementRoomJson>> listLiveDataReplacement, String ver) {
            this.listLiveDataReplacement = listLiveDataReplacement;
            this.ver = ver;
        }

        //public MutableLiveData<ReplacementsJson> getJsonReplMap() {
        public LiveData<List<ReplacementRoomJson>> getReplModelMap() {
            return listLiveDataReplacement;
        }

        public String getVer() {
            return ver;
        }

        public void addReplacement(Replacement newReplacement) {
        }
    }
}
