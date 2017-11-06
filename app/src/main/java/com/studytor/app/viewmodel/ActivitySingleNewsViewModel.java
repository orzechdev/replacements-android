package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.SingleNews;

/**
 * Created by przemek19980102 on 06.11.2017.
 */

public class ActivitySingleNewsViewModel extends AndroidViewModel{

    private NewsRepository newsRepository;
    private Observable observable;

    public ActivitySingleNewsViewModel(@NonNull Application application) {
        super(application);
        observable = new Observable();
    }

    public void setup(final int instId, final int pgNum, final int pId) {
        // If setup was already done, do not do it again
        if(this.getObservable() != null && this.getObservable().singleNews.get() != null)
            return;

        newsRepository = NewsRepository.getInstance(this.getApplication());
        newsRepository.getNewsData().observeForever(new Observer<News>() {
            @Override
            public void onChanged(@Nullable News news) {
                SingleNews temp = null;
                //Look for corresponding news on the given page in the given institution
                for(SingleNews n : news.getNewsList()){
                    if(n.getId() == pId){
                        temp = n;
                    }
                }
                setSingleNews(temp);
            }
        });

        newsRepository.getNewsWithCacheCheck(instId, pgNum);

    }

    public void setSingleNews(SingleNews news){
        this.observable.singleNews.set(news);
    }

    public Observable getObservable() {
        return observable;
    }

    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<SingleNews> singleNews = new ObservableField<>();

    }

    public class Handlers{

    }

}
