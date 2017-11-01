package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

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

    private static final String CLASS_NAME = InstitutionRepository.class.getName();

    private static NewsRepository repositoryInstance;

    private NewsCache newsCache;
    private WebService webService;
    private NewsDao newsDao;

    private NewsRepository(Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.newsDao = DatabaseSingleton.getInstance(context).getMainDatabase().newsDao();
        this.newsCache = NewsCache.getInstance();
    }

    public static NewsRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new NewsRepository(context);
        }

        return repositoryInstance;
    }

    private void observeDatabase(int institutionId){

        System.out.println("REPO NEWS CHECK DATABASE");


        this.newsDao.loadByInstitution(institutionId).observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                newsCache.putData(newsList);
            }
        });


    }

    //Get institution list from any available source (CACHE, DB, WEB)
    public MutableLiveData<List<SingleNews>> getNewsList(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        System.out.println("REPO NEWS AUTO GET");

        observeDatabase(institutionId);

        refreshData(institutionId);

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO NEWS RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleNews>> getNewsListFromCache(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO NEWS RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleNews>> getNewsListOffline(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()
        observeDatabase(institutionId);

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO NEWS RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });

        return  returnData;

    }

    //Load InstitutionList from web using retrofit
    public void refreshData(final int institutionId){

        System.out.println("REPO NEWS GET DATA FROM WEB");


        this.webService.getAllNews(institutionId).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, final Response<News> response) {
                System.out.println("REPO NEWS GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getNewsList() != null){
                    System.out.println("REPO NEWS GET DATA FROM WEB SUCCESSFUL");

                    //Save data to local database
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SingleNews> newsList = response.body().getNewsList();
                            if(newsList != null){

                                //Update database which will later update cache
                                for(int i = 0; i < newsList.size(); i++){
                                    newsList.get(i).setInstitutionId(institutionId);
                                }
                                newsDao.insertAll(newsList);

                            }
                        }
                    });
                    thread.start();


                }else{
                    System.out.println("REPO NEWS GET DATA FROM WEB IS NULL 1" + response.toString());

                    //Reobserve Database if already observed to force data check
                    observeDatabase(institutionId);

                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                System.out.println("REPO NEWS GET DATA FROM WEB IS NULL 2");

                //Reobserve Database if already observed to force data check
                observeDatabase(institutionId);

                t.printStackTrace();
            }
        });



    }

}
