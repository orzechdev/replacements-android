package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.studytor.app.repositories.ReplacementsRepository;
import com.studytor.app.repositories.models.ReplacementsJson;

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
                return input;
            }
        });


//        List<SingleReplacementJson> items = new ArrayList<>();
//
//        SingleReplacementJson singleRepl_1 = new SingleReplacementJson();
//        singleRepl_1.setId("1");
//        singleRepl_1.setDefaultField("Default 1");
//        singleRepl_1.setNumber("1");
//        singleRepl_1.setClassField("1a");
//        singleRepl_1.setReplacement("Repl 1");
//        items.add(singleRepl_1);
//        SingleReplacementJson singleRepl_2 = new SingleReplacementJson();
//        singleRepl_2.setId("2");
//        singleRepl_2.setDefaultField("Default 2");
//        singleRepl_2.setNumber("2");
//        singleRepl_2.setClassField("2a");
//        singleRepl_2.setReplacement("Repl 2");
//        items.add(singleRepl_2);
//        SingleReplacementJson singleRepl_3 = new SingleReplacementJson();
//        singleRepl_3.setId("3");
//        singleRepl_3.setDefaultField("Default 3");
//        singleRepl_3.setNumber("3");
//        singleRepl_3.setClassField("3a");
//        singleRepl_3.setReplacement("Repl 3");
//        items.add(singleRepl_3);
//
//        replacementsList.setValue(items);

//        new Observer<List<SingleReplacement>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleReplacement> replacementsList) {
//                setReplacementsList(replacementsList);
//            }
//        };

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

    private void refreshDate(){
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

    }

}
