package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.studytor.app.repositories.cache.ReplacementsCache;
import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.SingleReplacementJson;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dawid on 11.11.2017.
 */

public class ReplacementsRepository {

    private static final String CLASS_NAME = ReplacementsRepository.class.getName();

    private static ReplacementsRepository repositoryInstance;

    private ReplacementsCache replacementsCache;
    private WebService webService;

    private MutableLiveData<ReplacementsJson> replacementsData;

    private ReplacementsRepository(Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.replacementsCache = new ReplacementsCache();
        this.replacementsData = new MutableLiveData<>();
    }

    public static ReplacementsRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ReplacementsRepository(context);
        }

        return repositoryInstance;
    }

    public MutableLiveData<ReplacementsJson> getReplacementsData() {
        return replacementsData;
    }

    public LiveData<ReplacementsJson> getReplacements(int institutionId, String date) {
        Log.i(CLASS_NAME, "getReplacements 100");

        // TODO Check when was last update and update if there wasn't update for a long time try update
        // TODO (IMPORTANT: data currently is not updating in proper way (with using ver variable))

        LiveData<ReplacementsJson> replacementCache = replacementsCache.getReplByDate(date);
        if(replacementCache != null) {
            Log.i(CLASS_NAME, "getReplacements 200");
            return replacementCache;
        }
        Log.i(CLASS_NAME, "getReplacements 400");

        //TODO
        getReplacementsFromWeb(institutionId, date);

        return replacementCache;
    }

    public void getReplacementsFromWeb(final int institutionId, final String date) {
        this.webService.getReplacements(institutionId, date).enqueue(new Callback<ReplacementsJson>() {
            @Override
            public void onResponse(Call<ReplacementsJson> call, final Response<ReplacementsJson> response) {
                Log.i(CLASS_NAME, "getReplacementsFromWeb ENQUEUED");

                if(response.isSuccessful() && response.body().getReplacements() != null){
                    Log.i(CLASS_NAME, "getReplacementsFromWeb SUCCESSFUL");
                    ReplacementsJson replacementsJson = response.body();

                    replacementsData.postValue(replacementsJson);

                    replacementsCache.insertOrAddReplacements(date, replacementsData);

                }else{
                    Log.i(CLASS_NAME, "getReplacementsFromWeb SUCCESSFUL NULL" + response.toString());
                    replacementsData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<ReplacementsJson> call, Throwable t) {
                Log.i(CLASS_NAME, "getReplacementsFromWeb FAILURE");

                replacementsData.postValue(null);

                t.printStackTrace();
            }
        });
    }
}
