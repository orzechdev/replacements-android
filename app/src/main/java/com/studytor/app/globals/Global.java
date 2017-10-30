package com.studytor.app.globals;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by przemek19980102 on 30.10.2017.
 * Class used to enable Picasso caching
 * based on https://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
 */

public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }
}
