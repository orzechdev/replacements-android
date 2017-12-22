package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.studytor.app.repositories.cache.InstitutionCache;
import com.studytor.app.repositories.cache.NewsCache;
import com.studytor.app.repositories.database.DatabaseSingleton;
import com.studytor.app.repositories.database.InstitutionDao;
import com.studytor.app.repositories.database.NewsDao;
import com.studytor.app.repositories.models.Institutions;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by przemek19980102 on 31.10.2017.
 */

public class NewsRepository {

    private static NewsRepository repositoryInstance;

    private NewsCache newsCache;
    private WebService webService;
    private NewsDao newsDao;

    private MutableLiveData<News> newsData;

    private NewsRepository(Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.newsDao = DatabaseSingleton.getInstance(context).getMainDatabase().newsDao();
        this.newsCache = NewsCache.getInstance();
        this.newsData = new MutableLiveData<>();
    }

    public static NewsRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new NewsRepository(context);
        }

        return repositoryInstance;
    }

    public MutableLiveData<News> getNewsData() {
        return newsData;
    }

    public void getNewsWithCacheCheck(final int institutionId, final int pageNum){

        Log.i("Studytor","NEWS REPO CHECK CACHE");
        newsCache.getData().observeForever(new Observer<List<News>>() {
            @Override
            public void onChanged(@Nullable List<News> news) {
                Log.i("Studytor","NEWS REPO CACHE CHANGED");
                News temp = null;
                if(news == null){
                    getNews(institutionId, pageNum);
                }
                for(News n : news){
                    if(n.getInstitutionId() == institutionId && n.getCurrentPage() == pageNum){
                        temp = n;
                    }
                }
                if(temp != null){
                    newsData.postValue(temp);
                }else{
                    getNews(institutionId, pageNum);
                }

            }
        });

    }

    public void getNews(final int institutionId, final int pageNum) {

        Log.i("Studytor","REPO NEWS CALLING WEB");

        this.webService.getAllNews(institutionId, pageNum).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, final Response<News> response) {
                Log.i("Studytor","REPO NEWS GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getNewsList() != null){
                    Log.i("Studytor","REPO NEWS GET DATA FROM WEB SUCCESSFUL");
                    News news = response.body();
                    news.setCurrentPage(pageNum);
                    news.setInstitutionId(institutionId);

                    newsCache.insertOrAddNews(institutionId, pageNum, news);

                    newsData.postValue(news);
                }else{
                    Log.i("Studytor","REPO NEWS GET DATA FROM WEB IS NULL 1" + response.toString());
                    newsData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.i("Studytor","REPO NEWS GET DATA FROM WEB IS NULL 2");

                newsData.postValue(null);

                t.printStackTrace();
            }
        });

    }

}
