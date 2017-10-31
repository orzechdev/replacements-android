package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.studytor.app.BR;
import com.studytor.app.R;

/**
 * Created by przemek19980102 on 20.10.2017.
 * Custom model representing a single institution inside FragmentInstitutions and ActivityInstitutionProfile etc.
 * it is also used in RetroFit to match JSON remote data and in ROOM to match database Data
 */

@Entity
public class SingleInstitution extends BaseObservable {

    @SerializedName("id")
    @PrimaryKey
    @NonNull
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("slug")
    @Expose
    private String slug;

    @SerializedName("urlLogo")
    @Expose
    private String logoUrl;

    @SerializedName("urlHeader")
    @Expose
    private String headerUrl;


    //Whatever do describe an institution and pass it to Activity which is displaying Institution's profile
    private int institutionId;

    public SingleInstitution(int id, String name, String slug){
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    @Bindable
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }
}
