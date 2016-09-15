package com.replacements.replacements.models;

public class ClassTask {
    private boolean selected;
    private long id;
    private String ver;
    private String name;

    public ClassTask(long id, String name) {
        this.id = id;
        this.name = name;
        selected = false;
    }
    public ClassTask(boolean selected, long id, String name) {
        this.selected = selected;
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsSelected() {
        return selected;
    }

    public void setIsSelected(boolean selected) {
        this.selected = selected;
    }
}