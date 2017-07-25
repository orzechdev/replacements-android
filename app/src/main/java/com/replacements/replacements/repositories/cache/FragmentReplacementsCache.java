package com.replacements.replacements.repositories.cache;

import android.arch.lifecycle.MutableLiveData;

import com.replacements.replacements.models.JsonReplacements;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentReplacementsCache {

    private Map<String, ReplacementCache> jsonReplMapToday = new TreeMap<>();
    private Map<String, ReplacementCache> jsonReplMapTomorrow = new TreeMap<>();
    private Map<String, ReplacementCache> jsonReplMapOther = new TreeMap<>();

    private String today = "";
    private String tomorrow = "";
    private String other = "";

    public boolean putRepl(MutableLiveData<JsonReplacements> jsonReplacements, String institutionId, String ver, String date) {
        if(date.equals(""))
            return false;
        if (date.equals(today))
            return putReplToday(jsonReplacements, institutionId, ver);
        else if (date.equals(tomorrow))
            return putReplTomorrow(jsonReplacements, institutionId, ver);
        this.other = date;
        return putReplOther(jsonReplacements, institutionId, ver);
    }

    public boolean putReplToday(MutableLiveData<JsonReplacements> jsonReplacements, String institutionId, String ver) {
        jsonReplMapToday.put(institutionId, new ReplacementCache(jsonReplacements, ver));
        return true;
    }
    public boolean putReplTomorrow(MutableLiveData<JsonReplacements> jsonReplacements, String institutionId, String ver) {
        jsonReplMapTomorrow.put(institutionId, new ReplacementCache(jsonReplacements, ver));
        return true;
    }
    public boolean putReplOther(MutableLiveData<JsonReplacements> jsonReplacements, String institutionId, String ver) {
        jsonReplMapOther.put(institutionId, new ReplacementCache(jsonReplacements, ver));
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
        return jsonReplMapToday.get(institutionId);
    }

    public ReplacementCache getReplTomorrow(String institutionId) {
        return jsonReplMapTomorrow.get(institutionId);
    }

    public ReplacementCache getReplOther(String institutionId) {
        return jsonReplMapOther.get(institutionId);
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
        private MutableLiveData<JsonReplacements> jsonReplMap;
        private String ver;

        public ReplacementCache(MutableLiveData<JsonReplacements> jsonReplMap, String ver) {
            this.jsonReplMap = jsonReplMap;
            this.ver = ver;
        }

        public MutableLiveData<JsonReplacements> getJsonReplMap() {
            return jsonReplMap;
        }

        public String getVer() {
            return ver;
        }
    }
}
