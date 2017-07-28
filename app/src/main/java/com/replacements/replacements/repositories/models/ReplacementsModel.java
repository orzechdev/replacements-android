package com.replacements.replacements.repositories.models;

import java.util.List;

/**
 * Created by Dawid on 28.07.2017.
 */

public class ReplacementsModel {
    //private String institutionId;
    //private String date;
    private List<ReplacementModel> replacements = null;

    public List<ReplacementModel> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<ReplacementModel> replacements) {
        this.replacements = replacements;
    }

    public void addReplacement(int id, String ver, String replacement, String number, String class_number, String default_integer) {
        ReplacementModel replacementModel = new ReplacementModel();
        replacementModel.setId(id);
        replacementModel.setVer(ver);
        replacementModel.setReplacement(replacement);
        replacementModel.setNumber(number);
        replacementModel.setClass_number(class_number);
        replacementModel.setDefault_integer(default_integer);
        replacements.add(replacementModel);
    }

    public class ReplacementModel {
        private int id;
        private String ver;
        private String replacement;
        private String number;
        private String class_number;
        private String default_integer;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public String getClass_number() {
            return class_number;
        }

        public void setClass_number(String class_number) {
            this.class_number = class_number;
        }

        public String getDefault_integer() {
            return default_integer;
        }

        public void setDefault_integer(String default_integer) {
            this.default_integer = default_integer;
        }
    }
}
