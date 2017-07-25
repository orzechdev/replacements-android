package com.replacements.replacements.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.repositories.cache.FragmentReplacementsCache;
import com.replacements.replacements.repositories.webservices.RetrofitClientSingleton;
import com.replacements.replacements.repositories.webservices.WebService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentReplacementsRepository {
    private static FragmentReplacementsRepository repositoryInstance;
    private WebService webService;
    private FragmentReplacementsCache fragmentReplacementsCache;

    private FragmentReplacementsRepository () {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
    }

    public static FragmentReplacementsRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new FragmentReplacementsRepository();
        }

        return repositoryInstance;
    }

    public MutableLiveData<JsonReplacements> getReplacements(String institutionId, String date, String ver) {
        // This is not an optimal implementation, we'll fix it below
        final MutableLiveData<JsonReplacements> data = new MutableLiveData<>();
        webService.getReplacements(institutionId, date, ver).enqueue(new Callback<JsonReplacements>() {
            @Override
            public void onResponse(Call<JsonReplacements> call, Response<JsonReplacements> response) {
                // error case is left out for brevity

                if (response.isSuccessful()) {
                    // use response data and do some fancy stuff :)
                    data.setValue(response.body());
                    Log.i("ActivityMainRepository", "1");
                } else {
                    // parse the response body …
                    //APIError error = ErrorUtils.parseError(response);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.d("ActivityMainRepository","error message");//, error.message());
                }

                //List<JsonReplacements.JsonReplacement> jsonReplacements = response.body().getReplacements();
                //Log.i("ActivityMainRepository", jsonReplacements.get(1).getReplacement());
                Log.i("ActivityMainRepository", "2");
            }

            @Override
            public void onFailure(Call<JsonReplacements> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return data;
    }
}
