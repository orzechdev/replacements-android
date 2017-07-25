package com.replacements.replacements.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.repositories.cache.FragmentReplacementsCache;
import com.replacements.replacements.repositories.webservices.RetrofitClientSingleton;
import com.replacements.replacements.repositories.webservices.WebService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentReplacementsRepository {
    private static final String CLASS_NAME = FragmentReplacementsRepository.class.getName();

    private static FragmentReplacementsRepository repositoryInstance;
    private WebService webService;
    private FragmentReplacementsCache fragmentReplacementsCache;

    private FragmentReplacementsRepository () {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        fragmentReplacementsCache = new FragmentReplacementsCache();
    }

    public static FragmentReplacementsRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new FragmentReplacementsRepository();
        }

        return repositoryInstance;
    }

    public MutableLiveData<JsonReplacements> getReplacements(String institutionId, String date, String ver) {
        Log.i(CLASS_NAME, "getReplacements 100");

        prepareProperDaysInCache();

        // TODO Check when was last update and update if there wasn't update for a long time try update
        // TODO (IMPORTANT: data currently is not updating in proper way (with using ver variable))

        FragmentReplacementsCache.ReplacementCache replacementCache = fragmentReplacementsCache.getRepl(institutionId, date);
        if(replacementCache != null) {
            Log.i(CLASS_NAME, "getReplacements 200");
            MutableLiveData<JsonReplacements> cached = replacementCache.getJsonReplMap();
            if (cached != null)
            {
                Log.i(CLASS_NAME, "getReplacements 300");
                return cached;
            }
        }
        Log.i(CLASS_NAME, "getReplacements 400");

        final MutableLiveData<JsonReplacements> data = new MutableLiveData<>();

        fragmentReplacementsCache.putRepl(data, institutionId, ver, date);

        webService.getReplacements(institutionId, date, ver).enqueue(new Callback<JsonReplacements>() {
            @Override
            public void onResponse(Call<JsonReplacements> call, Response<JsonReplacements> response) {

                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)
                    data.setValue(response.body());
                    Log.i(CLASS_NAME, "getReplacements onResponse 100");
                } else {
                    // … or just log the issue like we’re doing :)
                    Log.d(CLASS_NAME,"getReplacements onResponse error message: " + response.message());
                    Log.d(CLASS_NAME,"getReplacements onResponse error code: " + response.code());
                    Log.d(CLASS_NAME,"getReplacements onResponse error errorBody: " + response.errorBody());
                }

                //List<JsonReplacements.JsonReplacement> jsonReplacements = response.body().getReplacements();
                //Log.i("ActivityMainRepository", jsonReplacements.get(1).getReplacement());
                Log.i(CLASS_NAME, "getReplacements onResponse 200");
            }

            @Override
            public void onFailure(Call<JsonReplacements> call, Throwable t) {
                Log.i(CLASS_NAME, "getReplacements onFailure 100");
                t.printStackTrace();
            }
        });
        return data;
    }

    private void prepareProperDaysInCache() {
        Log.i(CLASS_NAME, "prepareProperDaysInCache 100");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayAsString = dateFormat.format(today);
        if(!fragmentReplacementsCache.getToday().equals(todayAsString)) {
            Log.i(CLASS_NAME, "prepareProperDaysInCache 200");
            calendar.add(Calendar.DAY_OF_YEAR, +1);
            Date tomorrow = calendar.getTime();
            String tomorrowAsString = dateFormat.format(tomorrow);
            fragmentReplacementsCache.refreshDays(todayAsString, tomorrowAsString);
        }
        Log.i(CLASS_NAME, "prepareProperDaysInCache 300");
    }
}
