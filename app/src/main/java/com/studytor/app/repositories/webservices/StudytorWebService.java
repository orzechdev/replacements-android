package com.studytor.app.repositories.webservices;

import com.studytor.app.models.Institutions;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by przemek19980102 on 23.10.2017.
 * New WebService to represent studytor.com instead of 10.0.0.2
 */

public interface StudytorWebService {

    @GET("json/schoolList.json")
        Call<Institutions> getAllInstitutions(
    );

}
