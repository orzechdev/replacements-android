package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.view.View;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.studytor.app.R;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.InstitutionRepository;

import java.util.List;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentInstitutionListViewModel extends AndroidViewModel {

    private InstitutionRepository fragmentInstitutionRepository;

    private MutableLiveData<List<SingleInstitution>> institutionList = null;

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionListViewModel(@NonNull Application application){
        super(application);
    }

    public void setup() {
        // If setup was already done, do not do it again
        if(this.getInstitutionList() != null && this.getInstitutionList().getValue() != null)
            return;

        this.institutionList = new MutableLiveData<>();
        fragmentInstitutionRepository = InstitutionRepository.getInstance(this.getApplication());

        fragmentInstitutionRepository.getInstitutionList().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                System.out.println("REPO CHANGED");
                setInstitutionList(institutions);
                observable.isRefreshing.set(false);
                observable.notifyChange();
            }
        });
    }

    public void requestRepositoryUpdate(){
        System.out.println("UPDATING");
        fragmentInstitutionRepository.refreshData();
        observable.isRefreshing.set(true);
        observable.notifyChange();
    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<SingleInstitution> list) {
        this.institutionList.setValue(list);
        observable.institutionList.set(list);
        Log.i("FragmentInstitutionVM","set institution list");
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<List<SingleInstitution>> institutionList = new ObservableField<>();
        public final ObservableField<Boolean> isRefreshing = new ObservableField<>();

    }

}
