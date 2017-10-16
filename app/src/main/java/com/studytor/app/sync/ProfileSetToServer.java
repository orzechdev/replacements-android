package com.studytor.app.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.studytor.app.interfaces.ApplicationConstants;

/**
 * Created by Dawid on 2016-01-16.
 */
public class ProfileSetToServer extends IntentService {
    private static final String CLASS_NAME = ProfileSetToServer.class.getName();
    private static final String REG_ID = "regId";
    private RequestParams params = new RequestParams();

    public ProfileSetToServer(){
        super(CLASS_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle intentExtra = intent.getExtras();
        if(intentExtra != null) {
            int serverAction = intentExtra.getInt("serverAction", 0);
            String dataIds;
            String modules;
            Log.i(CLASS_NAME, "onHandleIntent serverAction = " + Integer.toString(serverAction));
            switch (serverAction) {
                case 0:
                    break;
                case 1:
                    dataIds = intentExtra.getString("dataIds", "");
                    storeDataIds(dataIds);
                    break;
                case 2:
                    modules = intentExtra.getString("modules", "1,2");
                    storeModules(modules);
                    break;
                case 3:
                    dataIds = intentExtra.getString("dataIds", "");
                    storeDataIds(dataIds);
                    modules = intentExtra.getString("modules", "1,2");
                    storeModules(modules);
                    break;
                default:
                    break;
            }
        }
    }

    private void storeDataIds(String dataIds){
//        SharedPreferences prefsGCM = getSharedPreferences("gcm", Context.MODE_PRIVATE);
//        String regId = prefsGCM.getString(REG_ID, "");
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        String regId = FirebaseInstanceId.getInstance().getToken();
        if(!TextUtils.isEmpty(regId)) {
            params.put("regId", regId);
            Log.i(CLASS_NAME, "storeDataIds regId = " + regId);
            params.put("dataIds", dataIds);
            Log.i(CLASS_NAME, "storeDataIds = " + dataIds);
            //Change automatically SCHOOL_SERVER_1 to SCHOOL_SERVER_2 and vice versa
            String urlServer = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
            urlServer = urlServer + ApplicationConstants.APP_SERVER_URL_INSERT_USER_DATA_IDS;
            // Make RESTful webservice call using AsyncHttpClient object
            AsyncHttpClient client = new SyncHttpClient();
            client.post(urlServer, params,
                    new AsyncHttpResponseHandler() {
                        // When the response returned by REST has Http
                        // response code '200'
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            //Reg Id shared successfully with Web App
                            Log.i(CLASS_NAME, "storeDataIds onSuccess");
                            SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                            localEditor.putBoolean("dataToChange", false);
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
                                Log.i(CLASS_NAME, "storeDataIds onFailure 10");
                            }
                            // When Http response code is '500'
                            else if (statusCode == 500) {
                                //Something went wrong at server end
                                Log.i(CLASS_NAME, "storeDataIds onFailure 20");
                            }
                            // When Http response code other than 404, 500
                            else {
                                //Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running], check for other errors as well
                                Log.i(CLASS_NAME, "storeDataIds onFailure 30");
                            }
                        }
                    });
        }
    }

    private void storeModules(String modules){
//        SharedPreferences prefsGCM = getSharedPreferences("gcm", Context.MODE_PRIVATE);
//        String regId = prefsGCM.getString(REG_ID, "");
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        String regId = FirebaseInstanceId.getInstance().getToken();
        if(!TextUtils.isEmpty(regId)) {
            params.put("regId", regId);
            Log.i(CLASS_NAME, "storeModules regId = " + regId);
            params.put("modules", modules);
            Log.i(CLASS_NAME, "storeModules = " + modules);
            //Change automatically SCHOOL_SERVER_1 to SCHOOL_SERVER_2 and vice versa
            String urlServer = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
            urlServer = urlServer + ApplicationConstants.APP_SERVER_URL_INSERT_USER_MODULES;
            // Make RESTful webservice call using AsyncHttpClient object
            AsyncHttpClient client = new SyncHttpClient();
            client.post(urlServer, params,
                    new AsyncHttpResponseHandler() {
                        // When the response returned by REST has Http
                        // response code '200'
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            //Reg Id shared successfully with Web App
                            Log.i(CLASS_NAME, "storeModules onSuccess");
                            SharedPreferences.Editor localEditor = getSharedPreferences("dane", 0).edit();
                            localEditor.putBoolean("modulesToChange", false);
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
                                Log.i(CLASS_NAME, "storeModules onFailure 10");
                            }
                            // When Http response code is '500'
                            else if (statusCode == 500) {
                                //Something went wrong at server end
                                Log.i(CLASS_NAME, "storeModules onFailure 20");
                            }
                            // When Http response code other than 404, 500
                            else {
                                //Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running], check for other errors as well
                                Log.i(CLASS_NAME, "storeModules onFailure 30");
                            }
                        }
                    });
        }
    }
}