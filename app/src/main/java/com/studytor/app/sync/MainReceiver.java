package com.studytor.app.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Dawid on 2016-08-14.
 */
public class MainReceiver extends BroadcastReceiver {
    private static final String CLASS_NAME =  MainReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MainReceiver","onReceive 1");
        //android.net.conn.CONNECTIVITY_CHANGE
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //Potrzebne jest utworzenie bazy dancyh jesli pierwszy raz uruchomiono dla rejestracji FCM i rejestracji profilu na serwerze
                //android.intent.action.MY_PACKAGE_REPLACED
                //lub jesli nie bylo internetu to
                //android.net.conn.CONNECTIVITY_CHANGE

                Log.i(CLASS_NAME, "onReceive activeNetwork: " + activeNetwork.getTypeName());
                SharedPreferences sharedPref = context.getSharedPreferences("dane", 0);
                String dirCurrent = sharedPref.getString("scheduleFilesDirNameCurrent", "");
                if (dirCurrent.equals("")) {
                    Intent newIntent = new Intent("messageSchedule");
                    newIntent.putExtra("networkIsOn", true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent);
                }

                if(sharedPref.getBoolean("schoolToChange", false)) {
                    Intent newIntent = new Intent("messageLoader");
                    newIntent.putExtra("networkIsOn", true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent);
                }
            }
        } else {
            // not connected to the internet
            Log.i(CLASS_NAME, "onReceive activeNetwork: no connection");

            SharedPreferences sharedPref = context.getSharedPreferences("dane", 0);
            if(sharedPref.getBoolean("schoolToChange", false)) {
                Intent newIntent = new Intent("messageLoader");
                newIntent.putExtra("networkIsOn", false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(newIntent);
            }
        }
    }

    
}
