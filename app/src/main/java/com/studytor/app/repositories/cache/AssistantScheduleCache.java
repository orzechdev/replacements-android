package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;

import com.studytor.app.repositories.models.Schedule;

/**
 * Created by Dawid on 25.07.2017.
 */

public class AssistantScheduleCache {
    private MutableLiveData<Schedule> cachedScheduleList = new MutableLiveData<>();
    private static AssistantScheduleCache instance;

    private AssistantScheduleCache() {
        this.cachedScheduleList = new MutableLiveData<>();
    }

    public static AssistantScheduleCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new AssistantScheduleCache();
        }

        return instance;
    }

    public void putData(Schedule list){
        cachedScheduleList.postValue(list);
    }

    public MutableLiveData<Schedule> getData(){
        return  cachedScheduleList;
    }
}
