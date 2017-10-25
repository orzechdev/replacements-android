package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.studytor.app.models.SingleInstitution;
import com.studytor.app.repositories.InstitutionRepository;
import com.studytor.app.repositories.database.DatabaseSingleton;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;

import java.util.List;

/**
 * Created by Dawid on 25.07.2017.
 */

public class InstitutionCache {
    private MutableLiveData<List<SingleInstitution>> cachedInstitutionList = new MutableLiveData<>();
    private static InstitutionCache instance;

    private InstitutionCache() {
        this.cachedInstitutionList = new MutableLiveData<>();
    }

    public static InstitutionCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new InstitutionCache();
        }

        return instance;
    }

    public void putData(List<SingleInstitution> list){
        cachedInstitutionList.postValue(list);
    }

    public MutableLiveData<List<SingleInstitution>> getData(){
        return  cachedInstitutionList;
    }

}