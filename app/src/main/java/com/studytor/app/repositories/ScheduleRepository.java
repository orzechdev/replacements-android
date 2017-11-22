package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

public class ScheduleRepository {

    private static final String CLASS_NAME = NewsRepository.class.getName();

    private static ScheduleRepository repositoryInstance;
    private WebService webService;

    private MutableLiveData<Schedule> scheduleData;

    public ScheduleRepository(Context c){
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.scheduleData = new MutableLiveData<>();
    }

    public static ScheduleRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ScheduleRepository(context);
        }

        return repositoryInstance;
    }

    public void getNews(final int institutionId) {

        System.out.println("REPO SCHEDULE CALLING WEB");

        this.webService.getSchedules(institutionId).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, final Response<Schedule> response) {
                System.out.println("REPO SCHEDULE GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getLessonplans() != null){
                    System.out.println("REPO SCHEDULE GET DATA FROM WEB SUCCESSFUL");
                    Schedule schedule = response.body();

                    schedule.setInstitutionId(institutionId);

                    //newsCache.insertOrAddNews(institutionId, pageNum, news);

                    scheduleData.postValue(schedule);
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
