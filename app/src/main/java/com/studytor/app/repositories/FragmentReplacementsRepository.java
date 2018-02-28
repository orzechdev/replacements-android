package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.studytor.app.repositories.database.DatabaseSingleton;
import com.studytor.app.repositories.database.ReplacementDao;
import com.studytor.app.repositories.models.UserReplacementRoomJson;
import com.studytor.app.repositories.models.UserReplacementsJson;
import com.studytor.app.repositories.cache.FragmentReplacementsCache;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: CONSIDER DELETING THIS REPO BECAUSE IT WAS UPDATED ALMOST 4 months ago AND NOW WE ARE USNG ReplacementsRepository
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

    //public MutableLiveData<UserReplacementsJson> getReplacements(String institutionId, String date, String ver) {
    public LiveData<List<UserReplacementRoomJson>> getReplacements(String institutionId, String date, String ver) {
        Log.i(CLASS_NAME, "getUserReplacements 100");

        prepareProperDaysInCache();

        // TODO Check when was last update and update if there wasn't update for a long time try update
        // TODO (IMPORTANT: data currently is not updating in proper way (with using ver variable))

        FragmentReplacementsCache.ReplacementCache replacementCache = fragmentReplacementsCache.getRepl(institutionId, date);
        if(replacementCache != null) {
            Log.i(CLASS_NAME, "getUserReplacements 200");
            //MutableLiveData<UserReplacementsJson> cached = replacementCache.getJsonReplMap();
            LiveData<List<UserReplacementRoomJson>> cached = replacementCache.getReplModelMap();
            //TODO bellow getVer is latest ver
            replacementCache.getVer();
            if (cached != null)
            {
                Log.i(CLASS_NAME, "getUserReplacements 300");
                return cached;
            }
        }
        Log.i(CLASS_NAME, "getUserReplacements 400");

        LiveData<List<UserReplacementRoomJson>> allReplacements = replacementDao.loadAll();

        fragmentReplacementsCache.putRepl(allReplacements, institutionId, ver, date);

        return allReplacements;
    }

    public MutableLiveData<UserReplacementsJson> refreshReplacementsFromInternet(final LiveData<List<UserReplacementRoomJson>> allReplacements, final String institutionId, String date, String ver) {
        replacementsRefreshInProgress = true;
        Log.d(CLASS_NAME, "refreshReplacementsFromInternet 100");
        final MutableLiveData<UserReplacementsJson> newReplacements = new MutableLiveData<>();

        webService.getUserReplacements(institutionId, date, ver).enqueue(new Callback<UserReplacementsJson>() {

            List<UserReplacementRoomJson> replsJson;

            @Override
            public void onResponse(Call<UserReplacementsJson> call, final Response<UserReplacementsJson> response) {

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

                                List<UserReplacementRoomJson> repls = response.body().getReplacements();

                                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse Thread 300");

                                for(UserReplacementRoomJson repl : repls){
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

                //List<UserReplacementsJson.UserReplacementRoomJson> jsonReplacements = response.body().getReplacements();
                //Log.i("ActivityMainRepository", jsonReplacements.get(1).getReplacement());
                Log.i(CLASS_NAME, "refreshReplacementsFromInternet onResponse 300");
            }

            @Override
            public void onFailure(Call<UserReplacementsJson> call, Throwable t) {
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
