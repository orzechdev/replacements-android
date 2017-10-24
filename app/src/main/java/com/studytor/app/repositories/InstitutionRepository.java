package com.studytor.app.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.studytor.app.R;
import com.studytor.app.models.Institutions;
import com.studytor.app.models.SingleInstitution;
import com.studytor.app.repositories.cache.FragmentInstitutionCache;
import com.studytor.app.repositories.database.DatabaseSingleton;
import com.studytor.app.repositories.database.InstitutionDao;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
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
    private static final String CLASS_NAME = InstitutionRepository.class.getName();

    private static InstitutionRepository repositoryInstance;

    private WebService webService;
    private FragmentInstitutionCache fragmentInstitutionCache;
    private InstitutionDao institutionDao;

    private InstitutionRepository(Context context) {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
        this.institutionDao = DatabaseSingleton.getInstance(context).getMainDatabase().institutionDao();
    }

    public static InstitutionRepository getInstance(Context context) {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new InstitutionRepository(context);
        }

        return repositoryInstance;
    }

    //Get institution list from any available source (CACHE, DB, WEB)
    public MutableLiveData<List<SingleInstitution>> getInstitutionList() {

        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        //Check CACHE
        getInstitutionListFromCache().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                System.out.println("REPO CHECK CACHE");
                if(institutions == null){
                    //Check Database
                    getInstitutionListFromDatabase().observeForever(new Observer<List<SingleInstitution>>() {
                        @Override
                        public void onChanged(@Nullable List<SingleInstitution> institutions) {
                            if(institutions == null){
                                //Check web
                                getInstitutionListFromWeb().observeForever(new Observer<List<SingleInstitution>>() {
                                    @Override
                                    public void onChanged(@Nullable List<SingleInstitution> institutions) {
                                        //Return data from web or null if can not access
                                        returnData.postValue(institutions);
                                    }
                                });
                            }else{
                                //Return data from DB if exists
                                returnData.postValue(institutions);
                            }
                        }
                    });
                }else{
                    //Return data from CACHE if exists
                    returnData.postValue(institutions);
                }
            }
        });

        return  returnData;

    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromCache(){

        //TODO: CACHE
        MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        /*returnData.setValue(Arrays.asList(
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c),
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c),
                new SingleInstitution(1, "1", "slug1", R.drawable.header_image_1_c),
                new SingleInstitution(2, "2", "slug2", R.drawable.header_image_2_c)
        ));*/

        //Pretend that there is no cache data for now
        returnData.setValue(null);

        return returnData;
    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromDatabase(){

        final MutableLiveData<List<SingleInstitution>> returnData = new MutableLiveData<>();

        this.institutionDao.loadAll().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                returnData.postValue(institutions);
            }
        });

        return returnData;

    }

    //Load InstitutionList from web using retrofit
    public MutableLiveData<List<SingleInstitution>> getInstitutionListFromWeb(){

        final MutableLiveData<List<SingleInstitution>> finalReturnData = new MutableLiveData<>();

        this.webService.getAllInstitutions().enqueue(new Callback<Institutions>() {
            @Override
            public void onResponse(Call<Institutions> call, final Response<Institutions> response) {
                if(response.isSuccessful()){

                    finalReturnData.postValue(response.body().getInstitutions());

                    //Save data to local database
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SingleInstitution> institutionList = response.body().getInstitutions();
                            if(institutionList != null){
                                institutionDao.insertAll(institutionList);
                            }
                        }
                    });
                    thread.start();


                }else{
                    finalReturnData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Institutions> call, Throwable t) {
                finalReturnData.postValue(null);
                t.printStackTrace();
            }
        });

        return finalReturnData;
    }
}
