package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by przemek19980102 on 31.10.2017.
 * Custom model representing a single institution inside FragmentInstitutions and ActivityInstitutionProfile etc.
 * it is also used in RetroFit to match JSON remote data and in ROOM to match database Data
 *
 */

@Entity
public class SingleNews extends BaseObservable{

    @SerializedName("id")
    @PrimaryKey
    @NonNull
    @Expose
    private int id;

    @SerializedName("institutionId")
    @NonNull
    @Expose
    private int institutionId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("contentIntro")
    @Expose
    private String shortenedContent;

    @SerializedName("contentMain")
    @Expose
    private String content;

    @SerializedName("headerThumbnail")
    @Expose
    private String thumbURL;

    @SerializedName("header")
    @Expose
    private String imageUrl;

    public SingleNews(){

    }

    public SingleNews(int id, int institutionId, String title, String shortenedContent, String content, String thumbUrl, String imageUrl){
        this.id = id;
        this.institutionId = institutionId;
        this.title = title;
        this.shortenedContent = shortenedContent;
        this.content = content;
        this.thumbURL = thumbUrl;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortenedContent() {
        return shortenedContent;
    }

    public void setShortenedContent(String shortenedContent) {
        this.shortenedContent = shortenedContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(@NonNull int institutionId) {
        this.institutionId = institutionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
