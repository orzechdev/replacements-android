package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class Schedule {

    @SerializedName("lessonplans")
    private List<ScheduleLessonplan> lessonplans;

    private Integer institutionId = null;

    public Schedule(){

    }

    public List<ScheduleLessonplan> getLessonplans() {
        return lessonplans;
    }

    public void setLessonplans(List<ScheduleLessonplan> lessonplans) {
        this.lessonplans = lessonplans;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }
}
