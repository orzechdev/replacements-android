package com.replacements.replacements.repositories;

import com.replacements.replacements.repositories.cache.FragmentInstitutionCache;
import com.replacements.replacements.repositories.webservices.RetrofitClientSingleton;
import com.replacements.replacements.repositories.webservices.WebService;

/**
 * Created by Dawid on 25.07.2017.
 */

public class FragmentInstitutionRepository {
    private static FragmentInstitutionRepository repositoryInstance;
    private WebService webService;
    private FragmentInstitutionCache fragmentReplacementsCache;

    private FragmentInstitutionRepository () {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
    }

    public static FragmentInstitutionRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new FragmentInstitutionRepository();
        }

        return repositoryInstance;
    }
}
