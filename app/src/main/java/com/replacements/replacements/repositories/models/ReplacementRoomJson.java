package com.replacements.replacements.repositories.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dawid on 01.08.2017.
 */

@Entity
public class ReplacementRoomJson {//extends ReplacementsJson {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    @NonNull
    private String id;
    // Bellow variable for JSON not exists
    // Bellow variable for Room exists
    private String institutId;
    // Bellow variable for JSON not exists
    // Bellow variable for Room exists
    private String ver;
    @SerializedName("replacement")
    @Expose
    private String replacement;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("class_number")
    @Expose
    private String classNumber;
    @SerializedName("default_integer")
    @Expose
    private String defaultInteger;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutId() {
        // If institutionId does not exist in database, return institutionId form JSON exists
//        if(institutId == null)
//            return getInstitutionIdForChild();
        return institutId;
    }

    public void setInstitutId(String institutionId) {
        // If institutionId does not exist in database, save institutionId as it is in JSON
//        if(institutionId == null)
//            this.institutId = getInstitutionIdForChild();
        this.institutId = institutionId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getDefaultInteger() {
        return defaultInteger;
    }

    public void setDefaultInteger(String defaultInteger) {
        this.defaultInteger = defaultInteger;
    }

}