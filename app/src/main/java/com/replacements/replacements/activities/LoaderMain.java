package com.replacements.replacements.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.replacements.replacements.R;

public class LoaderMain extends AppCompatActivity {
    private static final String CLASS_NAME = LoaderMain.class.getName();

    private TextView textViewMainLoader;
    private Button buttonMainLoader;
    private String loadingSchoolData;
    private String noInternet;

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
                }else{
                    textViewMainLoader.setText(noInternet);
                    SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
                    if(!prefs.getBoolean("schoolChangeStarted", false))
                        buttonMainLoader.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void loadData(){
        //TODO Change variables
        //TODO Change header in drawer menu
        //TODO Change profile
        //TODO Change schedule
        //TODO Change replacements
    }
}
