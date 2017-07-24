package com.replacements.replacements.repositories.webservices;

import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.models.JsonReplacementsOld;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dawid on 24.07.2017.
 */

public interface Webservice {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */
    @GET("restfultests/tekst.php")
    Call<ResponseBody> getUser(@Query("name") String name);

    @GET("json/schools/{schoolId}/replacements/{date}/{ver}")
    Call<JsonReplacements> getReplacements(
            @Path("institutionId") String institutionId,
            @Path("date") String date,
            @Path("ver") String ver
    );

    // Possible to call only if user have an account
    @GET("json/{user}/institutions")
    Call<JsonReplacements> getUserInstitutions(
            //@Path("school_id") String schoolId
    );
}
