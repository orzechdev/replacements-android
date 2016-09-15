package com.replacements.replacements.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;
import com.replacements.replacements.interfaces.ApplicationConstants;

import java.util.ArrayList;

/**
 * Created by Dawid on 2016-07-23.
 */
public class ProfileRegister extends IntentService {
    private static final String CLASS_NAME = ProfileRegister.class.getName();

    private RequestParams params = new RequestParams();
    private ArrayList<Integer> myDataSetAll = new ArrayList<>();
    private String token;

    public ProfileRegister(){
        super(CLASS_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle intentExtra = intent.getExtras();
        if(intentExtra != null) {
            int serverAction = intentExtra.getInt("serverAction", 0);
            token = intentExtra.getString("token", "");
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
                            registerUser(token);
                        }
                    });
                    break;
                case 2:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            unregisterUser(token);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    //Insert User to Server
    private void registerUser(String regId){
        SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
        localEditor.putBoolean("toDoRegisterUser", true);
        localEditor.putBoolean("toDoUnregisterUser", false);
        localEditor.apply();
        params.put("regId", regId);
        System.out.println("Reg Id = " + regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL_INSERT_USER, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        //Reg Id shared successfully with Web App
                        Log.i("GcmUserRegistration", "storeRegIdinServer 1");
                        changeSetInServer();
                        SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                        localEditor.putBoolean("toDoRegisterUser", false);
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
                    }
                });
    }

    //Remove User from Server
    private void unregisterUser(String regId){
        SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
        localEditor.putBoolean("toDoUnregisterUser", true);
        localEditor.putBoolean("toDoRegisterUser", false);
        localEditor.apply();
        Log.i("GcmUserUnregistration","removeRegIdfromServer");
        params.put("regId", regId);
        System.out.println("To remove Reg Id = " + regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL_REMOVE_USER, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        //Reg Id shared successfully with Web App
                        Log.i("GcmUserUnregistration", "removeRegIdfromServer 1");
                        SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                        localEditor.putBoolean("toDoUnregisterUser", false);
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
                readAllFromSQLite();
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

    public ArrayList<Integer> readAllFromSQLite() {
        myDataSetAll.clear();
        Cursor cursor;
        ClassDbAdapter classDbAdapter;
        TeacherDbAdapter teacherDbAdapter;

        classDbAdapter = new ClassDbAdapter(this);
        classDbAdapter.open();
        cursor = classDbAdapter.getAllIdsSelected();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int idOfData = cursor.getInt(cursor.getColumnIndex(ClassDbAdapter.KEY_ID));
                    if(cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED)) == 1){
                        myDataSetAll.add(idOfData);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        classDbAdapter.close();

        teacherDbAdapter = new TeacherDbAdapter(this);
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllIdsSelected();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int idOfData = cursor.getInt(cursor.getColumnIndex(ClassDbAdapter.KEY_ID));
                    if(cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED)) == 1){
                        myDataSetAll.add(idOfData);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        teacherDbAdapter.close();

        return myDataSetAll;
    }
}