package com.studytor.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.studytor.app.R;

public class ChooseSchool extends Activity {
    private static final String CLASS_NAME = ChooseSchool.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_school);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_and_semi_transparent));

        LinearLayout school1 = (LinearLayout) findViewById(R.id.school1);
        LinearLayout school2 = (LinearLayout) findViewById(R.id.school2);

        school1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(CLASS_NAME,"school1 socl 0");

                SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
                SharedPreferences.Editor localEditor = prefs.edit();

                if(prefs.getInt("chosenSchool", 0) == 2 && prefs.getBoolean("schoolToChange", false) && prefs.getBoolean("schoolChangeStarted", false)){
                    localEditor.putInt("chosenSchool", 1);
                    localEditor.putBoolean("schoolChangeStarted", false);
                    Log.i(CLASS_NAME,"school1 socl 1 0");
                }else if(prefs.getInt("chosenSchool", 0) == 2 && prefs.getBoolean("schoolToChange", false) && !prefs.getBoolean("schoolChangeStarted", true) && prefs.getBoolean("schoolChangeFinishedOnce", false)){
                    localEditor.putInt("chosenSchool", 1);
                    localEditor.putBoolean("schoolToChange", false);
                    Log.i(CLASS_NAME,"school1 socl 1 1");
                }else if(prefs.getInt("chosenSchool", 0) == 2 && !prefs.getBoolean("schoolToChange", true) && !prefs.getBoolean("schoolChangeStarted", true)){
                    localEditor.putInt("chosenSchool", 1);
                    localEditor.putBoolean("schoolToChange", true);
                    Log.i(CLASS_NAME,"school1 socl 1 2");
                }else if(prefs.getInt("chosenSchool", 0) == 0){
                    localEditor.putInt("chosenSchool", 1);
                    localEditor.putBoolean("schoolToChange", true);
                    localEditor.putBoolean("schoolChangeStarted", false);
                    Log.i(CLASS_NAME,"school1 socl 1 3");
                }else if(prefs.getInt("chosenSchool", 0) == 2 && prefs.getBoolean("schoolToChange", false) && !prefs.getBoolean("schoolChangeStarted", true) && !prefs.getBoolean("schoolChangeFinishedOnce", false)){
                    localEditor.putInt("chosenSchool", 1);
                    Log.i(CLASS_NAME,"school1 socl 1 4");
                }

                localEditor.apply();

                Log.i(CLASS_NAME,"school1 socl 2");

                if(prefs.getBoolean("schoolToChange", false)){
                    Log.i(CLASS_NAME,"school1 socl 3 0");
                    Intent loaderMain = new Intent(view.getContext(), LoaderMain.class);
                    loaderMain.putExtra("chosenSchool", 1);
                    startActivity(loaderMain);
                }else{
                    Log.i(CLASS_NAME,"school1 socl 3 1");
                    Intent replacementsMain = new Intent(view.getContext(), ReplacementsMain.class);
                    replacementsMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(replacementsMain);
                }
            }
        });

        school2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(CLASS_NAME,"school2 socl 0");

                SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
                SharedPreferences.Editor localEditor = prefs.edit();

                if(prefs.getInt("chosenSchool", 0) == 1 && prefs.getBoolean("schoolToChange", false) && prefs.getBoolean("schoolChangeStarted", false)){

                    localEditor.putInt("chosenSchool", 2);
                    localEditor.putBoolean("schoolChangeStarted", false);
                    Log.i(CLASS_NAME,"school2 socl 1 0");

                }else if(prefs.getInt("chosenSchool", 0) == 1 && prefs.getBoolean("schoolToChange", false) && !prefs.getBoolean("schoolChangeStarted", true) && prefs.getBoolean("schoolChangeFinishedOnce", false)){

                    localEditor.putInt("chosenSchool", 2);
                    localEditor.putBoolean("schoolToChange", false);
                    Log.i(CLASS_NAME,"school2 socl 1 1");

                }else if(prefs.getInt("chosenSchool", 0) == 1 && !prefs.getBoolean("schoolToChange", true) && !prefs.getBoolean("schoolChangeStarted", true)){

                    localEditor.putInt("chosenSchool", 2);
                    localEditor.putBoolean("schoolToChange", true);
                    Log.i(CLASS_NAME,"school2 socl 1 2");

                }else if(prefs.getInt("chosenSchool", 0) == 0){

                    localEditor.putInt("chosenSchool", 2);
                    localEditor.putBoolean("schoolToChange", true);
                    localEditor.putBoolean("schoolChangeStarted", false);
                    Log.i(CLASS_NAME,"school2 socl 1 3");

                }else if(prefs.getInt("chosenSchool", 0) == 1 && prefs.getBoolean("schoolToChange", false) && !prefs.getBoolean("schoolChangeStarted", true) && !prefs.getBoolean("schoolChangeFinishedOnce", false)){

                    localEditor.putInt("chosenSchool", 2);
                    Log.i(CLASS_NAME,"school2 socl 1 4");

                }

                localEditor.apply();

                Log.i(CLASS_NAME,"school2 socl 2");

                if(prefs.getBoolean("schoolToChange", false)){
                    Log.i(CLASS_NAME,"school2 socl 3 0");
                    Intent loaderMain = new Intent(view.getContext(), LoaderMain.class);
                    loaderMain.putExtra("chosenSchool", 2);
                    startActivity(loaderMain);
                }else{
                    Log.i(CLASS_NAME,"school2 socl 3 1");
                    Intent replacementsMain = new Intent(view.getContext(), ReplacementsMain.class);
                    replacementsMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(replacementsMain);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Bundle intentExtra = getIntent().getExtras();
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        Log.i(CLASS_NAME,"onBackPressed 0");
        if (!prefs.getBoolean("schoolToChange", false)) {
            Log.i(CLASS_NAME,"onBackPressed 1 0");
            super.onBackPressed();
        }else{
            Log.i(CLASS_NAME,"onBackPressed 1 1");
            if(intentExtra != null) {
                Log.i(CLASS_NAME,"onBackPressed 1 1 0");
                boolean activityParentExists = intentExtra.getBoolean("activityParentExists", false);
                if (!activityParentExists) {
                    Log.i(CLASS_NAME,"onBackPressed 1 1 0 0");
                    super.onBackPressed();
                }
            }else{
                Log.i(CLASS_NAME,"onBackPressed 1 1 1");
                super.onBackPressed();
            }
        }
        Log.i(CLASS_NAME,"onBackPressed 2");
    }
}
