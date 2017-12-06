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
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.studytor.app.repositories.InstitutionRepository;
import com.studytor.app.repositories.NewsRepository;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;
import com.studytor.app.views.PaginationView;

import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class FragmentInstitutionProfileNewsViewModel extends AndroidViewModel{

    private NewsRepository newsRepository;

    private MutableLiveData<News> news = null;
    public LiveData<News> liveData;

    private FragmentInstitutionProfileNewsViewModel.Observable observable = new FragmentInstitutionProfileNewsViewModel.Observable();
    private Handlers handlers = new Handlers();

    private int institutionId;
    private int firstPageNum = 1;
    private int currentPageNum = 1;
    private int lastPageNum = 1;

    public FragmentInstitutionProfileNewsViewModel.Observable getObservable() {
        return observable;
    }

    public FragmentInstitutionProfileNewsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(int institutionId) {
        // If setup was already done, do not do it again
        if(this.observable.news.get() != null)
            return;

        this.institutionId = institutionId;
        this.news = new MutableLiveData<>();
        newsRepository = NewsRepository.getInstance(this.getApplication());

        this.news = newsRepository.getNewsData();

        liveData = Transformations.switchMap(this.news, new Function<News, LiveData<News>>() {
            @Override
            public LiveData<News> apply(News input) {
                if(news != null)setLastPageNum(input.getLastPage());
                setNews(input);
                observable.scrollViewScroll.notifyChange();
                return liveData;
            }
        });

       /* news = Transformations.switchMap(newsRepository.getNewsData(), new Function<News, MutableLiveData<News>>() {
            @Override
            public MutableLiveData<News> apply(News input) {
                if(input.getNewsList().isEmpty()){
                    return new MutableLiveData<>();
                }
                return newsRepository
            }
        });*/


        /*newsRepository.getNewsData().observeForever(new Observer<News>() {
            @Override
            public void onChanged(@Nullable News news) {
                setNews(news);
                if (news != null) lastPageNum = news.getLastPage();
            }
        });*/

        newsRepository.getNewsWithCacheCheck(institutionId, currentPageNum);

    }

    public MutableLiveData<News> getNews() {
        return news;
    }

    public void setNews(News news) {
        //this.news.setValue(news);
        observable.news.set(news);
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<News> news = new ObservableField<>();
        public final ObservableField<Integer> scrollViewScroll = new ObservableField<>();

    }

    public class Handlers{

        public void onPageChangedListener(){
            int action = PaginationView.ACTION_NEXT;
            switch(action){
                case PaginationView.ACTION_FIRST:
                    goToFirstPage();
                    break;
                case PaginationView.ACTION_PREVIOUS:
                    goToPreviousPage();
                    break;
                case PaginationView.ACTION_NEXT:
                    goToNextPage();
                    break;
                case PaginationView.ACTION_LAST:
                    goToLastPage();
                    break;
            }
        }

    }

    public Handlers getHandlers() {
        return handlers;
    }

    public void setHandlers(Handlers handlers) {
        this.handlers = handlers;
    }

    public int getFirstPageNum() {
        return firstPageNum;
    }

    public void setFirstPageNum(int firstPageNum) {
        this.firstPageNum = firstPageNum;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getLastPageNum() {
        return lastPageNum;
    }

    public void setLastPageNum(int lastPageNum) {
        this.lastPageNum = lastPageNum;
    }

    public void goToFirstPage(){
        newsRepository.getNewsWithCacheCheck(this.institutionId, this.firstPageNum);
        this.currentPageNum = this.firstPageNum;
        System.out.println("PAGE NUMBER IS " + this.currentPageNum);
    }

    public void goToLastPage(){
        newsRepository.getNewsWithCacheCheck(this.institutionId, this.lastPageNum);
        this.currentPageNum = this.lastPageNum;
        System.out.println("PAGE NUMBER IS " + this.currentPageNum);
    }

    public void goToNextPage(){
        if(isNextPageAvailable()) newsRepository.getNewsWithCacheCheck(this.institutionId, ++this.currentPageNum);
        System.out.println("PAGE NUMBER IS " + this.currentPageNum);
    }

    public void goToPreviousPage(){
        if(isPreviousPageAvailable()) newsRepository.getNewsWithCacheCheck(this.institutionId, --this.currentPageNum);
        System.out.println("PAGE NUMBER IS " + this.currentPageNum);
    }

    public boolean isNextPageAvailable(){
        if(this.currentPageNum + 1 > this.lastPageNum) return false;
        return true;
    }

    public boolean isPreviousPageAvailable(){
        if(this.currentPageNum - 1 < this.firstPageNum) return false;
        return true;
    }
}
