package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.ReplacementsRepository;
import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.SingleNews;
import com.studytor.app.repositories.models.SingleReplacementJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class FragmentInstitutionProfileReplacementsViewModel extends AndroidViewModel {

    private ReplacementsRepository replacementsRepository;

    private MutableLiveData<ReplacementsJson> replacementsList = null;

    private int institutionId;
    private String date = "11-11-2017";

    private FragmentInstitutionProfileReplacementsViewModel.Observable observable = new FragmentInstitutionProfileReplacementsViewModel.Observable();

    public FragmentInstitutionProfileReplacementsViewModel.Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionProfileReplacementsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(int institutionId) {
        // If setup was already done, do not do it again
        if(this.getReplacementsList() != null && this.getReplacementsList().getValue() != null)
            return;

        this.institutionId = institutionId;
        this.replacementsList = new MutableLiveData<>();

        replacementsRepository = ReplacementsRepository.getInstance(this.getApplication());

        replacementsRepository.getReplacementsData().observeForever(new Observer<ReplacementsJson>() {
            @Override
            public void onChanged(@Nullable ReplacementsJson replacementsJson) {
                setReplacementsList(replacementsJson);
            }
        });

        replacementsRepository.getReplacements(institutionId, date);

        //TODO Obtain repl list from repo i.e. only from web

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

    public MutableLiveData<ReplacementsJson> getReplacementsList() {
        return replacementsList;
    }

    public void setReplacementsList(ReplacementsJson replacementsJson) {
        this.replacementsList.setValue(replacementsJson);
        observable.replacementsList.set(replacementsJson);
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<ReplacementsJson> replacementsList = new ObservableField<>();

    }

}
