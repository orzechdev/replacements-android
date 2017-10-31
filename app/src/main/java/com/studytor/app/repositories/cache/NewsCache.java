package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;

import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;

import java.util.List;

/**
 * Created by przemek19980102 on 31.10.2017.
 */

public class NewsCache {

    private MutableLiveData<List<SingleNews>> cachedNewsList = new MutableLiveData<>();
    private static NewsCache instance;

    private NewsCache() {
        this.cachedNewsList = new MutableLiveData<>();
    }

    public static NewsCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new NewsCache();
        }

        return instance;
    }

    public void putData(List<SingleNews> list){
        cachedNewsList.postValue(list);
    }

    public MutableLiveData<List<SingleNews>> getData(){
        return  cachedNewsList;
    }


}
