package com.replacements.replacements.repositories.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Dawid on 28.07.2017.
 */

@Entity
public class Replacement {
    @PrimaryKey
    private String id;
    private String ver;
    private String replacement;
    private String number;
    private String class_number;
    private String default_integer;
    private boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
