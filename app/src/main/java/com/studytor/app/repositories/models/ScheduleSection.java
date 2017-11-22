package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleSection {

    @SerializedName("name")
    private String name;

    @SerializedName("schedules")
    private List<ScheduleUnit> scheduleUnits;

    public ScheduleSection(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScheduleUnit> getScheduleUnits() {
        return scheduleUnits;
    }

    public void setScheduleUnits(List<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
    }
}