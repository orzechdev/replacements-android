package com.replacements.replacements.repositories.webservices.json;

/**
 * Created by Dawid on 24.07.2017.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonReplacements {

    @SerializedName("replacements")
    @Expose
    private List<JsonReplacement> replacements = null;
    @SerializedName("ids")
    @Expose
    private List<String> ids = null;

    public List<JsonReplacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<JsonReplacement> replacements) {
        this.replacements = replacements;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public class JsonReplacement {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("ver")
        @Expose
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

}
