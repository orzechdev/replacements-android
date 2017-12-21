package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    private static final String CLASS_NAME = NewsRepository.class.getName();

    private static ScheduleTimetableRepository repositoryInstance;
    private ScheduleTimetableCache scheduleTimetableCache;
    private WebService webService;

    private MutableLiveData<ScheduleTimetable> scheduleData;

    public ScheduleTimetableRepository(){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
        this.scheduleTimetableCache = ScheduleTimetableCache.getInstance();
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
                System.out.println("REPO TIMETABLE CACHE CHANGED");
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
                    System.out.println("REPO TIMETABLE CACHE FOUND");
                    scheduleData.postValue(temp);
                }else{
                    System.out.println("REPO TIMETABLE CACHE DOWNLOADING");
                    getSchedules(url);
                }
            }
        });

    }

    public void getSchedules(@NonNull final String timetableURL) {

        System.out.println("REPO TIMETABLE CALLING WEB");

        this.webService.getScheduleTimetable(timetableURL).enqueue(new Callback<ScheduleTimetable>() {
            @Override
            public void onResponse(Call<ScheduleTimetable> call, Response<ScheduleTimetable> response) {
                System.out.println("REPO TIMETABLE GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getDays() != null){
                    System.out.println("REPO TIMETABLE GET DATA FROM WEB SUCCESSFUL");
                    ScheduleTimetable timetable = response.body();

                    timetable.setUrl(timetableURL);

                    //scheduleData.postValue(timetable);

                    scheduleTimetableCache.updateOrAddNews(timetableURL, timetable);

                }else{
                    System.out.println("REPO TIMETABLE GET DATA FROM WEB IS NULL 1" + response.toString());
                    scheduleData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ScheduleTimetable> call, Throwable t) {
                System.out.println("REPO TIMETABLE GET DATA FROM WEB IS NULL 2");

                scheduleData.postValue(null);

                t.printStackTrace();
            }
        });

    }

    public MutableLiveData<ScheduleTimetable> getScheduleData() {
        return scheduleData;
    }

}
