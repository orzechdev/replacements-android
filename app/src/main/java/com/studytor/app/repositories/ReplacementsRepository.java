package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.SingleReplacementJson;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dawid on 11.11.2017.
 */

public class ReplacementsRepository {

    private static final String CLASS_NAME = "ReplacementsRepository";

    private static ReplacementsRepository repositoryInstance;

    private Map<String, ReplacementsJson> replsContainerCache = new TreeMap<>();
    private MutableLiveData<ReplacementsJson> repls = null;
    private MutableLiveData<Boolean> isRefreshing = null;
    private MutableLiveData<String> selectedDate = null;
    private int currentInstitutId = 0;

    private WebService webService;

    //private MutableLiveData<ReplacementsJson> replacementsData;

    private ReplacementsRepository() {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();

        repls = new MutableLiveData<>();

        // DAWID --- checking whether refreshing is in progress
        isRefreshing = new MutableLiveData<>();
        isRefreshing.setValue(false);

        selectedDate = new MutableLiveData<>();
    }

    public static ReplacementsRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new ReplacementsRepository();
        }

        return repositoryInstance;
    }

    // DAWID --- Very huge advantage of having this variable in repo - even if we switch between screens and return to list of institutions,
    // --------- if the refreshing is not yet ended we will see, that it is refreshing, and any change of the screen does not cause that this refreshing will not be visible! :)
    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    //TODO Consider making repository free of such declarations as selected date
    //beacuse in some cases, when user navigates fast, or changes the date while it is still downloaded
    //repo can possibly assingn data to wrong date in cache, making a big mess!

    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    public LiveData<ReplacementsJson> getReplacements(final int institutionId, final String date) {
        selectedDate.setValue(date);

        Log.i(CLASS_NAME, "getReplacements 0");

        // If new institution selected - clear cache and download again for new institution
        if(currentInstitutId != institutionId){
            currentInstitutId = institutionId;
            replsContainerCache.clear();
            repls.setValue(null);
            refreshData(institutionId, date);
            return repls;
        }

        Log.i(CLASS_NAME, "getReplacements 1");
        // DAWID --- 1st check if there is cached institution list - if yes return it
        ReplacementsJson replsCache = replsContainerCache.get(date);
        if(replsCache != null){
            repls.setValue(replsCache);
            return repls;
        }

        Log.i(CLASS_NAME, "getReplacements 2");

        // DAWID --- 2nd if cached institution list doesn't exist - take it from database ( single source of truth - there is not other way from which live data is taken )
        // --------- HERE WE HAVE NOT DATABASE, SO WE OMIT THIS STEP
        //institutionListCache = institutionDao.loadAll();
        //Log.i("InstitutionRepository", "getInstitutionList 3");

        // DAWID --- 3rd if institution list wasn't in database - call to take it from the web
        // --------- HERE WE HAVE NOT DATABASE, SO WE OMIT IF CLAUSE
        // if(institutionListCache == null)
        refreshData(institutionId, date);
        Log.i(CLASS_NAME, "getReplacements 4")

        ;

        // DAWID --- 3rd do not wait until it will be taken from the web, but return live data object
        return repls;
    }


    public void forceRefreshData(final int institutionId, final String date) {
        replsContainerCache.clear();
        refreshData(institutionId, date);
    }

    private void refreshData(final int institutionId, final String date) {

        Log.i(CLASS_NAME, "refreshData");

        isRefreshing.setValue(true);

        this.webService.getReplacements(institutionId, date).enqueue(new Callback<ReplacementsJson>() {
            @Override
            public void onResponse(Call<ReplacementsJson> call, final Response<ReplacementsJson> response) {
                Log.i(CLASS_NAME, "refreshData onResponse");

                ReplacementsJson replsResponse = response.body();

                if(response.isSuccessful() && replsResponse != null && replsResponse.getReplacements() != null){
                    Log.i(CLASS_NAME, "refreshData onResponse SUCCESSFUL 1");

                    // DAWID --- Set news for cache
                    replsContainerCache.put(date, replsResponse);

                    // DAWID --- Set news for liveData available for viewModel
                    repls.postValue(replsResponse);

                    Log.i(CLASS_NAME, "refreshData onResponse SUCCESSFUL 2");
                }else{
                    // TODO Handle errors - if needed show on screen appropriate info
                    Log.i(CLASS_NAME, "refreshData onResponse IS NULL");

                    replsContainerCache.remove(date);

                    repls.postValue(null);
                }

                isRefreshing.setValue(false);

            }

            @Override
            public void onFailure(Call<ReplacementsJson> call, Throwable t) {
                Log.i(CLASS_NAME, "refreshData onFailure");

                replsContainerCache.remove(date);

                repls.postValue(null);

                t.printStackTrace();

                isRefreshing.setValue(false);
            }
        });
    }
}
