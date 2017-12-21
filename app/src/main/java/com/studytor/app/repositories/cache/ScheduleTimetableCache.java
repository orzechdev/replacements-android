package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;

import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.repositories.models.ScheduleTimetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 21.12.2017.
 */

public class ScheduleTimetableCache {

    private MutableLiveData<List<ScheduleTimetable>> cachedTimetables = new MutableLiveData<>();
    private static ScheduleTimetableCache instance;

    private ScheduleTimetableCache() {
        this.cachedTimetables = new MutableLiveData<>();
        List<ScheduleTimetable> temp = new ArrayList<>();
        this.cachedTimetables.postValue(temp);
    }

    public static ScheduleTimetableCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new ScheduleTimetableCache();
        }

        return instance;
    }

    public void putData(List<ScheduleTimetable> list){
        cachedTimetables.postValue(list);
    }

    public boolean updateOrAddNews(String url, ScheduleTimetable lessonplan){
        if(cachedTimetables != null){
            if(cachedTimetables.getValue() != null){
                List<ScheduleTimetable> temp = cachedTimetables.getValue();
                for(int i = 0; i < cachedTimetables.getValue().size(); i++){
                    if(cachedTimetables.getValue().get(i).getUrl().equals(url)){
                        System.out.println("REPO TIMETABLE CACHE UPDATED SOMETHING");
                        temp.set(i, lessonplan);
                        cachedTimetables.postValue(temp);
                        return false;
                    }
                }
                System.out.println("REPO TIMETABLE CACHE INSERTED SOMETHING");
                temp.add(lessonplan);
                cachedTimetables.postValue(temp);
            }
        }
        return true;
    }

    public MutableLiveData<List<ScheduleTimetable>> getData(){
        return  cachedTimetables;
    }

}