package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
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

    private MutableLiveData<List<SingleInstitution>> institutionListVM = null;
    private LiveData<List<SingleInstitution>> institutionList = null;

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

        fragmentInstitutionRepository = InstitutionRepository.getInstance(this.getApplication());

        this.institutionListVM = fragmentInstitutionRepository.getInstitutionList();
        institutionList = Transformations.switchMap(institutionListVM, new Function<List<SingleInstitution>, LiveData<List<SingleInstitution>>>() {
            @Override
            public LiveData<List<SingleInstitution>> apply(List<SingleInstitution> input) {
                Log.i("Studytor","REPO CHANGED newest apply");
                if (input.isEmpty()) {
                    return new MutableLiveData<>();
                }
                return fragmentInstitutionRepository.getInstitutionListFromCache();
            }
        });

        //institutionListVM = fragmentInstitutionRepository.getInstitutionList();

//        fragmentInstitutionRepository.getInstitutionList().observeForever(new Observer<List<SingleInstitution>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleInstitution> institutions) {
//                Log.i("Studytor","REPO CHANGED");
//                //setInstitutionList(institutions);
//                observable.institutionList.set(institutions);
//                observable.isRefreshing.set(false);
//                observable.notifyChange();
//            }
//        });
    }

    public void requestRepositoryUpdate(){
        Log.i("Studytor","UPDATING");
        fragmentInstitutionRepository.refreshData();
        observable.isRefreshing.set(true);
        observable.notifyChange();
    }

    public LiveData<List<SingleInstitution>> getInstitutionList() {
//        observable.institutionList.set(institutionList.getValue());
//        observable.isRefreshing.set(false);
//        observable.notifyChange();

        return institutionList;
    }

    public void setInstitutionList(List<SingleInstitution> list) {
        //this.institutionList.setValue(list);
        observable.institutionList.set(list);
        Log.i("FragmentInstitutionVM","set institution list");
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<List<SingleInstitution>> institutionList = new ObservableField<>();
        public final ObservableField<Boolean> isRefreshing = new ObservableField<>();

    }

}
