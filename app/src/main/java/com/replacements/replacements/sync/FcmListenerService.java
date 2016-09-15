package com.replacements.replacements.sync;

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
import com.replacements.replacements.R;
import com.replacements.replacements.activities.ReplacementsMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Dawid on 2016-07-21.
 */
public class FcmListenerService extends FirebaseMessagingService {
    // Sets an ID for the notification, so it can be updated
    public static final int notifyID = 9001;
    //NotificationCompat.Builder builder;

    private String new_profile;
    private String new_home;
    private String new_replacements;
    private String new_schedule;
    private String click_to_view;
    private String app_update_link;
    private String new_app_update;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(localSharedPreferences.getBoolean("pref_notify_switch",true)) {
            new_profile = getString(R.string.new_profile);
            new_home = getString(R.string.new_home);
            new_replacements = getString(R.string.new_replacements);
            new_schedule = getString(R.string.new_schedule);
            click_to_view = getString(R.string.click_to_view);
            app_update_link = getString(R.string.app_update_link);
            new_app_update = getString(R.string.new_app_update);

            Map data = remoteMessage.getData();
            String message = data.get("m").toString();
            sendNotification(message);
        }
    }

    public void sendNotification(String jsonMsg){
        Log.i("P", "12");
        //Intent localIntent = new Intent(getBaseContext(), ReplacementsMain.class);
        int msgSubject = 2;
        if (jsonMsg != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonMsg);
                msgSubject = jsonObj.getInt("msgSubject");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(msgSubject < 0 || msgSubject > 5){
            msgSubject = 2;
        }

        boolean makeNotification = false;
        Intent resultIntent;
        PendingIntent resultPendingIntent;
        if(msgSubject == 5){
            //Przelaczanie widocznosci opcji deweloperskich
            SharedPreferences sharedPref = getSharedPreferences("dane", 0);
            boolean devOptionsVisible = sharedPref.getBoolean("developerOptionsVisible",false);
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putBoolean("developerOptionsVisible", !devOptionsVisible);
            sharedPrefEditor.apply();

            //NIE POTRZEBNE, ALE WYWALA BLEDY WIEC NIECH BEDZIE
            resultIntent = new Intent();
            resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else if(msgSubject == 3){
//            SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            //Jesli plan mozna pobrac do dostepu offline - najpierw pobierz a pozniej wyslij powiadomienie, jesli nie - wyslij tylko powiadomienie
//            if(localSharedPreferences.getBoolean("pref_schedule_offline_switch",true)) {
            //Najpierw pobierz plan a pozniej wyslij powiadomienie
            SharedPreferences sharedPref = getSharedPreferences("dane", 0);
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putBoolean("scheduleUpdateToNotify",true);
            sharedPrefEditor.apply();
            Intent scheduleIntent = new Intent(getBaseContext(), ScheduleUpdate.class);
            scheduleIntent.putExtra("jsonUpdate", true);
            this.startService(scheduleIntent);

            //NIE POTRZEBNE, ALE WYWALA BLEDY WIEC NIECH BEDZIE
            resultIntent = new Intent();
            resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            }else{
//                makeNotification = true;
//                resultIntent = new Intent(getBaseContext(), ReplacementsMain.class);
//                resultIntent.putExtra("isNotified", true);
//                Log.i("GNIS sendNotification", Integer.toString(msgSubject));
//                resultIntent.putExtra("menuItem", msgSubject);
//                resultIntent.putExtra("greetjson", jsonMsg);
//                resultIntent.setAction(Intent.ACTION_MAIN);
//                resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//            }
        }else if(msgSubject == 4){
            makeNotification = true;
            // pending implicit intent to view url
            resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(app_update_link));
            resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            makeNotification = true;
            resultIntent = new Intent(getBaseContext(), ReplacementsMain.class);
            resultIntent.putExtra("isNotified", true);
            Log.i("GNIS sendNotification", Integer.toString(msgSubject));
            resultIntent.putExtra("menuItem", msgSubject);
            resultIntent.putExtra("greetjson", jsonMsg);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }

        if(makeNotification) {
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String notifyTitle;
            switch (msgSubject) {
                case 0:
                    notifyTitle = new_profile;
                    break;
                case 1:
                    notifyTitle = new_home;
                    break;
                case 2:
                    notifyTitle = new_replacements;
                    break;
                case 3:
                    notifyTitle = new_schedule;
                    break;
                case 4:
                    notifyTitle = new_app_update;
                    break;
                default:
                    notifyTitle = new_replacements;
                    break;
            }
            mNotifyBuilder = new NotificationCompat.Builder(getBaseContext())
                    .setContentTitle(notifyTitle)
                    .setContentText(click_to_view)
                    .setSmallIcon(R.drawable.ic_launcher_white);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);

            // Set Vibrate, Sound and Light
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;
            mNotifyBuilder.setDefaults(defaults);

            //Ponizszy kod dziala tylko w API >= 16, wiec jest usuniety
            //mNotifyBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            mNotifyBuilder.setLights(ContextCompat.getColor(getApplicationContext(), R.color.blue_led_light), 600, 2400);
            mNotifyBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
            mNotifyBuilder.setAutoCancel(true);

            // Post a notification
            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        }
    }
}