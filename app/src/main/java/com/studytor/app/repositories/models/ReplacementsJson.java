package com.studytor.app.repositories.models;

/**
 * Created by Dawid on 24.07.2017.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplacementsJson {

    // Bellow variable for JSON exists
    // Bellow variable for Room not exists, thus @Ignore - it is required because ReplacementRoomJson extends this class
 //   @Ignore
    @SerializedName("replacements")
    @Expose
    private List<ReplacementRoomJson> replacements = null;

    // Bellow variable for JSON exists
    // Bellow variable for Room not exists, thus @Ignore - it is required because ReplacementRoomJson extends this class
 //   @Ignore
    @SerializedName("institutionId")
    @Expose
    private String institutionId = null;

    @SerializedName("ver")
    @Expose
    private String ver = null;

//    // For JSON not exists
//    // For Room exists
//    private ReplacementRoomJson jsonReplacement = null;

    public List<ReplacementRoomJson> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<ReplacementRoomJson> replacements) {
        this.replacements = replacements;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getInstitutionIdForChild() {
        return institutionId;
    }



}
