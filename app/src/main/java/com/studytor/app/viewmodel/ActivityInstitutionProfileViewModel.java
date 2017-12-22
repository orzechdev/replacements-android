package com.studytor.app.viewmodel;

import android.app.Application;
import android.support.v4.app.FragmentManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.studytor.app.activities.ActivityInstitutionProfile;
import com.studytor.app.fragments.FragmentInstitutionProfileNews;
import com.studytor.app.fragments.FragmentInstitutionProfileReplacements;
import com.studytor.app.fragments.FragmentInstitutionProfileSchedule;
import com.studytor.app.repositories.InstitutionRepository;
import com.studytor.app.repositories.models.SingleInstitution;

import java.util.List;

/**
 * Created by przemek19980102 on 30.10.2017.
 */

public class ActivityInstitutionProfileViewModel extends AndroidViewModel{

    private Observable observable = new Observable();
    private InstitutionRepository institutionRepository;
    private int institutionId;

    public ActivityInstitutionProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(final int institutionId, final FragmentManager fragmentManager) {
        // If setup was already done, do not do it again
        //if(this.getInstitutionList() != null && this.getInstitutionList().getValue() != null)
            //return;

        //this.institutionList = new MutableLiveData<>();
        if(institutionId != -1)this.institutionId = institutionId;

        Log.i("Studytor","GOT INSTITUTION WITH ID " + this.institutionId);
        institutionRepository = InstitutionRepository.getInstance(this.getApplication());

        institutionRepository.getInstitutionListFromCache().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                for(int i = 0; i < institutions.size(); i++){
                    if(institutions.get(i).getId() == institutionId){
                        observable.singleInstitution.set(institutions.get(i));
                        break;
                    }
                }
            }
        });

    }

    public Observable getObservable() {
        return observable;
    }

    public class Observable extends BaseObservable{

        public ObservableField<SingleInstitution> singleInstitution = new ObservableField<>();

    }
}
