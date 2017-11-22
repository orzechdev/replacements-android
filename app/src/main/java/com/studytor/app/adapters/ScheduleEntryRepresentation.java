package com.studytor.app.adapters;

import android.databinding.BaseObservable;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

public class ScheduleEntryRepresentation extends BaseObservable{
    private String name;

    public ScheduleEntryRepresentation(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
