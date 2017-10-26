package com.studytor.app.repositories.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 23.10.2017.
 * Class to represent json data available on the web repo
 */

public class Institutions {

    @SerializedName("schools")
    private List<SingleInstitution> institutions;

    public Institutions(){
        this.institutions = null;
    }

    public Institutions(List<SingleInstitution> institutionsList){
        this.institutions = institutionsList;
    }

    public List<SingleInstitution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<SingleInstitution> institutions) {
        this.institutions = institutions;
    }
}
