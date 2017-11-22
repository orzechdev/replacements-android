package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleUnit {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    public ScheduleUnit(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
