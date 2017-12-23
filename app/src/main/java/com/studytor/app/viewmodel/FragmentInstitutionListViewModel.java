package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.databinding.ObservableField;
import android.util.Log;

import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.InstitutionRepository;

import java.util.List;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentInstitutionListViewModel extends AndroidViewModel {

    private InstitutionRepository fragmentInstitutionRepository;

    private LiveData<List<SingleInstitution>> institutionListRepo = null;
    private LiveData<List<SingleInstitution>> institutionList = null;

    private LiveData<Boolean> isRefreshingRepo = null;
    private LiveData<Boolean> isRefreshing = null;

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionListViewModel(@NonNull Application application){
        super(application);
        Log.i("FragmentInstitutListVM", "FragmentInstitutionListViewModel");
    }

    public void setup() {
        Log.i("FragmentInstitutListVM", "setup Start");
        // If setup was already done, do not do it again
        if(this.getInstitutionList() != null && this.getInstitutionList().getValue() != null)
            return;

        fragmentInstitutionRepository = InstitutionRepository.getInstance(this.getApplication());

        institutionListRepo = fragmentInstitutionRepository.getInstitutionList();
        isRefreshingRepo = fragmentInstitutionRepository.isRefreshing();

//        institutionList = Transformations.switchMap(institutionListRepo, new Function<List<SingleInstitution>, LiveData<List<SingleInstitution>>>() {
//            @Override
//            public LiveData<List<SingleInstitution>> apply(List<SingleInstitution> input) {
//                Log.i("Studytor","REPO CHANGED newest apply");
//                if (input.isEmpty()) {
//                    return new MutableLiveData<>();
//                }
//                return fragmentInstitutionRepository.getInstitutionListFromCache();
//            }
//        });

        institutionList = Transformations.map(institutionListRepo, new Function<List<SingleInstitution>, List<SingleInstitution>>() {
            @Override
            public List<SingleInstitution> apply(List<SingleInstitution> input) {
                return input;
            }
        });

        isRefreshing = Transformations.map(isRefreshingRepo, new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean input) {
                return input;
            }
        });

        //institutionListRepo = fragmentInstitutionRepository.getInstitutionList();

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
        Log.i("FragmentInstitutListVM", "setup End");
    }

    public void requestRepositoryUpdate(){
        Log.i("FragmentInstitutListVM", "requestRepositoryUpdate 1");
        fragmentInstitutionRepository.refreshData();
        // DAWID: --- info about refreshing is now taken from livedata
        //observable.isRefreshing.set(true);
        //observable.notifyChange();
        Log.i("FragmentInstitutListVM", "requestRepositoryUpdate 2");
    }

    public LiveData<List<SingleInstitution>> getInstitutionList() {
        return institutionList;
    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<List<SingleInstitution>> institutionList = new ObservableField<>();
        public final ObservableField<Boolean> isRefreshing = new ObservableField<>();

    }

}
