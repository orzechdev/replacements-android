package com.replacements.replacements.sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.replacements.replacements.R;
import com.replacements.replacements.activities.ReplacementsMain;
import com.replacements.replacements.data.DbAdapter;
import com.replacements.replacements.data.ScheduleUrlFilesDbAdapter;
import com.replacements.replacements.interfaces.ApplicationConstants;
import com.replacements.replacements.models.JsonSchedule;
import com.replacements.replacements.models.JsonScheduleFiles;
import com.replacements.replacements.models.JsonScheduleFilesDeserialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Dawid on 2016-08-07.
 */
public class ScheduleUpdate extends IntentService {
    private static final String CLASS_NAME = ScheduleUpdate.class.getName();

    private ArrayList<String> fileUrlsToDownload = new ArrayList<>();
    private int fileDownloadCounterMain = 0;
    private int fileDownloadCounterCache = Integer.MAX_VALUE;
    private String prefixMain;
    private DbAdapter dbAdapter;
    private ScheduleUrlFilesDbAdapter scheduleUrlFilesDbAdapter;
    private boolean checkServicePossible = true;

    public ScheduleUpdate(){
        super(CLASS_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(CLASS_NAME, "onHandleIntent");
        //Zapisz w pamieci, ze do pobrania jest plan
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putBoolean("scheduleUpdateToDo", true);
        boolean startServiceIfNotConnection = false;
        //Zapisz w pamieci, ze do pobrania jest lista plikow w json dla planu jesli przyszlo dopiero co powiadomienie
        Bundle intentExtra = intent.getExtras();
        if(intentExtra != null) {
            checkServicePossible = intentExtra.getBoolean("checkServicePossible", true);
            boolean jsonUpdate = intentExtra.getBoolean("jsonUpdate", false);
            if(jsonUpdate) {
                sharedPrefEditor.putBoolean("scheduleUpdateJson", true);
                //W razie braku internetu wlacz alarm na serwis
                startServiceIfNotConnection = true;
                //Zapisz nazwe nowego folderu dla planu w sharedPref
                long seconds = System.currentTimeMillis()/1000;
                String folderName = "schedule_" + Long.toString(seconds,36);
                sharedPrefEditor.putString("scheduleFilesDirNameNew", folderName);
                Log.i(CLASS_NAME, "onHandleIntent dirNameNew: " + folderName);
            }
        }
        sharedPrefEditor.apply();

        ConnectivityManager connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            //Jesli w pamieci jest zapisane, ze do pobrania jest json dla plikow - pobierz go
            if (sharedPref.getBoolean("scheduleUpdateJson", false)) {
                downloadFileList();
            } else if (fileUrlsToDownload.size() == 0) {
                getFileUrlsFromDB();
            }
        }else if(startServiceIfNotConnection){
            startCheckingService();
        }
    }

    private void downloadFileList(){
        String url_data;
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        url_data = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
        url_data = url_data + ApplicationConstants.APP_SERVER_URL_LESSON_PLAN;
        GsonRequest<JsonSchedule> jsObjRequest = new GsonRequest<>(
                Request.Method.GET,
                url_data,
                JsonSchedule.class, null,
                this.downloadFileListRequestSuccessListener(),
                this.downloadFileListRequestErrorListener());
        MainSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    private Response.Listener<JsonSchedule> downloadFileListRequestSuccessListener() {
        return new Response.Listener<JsonSchedule>() {
            @Override
            public void onResponse(JsonSchedule response) {
                Log.i(CLASS_NAME, "downloadFileList Success");

                String prefix = response.getPrefix();
                SharedPreferences sharedPref = getSharedPreferences("dane", 0);
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putString("scheduleUpdatePrefix", prefix);
                sharedPrefEditor.apply();
                Log.i(CLASS_NAME, "downloadFileList Success prefix: " + prefix);

                JsonElement files = response.getFiles();
                Log.i(CLASS_NAME, "downloadFileList Success 1");
                if (files != null){
                    Log.i(CLASS_NAME, "downloadFileList Success 2");
                    deserializeFileList(prefix, files);
                }
            }
        };
    }

    private Response.ErrorListener downloadFileListRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(CLASS_NAME, "downloadFileList Error: " + error.toString());
                startCheckingService();
            }
        };
    }

    private JsonScheduleFiles deserializeFileList(String prefix, JsonElement files) {
        String json = files.toString();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(JsonScheduleFiles.class, new JsonScheduleFilesDeserialize())
                .create();
        JsonScheduleFiles jsf = gson.fromJson(json, JsonScheduleFiles.class);

        ArrayList<String> fileUrls = new ArrayList<>();

        if(jsf.getCss() != null)
            for (JsonElement url : jsf.getCss()) {
                String urlStr = url.getAsString();
                if(!urlStr.contains("."))
                    urlStr += ".html";
                fileUrls.add(prefix + "css/" + urlStr);
            }
        if(jsf.getImages() != null)
            for (JsonElement url : jsf.getImages()) {
                String urlStr = url.getAsString();
                if(!urlStr.contains("."))
                    urlStr += ".html";
                fileUrls.add(prefix + "images/" + urlStr);
            }
        if(jsf.getPlany() != null)
            for (JsonElement url : jsf.getPlany()) {
                String urlStr = url.getAsString();
                if(!urlStr.contains("."))
                    urlStr += ".html";
                fileUrls.add(prefix + "plany/" + urlStr);
            }
        if(jsf.getScripts() != null)
            for (JsonElement url : jsf.getScripts()) {
                String urlStr = url.getAsString();
                if(!urlStr.contains("."))
                    urlStr += ".html";
                fileUrls.add(prefix + "scripts/" + urlStr);
            }
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor sharedPrefEditor = localSharedPreferences.edit();
        sharedPrefEditor.putString("schedule_main_file", "index.html");
        if(jsf.getMainFiles() != null)
            for (JsonElement url : jsf.getMainFiles()) {
                String urlStr = url.getAsString();
                if(!urlStr.contains("."))
                    urlStr += ".html";
                fileUrls.add(prefix + urlStr);
                String urlStrSave;
                if(urlStr.equals("lista.html")) {
                    urlStrSave = urlStr;
                    sharedPrefEditor.putString("schedule_main_file", urlStrSave);
                }
            }
        sharedPrefEditor.apply();

        Log.i(CLASS_NAME, "saveFileUrlsInDB filesQuantity: " + fileUrls.size());

        if(fileUrls.size() != 0) {
            fileUrlsToDownload = fileUrls;
            deleteAndSaveFileUrlsInDB(fileUrls);
        }

        return jsf;
    }

    private void deleteAndSaveFileUrlsInDB(ArrayList<String> fileUrls){
        dbAdapter = new DbAdapter(getApplicationContext());
        dbAdapter.open();
        dbAdapter.close();
        scheduleUrlFilesDbAdapter = new ScheduleUrlFilesDbAdapter(getApplicationContext());
        scheduleUrlFilesDbAdapter.open();
        scheduleUrlFilesDbAdapter.deleteAllUrls();
        Log.i(CLASS_NAME, "deleteAndSaveFileUrlsInDB all urls are deleted");
        int id = 1;
        for (String url : fileUrls) {
            Log.i(CLASS_NAME, "deleteAndSaveFileUrlsInDB id: " + id + " and url: " + url);
            scheduleUrlFilesDbAdapter.insertUrl(id, url);
            id++;
        }
        scheduleUrlFilesDbAdapter.close();
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putBoolean("scheduleUpdateJson", false);
        sharedPrefEditor.apply();
        downloadFiles(fileUrlsToDownload);
    }

    private void getFileUrlsFromDB() {
        scheduleUrlFilesDbAdapter = new ScheduleUrlFilesDbAdapter(getApplicationContext());
        scheduleUrlFilesDbAdapter.open();
        Cursor cursor = scheduleUrlFilesDbAdapter.getAllUrls(false);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String url = cursor.getString(cursor.getColumnIndex(ScheduleUrlFilesDbAdapter.KEY_URL));
                    fileUrlsToDownload.add(url);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        scheduleUrlFilesDbAdapter.close();
        downloadFiles(fileUrlsToDownload);
    }

    private void downloadFiles(ArrayList<String> fileUrls){
        fileDownloadCounterMain = fileUrls.size();
        prefixMain = getSharedPreferences("dane", 0).getString("scheduleUpdatePrefix","no_name_file");
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        for (final String url : fileUrls) {
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse");
                        String newResponse = response;
                        try {
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse UTF-8");
                            newResponse = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse UnsupportedEncodingException");
                        }
                        saveFile(newResponse, url);
                        fileUrlsToDownload.remove(url);
                        fileDownloadCounterMain--;
                        if(fileDownloadCounterMain == 0 && fileUrlsToDownload.size() > 0 && fileUrlsToDownload.size() != fileDownloadCounterCache){
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse loop finished not all files");
                            fileDownloadCounterCache = fileUrlsToDownload.size();
                            downloadFiles(fileUrlsToDownload);
                        }
                        if(fileUrlsToDownload.size() == 0){
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse loop finished all files");
                            SharedPreferences sharedPref = getSharedPreferences("dane", 0);
                            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                            sharedPrefEditor.putBoolean("scheduleUpdateToDo", false);
                            sharedPrefEditor.apply();
                            stopCheckingService();
                            finishUpdate();
                            sendNotificationOrMessage();
                        }
                        if(fileDownloadCounterMain == 0 && fileUrlsToDownload.size() > 0 && fileUrlsToDownload.size() == fileDownloadCounterCache){
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onResponse second same loop finished not all files");
                            startCheckingService();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(CLASS_NAME, "downloadFiles stringRequest onErrorResponse");
                        fileDownloadCounterMain--;
                        if(fileDownloadCounterMain == 0 && fileUrlsToDownload.size() > 0 && fileUrlsToDownload.size() != fileDownloadCounterCache){
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onErrorResponse loop finished not all files");
                            fileDownloadCounterCache = fileUrlsToDownload.size();
                            downloadFiles(fileUrlsToDownload);
                        }
                        if(fileDownloadCounterMain == 0 && fileUrlsToDownload.size() > 0 && fileUrlsToDownload.size() == fileDownloadCounterCache){
                            Log.i(CLASS_NAME, "downloadFiles stringRequest onErrorResponse second same loop finished not all files");
                            startCheckingService();
                        }
                    }
                });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private boolean saveFile(String file, String url){
        Log.i(CLASS_NAME, "saveFile");
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        String dirNew = sharedPref.getString("scheduleFilesDirNameNew","");

        String dirWithFile = url.replaceFirst(prefixMain,"");
        String[] dirFileSplit = dirWithFile.split("/");
        Log.i(CLASS_NAME, dirFileSplit[0]);
        try {
            if(dirFileSplit.length > 1){
                final File newFile = new File(getApplicationContext().getFilesDir() + File.separator + dirNew + File.separator + dirFileSplit[0]);
                newFile.mkdirs();
                File fileWithinMyDir2 = new File(getApplicationContext().getFilesDir() + File.separator + dirNew + File.separator + dirFileSplit[0]); //Getting a file within the dir.
                File fileWithinMyDir = new File(fileWithinMyDir2, dirFileSplit[1]); //Getting a file within the dir.
                FileOutputStream outputStream = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
                outputStream.write(file.getBytes());
                outputStream.close();
                Log.i(CLASS_NAME, "saveFile in dir");
            }else {
                final File newFile = new File(getApplicationContext().getFilesDir() + File.separator + dirNew);
                newFile.mkdirs();
                File fileWithinMyDir2 = new File(getApplicationContext().getFilesDir() + File.separator + dirNew); //Getting a file within the dir.
                File fileWithinMyDir = new File(fileWithinMyDir2, dirFileSplit[0]); //Getting a file within the dir.
                FileOutputStream outputStream = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.
                outputStream.write(file.getBytes());
                outputStream.close();
                Log.i(CLASS_NAME, "saveFile out of dir");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        scheduleUrlFilesDbAdapter = new ScheduleUrlFilesDbAdapter(getApplicationContext());
        scheduleUrlFilesDbAdapter.open();
        scheduleUrlFilesDbAdapter.setDownloaded(url, true);
        scheduleUrlFilesDbAdapter.close();
        return true;
    }

    private void startCheckingService(){
        if(checkServicePossible) {
            Log.i(CLASS_NAME, "startCheckingService");
            Intent serviceIntent = new Intent(this, ScheduleUpdate.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setInexactRepeating(
                    AlarmManager.RTC,
                    System.currentTimeMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent);
        }else{
            Intent newIntent = new Intent("messageLoader");
            newIntent.putExtra("finishService", "ScheduleUpdateFailure");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
        }
    }

    private void stopCheckingService(){
        Log.i(CLASS_NAME, "stopCheckingService");
        Intent serviceIntent = new Intent(this, ScheduleUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private void finishUpdate(){
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        sharedPrefEditor.putString("lastUrlSchedule", "");

        String dirCurrent = sharedPref.getString("scheduleFilesDirNameCurrent","");
        String dirNew = sharedPref.getString("scheduleFilesDirNameNew","");

        if(dirCurrent.equals("")){
            sharedPrefEditor.putString("scheduleFilesDirNameCurrent", dirNew);
            sharedPrefEditor.putString("scheduleFilesDirNameNew", "");
        }else{
            //Delete old files if exist
            File dir = new File(getApplicationContext().getFilesDir() + File.separator + dirCurrent);
            if (dir.isDirectory()){
                deleteOldFiles(dir);
            }
            sharedPrefEditor.putString("scheduleFilesDirNameCurrent", dirNew);
            sharedPrefEditor.putString("scheduleFilesDirNameNew", "");
        }

        sharedPrefEditor.apply();
    }

    private void deleteOldFiles(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteOldFiles(child);

        fileOrDirectory.delete();
    }

    private void sendNotificationOrMessage(){
        Log.i(CLASS_NAME, "sendNotificationOrMessage");
        SharedPreferences sharedPref = getSharedPreferences("dane", 0);
        if(sharedPref.getBoolean("scheduleUpdateToNotify",false)){
            Log.i(CLASS_NAME, "sendNotification");
            // Sets an ID for the notification, so it can be updated
            final int notifyID = 9001;

            Intent resultIntent;
            PendingIntent resultPendingIntent;
            resultIntent = new Intent(getBaseContext(), ReplacementsMain.class);
            resultIntent.putExtra("isNotified", true);
            int msgSubject = 3;
            Log.i("GNIS sendNotification", Integer.toString(msgSubject));
            resultIntent.putExtra("menuItem", msgSubject);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String notifyTitle = getString(R.string.new_schedule);

            mNotifyBuilder = new NotificationCompat.Builder(getBaseContext())
                    .setContentTitle(notifyTitle)
                    .setContentText(getString(R.string.click_to_view))
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

            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putBoolean("scheduleUpdateToNotify",false);
            sharedPrefEditor.apply();
        }else{
            Intent intent = new Intent("messageSchedule");
            intent.putExtra("scheduleUpdated", true);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putBoolean("scheduleUpdateToNotify",false);
            sharedPrefEditor.apply();


            Intent newIntent = new Intent("messageLoader");
            newIntent.putExtra("finishService", "ScheduleUpdated");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);
        }
    }
}