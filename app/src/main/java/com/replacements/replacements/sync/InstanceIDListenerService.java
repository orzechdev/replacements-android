package com.replacements.replacements.sync;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Dawid on 2016-07-21.
 */
public class InstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String CLASS_NAME = InstanceIDListenerService.class.getName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SendTokenToServer(refreshedToken);
        Log.i(CLASS_NAME, "onTokenRefresh");
    }

    public void SendTokenToServer(String token) {
        Log.i(CLASS_NAME, "SendTokenToServer token = " + token);
        Intent profileRegister = new Intent(getApplicationContext(), ProfileRegister.class);
        profileRegister.putExtra("serverAction", 1);
        profileRegister.putExtra("token", token);
        getApplicationContext().startService(profileRegister);
    }
}