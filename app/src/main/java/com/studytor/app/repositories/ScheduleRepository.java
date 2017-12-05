package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.cache.ScheduleListCache;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

public class ScheduleRepository {

    private static final String CLASS_NAME = NewsRepository.class.getName();

    private static ScheduleRepository repositoryInstance;
    private ScheduleListCache scheduleListCache;
    private WebService webService;

    private MutableLiveData<Schedule> scheduleData;

    public ScheduleRepository(Context c){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
        this.scheduleListCache = ScheduleListCache.getInstance();
    }

    public static ScheduleRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ScheduleRepository(context);
        }

        return repositoryInstance;
    }

    public void getSchedulesWithCacheCheck(final int institutionId){

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

    }

    public void getSchedules(@NonNull final int institutionId) {

        System.out.println("REPO SCHEDULE CALLING WEB");

        this.webService.getSchedules(institutionId).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, final Response<Schedule> response) {
                System.out.println("REPO SCHEDULE GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getLessonplans() != null){
                    System.out.println("REPO SCHEDULE GET DATA FROM WEB SUCCESSFUL");
                    Schedule schedule = response.body();

                    schedule.setInstitutionId(institutionId);
                    System.out.println("SCHEDULE JAKIES_DZIWNE_INSTITUTION: " + institutionId);

                    scheduleData.postValue(schedule);
                    scheduleListCache.updateOrAddNews(institutionId, schedule);

                }else{
                    if(response.body().getLessonplans() == null){
                        System.out.println("REPO SCHEDULE GET DATA FROM WEB NO LESSONPLANS!" + response.toString());
                    }
                    System.out.println("REPO SCHEDULE GET DATA FROM WEB IS NULL 1" + response.toString());
                    scheduleData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                System.out.println("REPO SCHEDULE GET DATA FROM WEB IS NULL 2");

                scheduleData.postValue(null);

                t.printStackTrace();
            }
        });

    }

    public MutableLiveData<Schedule> getScheduleData() {
        return scheduleData;
    }

}
