package com.replacements.replacements.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.replacements.replacements.repositories.database.DatabaseSingleton;
import com.replacements.replacements.repositories.database.MainDatabase;
import com.replacements.replacements.repositories.database.Replacement;
import com.replacements.replacements.repositories.database.ReplacementDao;
import com.replacements.replacements.repositories.database.ReplacementDao_Impl;
import com.replacements.replacements.repositories.models.ReplacementsModel;
import com.replacements.replacements.repositories.webservices.json.JsonReplacements;
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

    //public MutableLiveData<JsonReplacements> getReplacements(String institutionId, String date, String ver) {
    public LiveData<List<Replacement>> getReplacements(String institutionId, String date, String ver) {
        Log.i(CLASS_NAME, "getReplacements 100");

        prepareProperDaysInCache();

        // TODO Check when was last update and update if there wasn't update for a long time try update
        // TODO (IMPORTANT: data currently is not updating in proper way (with using ver variable))

        FragmentReplacementsCache.ReplacementCache replacementCache = fragmentReplacementsCache.getRepl(institutionId, date);
        if(replacementCache != null) {
            Log.i(CLASS_NAME, "getReplacements 200");
            //MutableLiveData<JsonReplacements> cached = replacementCache.getJsonReplMap();
            LiveData<List<Replacement>> cached = replacementCache.getReplModelMap();
            if (cached != null)
            {
                Log.i(CLASS_NAME, "getReplacements 300");
                return cached;
            }
        }
        Log.i(CLASS_NAME, "getReplacements 400");

        //List<LiveData<Replacement>> allReplacements = replacementDao.loadAll();
        //List<LiveData<ReplacementsModel>> allReplacements = replacementDao.loadAll().;
        LiveData<List<Replacement>> allReplacements = replacementDao.loadAll();

//        final MutableLiveData<JsonReplacements> newReplacements = new MutableLiveData<>();

        fragmentReplacementsCache.putRepl(allReplacements, institutionId, ver, date);

//        webService.getReplacements(institutionId, date, ver).enqueue(new Callback<JsonReplacements>() {
//
//            List<JsonReplacements.JsonReplacement> replsJson;
//
//            @Override
//            public void onResponse(Call<JsonReplacements> call, Response<JsonReplacements> response) {
//
//                if (response.isSuccessful()) {
//                    // use response data and do some fancy stuff :)
//                    newReplacements.setValue(response.body());
//                    Log.i(CLASS_NAME, "getReplacements onResponse 100");
//                } else {
//                    // … or just log the issue like we’re doing :)
//                    Log.d(CLASS_NAME,"getReplacements onResponse error message: " + response.message());
//                    Log.d(CLASS_NAME,"getReplacements onResponse error code: " + response.code());
//                    Log.d(CLASS_NAME,"getReplacements onResponse error errorBody: " + response.errorBody());
//                }
//
//                //List<JsonReplacements.JsonReplacement> jsonReplacements = response.body().getReplacements();
//                //Log.i("ActivityMainRepository", jsonReplacements.get(1).getReplacement());
//                Log.i(CLASS_NAME, "getReplacements onResponse 200");
//            }
//
//            @Override
//            public void onFailure(Call<JsonReplacements> call, Throwable t) {
//                Log.i(CLASS_NAME, "getReplacements onFailure 100");
//                t.printStackTrace();
//            }
//        });

        MutableLiveData<JsonReplacements> newReplacements = refreshReplacementsFromInternet(institutionId, ver, date);

        List<JsonReplacements.JsonReplacement> newReplsList = null;
        List<String> activeRepls = null;

        if (newReplacements.getValue() != null) {
            newReplsList = newReplacements.getValue().getReplacements();
            activeRepls = newReplacements.getValue().getIds();
        }

        if(newReplsList != null)
            for (int i = 0; i < newReplsList.size(); i++) {

                Replacement newRepl = new Replacement();
                JsonReplacements.JsonReplacement newJsonRepl = newReplsList.get(i);

                newRepl.setId(newJsonRepl.getId());
                newRepl.setVer(newJsonRepl.getVer());
                newRepl.setReplacement(newJsonRepl.getReplacement());
                newRepl.setNumber(newJsonRepl.getNumber());
                newRepl.setClass_number(newJsonRepl.getClassNumber());
                newRepl.setDefault_integer(newJsonRepl.getDefaultInteger());

                replacementDao.save(newRepl);

                if(allReplacements.getValue() != null)
                    allReplacements.getValue().add(newRepl);
            }

        //jeśli w fragmentReplacementsCache istnieje replacement z id z activeRepls - oznacz w fragmentReplacementsCache ten replacement jako aktywny (i w bazie)
        //jeśli w fragmentReplacementsCache istnieje replacement ktorego id nie istnieje w activeRepls - oznacz w fragmentReplacementsCache ten replacement jako nieaktywny (i w bazie)
        if(activeRepls != null && allReplacements.getValue() != null) {
            boolean active = false;
            for (int i = 0; i < allReplacements.getValue().size(); i++) {
                Replacement repl = allReplacements.getValue().get(i);
                if (repl != null) {
                    for (int j = 0; j < activeRepls.size(); j++) {
                        if (activeRepls.get(j).equals(repl.getId())) {
                            active = true;
                        }
                    }
                    if(repl.isActive() ^ active) {
                        repl.setActive(active);
                        replacementDao.updateReplacements(repl);
                    }
                    active = false;
                }
            }
        }

        //


        return allReplacements;
    }

//    public MutableLiveData<ReplacementsModel> getReplacementsFromDatabase(String institutionId, String date) {
//        List<LiveData<Replacement>> allReplacements = replacementDao.loadAll();
//
//        ReplacementsModel allReplacementsModel = new ReplacementsModel();
//        //MutableLiveData<ReplacementsModel> allReplacementsModel;
//
//        for (int i = 0; i < allReplacements.size(); i++) {
//            LiveData<Replacement> replacementLiveData =  allReplacements.get(i);
//            Replacement repl = replacementLiveData.getValue();
//            if(repl != null)
//                allReplacementsModel.addReplacement(repl.getId(), repl.getVer(), repl.getReplacement(), repl.getNumber(), repl.getClass_number(), repl.getDefault_integer());
//        }
//
//        MutableLiveData<ReplacementsModel> allReplacementsModelLiveData = new MutableLiveData<>();
//        allReplacementsModelLiveData.setValue(allReplacementsModel);
//
//        return allReplacementsModelLiveData;
//    }

    public MutableLiveData<JsonReplacements> refreshReplacementsFromInternet(String institutionId, String date, String ver) {
        final MutableLiveData<JsonReplacements> newReplacements = new MutableLiveData<>();

        webService.getReplacements(institutionId, date, ver).enqueue(new Callback<JsonReplacements>() {

            List<JsonReplacements.JsonReplacement> replsJson;

            @Override
            public void onResponse(Call<JsonReplacements> call, Response<JsonReplacements> response) {

                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)
                    newReplacements.setValue(response.body());
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
