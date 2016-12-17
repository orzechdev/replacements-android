package com.replacements.replacements.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.replacements.replacements.R;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.ReplacementDbAdapter;
import com.replacements.replacements.data.ScheduleUrlFilesDbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;
import com.replacements.replacements.interfaces.ApplicationConstants;
import com.replacements.replacements.models.JsonData;
import com.replacements.replacements.sync.GsonRequest;
import com.replacements.replacements.sync.MainSingleton;
import com.replacements.replacements.sync.ProfileRegister;
import com.replacements.replacements.sync.ScheduleUpdate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LoaderMain extends AppCompatActivity {
    private static final String CLASS_NAME = LoaderMain.class.getName();

    private TextView textViewMainLoader;
    private Button buttonMainLoader;
    private String loadingSchoolData;
    private String noInternet;
    private boolean loadingInProgress;
    private ClassDbAdapter classDbAdapter;
    private TeacherDbAdapter teacherDbAdapter;
    private Cursor cursor;
    private SharedPreferences mSharedPreferences;
    private String data_last;
    private ScheduleUrlFilesDbAdapter scheduleUrlFilesDbAdapter;
    private ReplacementDbAdapter replacementDbAdapter;
    private boolean allTasksDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_and_semi_transparent));

        //TODO
        textViewMainLoader = (TextView) findViewById(R.id.textViewMainLoader);
        buttonMainLoader = (Button) findViewById(R.id.buttonMainLoader);
        loadingSchoolData = getString(R.string.loading_school_data);
        noInternet = getString(R.string.no_internet_connect);

        ConnectivityManager connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            textViewMainLoader.setText((loadingSchoolData + " - " + Integer.toString(getSharedPreferences("dane", Context.MODE_PRIVATE).getInt("chosenSchool", 0))));
            buttonMainLoader.setVisibility(View.GONE);

            loadData();
        }else{
            textViewMainLoader.setText((noInternet + " - " + Integer.toString(getSharedPreferences("dane", Context.MODE_PRIVATE).getInt("chosenSchool", 0))));
            SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
            if(!prefs.getBoolean("schoolChangeStarted", false))
                buttonMainLoader.setVisibility(View.VISIBLE);
            else
                buttonMainLoader.setVisibility(View.GONE);
        }

        buttonMainLoader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseSchool = new Intent(view.getContext(), ChooseSchool.class);
                startActivity(chooseSchool);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("messageLoader"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("networkIsOn")){
                if(intent.getBooleanExtra("networkIsOn",false)) {
//                    if (!isMyServiceRunning(ScheduleUpdate.class))
//                        refreshAll();
                    textViewMainLoader.setText(loadingSchoolData);
                    buttonMainLoader.setVisibility(View.GONE);

                    loadData();
                }else{
                    textViewMainLoader.setText(noInternet);
                    SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
                    if(!prefs.getBoolean("schoolChangeStarted", false))
                        buttonMainLoader.setVisibility(View.VISIBLE);
                }
            }
            if(intent.hasExtra("finishService")){
                finishLoadData();
            }
        }
    };

    private void loadData(){
        allTasksDone = false;

        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        SharedPreferences.Editor localEditor = prefs.edit();
        localEditor.putBoolean("schoolChangeStarted", true);
        localEditor.apply();

        Log.i(CLASS_NAME, "loadData classesTeachersData delete");
        classDbAdapter = new ClassDbAdapter(getApplicationContext());
        classDbAdapter.open();
        classDbAdapter.deleteAllClasses();
        classDbAdapter.close();
        teacherDbAdapter = new TeacherDbAdapter(getApplicationContext());
        teacherDbAdapter.open();
        teacherDbAdapter.deleteAllTeachers();
        teacherDbAdapter.close();

        Log.i(CLASS_NAME, "loadData unregisterUser");
        if(localSharedPreferences.getBoolean("pref_notify_switch", true)) {
            String FCMToken = FirebaseInstanceId.getInstance().getToken();
            //Choosing school url to delete
            String url = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_2 : ApplicationConstants.SCHOOL_SERVER_1;
            Intent profileRegister = new Intent(this, ProfileRegister.class);
            profileRegister.putExtra("serverAction", 2);
            profileRegister.putExtra("token", FCMToken);
            profileRegister.putExtra("url", url);
            this.startService(profileRegister);
        }else{
            if(prefs.getBoolean("toDoUnregisterUser1", false)){
                String FCMToken = FirebaseInstanceId.getInstance().getToken();
                String url = ApplicationConstants.SCHOOL_SERVER_1;
                Intent profileRegister = new Intent(this, ProfileRegister.class);
                profileRegister.putExtra("serverAction", 2);
                profileRegister.putExtra("token", FCMToken);
                profileRegister.putExtra("url", url);
                this.startService(profileRegister);
            }
            if(prefs.getBoolean("toDoUnregisterUser2", false)){
                String FCMToken = FirebaseInstanceId.getInstance().getToken();
                String url = ApplicationConstants.SCHOOL_SERVER_2;
                Intent profileRegister = new Intent(this, ProfileRegister.class);
                profileRegister.putExtra("serverAction", 2);
                profileRegister.putExtra("token", FCMToken);
                profileRegister.putExtra("url", url);
                this.startService(profileRegister);
            }
        }

        Log.i(CLASS_NAME, "loadData classesTeachersData get new");
        startRequest();

        Log.i(CLASS_NAME, "loadData registerUser");
        if(localSharedPreferences.getBoolean("pref_notify_switch", true)) {
            String FCMToken = FirebaseInstanceId.getInstance().getToken();
            //Choosing school url to get
            String url = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
            Intent profileRegister = new Intent(this, ProfileRegister.class);
            profileRegister.putExtra("serverAction", 1);
            profileRegister.putExtra("token", FCMToken);
            profileRegister.putExtra("url", url);
            this.startService(profileRegister);
        }

        Log.i(CLASS_NAME, "loadData scheduleUpdate");
        String dirCurrent = prefs.getString("scheduleFilesDirNameCurrent","");
        if(!dirCurrent.equals("")) {
            scheduleUrlFilesDbAdapter = new ScheduleUrlFilesDbAdapter(getApplicationContext());
            scheduleUrlFilesDbAdapter.open();
            scheduleUrlFilesDbAdapter.deleteAllUrls();
            scheduleUrlFilesDbAdapter.close();
            //Delete old files if exist
            File dir = new File(getApplicationContext().getFilesDir() + File.separator + dirCurrent);
            if (dir.isDirectory()) {
                deleteOldFiles(dir);
            }
            localEditor.putString("scheduleFilesDirNameCurrent", "");
            localEditor.putBoolean("scheduleUpdateToNotify", false);
            localEditor.apply();
        }
        Intent scheduleIntent = new Intent(getBaseContext(), ScheduleUpdate.class);
        scheduleIntent.putExtra("jsonUpdate", true);
        startService(scheduleIntent);

        Log.i(CLASS_NAME, "loadData replacements delete");
        localEditor.putString("repl_date_today", "0");
        localEditor.putString("repl_date_tomorrow", "0");
        localEditor.putString("repl_date_last", "0");
        replacementDbAdapter = new ReplacementDbAdapter(getApplicationContext());
        replacementDbAdapter.open();
        replacementDbAdapter.deleteAllReplacements(true);
        replacementDbAdapter.deleteAllReplacements(false);
        replacementDbAdapter.close();

        //Reset other variable from application
        localEditor.putString("repl_data_date_last", "0");
        localEditor.putString("repl_date_today", "0");
        localEditor.putString("repl_date_last", "0");
        localEditor.putString("repl_date_tomorrow", "0");
        localEditor.apply();

        //Main variable using in loading data
        localEditor.putBoolean("schoolToChange", false);
        localEditor.putBoolean("schoolChangeStarted", false);
        localEditor.apply();

        allTasksDone = true;

        finishLoadData();
    }

    //check whether services is finished in loadData() and then run main activity (but without unregister service, because if previous school site doesn't work, user can't wait until it'll work)
    private void finishLoadData(){
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        boolean toDoRegisterUser;
        if(prefs.getInt("chosenSchool", 1) == 1){
            toDoRegisterUser = prefs.getBoolean("toDoRegisterUser1", false);
        }else{
            toDoRegisterUser = prefs.getBoolean("toDoRegisterUser2", false);
        }
        boolean scheduleUpdateToDo = prefs.getBoolean("scheduleUpdateToDo",false);
        if(allTasksDone && !toDoRegisterUser && !scheduleUpdateToDo){
            runMainActivity();
        }
    }

    private void runMainActivity(){
        Intent replacementsMain = new Intent(getBaseContext(), ReplacementsMain.class);
        replacementsMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(replacementsMain);
    }

    @Override
    public void onBackPressed() {
        Log.i(CLASS_NAME,"onBackPressed 0");
        ConnectivityManager connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if (!((connManager != null) && (connManager.getActiveNetworkInfo() != null))) {
            Log.i(CLASS_NAME,"onBackPressed 1");
            super.onBackPressed();
        }
        Log.i(CLASS_NAME,"onBackPressed 2");
    }

    public void startRequest() {
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        String urlDataUpdate;
        String urlData = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
        urlData = urlData + ApplicationConstants.APP_SERVER_URL_REPL_DATA;
        SharedPreferences.Editor localEditor = prefs.edit();
        localEditor.putString("repl_data_date_last", "0");
        localEditor.apply();

        //Ustalenie url dla danych i ich ostatniej aktualizacji
        urlDataUpdate = urlData + "?ver=0";
        Log.i("Request Profile 1", urlDataUpdate);

        //Pobranie danych
        GsonRequest<JsonData> jsObjRequestToday = new GsonRequest<>(
                Request.Method.GET,
                urlDataUpdate,
                JsonData.class, null,
                this.createRequestSuccessListener(),
                this.createRequestErrorListener());
        MainSingleton.getInstance(this).addToRequestQueue(jsObjRequestToday);
    }
    private Response.Listener<JsonData> createRequestSuccessListener() {
        return new Response.Listener<JsonData>() {
            @Override
            public void onResponse(JsonData response) {
                //Sprawdzenie czy aktywnosc nadal istnieje (czy nie nacisnieto np. przycisku wstecz)
//                if (getActivity() == null)
//                    return;
                int responClassSize = response.getClassesSize();
                classDbAdapter = new ClassDbAdapter(getApplicationContext());
                classDbAdapter.open();
                if(responClassSize>0) {
                    mSharedPreferences = getSharedPreferences("dane", 0);
                    for (int i = 0; i < responClassSize; i++) {
                        if (classDbAdapter.getClass(response.getClassTask(i).getId()) == null) {
                            classDbAdapter.insertClass(false, response.getClassTask(i));
                            Log.i("Profile Request Success", "Insert Class");
                        } else {
                            classDbAdapter.updateClass(response.getClassTask(i));
                            Log.i("Profile Request Success", "Update Class");
                        }
                        try {
                            String repl_date;
                            repl_date = mSharedPreferences.getString("repl_data_date_last", "0");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                            if (repl_date.equals("0")) {
                                data_last = response.getClassTask(i).getVer();
                                mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                Log.i("Profile Request S C", "repl_data_date_last = create");
                            } else {
                                Date dateSaved = sdf.parse(repl_date);
                                Date dateNew = sdf.parse(response.getClassTask(i).getVer());
                                if (dateNew.after(dateSaved)) {
                                    data_last = response.getClassTask(i).getVer();
                                    mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                    Log.i("Profile Request S C", "repl_data_date_last = update");
                                }
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                ArrayList<Long> toDeleteC = new ArrayList<>();
                cursor = classDbAdapter.getAllIds();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                            if(!response.getIdsClass().contains(id)) toDeleteC.add(id);
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
                for(int i=0; i < toDeleteC.size(); i++){
                    classDbAdapter.deleteClass(toDeleteC.get(i));
                    Log.i("Profile Request Success", "Delete Data Class");
                }
                classDbAdapter.close();
                int responTeacherSize = response.getTeachersSize();
                teacherDbAdapter = new TeacherDbAdapter(getApplicationContext());
                teacherDbAdapter.open();
                if(responTeacherSize>0) {
                    mSharedPreferences = getSharedPreferences("dane", 0);
                    for (int i = 0; i < responTeacherSize; i++) {
                        if (teacherDbAdapter.getTeacher(response.getTeacherTask(i).getId()) == null) {
                            teacherDbAdapter.insertTeacher(false, response.getTeacherTask(i));
                            Log.i("Profile Request Success", "Insert Teacher");
                        } else {
                            teacherDbAdapter.updateTeacher(response.getTeacherTask(i));
                            Log.i("Profile Request Success", "Update Teacher");
                        }
                        try {
                            String repl_date;
                            repl_date = mSharedPreferences.getString("repl_data_date_last", "0");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                            if (repl_date.equals("0")) {
                                data_last = response.getTeacherTask(i).getVer();
                                mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                Log.i("Profile Request S T", "repl_data_date_last = create");
                            } else {
                                Date dateSaved = sdf.parse(repl_date);
                                Date dateNew = sdf.parse(response.getTeacherTask(i).getVer());
                                if (dateNew.after(dateSaved)) {
                                    data_last = response.getTeacherTask(i).getVer();
                                    mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                    Log.i("Profile Request S T", "repl_data_date_last = update");
                                }
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                ArrayList<Long> toDeleteT = new ArrayList<>();
                cursor = teacherDbAdapter.getAllIds();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                            if(!response.getIdsTeacher().contains(id)) toDeleteT.add(id);
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
                for(int i=0; i < toDeleteT.size(); i++){
                    teacherDbAdapter.deleteTeacher(toDeleteT.get(i));
                    Log.i("Profile Request Success", "Delete Data Teacher");
                }
                teacherDbAdapter.close();
            }
        };
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Profile Request Error", "Error");
            }
        };
    }

    private void deleteOldFiles(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteOldFiles(child);

        fileOrDirectory.delete();
    }
}
