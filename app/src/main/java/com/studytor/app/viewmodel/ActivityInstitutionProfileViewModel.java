package com.studytor.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.InstitutionRepository;
import com.studytor.app.repositories.models.SingleInstitution;

import java.util.List;

/**
 * Created by przemek19980102 on 30.10.2017.
 */

public class ActivityInstitutionProfileViewModel extends ViewModel{

    private Observable observable = new Observable();
    private InstitutionRepository institutionRepository;
    private int institutionId;
    Context context;

    public void setup(Context context, final int institutionId) {
        // If setup was already done, do not do it again
        //if(this.getInstitutionList() != null && this.getInstitutionList().getValue() != null)
            //return;

        //this.institutionList = new MutableLiveData<>();
        this.context = context;
        if(institutionId != -1)this.institutionId = institutionId;
        System.out.println("GOT INSTITUTION WITH ID " + this.institutionId);
        institutionRepository = InstitutionRepository.getInstance(context);

        institutionRepository.getInstitutionListOffline().observeForever(new Observer<List<SingleInstitution>>() {
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

        //TODO Pobieranie z repository
        SingleInstitution temp = new SingleInstitution(1, "Zespół szkoł ABCD w Wawce", "ABCWAW");
        temp.setLogoUrl("studytor.com/json/school/zschocianow/logo.png");
        temp.setHeaderUrl("studytor.com/json/school/zschocianow/header.png");
        observable.singleInstitution.set(temp);

    }

    public Observable getObservable() {
        return observable;
    }

    public class Observable extends BaseObservable{

        public ObservableField<SingleInstitution> singleInstitution = new ObservableField<>();

    }

    public class Handlers{
        //UI clicks etc.
    }
}
