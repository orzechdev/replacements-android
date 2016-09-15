package com.replacements.replacements.models;

import com.google.gson.JsonElement;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Dawid on 2016-08-07.
 */
public class JsonSchedule {
    private String prefix;
    private JsonElement files;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public JsonElement getFiles() {
        return files;
    }
}
