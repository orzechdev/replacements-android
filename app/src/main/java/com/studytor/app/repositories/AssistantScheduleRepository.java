package com.studytor.app.repositories;

import com.studytor.app.repositories.cache.AssistantScheduleCache;
import com.studytor.app.repositories.webservices.RetrofitClientSingleton;
import com.studytor.app.repositories.webservices.WebService;

/**
 * Created by Dawid on 25.07.2017.
 */

public class AssistantScheduleRepository {
    private static AssistantScheduleRepository repositoryInstance;
    private WebService webService;
    private AssistantScheduleCache fragmentReplacementsCache;

    private AssistantScheduleRepository() {
        this.webService = RetrofitClientSingleton.getInstance().getWebService();
    }

    public static AssistantScheduleRepository getInstance() {
        if (repositoryInstance == null){ //if there is no instance available... create new one
            repositoryInstance = new AssistantScheduleRepository();
        }

        return repositoryInstance;
    }
}
