package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.cache.ScheduleListCache;
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
    //private ScheduleListCache scheduleListCache;
    private WebService webService;

    private MutableLiveData<ScheduleTimetable> scheduleData;

    public ScheduleTimetableRepository(){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
        //this.scheduleListCache = ScheduleListCache.getInstance();
    }

    public static ScheduleTimetableRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ScheduleTimetableRepository();
        }

        return repositoryInstance;
    }

    /*public void getSchedulesWithCacheCheck(final int institutionId){

        scheduleListCache.getData().observeForever(new Observer<List<Schedule>>() {
            @Override
            public void onChanged(@Nullable List<Schedule> schedules) {
                System.out.println("SCHEDULE REPO CACHE CHANGED");
                Schedule temp = null;
                if(schedules == null){
                    getSchedules(institutionId);
                }
                for(Schedule n : schedules){
                    if(n.getInstitutionId() == institutionId){
                        temp = n;
                    }
                }
                if(temp != null){
                    scheduleData.postValue(temp);
                }else{
                    getSchedules(institutionId);
                }
            }
        });

    }*/

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

                    scheduleData.postValue(timetable);
                    //scheduleListCache.updateOrAddNews(institutionId, schedule);

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
