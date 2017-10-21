package com.studytor.app.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.studytor.app.BR;

/**
 * Created by przemek19980102 on 20.10.2017.
 * Custom class representing a single institution inside FragmentInstitutions
 * it can be improved in the future to be used with InstitutionActivity
 */

public class SingleInstitution extends BaseObservable {

    private int imageResource;
    private String name;

    //Whatever do describe an institution and pass it to Activity which is displaying Institution's profile
    private int institutionId;


    public SingleInstitution(int imageResId, String name){
        this.imageResource = imageResId;
        this.name = name;
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

}
