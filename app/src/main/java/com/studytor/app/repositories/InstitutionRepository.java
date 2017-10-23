package com.studytor.app.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.studytor.app.R;
import com.studytor.app.models.Institutions;
import com.studytor.app.models.SingleInstitution;
import com.studytor.app.repositories.cache.FragmentInstitutionCache;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.StudytorRetrofitClientSingleton;
import com.studytor.app.repositories.webservices.StudytorWebService;
import com.studytor.app.repositories.webservices.WebService;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by Dawid on 25.07.2017.
 *
 * This repository might be used by multiple activites and fragments sharing SingleInstitution model
 * Using the same repository is less flexible but much cleaner to update in the future.
 */

public class InstitutionRepository {
    private static final String CLASS_NAME = FragmentReplacementsRepository.class.getName();

    private static InstitutionRepository repositoryInstance;
    private StudytorWebService webService;
    private FragmentInstitutionCache fragmentReplacementsCache;

    private InstitutionRepository() {
        this.webService = StudytorRetrofitClientSingleton.getInstance().getWebService();
    }

    public static InstitutionRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new InstitutionRepository();
        }

        return repositoryInstance;
    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromCache(){
        log.i(CLASS_NAME, "getInstitutionList 100");

        //TODO: CACHE
        MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        returnData.setValue(Arrays.asList(
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c),
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c),
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c)
        ));

        return returnData;
    }

    //Load InstitutionList from web using retrofit
    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromWeb(){

        final MutableLiveData<List<SingleInstitution>> finalReturnData = new MutableLiveData<>();

        this.webService.getAllInstitutions().enqueue(new Callback<Institutions>() {
            @Override
            public void onResponse(Call<Institutions> call, Response<Institutions> response) {
                if(response.isSuccessful()){
                    finalReturnData.postValue(response.body().getInstitutions());
                }else{
                    finalReturnData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Institutions> call, Throwable t) {
                finalReturnData.postValue(null);
            }
        });

        return finalReturnData;
    }
}
