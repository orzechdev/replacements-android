package com.studytor.app.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.studytor.app.BR;
import com.studytor.app.R;

/**
 * Created by przemek19980102 on 20.10.2017.
 * Custom model representing a single institution inside FragmentInstitutions and ActivityInstitutionProfile
 * it is also used in RetroFit to match JSON remote data
 */

public class SingleInstitution extends BaseObservable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    private int imageResource;

    //Whatever do describe an institution and pass it to Activity which is displaying Institution's profile
    private int institutionId;

    public SingleInstitution(int id, String name, String slug){
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.imageResource = R.drawable.header_image_1;
    }

    public SingleInstitution(int id, String name, String slug, int imageResId){
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.imageResource = imageResId;
    }

    @Bindable
    public int getImageResource(){
        return imageResource;
    }

    public void setImageResource(int imageResId){
        this.imageResource = imageResId;
        notifyPropertyChanged(BR.imageResource);
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
}
