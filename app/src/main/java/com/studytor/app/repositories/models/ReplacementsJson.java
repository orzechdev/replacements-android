package com.studytor.app.repositories.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dawid on 11.11.2017.
 */

public class ReplacementsJson {
    @SerializedName("replacements")
    @Expose
    private List<SingleReplacementJson> replacements = null;

    public List<SingleReplacementJson> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<SingleReplacementJson> replacements) {
        this.replacements = replacements;
    }
}
