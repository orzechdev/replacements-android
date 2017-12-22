package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 28.11.2017.
 */

public class ScheduleListCache {

    private MutableLiveData<List<Schedule>> cachedSchedules = new MutableLiveData<>();
    private static ScheduleListCache instance;

    private ScheduleListCache() {
        this.cachedSchedules = new MutableLiveData<>();
        List<Schedule> temp = new ArrayList<>();
        this.cachedSchedules.postValue(temp);
    }

    public static ScheduleListCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new ScheduleListCache();
        }

        return instance;
    }

    public void putData(List<Schedule> list){
        cachedSchedules.postValue(list);
    }

    public boolean updateOrAddNews(int institutionId, Schedule schedule){
        if(cachedSchedules != null){
            if(cachedSchedules.getValue() != null){
                for(int i = 0; i < cachedSchedules.getValue().size(); i++){
                    if(cachedSchedules.getValue().get(i).getInstitutionId() == institutionId){
                        List<Schedule> temp = cachedSchedules.getValue();
                        temp.set(i, schedule);
                        cachedSchedules.postValue(temp);
                        return false;
                    }
                }
                Log.i("Studytor","INSERTED SOMETHING");
                List<Schedule> temp = cachedSchedules.getValue();
                temp.add(schedule);
                cachedSchedules.postValue(temp);
            }
        }
        return true;
    }

    public MutableLiveData<List<Schedule>> getData(){
        return  cachedSchedules;
    }

}
