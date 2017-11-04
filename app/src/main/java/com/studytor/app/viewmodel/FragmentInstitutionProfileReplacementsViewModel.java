package com.studytor.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.models.SingleNews;

import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class FragmentInstitutionProfileReplacementsViewModel extends ViewModel{

//    private MutableLiveData<List<SingleReplacement>> replacementsList = null;
//
//    Context context;
//
//    private FragmentInstitutionProfileReplacementsViewModel.Observable observable = new FragmentInstitutionProfileReplacementsViewModel.Observable();
//
//    public FragmentInstitutionProfileReplacementsViewModel.Observable getObservable() {
//        return observable;
//    }
//
//    public void setup(Context context, int institutionId) {
//        // If setup was already done, do not do it again
//        if(this.getReplacementsList() != null && this.getReplacementsList().getValue() != null)
//            return;
//
//        this.replacementsList = new MutableLiveData<>();
//        this.context = context;
//
//        //TODO Obtain repl list from repo i.e. only from web
//
//        new Observer<List<SingleReplacement>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleReplacement> replacementsList) {
//                setReplacementsList(replacementsList);
//            }
//        };
//
//    }
//
//    public MutableLiveData<List<SingleReplacement>> getReplacementsList() {
//        return replacementsList;
//    }
//
//    public void setReplacementsList(List<SingleReplacement> list) {
//        this.replacementsList.setValue(list);
//        observable.replacementsList.set(list);
//    }
//
//    // Class handled by Data Binding library
//    public class Observable extends BaseObservable {
//
//        public final ObservableField<List<SingleReplacement>> replacementsList = new ObservableField<>();
//
//    }

}
