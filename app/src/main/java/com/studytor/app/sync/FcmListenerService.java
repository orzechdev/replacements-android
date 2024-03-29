package com.studytor.app.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studytor.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Dawid on 2016-07-21.
 */
public class FcmListenerService extends FirebaseMessagingService {
    private static final String CLASS_NAME = FcmListenerService.class.getName();
    //NotificationCompat.Builder builder;

    private String new_profile;
    private String new_home;
    private String new_replacements;
    private String new_schedule;
    private String click_to_view;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(CLASS_NAME, "onMessageReceived 100");
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(localSharedPreferences.getBoolean("pref_notify_switch",true)) {
            Log.i(CLASS_NAME, "onMessageReceived 200");
            new_profile = "new_profile";
            new_home = "new_home";
            new_replacements = "new_replacements";
            new_schedule = "new_schedule";
            click_to_view = "click_to_view";

            Map data = remoteMessage.getData();
            String message = data.get("m").toString();
            processMessage(message);
        }
    }

    public void processMessage(String jsonMsg){
        Log.i(CLASS_NAME, "processMessage 100");
        int msgSubject = 9000;
        if (jsonMsg != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonMsg);
                msgSubject = jsonObj.getInt("msgSubject");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent resultIntent;
        PendingIntent resultPendingIntent;
        // Sets an ID for the notification, so it can be updated
        int notifyID;
        String notifyTitle;
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        switch (msgSubject) {
            case 0:
                Log.i(CLASS_NAME, "processMessage 200");
                //notifyTitle = new_profile;
                //notifyID = 0;
                break;
            case 1:
                Log.i(CLASS_NAME, "processMessage 210");
                //notifyTitle = new_home;
                //notifyID = 100;
                break;
            case 2:
                Log.i(CLASS_NAME, "processMessage 220");
                //New replacements notification
                break;
            case 5:
                Log.i(CLASS_NAME, "processMessage 250");
                //Switching developer's options
                boolean devOptionsVisible = sharedPref.getBoolean("developerOptionsVisible",false);
                sharedPrefEditor.putBoolean("developerOptionsVisible", !devOptionsVisible);
                sharedPrefEditor.apply();
                break;
        }
        Log.i(CLASS_NAME, "processMessage 300");
    }

    private void sendNotification(int notifyID, PendingIntent resultPendingIntent, String notifyTitle){
        Log.i(CLASS_NAME, "sendNotification 100");
        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.setBigContentTitle("Enter Content Text");
//        inboxStyle.addLine("hi events");

        mNotifyBuilder = new NotificationCompat.Builder(getBaseContext())
                .setContentTitle(notifyTitle)
                .setContentText(click_to_view)
                .setSmallIcon(R.drawable.ic_launcher_white);
        //.setStyle(inboxStyle);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        mNotifyBuilder.setDefaults(defaults);

        //Ponizszy kod dziala tylko w API >= 16, wiec jest usuniety
        //mNotifyBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mNotifyBuilder.setLights(ContextCompat.getColor(getApplicationContext(), R.color.LED_blue), 600, 2400);
        mNotifyBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_blue_dark));
        mNotifyBuilder.setAutoCancel(true);

        // Post a notification
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());

        Log.i(CLASS_NAME, "sendNotification 200");
    }
}