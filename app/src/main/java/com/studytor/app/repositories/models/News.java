package com.studytor.app.repositories.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class News {

    @SerializedName("news")
    private List<SingleNews> newsList;

    @SerializedName("allPages")
    private int lastPage;

    private int currentPage;

    private int institutionId;

    public News(){
        this.newsList = null;
    }

    public News(List<SingleNews> newsList){
        this.newsList = newsList;
    }

    public List<SingleNews> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<SingleNews> newsList) {
        this.newsList = newsList;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }
}
