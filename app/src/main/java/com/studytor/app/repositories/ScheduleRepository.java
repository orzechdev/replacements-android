package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.studytor.app.R;
import com.studytor.app.repositories.cache.ScheduleListCache;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.repositories.models.ScheduleSection;
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

    private static final String SECTION_NAME_CLASSES = "classes";
    private static final String SECTION_NAME_TEACHERS = "teachers";
    private static final String SECTION_NAME_CLASSROOMS = "classrooms";

    private String sectionVisibleNameClasses;
    private String sectionVisibleNameTeachers;
    private String sectionVisibleNameClassrooms;

    private static ScheduleRepository repositoryInstance;
    private ScheduleListCache scheduleListCache;
    private WebService webService;

    private MutableLiveData<Schedule> scheduleData;

    private ScheduleRepository(Context context){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
        this.scheduleListCache = ScheduleListCache.getInstance();
        prepareSectionVisibleNames(context);
    }

    public static ScheduleRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ScheduleRepository(context);
        }

        return repositoryInstance;
    }

    private void prepareSectionVisibleNames(Context context){
        sectionVisibleNameClasses = context.getResources().getString(R.string.schedule_section_classes);
        sectionVisibleNameTeachers = context.getResources().getString(R.string.schedule_section_teachers);
        sectionVisibleNameClassrooms = context.getResources().getString(R.string.schedule_section_classrooms);
    }

    public void getSchedulesWithCacheCheck(final int institutionId){

        scheduleListCache.getData().observeForever(new Observer<List<Schedule>>() {
            @Override
            public void onChanged(@Nullable List<Schedule> schedules) {
                Log.i("Studytor","SCHEDULE REPO CACHE CHANGED");
                Schedule temp = null;
                if(schedules == null){
                    getSchedules(institutionId);
                }else{
                    for (Schedule n : schedules) {
                        if (n.getInstitutionId() == institutionId) {
                            temp = n;
                        }
                    }
                    if(temp != null){
                        scheduleData.postValue(temp);
                    }else{
                        getSchedules(institutionId);
                    }
                }
            }
        });

    }

    public void getSchedules(@NonNull final int institutionId) {

        Log.i("Studytor","REPO SCHEDULE CALLING WEB");

        this.webService.getSchedules(institutionId).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, final Response<Schedule> response) {
                Log.i("Studytor","REPO SCHEDULE GET DATA FROM WEB ENQUEUED");

                Schedule schedule = response.body();

                if(response.isSuccessful() && schedule != null && schedule.getLessonplans() != null){
                    Log.i("Studytor","REPO SCHEDULE GET DATA FROM WEB SUCCESSFUL");

                    schedule.setInstitutionId(institutionId);
                    Log.i("Studytor","SCHEDULE JAKIES_DZIWNE_INSTITUTION: " + institutionId);

                    List<ScheduleLessonplan> lessonplans = schedule.getLessonplans();

                    for (ScheduleLessonplan scheduleLessonplan : lessonplans) {
                        List<ScheduleSection> sections = scheduleLessonplan.getSections();
                        for (ScheduleSection section : sections) {
                            String sectionName = section.getName();
                            switch (sectionName) {
                                case SECTION_NAME_CLASSES:
                                    section.setVisibleName(sectionVisibleNameClasses);
                                    break;
                                case SECTION_NAME_TEACHERS:
                                    section.setVisibleName(sectionVisibleNameTeachers);
                                    break;
                                case SECTION_NAME_CLASSROOMS:
                                    section.setVisibleName(sectionVisibleNameClassrooms);
                                    break;
                                default:
                                    section.setVisibleName(sectionName);
                                    break;
                            }
                        }
                    }

                    scheduleData.postValue(schedule);
                    scheduleListCache.updateOrAddSchedules(institutionId, schedule);

                }else{
                    // TODO Handle errors - if needed show on screen appropriate info
                    Log.i("Studytor","REPO SCHEDULE GET DATA FROM WEB IS NULL 1" + response.toString());
                    scheduleData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.i("Studytor","REPO SCHEDULE GET DATA FROM WEB IS NULL 2");

                scheduleData.postValue(null);

                t.printStackTrace();
            }
        });

    }

    public MutableLiveData<Schedule> getScheduleData() {
        return scheduleData;
    }

}
