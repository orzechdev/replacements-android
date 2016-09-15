package com.replacements.replacements.models;

import com.google.gson.JsonArray;

import java.util.ArrayList;

/**
 * Created by Dawid on 2016-08-07.
 */
public class JsonScheduleFiles {
    private JsonArray css = new JsonArray();
    private JsonArray images = new JsonArray();
    private JsonArray plany = new JsonArray();
    private JsonArray scripts = new JsonArray();
    private JsonArray mainFiles = new JsonArray();

    public JsonArray getCss() {
        return css;
    }

    public void setCss(JsonArray css) {
        this.css = css;
    }

    public JsonArray getImages() {
        return images;
    }

    public void setImages(JsonArray images) {
        this.images = images;
    }

    public JsonArray getPlany() {
        return plany;
    }

    public void setPlany(JsonArray plany) {
        this.plany = plany;
    }

    public JsonArray getScripts() {
        return scripts;
    }

    public void setScripts(JsonArray scripts) {
        this.scripts = scripts;
    }

    public JsonArray getMainFiles() {
        return mainFiles;
    }

    public void setMainFiles(JsonArray mainFiles) {
        this.mainFiles = mainFiles;
    }
}
