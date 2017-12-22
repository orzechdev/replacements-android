package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.studytor.app.repositories.models.Institutions;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.cache.InstitutionCache;
import com.studytor.app.repositories.database.DatabaseSingleton;
import com.studytor.app.repositories.database.InstitutionDao;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dawid on 25.07.2017.
 *
 * This repository might be used by multiple activites and fragments sharing SingleInstitution model
 * Using the same repository is less flexible but much cleaner to update in the future.
 */

public class InstitutionRepository {

    private static InstitutionRepository repositoryInstance;

    private InstitutionCache institutionCache;
    private WebService webService;
    private InstitutionDao institutionDao;

    private InstitutionRepository(Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.institutionDao = DatabaseSingleton.getInstance(context).getMainDatabase().institutionDao();
        this.institutionCache = InstitutionCache.getInstance();
    }

    public static InstitutionRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new InstitutionRepository(context);
        }

        return repositoryInstance;
    }

    private void observeDatabase(){

        Log.i("Studytor","REPO CHECK DATABASE");

        this.institutionDao.loadAll().getValue();

        this.institutionDao.loadAll().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                institutionCache.putData(institutions);
            }
        });


    }

    //Get institution list from any available source (CACHE, DB, WEB)
    public MutableLiveData<List<SingleInstitution>> getInstitutionList() {

        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        Log.i("Studytor","REPO AUTO GET");

        refreshData();

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()
        observeDatabase();

        //Observe cache only, because it gets updated with database or web updates
        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
                returnData.postValue(institutions);
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromCache() {

        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        //Observe cache only, because it gets updated with database or web updates
        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
                returnData.postValue(institutions);
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionListOffline() {

        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        //Observe database updates to be able to update CACHE data
        //Needs to be separated, because it is also used in refreshData()
        observeDatabase();

        //Observe cache only, because it gets updated with database or web updates
        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
                returnData.postValue(institutions);
            }
        });

        return  returnData;

    }

    //Load InstitutionList from web using retrofit
    public void refreshData(){

        Log.i("Studytor","REPO GET DATA FROM WEB");

        this.webService.getAllInstitutions().enqueue(new Callback<Institutions>() {
            @Override
            public void onResponse(Call<Institutions> call, final Response<Institutions> response) {
                Log.i("Studytor","REPO GET DATA FROM WEB ENQUEUED");

                if(response.isSuccessful() && response.body().getInstitutions() != null){
                    Log.i("Studytor","REPO GET DATA FROM WEB SUCCESSFUL");

                    //Save data to local database
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SingleInstitution> institutionList = response.body().getInstitutions();
                            if(institutionList != null){

                                //Update database which will later update cache
                                institutionDao.insertAll(institutionList);

                            }
                        }
                    });
                    thread.start();


                }else{
                    Log.i("Studytor","REPO GET DATA FROM WEB IS NULL 1");

                    //Reobserve Database if already observed to force data check
                    observeDatabase();

                }

            }

            @Override
            public void onFailure(Call<Institutions> call, Throwable t) {
                Log.i("Studytor","REPO GET DATA FROM WEB IS NULL 2");

                //Reobserve Database if already observed to force data check
                observeDatabase();

                t.printStackTrace();
            }
        });

    }
}
