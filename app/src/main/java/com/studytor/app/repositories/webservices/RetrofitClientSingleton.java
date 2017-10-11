package com.studytor.app.repositories.webservices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dawid on 25.07.2017.
 */

public class RetrofitClientSingleton {
    private static RetrofitClientSingleton retrofitClientInstance;
    private WebService webService;

    private RetrofitClientSingleton(){
        buildRetrofit();
    }

    private void buildRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:80/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webService = retrofit.create(WebService.class);
    }

    public static RetrofitClientSingleton getInstance(){
        if (retrofitClientInstance == null){ //if there is no instance available... create new one
            retrofitClientInstance = new RetrofitClientSingleton();
        }

        return retrofitClientInstance;
    }

    public WebService getWebService() {
        return this.webService;
    }
}
