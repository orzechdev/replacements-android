package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.ScheduleRepository;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.Schedule;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

public class FragmentInstitutionProfileSchedulesViewModel extends AndroidViewModel{

    private Observable observable = new Observable();
    private int institutionId;
    private ScheduleRepository scheduleRepository;

    public Observable getObservable(){return this.observable;}

    public FragmentInstitutionProfileSchedulesViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(int institutionId) {
        // If setup was already done, do not do it again
        if(this.observable != null && this.observable.schedule != null)
            return;

        this.institutionId = institutionId;
        this.observable.schedule = new MutableLiveData<>();

        this.observable.helper = (new BindingHelper());

        scheduleRepository = ScheduleRepository.getInstance(this.getApplication());

        scheduleRepository.getScheduleData().observeForever(new Observer<Schedule>() {
            @Override
            public void onChanged(@Nullable Schedule s) {
                observable.schedule.postValue(s);
            }
        });

        scheduleRepository.getNews(this.institutionId);
    }

    public class Observable extends BaseObservable{

        public MutableLiveData<Schedule> schedule = null;

        public BindingHelper helper = null;

    }

    public class BindingHelper{
        public String level0Title = "";
        public String level1Title = "";

        public BindingHelper(){}

        public String getLevel0Title() {
            return level0Title;
        }

        public void setLevel0Title(String level0Title) {
            this.level0Title = level0Title;
        }

        public String getLevel1Title() {
            return level1Title;
        }

        public void setLevel1Title(String level1Title) {
            this.level1Title = level1Title;
        }
    }

}
