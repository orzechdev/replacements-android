package com.studytor.app.repositories.webservices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by przemek19980102 on 23.10.2017.
 */

public class StudytorRetrofitClientSingleton {
    private static StudytorRetrofitClientSingleton retrofitClientInstance;
    private StudytorWebService webService;

    private StudytorRetrofitClientSingleton(){
        buildRetrofit();
    }

    private void buildRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://studytor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webService = retrofit.create(StudytorWebService.class);
    }

    public static StudytorRetrofitClientSingleton getInstance(){
        if (retrofitClientInstance == null){ //if there is no instance available... create new one
            retrofitClientInstance = new StudytorRetrofitClientSingleton();
        }

        return retrofitClientInstance;
    }

    public StudytorWebService getWebService() {
        return this.webService;
    }
}
