package com.replacements.replacements.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.models.JsonReplacementsOld;
import com.replacements.replacements.repositories.webservices.Webservice;

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
    private Webservice webservice;

    public void setup() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webservice = retrofit.create(Webservice.class);
    }

    // ...
    public MutableLiveData<String> getUser(String userId) {
        // This is not an optimal implementation, we'll fix it below
        final MutableLiveData<String> data = new MutableLiveData<>();
        webservice.getUser(userId).enqueue(new Callback<ResponseBody>() {
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

    public MutableLiveData<JsonReplacements> getReplacements(String institutionId, String date, String ver) {
        // This is not an optimal implementation, we'll fix it below
        final MutableLiveData<JsonReplacements> data = new MutableLiveData<>();
        webservice.getReplacements(institutionId, date, ver).enqueue(new Callback<JsonReplacements>() {
            @Override
            public void onResponse(Call<JsonReplacements> call, Response<JsonReplacements> response) {
                // error case is left out for brevity
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonReplacements> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return data;
    }
}
