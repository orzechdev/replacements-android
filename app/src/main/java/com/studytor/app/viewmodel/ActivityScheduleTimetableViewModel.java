package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.ScheduleRepository;
import com.studytor.app.repositories.ScheduleTimetableRepository;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleTimetable;

/**
 * Created by przemek19980102 on 27.11.2017.
 */

public class ActivityScheduleTimetableViewModel extends AndroidViewModel {

    private Observable observable;
    private ScheduleTimetableRepository repository;
    private Handler handler;
    public LiveData<ScheduleTimetable> liveData = new MutableLiveData<>();

    private String dataURL;
    private String timetableName;

    public ActivityScheduleTimetableViewModel(@NonNull Application app){
        super(app);
    }

    public Observable getObservable(){return this.observable;}

    public void setup(final String dataURL, final String timetableName) {
        // If setup was already done, do not do it again
        if(this.observable != null && this.observable.schedule != null)
            return;

        if(dataURL != null && timetableName != null){

            this.dataURL = dataURL;
            this.timetableName = timetableName;

            this.observable = new Observable();
            this.observable.schedule = new ObservableField<>();

            this.handler = new Handler();

            //Set default observable values
            this.observable.currentItem.set(0);
            this.observable.itemsPerPage.set(1);
            this.observable.maxItemCount.set(1);

            repository = ScheduleTimetableRepository.getInstance();

            liveData = Transformations.map(repository.getScheduleData(), new Function<ScheduleTimetable, ScheduleTimetable>() {

                @Override
                public ScheduleTimetable apply(ScheduleTimetable input) {
                    input.setUrl(dataURL);
                    input.setName(timetableName);

                    observable.schedule.set(input);
                    observable.notifyChange();

                    return input;
                }

            });


            repository.getSchedules(dataURL);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void displayNext(){
        if(observable == null) return;
        if(observable.schedule.get() == null) return;
        if(observable.schedule.get().getDays() == null || observable.schedule.get().getDays().size() <= 0) return;
        if(observable.itemsPerPage == null || observable.itemsPerPage.get() == null) return;
        if(observable.currentItem == null || observable.currentItem.get() == null) return;
        if(observable.maxItemWidth == null || observable.maxItemWidth.get() == null) return;

        if (observable.currentItem.get() + 1 <= (observable.schedule.get().getDays().size() - observable.itemsPerPage.get())) {
            observable.currentItem.set(observable.currentItem.get()+1);
            observable.currentItem.notifyChange();
        }

    }

    public void displayPrevious(){
        if(observable == null) return;
        if(observable.schedule.get() == null) return;
        if(observable.schedule.get().getDays() == null || observable.schedule.get().getDays().size() <= 0) return;
        if(observable.itemsPerPage == null || observable.itemsPerPage.get() == null) return;
        if(observable.currentItem == null || observable.currentItem.get() == null) return;
        if(observable.maxItemWidth == null || observable.maxItemWidth.get() == null) return;

        if (observable.currentItem.get() - 1 >= 0) {
            observable.currentItem.set(observable.currentItem.get()-1);
            observable.currentItem.notifyChange();
        }

    }

    public class Observable extends BaseObservable {

        public ObservableField<ScheduleTimetable> schedule = new ObservableField<>();
        public ObservableField<Integer> itemsPerPage = new ObservableField<>();
        public ObservableField<Integer> maxItemWidth = new ObservableField<>();
        public ObservableField<Integer> currentItem = new ObservableField<>();
        public ObservableField<Integer> maxItemCount = new ObservableField<>();

    }

    public class Handler{

        public void arrowLeft(){
            displayPrevious();
        }

        public void arrowRight(){
            displayNext();
        }

    }

}
