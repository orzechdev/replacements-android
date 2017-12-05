package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;
import com.studytor.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleLessonplan extends BaseObservable{

    @SerializedName("sections")
    private List<ScheduleSection> sections;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    private boolean isExpanded = false;

    public static int ARROW_UP_RES = R.drawable.ic_keyboard_arrow_up_black_24dp;
    public static int ARROW_DOWN_RES = R.drawable.ic_keyboard_arrow_down_black_24dp;

    public ScheduleLessonplan(){}

    public List<ScheduleSection> getSections() {
        return sections;
    }

    public void setSections(List<ScheduleSection> sections) {
        this.sections = sections;
        notifyChange();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyChange();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyChange();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void toggleExpansion(){
        this.isExpanded = !this.isExpanded;
        notifyChange();
    }
}
