package com.studytor.app.repositories.webservices;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        // ####
        // #### TEMPORARY beginning
        // ####
        // Basic HTTP Authentication
        String username = "httpauth";
        String password = "$tudy-|-0r20!8";
        String authToken = Credentials.basic(username, password);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json;versions=1");
                        //if (isUserLoggedIn()) {
                            ongoing.addHeader("Authorization", authToken);
                        //}
                        return chain.proceed(ongoing.build());
                    }
                })
                .build();
        // ####
        // #### TEMPORARY end
        // ####

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://studytor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
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
