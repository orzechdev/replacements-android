package com.studytor.app.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.studytor.app.interfaces.ApplicationConstants;

import java.util.ArrayList;

/**
 * Created by Dawid on 2016-07-23.
 */
public class ProfileRegister extends IntentService {
    private static final String CLASS_NAME = ProfileRegister.class.getName();

    private RequestParams params = new RequestParams();
    private ArrayList<Integer> myDataSetAll = new ArrayList<>();
    private String token;
    private String url;

    public ProfileRegister(){
        super(CLASS_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle intentExtra = intent.getExtras();
        if(intentExtra != null) {
            int serverAction = intentExtra.getInt("serverAction", 0);
            token = intentExtra.getString("token", "");
            url = intentExtra.getString("url", "");
            Log.i(CLASS_NAME, "onHandleIntent serverAction = " + Integer.toString(serverAction));
            Log.i(CLASS_NAME, "onHandleIntent token = " + token);
            Handler handler = new Handler(Looper.getMainLooper());
            switch (serverAction) {
                case 0:
                    break;
                case 1:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            registerUser(token, url);
                        }
                    });
                    break;
                case 2:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            unregisterUser(token, url);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    //Insert User to Server
    private void registerUser(String regId, String url){
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        SharedPreferences.Editor localEditor = prefs.edit();
        if(prefs.getInt("chosenSchool", 1) == 1){
            localEditor.putBoolean("toDoRegisterUser1", true);
            localEditor.putBoolean("toDoUnregisterUser1", false);
        }else{
            localEditor.putBoolean("toDoRegisterUser2", true);
            localEditor.putBoolean("toDoUnregisterUser2", false);
        }
        localEditor.apply();
        params.put("regId", regId);
        Log.i("Studytor","Reg Id = " + regId);
        String urlServer;
        if(!url.equals("")) {
            urlServer = url + ApplicationConstants.APP_SERVER_URL_INSERT_USER;
        }else{
            urlServer = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
            urlServer = urlServer + ApplicationConstants.APP_SERVER_URL_INSERT_USER;
        }
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlServer, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        //Reg Id shared successfully with Web App
                        Log.i("GcmUserRegistration", "storeRegIdinServer 1");
                        changeSetInServer();
                        SharedPreferences prefs = getSharedPreferences("dane", 0);
                        SharedPreferences.Editor localEditor = prefs.edit();
                        localEditor.putBoolean("toDoRegisterUser1", false);
                        localEditor.putBoolean("toDoRegisterUser2", false);
                        localEditor.putBoolean("registerOnServerExists", true);
                        localEditor.apply();

                        Intent newIntent = new Intent("messageLoader");
                        newIntent.putExtra("finishService", "UserRegistered");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            //Requested resource not found
                            Log.i("GcmUserRegistration", "storeRegIdinServer 2");
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            //Something went wrong at server end
                            Log.i("GcmUserRegistration", "storeRegIdinServer 3");
                        }
                        // When Http response code other than 404, 500
                        else {
                            //Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running], check for other errors as well
                            Log.i("GcmUserRegistration", "storeRegIdinServer 4");
                        }

                        Intent newIntent = new Intent("messageLoader");
                        newIntent.putExtra("finishService", "UserRegisterFailure");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
                    }
                });
    }

    //Remove User from Server
    private void unregisterUser(String regId, String url){
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        SharedPreferences.Editor localEditor = prefs.edit();
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(localSharedPreferences.getBoolean("pref_notify_switch", true)) {
            if (prefs.getInt("chosenSchool", 1) == 1) {
                localEditor.putBoolean("toDoUnregisterUser2", true);
                localEditor.putBoolean("toDoRegisterUser2", false);
            } else {
                localEditor.putBoolean("toDoUnregisterUser1", true);
                localEditor.putBoolean("toDoRegisterUser1", false);
            }
            localEditor.apply();
        }
        Log.i("GcmUserUnregistration","removeRegIdfromServer");
        params.put("regId", regId);
        Log.i("Studytor","To remove Reg Id = " + regId);
        String urlServer;
        final String urlDomain;
        if(!url.equals("")) {
            urlDomain = url;
            urlServer = url + ApplicationConstants.APP_SERVER_URL_REMOVE_USER;
        }else{
            urlServer = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_2 : ApplicationConstants.SCHOOL_SERVER_1;
            urlDomain = urlServer;
            urlServer = urlServer + ApplicationConstants.APP_SERVER_URL_REMOVE_USER;
        }
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlServer, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        //Reg Id shared successfully with Web App
                        Log.i("GcmUserUnregistration", "removeRegIdfromServer 1");
                        SharedPreferences prefs = getSharedPreferences("dane", 0);
                        SharedPreferences.Editor localEditor = prefs.edit();
                        if(urlDomain.equals(ApplicationConstants.SCHOOL_SERVER_1)){
                            localEditor.putBoolean("toDoUnregisterUser1", false);
                        }else{
                            localEditor.putBoolean("toDoUnregisterUser2", false);
                        }
                        localEditor.apply();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            //Requested resource not found
                            Log.i("GcmUserUnregistration","removeRegIdfromServer 2");
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            //Something went wrong at server end
                            Log.i("GcmUserUnregistration","removeRegIdfromServer 3");
                        }
                        // When Http response code other than 404, 500
                        else {
                            //Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running], check for other errors as well
                            Log.i("GcmUserUnregistration","removeRegIdfromServer 4");
                        }
                    }
                });
    }

    public void changeSetInServer(){
        Log.i(CLASS_NAME, "changeSetInServer 10");
        // Run service which will change user set in server
        Intent profileSetToServer = new Intent(this, ProfileSetToServer.class);
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //if(localSharedPreferences.getString("pref_notify_schedule", "1").equals("1")) {
            if (localSharedPreferences.getString("pref_notify_repl", "1").equals("1")) {
                // Wedlug profilu
                // Get all selected user classes and teachers
                if(!myDataSetAll.isEmpty()){
                    String dataIdsString = myDataSetAll.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                    profileSetToServer.putExtra("serverAction", 1);
                    profileSetToServer.putExtra("dataIds", dataIdsString);
                    SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                    localEditor.putBoolean("dataToChange", true);
                    localEditor.apply();
                }
            }else if(localSharedPreferences.getString("pref_notify_repl", "1").equals("3")){
                profileSetToServer.putExtra("serverAction", 2);
                profileSetToServer.putExtra("modules", "2");
                SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                localEditor.putBoolean("modulesToChange", true);
                localEditor.apply();
            }
//        }else{
//            if (localSharedPreferences.getString("pref_notify_repl", "1").equals("1")) {
//                readAllFromSQLite();
//                if(!myDataSetAll.isEmpty()){
//                    String dataIdsString = myDataSetAll.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
//                    profileSetToServer.putExtra("serverAction", 3);
//                    profileSetToServer.putExtra("dataIds", dataIdsString);
//                    profileSetToServer.putExtra("modules", "1");
//                    SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
//                    localEditor.putBoolean("dataToChange", true);
//                    localEditor.putBoolean("modulesToChange", true);
//                    localEditor.apply();
//                }else{
//                    profileSetToServer.putExtra("serverAction", 2);
//                    profileSetToServer.putExtra("modules", "1");
//                    SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
//                    localEditor.putBoolean("modulesToChange", true);
//                    localEditor.apply();
//                }
//            }else if(localSharedPreferences.getString("pref_notify_repl", "1").equals("2")){
//                profileSetToServer.putExtra("serverAction", 2);
//                profileSetToServer.putExtra("modules", "1");
//                SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
//                localEditor.putBoolean("modulesToChange", true);
//                localEditor.apply();
//            }else{
//                profileSetToServer.putExtra("serverAction", 2);
//                profileSetToServer.putExtra("modules", "");
//                SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
//                localEditor.putBoolean("modulesToChange", true);
//                localEditor.apply();
//            }
//
//        }
        Log.i(CLASS_NAME, "changeSetInServer 20");
        if(profileSetToServer.hasExtra("dataIds") || profileSetToServer.hasExtra("modules")) {
            Log.i(CLASS_NAME, "changeSetInServer start service");
            this.startService(profileSetToServer);
        }
        Log.i(CLASS_NAME, "changeSetInServer 30");
    }
}