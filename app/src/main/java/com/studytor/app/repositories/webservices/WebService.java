package com.studytor.app.repositories.webservices;

import com.studytor.app.repositories.models.Institutions;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.repositories.models.ScheduleTimetable;
import com.studytor.app.repositories.models.UserReplacementsJson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static com.google.gson.internal.bind.TypeAdapters.URL;

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
    Call<UserReplacementsJson> getUserReplacements(
            @Path("institutionId") String institutionId,
            @Path("date") String date,
            @Path("ver") String ver
    );

    @GET("json/school/{id}/replacements/{date}.json")
    Call<ReplacementsJson> getReplacements(
            @Path("id") int institutionId,
            @Path("date") String date
    );

    // Possible to call only if user have an account
    @GET("json/{user}/institutions")
    Call<UserReplacementsJson> getUserInstitutions(
            //@Path("school_id") String schoolId
    );

    @GET("json/school/list")
    Call<Institutions> getAllInstitutions(
    );

    @GET("json/school/{id}/news/page/{pageNum}.json")
    Call<News> getAllNews(@Path("id") int institutionId, @Path("pageNum") int pageNum);

    @GET("json/school/{id}/lessonplan/list.json")
    Call<Schedule> getSchedules(@Path("id") int institutionId);

    /* Timetables have custom URLs, so we need to declare a full path to the remote JSON data. */
    @GET
    Call<ScheduleTimetable> getScheduleTimetable(@Url String url);
}
