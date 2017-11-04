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
import android.util.Log;

import com.studytor.app.repositories.InstitutionRepository;
import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;

import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class FragmentInstitutionProfileNewsViewModel extends AndroidViewModel{

    private NewsRepository newsRepository;

    private MutableLiveData<List<SingleNews>> newsList = null;

    private FragmentInstitutionProfileNewsViewModel.Observable observable = new FragmentInstitutionProfileNewsViewModel.Observable();

    public FragmentInstitutionProfileNewsViewModel.Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionProfileNewsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(int institutionId) {
        // If setup was already done, do not do it again
        if(this.getNewsList() != null && this.getNewsList().getValue() != null)
            return;

        this.newsList = new MutableLiveData<>();
        newsRepository = NewsRepository.getInstance(this.getApplication());

        newsRepository.getNewsListFromWebOnly(institutionId).observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                setNewsList(newsList);
            }
        });

    }

    public MutableLiveData<List<SingleNews>> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<SingleNews> list) {
        this.newsList.setValue(list);
        observable.newsList.set(list);
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<List<SingleNews>> newsList = new ObservableField<>();

    }

}
