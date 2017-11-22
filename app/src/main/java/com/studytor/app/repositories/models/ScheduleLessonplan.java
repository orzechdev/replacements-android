package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleLessonplan {

    @SerializedName("sections")
    private List<ScheduleSection> sections;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public ScheduleLessonplan(){}

    public List<ScheduleSection> getSections() {
        return sections;
    }

    public void setSections(List<ScheduleSection> sections) {
        this.sections = sections;
    }

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

}
