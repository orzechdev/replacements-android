package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
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

    //private InstitutionCache institutionCache;
    private LiveData<List<SingleInstitution>> institutionListCache = null;
    private MutableLiveData<Boolean> isRefreshing = null;

    private WebService webService;
    private InstitutionDao institutionDao;

    private InstitutionRepository(Context context) {
        Log.i("InstitutionRepository", "InstitutionRepository 1");
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.institutionDao = DatabaseSingleton.getInstance(context).getMainDatabase().institutionDao();
        // DAWID --- No singleton, even no special class
        //this.institutionCache = InstitutionCache.getInstance();
        // DAWID --- checking whether refreshing is in progress
        isRefreshing = new MutableLiveData<>();
        isRefreshing.setValue(false);
        Log.i("InstitutionRepository", "InstitutionRepository 2");
    }

    public static InstitutionRepository getInstance(Context context) {
        Log.i("InstitutionRepository", "getInstance");
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new InstitutionRepository(context);
        }

        return repositoryInstance;
    }

    // DAWID --- Observating database livedata object is not needed, due to having in institutionListCache the same original livedata object (object cannot observe itself)
    // --------- So, in other words, we use single source of truth, what is livedata which lives in institutionDao - we do not create any new object
//    private void observeDatabase(){
//
//        Log.i("Studytor","REPO CHECK DATABASE");
//
//        this.institutionDao.loadAll().getValue();
//
//        this.institutionDao.loadAll().observeForever(new Observer<List<SingleInstitution>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleInstitution> institutions) {
//                institutionCache.putData(institutions);
//            }
//        });
//
//
//    }

    // DAWID --- Very huge advantage of having this variable in repo - even if we switch between screens and return to list of institutions,
    // --------- if the refreshing is not yet ended we will see, that it is refreshing, and any change of the screen does not cause that this refreshing will not be visible! :)
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    public LiveData<List<SingleInstitution>> getInstitutionList() {
        Log.i("InstitutionRepository", "getInstitutionList 1");
        // DAWID --- 1st check if there is cached institution list - if yes return it
        if(institutionListCache != null)
            return institutionListCache;
        Log.i("InstitutionRepository", "getInstitutionList 2");

        // DAWID --- 2nd if cached institution list doesn't exist - take it from database ( single source of truth - there is not other way from which live data is taken )
        institutionListCache = institutionDao.loadAll();
        Log.i("InstitutionRepository", "getInstitutionList 3");

        // DAWID --- 3rd if institution list wasn't in database - call to take it from the web
        if(institutionListCache == null)
            refreshData();
        Log.i("InstitutionRepository", "getInstitutionList 4");

        // DAWID --- 3rd do not wait until it will be taken from the web, but return live data object
        return institutionListCache;
    }

    // DAWID --- Previous getInstitutionList() is better
    //Get institution list from any available source (CACHE, DB, WEB)
//    public MutableLiveData<List<SingleInstitution>> getInstitutionList() {
//
//        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();
//
//        Log.i("Studytor","REPO AUTO GET");
//
//        refreshData();
//
//        //Observe database updates to be able to update CACHE data
//        //Needs to be separated, because it is also used in refreshData()
////        observeDatabase();
//
//        // DAWID --- Nonsense, cache should be returned straight as cache and shouldn't be observe
//        //Observe cache only, because it gets updated with database or web updates
////        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
////            @Override
////            public void onChanged(@Nullable List<SingleInstitution> institutions) {
////                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
////                returnData.postValue(institutions);
////            }
////        });
//
//        return  returnData;
//
//    }

    // DAWID --- Same as before
//    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromCache() {
//
//        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();
//
//        //Observe cache only, because it gets updated with database or web updates
//        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleInstitution> institutions) {
//                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
//                returnData.postValue(institutions);
//            }
//        });
//
//        return  returnData;
//
//    }

    // DAWID --- Not needed, getInstitutionList() is enough
//    public MutableLiveData<List<SingleInstitution>> getInstitutionListOffline() {
//
//        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();
//
//        //Observe database updates to be able to update CACHE data
//        //Needs to be separated, because it is also used in refreshData()
//        observeDatabase();
//
//        //Observe cache only, because it gets updated with database or web updates
//        institutionCache.getData().observeForever(new Observer<List<SingleInstitution>>() {
//            @Override
//            public void onChanged(@Nullable List<SingleInstitution> institutions) {
//                Log.i("Studytor","REPO RETURN OBSERVED CACHE");
//                returnData.postValue(institutions);
//            }
//        });
//
//        return  returnData;
//
//    }

    //Load InstitutionList from web using retrofit
    public void refreshData(){

        Log.i("InstitutionRepository", "refreshData");

        isRefreshing.setValue(true);

        this.webService.getAllInstitutions().enqueue(new Callback<Institutions>() {
            @Override
            public void onResponse(Call<Institutions> call, final Response<Institutions> response) {
                Log.i("InstitutionRepository", "refreshData onResponse");

                if(response.isSuccessful() && response.body().getInstitutions() != null){
                    Log.i("InstitutionRepository", "refreshData onResponse SUCCESSFUL 1");

                    //Save data to local database
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("InstitutionRepository", "refreshData onResponse SUCCESSFUL thread 1");
                            List<SingleInstitution> institutionList = response.body().getInstitutions();
                            if(institutionList != null){

                                Log.i("InstitutionRepository", "refreshData onResponse SUCCESSFUL thread 2");
                                // DAWID: --- Inserting newly downloaded list of institution into database will cause triggering livedata change,
                                // ---------- and thus live data will change which was returned from getInstitutionList() previously,
                                // ---------- because there are no other way - liveData object must be the same as from database according to following
                                // ---------- "institutionListCache = institutionDao.loadAll();" - i.e. this is the single source of truth. :D
                                //Update database which will later update cache
                                institutionDao.insertAll(institutionList);

                            }
                        }
                    });
                    thread.start();
                    Log.i("InstitutionRepository", "refreshData onResponse SUCCESSFUL 2");


                }else{
                    Log.i("InstitutionRepository", "refreshData onResponse IS NULL");

                    //Reobserve Database if already observed to force data check
        //            observeDatabase();

                }

                isRefreshing.setValue(false);

            }

            @Override
            public void onFailure(Call<Institutions> call, Throwable t) {
                Log.i("InstitutionRepository", "refreshData onFailure");

                //Reobserve Database if already observed to force data check
        //        observeDatabase();

                t.printStackTrace();

                isRefreshing.setValue(false);
            }
        });

    }
}
