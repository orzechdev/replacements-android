package com.studytor.app.repositories.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 01.11.2017.
 */

public class News {

    @SerializedName("news")
    private List<SingleNews> newsList;

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

}
