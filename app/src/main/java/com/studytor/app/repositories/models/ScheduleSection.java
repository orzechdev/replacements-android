package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.databinding.BaseObservable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleSection extends BaseObservable{

    @SerializedName("name")
    private String name;

    private String visibleName;

    @SerializedName("schedules")
    private List<ScheduleUnit> scheduleUnits;

    public ScheduleSection(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    public String getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public List<ScheduleUnit> getScheduleUnits() {
        return scheduleUnits;
    }

    public void setScheduleUnits(List<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
        notifyChange();
    }
}
