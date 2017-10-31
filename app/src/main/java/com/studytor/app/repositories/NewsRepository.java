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

    private void observeDatabase(){

        System.out.println("REPO CHECK DATABASE");


        this.newsDao.loadAll().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                newsCache.putData(newsList);
            }
        });


    }

    //Get institution list from any available source (CACHE, DB, WEB)
    public MutableLiveData<List<SingleNews>> getNewsList(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        List<SingleNews> temp = new ArrayList<>();
        temp.add(new SingleNews(0,"News 0", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));
        temp.add(new SingleNews(1,"News 1", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));
        temp.add(new SingleNews(2,"News 2", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));
        temp.add(new SingleNews(3,"News 3", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));
        temp.add(new SingleNews(4,"News 4", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));
        temp.add(new SingleNews(5,"News 5", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w...", "Lorem Ipsum jest tekstem stosowanym jako przykładowy wypełniacz w przemyśle poligraficznym. Został po raz pierwszy użyty w XV w. przez nieznanego drukarza do wypełnienia tekstem próbnej książki. Pięć wieków później zaczął być używany przemyśle elektronicznym, pozostając praktycznie niezmienionym", "http://studytor.com/json/school/gimchocianow/logo.png", "http://studytor.com/json/school/gimchocianow/header.jpg"));

        System.out.println("REPO AUTO GET");

        /*refreshData();

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()
        observeDatabase();

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });*/

        return  returnData;

    }

    public MutableLiveData<List<SingleNews>> getNewsListFromCache(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleNews>> getNewsListOffline(int institutionId) {

        final MutableLiveData<List<SingleNews>> returnData = new MutableLiveData<>();

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()
        observeDatabase();

        //Observe cache only, because it gets updated with database or web updates
        newsCache.getData().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {
                System.out.println("REPO RETURN OBSERVED CACHE");
                returnData.postValue(newsList);
            }
        });

        return  returnData;

    }

    //Load InstitutionList from web using retrofit
    public void refreshData(int institutionId){

        System.out.println("REPO GET DATA FROM WEB");
        /*

        this.webService.getAllInstitutions().enqueue(new Callback<Institutions>() {
            @Override
            public void onResponse(Call<Institutions> call, final Response<Institutions> response) {
                System.out.println("REPO GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getInstitutions() != null){
                    System.out.println("REPO GET DATA FROM WEB SUCCESSFUL");

                    //Save data to local database
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SingleNews> newsList = response.body().ge();
                            if(institutionList != null){

                                //Update database which will later update cache
                                newsDao.insertAll(institutionList);

                            }
                        }
                    });
                    thread.start();


                }else{
                    System.out.println("REPO GET DATA FROM WEB IS NULL 1");

                    //Reobserve Database if already observed to force data check
                    observeDatabase();

                }

            }

            @Override
            public void onFailure(Call<Institutions> call, Throwable t) {
                System.out.println("REPO GET DATA FROM WEB IS NULL 2");

                //Reobserve Database if already observed to force data check
                observeDatabase();

                t.printStackTrace();
            }
        });

        */

    }

}
