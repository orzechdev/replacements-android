package com.studytor.app.models;

import java.util.ArrayList;

/**
 * Created by Dawid on 2015-08-21.
 */
public class JsonReplacementsOld {
    private ArrayList<ReplacementTask> replacements = new ArrayList<>();
    private ArrayList<Long> ids = new ArrayList<>();

    public ReplacementTask getReplacement(int position) {
        return replacements.get(position);
    }
    public void setReplacement(int position, ReplacementTask replacementTask) {
        this.replacements.set(position, replacementTask);
    }
    public ArrayList<Long> getIds() {
        return ids;
    }
    public int getSize(){
        return replacements.size();
    }
}