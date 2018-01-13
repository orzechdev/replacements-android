package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by przemek19980102 on 31.10.2017.
 */

public class NewsRepository {

    private static NewsRepository repositoryInstance;

    private Map<Integer, News> newsListCache = new TreeMap<>();
    private MutableLiveData<News> news = null;
    private MutableLiveData<Boolean> isRefreshing = null;
    private int currentInstitutId = 0;

    private WebService webService;

    //private MutableLiveData<News> news;

    private NewsRepository() {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();

        news = new MutableLiveData<>();

        // DAWID --- checking whether refreshing is in progress
        isRefreshing = new MutableLiveData<>();
        isRefreshing.setValue(false);
    }

    public static NewsRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new NewsRepository();
        }

        return repositoryInstance;
    }

    // DAWID --- Very huge advantage of having this variable in repo - even if we switch between screens and return to list of institutions,
    // --------- if the refreshing is not yet ended we will see, that it is refreshing, and any change of the screen does not cause that this refreshing will not be visible! :)
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    // DAWID --- We are retrieving one page of news list
    public LiveData<News> getNews(final int institutionId, final int pageNum) {
        // If new institution selected
        if(currentInstitutId != institutionId){
            currentInstitutId = institutionId;
            newsListCache.clear();
            news.setValue(null);
            refreshData(institutionId, pageNum);
            return news;
        }

        Log.i("NewsRepository", "getNewsList 1");
        // DAWID --- 1st check if there is cached institution list - if yes return it
        News newsCache = newsListCache.get(pageNum);
        if(newsCache != null){
            news.setValue(newsCache);
            return news;
        }

        Log.i("NewsRepository", "getNewsList 2");

        // DAWID --- 2nd if cached institution list doesn't exist - take it from database ( single source of truth - there is not other way from which live data is taken )
        // --------- HERE WE HAVE NOT DATABASE, SO WE OMIT THIS STEP
        //institutionListCache = institutionDao.loadAll();
        //Log.i("InstitutionRepository", "getInstitutionList 3");

        // DAWID --- 3rd if institution list wasn't in database - call to take it from the web
        // --------- HERE WE HAVE NOT DATABASE, SO WE OMIT IF CLAUSE
        // if(institutionListCache == null)
        refreshData(institutionId, pageNum);
        Log.i("NewsRepository", "getNewsList 4");

        // DAWID --- 3rd do not wait until it will be taken from the web, but return live data object
        return news;
    }

    public void forceRefreshData(final int institutionId, final int pageNum) {
        newsListCache.clear();
        refreshData(institutionId, pageNum);
    }

    private void refreshData(final int institutionId, final int pageNum) {

        Log.i("NewsRepository", "refreshData");

        isRefreshing.setValue(true);

        this.webService.getAllNews(institutionId, pageNum).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, final Response<News> response) {
                Log.i("NewsRepository", "refreshData onResponse");

                News newsResponse = response.body();

                if(response.isSuccessful() && newsResponse != null && newsResponse.getNewsList() != null){
                    Log.i("NewsRepository", "refreshData onResponse SUCCESSFUL 1");

                    newsResponse.setCurrentPage(pageNum);
                    newsResponse.setInstitutionId(institutionId);

                    // DAWID --- Set news for cache
                    newsListCache.put(pageNum, newsResponse);

                    // DAWID --- Set news for liveData available for viewModel
                    news.postValue(newsResponse);

                    Log.i("NewsRepository", "refreshData onResponse SUCCESSFUL 2");
                }else{
                    Log.i("NewsRepository", "refreshData onResponse IS NULL");

                    newsListCache.remove(pageNum);

                    news.postValue(null);
                }

                isRefreshing.setValue(false);

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.i("NewsRepository", "refreshData onFailure");

                newsListCache.remove(pageNum);

                news.postValue(null);

                t.printStackTrace();

                isRefreshing.setValue(false);
            }
        });

    }

}
