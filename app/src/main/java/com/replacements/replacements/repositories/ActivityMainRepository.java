package com.replacements.replacements.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.replacements.replacements.repositories.cache.ActivityMainCache;
import com.replacements.replacements.repositories.webservices.RetrofitClientSingleton;
import com.replacements.replacements.repositories.webservices.WebService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dawid on 24.07.2017.
 */

public class ActivityMainRepository {
    private static ActivityMainRepository repositoryInstance;
    private WebService webService;
    private ActivityMainCache activityMainCache;

    private ActivityMainRepository() {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
    }

    public static ActivityMainRepository getInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new ActivityMainRepository();
        }

        return repositoryInstance;
    }

//    public void setup() {
//        webService = RetrofitClientSingleton.getInstance().getWebService();
//    }

    // ...
    public MutableLiveData<String> getUser(String userId) {
        // This is not an optimal implementation, we'll fix it below
        final MutableLiveData<String> data = new MutableLiveData<>();
        webService.getUser(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // error case is left out for brevity
                try {
                    //System.out.println(response.body().string());//convert reponse to string
                    data.setValue(response.body().string());
                    Log.i("ActivityMainRepository", "1");
                    Log.i("ActivityMainRepository", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("ActivityMainRepository", "2");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return data;
    }
}
