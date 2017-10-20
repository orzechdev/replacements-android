package com.studytor.app.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.studytor.app.BR;

/**
 * Created by przemek19980102 on 20.10.2017.
 */

public class SingleInstitution extends BaseObservable {

    private int imageResource;
    private String name;

    public SingleInstitution(int imageResId, String name){
        this.imageResource = imageResId;
        this.name = name;
    }

    @Bindable
    public int getImageResource(){
        return imageResource;
    }

    @Bindable
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setImageResId(int imageResId){
        this.imageResource = imageResId;
        notifyPropertyChanged(BR.imageResource);
    }

}
