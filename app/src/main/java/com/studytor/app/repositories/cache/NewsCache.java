package com.studytor.app.repositories.cache;

import android.arch.lifecycle.MutableLiveData;

import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 31.10.2017.
 */

public class NewsCache {

    private MutableLiveData<List<News>> cachedNews = new MutableLiveData<>();
    private static NewsCache instance;

    private NewsCache() {
        this.cachedNews = new MutableLiveData<>();
        List<News> temp = new ArrayList<>();
        this.cachedNews.postValue(temp);
    }

    public static NewsCache getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new NewsCache();
        }

        return instance;
    }

    public void putData(List<News> list){
        cachedNews.postValue(list);
    }

    public boolean insertOrAddNews(int institutionId, int pageNum, News news){
        if(cachedNews != null){
            if(cachedNews.getValue() != null){
                for(int i = 0; i < cachedNews.getValue().size(); i++){
                    if(cachedNews.getValue().get(i).getCurrentPage() == pageNum && cachedNews.getValue().get(i).getInstitutionId() == institutionId){
                        cachedNews.getValue().set(i, news);
                        return false;
                    }
                }
                System.out.println("INSERTED SOMETHING");
                cachedNews.getValue().add(news);
            }
        }
        return true;
    }

    public MutableLiveData<List<News>> getData(){
        return  cachedNews;
    }


}
