package com.studytor.app.repositories.webservices;

import com.studytor.app.repositories.models.Institutions;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.ReplacementsJson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dawid on 24.07.2017.
 */

public interface WebService {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     *
     */
    @GET("restfultests/tekst.php")
    Call<ResponseBody> getUser(@Query("name") String name);

    @GET("tutortest/json/institutions/{institutionId}/replacements/{date}/{ver}.php")
    Call<ReplacementsJson> getReplacements(
            @Path("institutionId") String institutionId,
            @Path("date") String date,
            @Path("ver") String ver
    );

    // Possible to call only if user have an account
    @GET("json/{user}/institutions")
    Call<ReplacementsJson> getUserInstitutions(
            //@Path("school_id") String schoolId
    );

    @GET("json/schoolList.json")
    Call<Institutions> getAllInstitutions(
    );

    @GET("json/school/{id}/news/page/{pageNum}.json")
    Call<News> getAllNews(@Path("id") int institutionId, @Path("pageNum") int pageNum);
}
