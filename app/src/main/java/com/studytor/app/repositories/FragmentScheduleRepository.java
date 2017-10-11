package com.studytor.app.repositories;

import com.studytor.app.repositories.cache.FragmentScheduleCache;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentScheduleRepository {
    private static FragmentScheduleRepository repositoryInstance;
    private WebService webService;
    private FragmentScheduleCache fragmentReplacementsCache;

    private FragmentScheduleRepository () {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
    }

    public static FragmentScheduleRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new FragmentScheduleRepository();
        }

        return repositoryInstance;
    }
}
