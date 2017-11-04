package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.studytor.app.repositories.models.ReplacementRoomJson;
import com.studytor.app.repositories.FragmentReplacementsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentReplacementsViewModel extends AndroidViewModel {

    private static final String CLASS_NAME = FragmentReplacementsViewModel.class.getName();

    private FragmentReplacementsRepository fragmentReplacementsRepository;

    //private MutableLiveData<ReplacementsJson> jsonReplacements;

    private LiveData<List<ReplacementRoomJson>> allReplacements;

    private boolean replacementsRefreshAllowed = false;
    private boolean replacementsRefreshInProgress = false;

    public List<String> institutionIds = new ArrayList<String>();
    public String selectedDate;
    public String replVer;

    private MutableLiveData<String> text = new MutableLiveData<>();

    private String mainText;

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public FragmentReplacementsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup() {
        // If setup was already done, do not do it again
        if(this.getText().getValue() != null)
            return;
        prepareStrings(this.getApplication());
        this.setText(mainText);

        Log.i(CLASS_NAME, "setup 100");

        // Here works repository and Retrofit
        fragmentReplacementsRepository = FragmentReplacementsRepository.getInstance(this.getApplication());

        institutionIds.add("3g5et");
        selectedDate = "2015-09-25";
        replVer = "0";

        //jsonReplacements = fragmentReplacementsRepository.getReplacements("3g5et","2015-09-25","0");
        allReplacements = fragmentReplacementsRepository.getReplacements(institutionIds.get(0), selectedDate, replVer);
        //Log.i(CLASS_NAME,"setup jsonReplacements: " + jsonReplacements.getValue().getReplacements().get(1).getReplacement());

        replacementsRefreshAllowed = true;

        setupUpdatingReplacementsFromInternet();
    }

    private void setupUpdatingReplacementsFromInternet() {
        Log.i(CLASS_NAME, "setupUpdatingReplacementsFromInternet 100");
        allReplacements.observeForever(new Observer<List<ReplacementRoomJson>>() {
            @Override
            public void onChanged(@Nullable List<ReplacementRoomJson> replacements) {
                Log.i(CLASS_NAME, "setupUpdatingReplacementsFromInternet onChanged 100");
                if(replacementsRefreshAllowed && !fragmentReplacementsRepository.replacementsRefreshInProgress) {
                    Log.i(CLASS_NAME, "setupUpdatingReplacementsFromInternet onChanged 200");
                    fragmentReplacementsRepository.refreshReplacementsFromInternet(allReplacements, institutionIds.get(0), selectedDate, replVer);
                }
                Log.i(CLASS_NAME, "setupUpdatingReplacementsFromInternet onChanged 300");
            }
        });
    }

    public LiveData<List<ReplacementRoomJson>> getAllReplacements() {
        //return jsonReplacements;
        return allReplacements;
    }

    public void refreshJsonReplacements() {

    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setValue(text);
        observable.text.set(text);
        Log.i("FragmentInstitutionVM","setText: " + text);
    }

    private void prepareStrings(Context context) {
        mainText = "Here You will see replacements.";
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<String> text = new ObservableField<>();

    }
}
