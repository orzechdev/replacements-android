package com.studytor.app.viewmodel;

import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.studytor.app.repositories.ReplacementsRepository;
import com.studytor.app.repositories.models.ReplacementsJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class FragmentInstitutionProfileReplacementsViewModel extends AndroidViewModel {

    private ReplacementsRepository replacementsRepository;

    private LiveData<ReplacementsJson> replsRepo = null;
    private LiveData<ReplacementsJson> repls = null;

    private LiveData<Boolean> isRefreshingRepo = null;
    private LiveData<Boolean> isRefreshing = null;

    private int institutionId;
    private LiveData<String> selectedDateRepo = null;
    private LiveData<String> selectedDate = null;
    private MutableLiveData<Long> daysFromToday = null;
    private String yesterdayDate = "10-11-2017";
    private String todayDate = "11-11-2017";
    private String tomorrowDate = "12-11-2017";

    private FragmentInstitutionProfileReplacementsViewModel.Observable observable = new FragmentInstitutionProfileReplacementsViewModel.Observable();
    private FragmentInstitutionProfileReplacementsViewModel.Handlers handlers = new FragmentInstitutionProfileReplacementsViewModel.Handlers();

    public FragmentInstitutionProfileReplacementsViewModel.Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionProfileReplacementsViewModel.Handlers getHandlers() {
        return handlers;
    }

    public FragmentInstitutionProfileReplacementsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(int institutionId) {
        // If setup was already done, do not do it again
        if(this.observable.replacements.get() != null)
            return;

        this.institutionId = institutionId;

        replacementsRepository = ReplacementsRepository.getInstance();

        selectedDateRepo = replacementsRepository.getSelectedDate();

        daysFromToday = new MutableLiveData<>();

        replsRepo = replacementsRepository.getReplacements(institutionId, todayDate);
        isRefreshingRepo = replacementsRepository.isRefreshing();

        repls = Transformations.map(replsRepo, new Function<ReplacementsJson, ReplacementsJson>() {
            @Override
            public ReplacementsJson apply(ReplacementsJson input) {
                return input;
            }
        });

        isRefreshing = Transformations.map(isRefreshingRepo, new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean input) {
                return input;
            }
        });

        selectedDate = Transformations.map(selectedDateRepo, new Function<String, String>() {
            @Override
            public String apply(String input) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date dateSelected = new Date(2010,1,1);
                Date dateToday = new Date(2010,1,1);
                try {
                    dateSelected = sdf.parse(input);
                    dateToday = sdf.parse(todayDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = dateSelected.getTime() - dateToday.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                daysFromToday.setValue(days);

                return input;
            }
        });
    }

    public LiveData<ReplacementsJson> getReplacements() {
        return repls;
    }

    public void requestRepositoryUpdate(){
        Log.i("FragInstProfReplsVM", "requestRepositoryUpdate 1");
        replacementsRepository.forceRefreshData(institutionId, selectedDate.getValue());
        Log.i("FragInstProfReplsVM", "requestRepositoryUpdate 2");
    }

    public LiveData<String> getSelectedDate(){
        return selectedDate;
    }

    public LiveData<Long> getDaysFromToday(){
        return daysFromToday;
    }

    private void refreshDate(){
        //TODO Taking dates from Android API; for now it is the data for tests
        yesterdayDate = "10-11-2017";
        todayDate = "11-11-2017";
        tomorrowDate = "12-11-2017";
    }

    public class Handlers {

        public void onClickYesterday(View view) {
            replacementsRepository.getReplacements(institutionId, yesterdayDate);
        }

        public void onClickToday(View view) {
            replacementsRepository.getReplacements(institutionId, todayDate);
        }

        public void onClickTomorrow(View view) {
            replacementsRepository.getReplacements(institutionId, tomorrowDate);
        }

        public void onClickDate(View view) {
            view.getContext();

            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String dateStr = Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" + Integer.toString(year);
                    replacementsRepository.getReplacements(institutionId, dateStr);
                }

            };

            String[] dateSplit = {"01","01","2010"};
            if(selectedDate.getValue() != null)
                dateSplit = selectedDate.getValue().split("-");

            new DatePickerDialog(view.getContext(), date, Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[1]) - 1, Integer.parseInt(dateSplit[0])).show();
        }
    }

    // DAWID --- Very huge advantage of having this variable in repo - even if we switch between screens and return to list of institutions,
    // --------- if the refreshing is not yet ended we will see, that it is refreshing, and any change of the screen does not cause that this refreshing will not be visible! :)
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<ReplacementsJson> replacements = new ObservableField<>();
        public final ObservableField<Boolean> isRefreshing = new ObservableField<>();
        public final ObservableField<String> selectedDate = new ObservableField<>();
        public final ObservableField<Long> daysFromToday = new ObservableField<>();

    }

}
