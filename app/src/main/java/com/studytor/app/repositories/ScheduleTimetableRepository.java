package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.studytor.app.repositories.cache.ScheduleListCache;
import com.studytor.app.repositories.cache.ScheduleTimetableCache;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleTimetable;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by przemek19980102 on 13.12.2017.
 */

public class ScheduleTimetableRepository {

    private static ScheduleTimetableRepository repositoryInstance;
    private ScheduleTimetableCache scheduleTimetableCache;
    private WebService webService;

    private MutableLiveData<ScheduleTimetable> scheduleData;
    private static ScheduleTimetable cachedTimetable;

    public ScheduleTimetableRepository(){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
        this.scheduleTimetableCache = ScheduleTimetableCache.getInstance();
        cachedTimetable = new ScheduleTimetable();
    }

    public static ScheduleTimetableRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ScheduleTimetableRepository();
        }

        return repositoryInstance;
    }

    public void getSchedulesWithCacheCheck(final String url){

        scheduleTimetableCache.getData().observeForever(new Observer<List<ScheduleTimetable>>() {
            @Override
            public void onChanged(@Nullable List<ScheduleTimetable> schedules) {
                Log.i("Studytor","REPO TIMETABLE CACHE CHANGED");
                ScheduleTimetable temp = null;
                if(schedules == null){
                    getSchedules(url);
                    return;
                }
                for(ScheduleTimetable n : schedules){
                    if(n.getUrl().equals(url)){
                        temp = n;
                        break;
                    }
                }
                if(temp != null){
                    Log.i("Studytor","REPO TIMETABLE CACHE FOUND");
                    scheduleData.postValue(temp);
                }else{
                    Log.i("Studytor","REPO TIMETABLE CACHE DOWNLOADING");
                    getSchedules(url);
                }
            }
        });

    }

    public void getSchedules(@NonNull final String timetableURL) {

        Log.i("Studytor","REPO TIMETABLE CALLING WEB");

        if(cachedTimetable.getUrl() != null && cachedTimetable.getUrl().equals(timetableURL)){
            scheduleData.postValue(cachedTimetable);
            return;
        }

        this.webService.getScheduleTimetable(timetableURL).enqueue(new Callback<ScheduleTimetable>() {
            @Override
            public void onResponse(Call<ScheduleTimetable> call, Response<ScheduleTimetable> response) {
                Log.i("Studytor","REPO TIMETABLE GET DATA FROM WEB ENQUEUED");

                ScheduleTimetable timetable = response.body();

                if(response.isSuccessful() && timetable != null && timetable.getDays() != null){
                    Log.i("Studytor","REPO TIMETABLE GET DATA FROM WEB SUCCESSFUL");

                    timetable.setUrl(timetableURL);

                    scheduleData.postValue(timetable);
                    cachedTimetable = timetable;
                    //scheduleTimetableCache.updateOrAddTimetable(timetableURL, timetable);

                }else{
                    // TODO Handle errors - if needed show on screen appropriate info
                    Log.i("Studytor","REPO TIMETABLE GET DATA FROM WEB IS NULL 1" + response.toString());
                    scheduleData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ScheduleTimetable> call, Throwable t) {
                Log.i("Studytor","REPO TIMETABLE GET DATA FROM WEB IS NULL 2");

                scheduleData.postValue(null);

                t.printStackTrace();
            }
        });

    }

    public MutableLiveData<ScheduleTimetable> getScheduleData() {
        return scheduleData;
    }

}
