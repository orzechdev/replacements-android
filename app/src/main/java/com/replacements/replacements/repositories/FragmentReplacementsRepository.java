package com.replacements.replacements.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.replacements.replacements.repositories.database.DatabaseSingleton;
import com.replacements.replacements.repositories.database.ReplacementDao;
import com.replacements.replacements.repositories.models.ReplacementRoomJson;
import com.replacements.replacements.repositories.models.ReplacementsJson;
import com.replacements.replacements.repositories.cache.FragmentReplacementsCache;
import com.replacements.replacements.repositories.webservices.RetrofitClientSingleton;
import com.replacements.replacements.repositories.webservices.WebService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private ReplacementDao replacementDao;

    public boolean replacementsRefreshInProgress = false;

    private FragmentReplacementsRepository (Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        fragmentReplacementsCache = new FragmentReplacementsCache();

        this.replacementDao = DatabaseSingleton.getInstance(context).getMainDatabase().replacementDao();
    }

    public static FragmentReplacementsRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new FragmentReplacementsRepository(context);
        }

        return repositoryInstance;
    }

    //public MutableLiveData<ReplacementsJson> getReplacements(String institutionId, String date, String ver) {
    public LiveData<List<ReplacementRoomJson>> getReplacements(String institutionId, String date, String ver) {
        Log.i(CLASS_NAME, "getReplacements 100");

        prepareProperDaysInCache();

        // TODO Check when was last update and update if there wasn't update for a long time try update
        // TODO (IMPORTANT: data currently is not updating in proper way (with using ver variable))

        FragmentReplacementsCache.ReplacementCache replacementCache = fragmentReplacementsCache.getRepl(institutionId, date);
        if(replacementCache != null) {
            Log.i(CLASS_NAME, "getReplacements 200");
            //MutableLiveData<ReplacementsJson> cached = replacementCache.getJsonReplMap();
            LiveData<List<ReplacementRoomJson>> cached = replacementCache.getReplModelMap();
            //TODO bellow getVer is latest ver
            replacementCache.getVer();
            if (cached != null)
            {
                Log.i(CLASS_NAME, "getReplacements 300");
                return cached;
            }
        }
        Log.i(CLASS_NAME, "getReplacements 400");

        LiveData<List<ReplacementRoomJson>> allReplacements = replacementDao.loadAll();

        fragmentReplacementsCache.putRepl(allReplacements, institutionId, ver, date);

        return allReplacements;
    }

    public MutableLiveData<ReplacementsJson> refreshReplacementsFromInternet(final LiveData<List<ReplacementRoomJson>> allReplacements, final String institutionId, String date, String ver) {
        replacementsRefreshInProgress = true;
        Log.d(CLASS_NAME, "refreshReplacementsFromInternet 100");
        final MutableLiveData<ReplacementsJson> newReplacements = new MutableLiveData<>();

        webService.getReplacements(institutionId, date, ver).enqueue(new Callback<ReplacementsJson>() {

            List<ReplacementRoomJson> replsJson;

            @Override
            public void onResponse(Call<ReplacementsJson> call, final Response<ReplacementsJson> response) {

                Log.d(CLASS_NAME, "refreshReplacementsFromInternet onResponse url: " + call.request().url());

                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)
                    Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse 100");
                    newReplacements.setValue(response.body());
                //    afterRefreshFromInternet(allReplacements, newReplacements);

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 100");
                            if(response.body() != null) {
                                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 200");

                                List<ReplacementRoomJson> repls = response.body().getReplacements();

                                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 300");

                                for(ReplacementRoomJson repl : repls){
                                    Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 400");
                                    repl.setInstitutId(institutionId);
                                    repl.setVer(response.body().getVer());
                                }

                                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 500");

                                replacementDao.insertAll(repls);
                            }
                            Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 600");
                            // Insert some method call here.
                        }
                    });

                    t.start();

                    Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse 200");
                } else {
                    // … or just log the issue like we’re doing :)
                    Log.d(CLASS_NAME, "refreshReplacementsFromInternet onResponse error message: " + response.message());
                    Log.d(CLASS_NAME, "refreshReplacementsFromInternet onResponse error code: " + response.code());
                    Log.d(CLASS_NAME, "refreshReplacementsFromInternet onResponse error errorBody: " + response.errorBody());
                }

                //List<ReplacementsJson.ReplacementRoomJson> jsonReplacements = response.body().getReplacements();
                //Log.i("ActivityMainRepository", jsonReplacements.get(1).getReplacement());
                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse 300");
            }

            @Override
            public void onFailure(Call<ReplacementsJson> call, Throwable t) {
                Log.d(CLASS_NAME, "refreshReplacementsFromInternet onFailure url: " + call.request().url());
                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onFailure 100");
                t.printStackTrace();
            }
        });

        return newReplacements;
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
