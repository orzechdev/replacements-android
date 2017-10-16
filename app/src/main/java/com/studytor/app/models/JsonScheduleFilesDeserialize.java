package com.studytor.app.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Dawid on 2016-08-07.
 */
public class JsonScheduleFilesDeserialize implements JsonDeserializer<JsonScheduleFiles> {

    @Override
    public JsonScheduleFiles deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonArray css;
        if(jsonObject.has("css")){
            css = jsonObject.get("css").getAsJsonArray();
        }else{
            css = null;
        }
        final JsonArray images;
        if(jsonObject.has("images")){
            images = jsonObject.get("images").getAsJsonArray();
        }else{
            images = null;
        }
        final JsonArray plany;
        if(jsonObject.has("plany")){
            plany = jsonObject.get("plany").getAsJsonArray();
        }else{
            plany = null;
        }
        final JsonArray scripts;
        if(jsonObject.has("scripts")){
            scripts = jsonObject.get("scripts").getAsJsonArray();
        }else{
            scripts = null;
        }
        final JsonArray mainFiles = new JsonArray();
        int objectNumber = 0;
        while(jsonObject.has(Integer.toString(objectNumber))){
            mainFiles.add(jsonObject.get(Integer.toString(objectNumber)).getAsString());
            objectNumber++;
        }

        final JsonScheduleFiles jsonFiles = new JsonScheduleFiles();
        jsonFiles.setCss(css);
        jsonFiles.setImages(images);
        jsonFiles.setPlany(plany);
        jsonFiles.setScripts(scripts);
        jsonFiles.setMainFiles(mainFiles);
        return jsonFiles;
    }
}