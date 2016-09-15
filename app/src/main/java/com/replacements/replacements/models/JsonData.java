package com.replacements.replacements.models;

import java.util.ArrayList;

/**
 * Created by Dawid on 2015-09-10.
 */
public class JsonData {
    private ArrayList<ClassTask> classes = new ArrayList<>();
    private ArrayList<TeacherTask> teachers = new ArrayList<>();
    private ArrayList<Long> ids_classes = new ArrayList<>();
    private ArrayList<Long> ids_teachers = new ArrayList<>();

    public ClassTask getClassTask(int position) {
        return classes.get(position);
    }
    public void setClassTask(int position, ClassTask classTask) {
        this.classes.set(position, classTask);
    }

    public TeacherTask getTeacherTask(int position) {
        return teachers.get(position);
    }
    public void setTeacherTask(int position, TeacherTask teacherTask) {
        this.teachers.set(position, teacherTask);
    }

    public ArrayList<Long> getIdsClass() {
        return ids_classes;
    }

    public ArrayList<Long> getIdsTeacher() {
        return ids_teachers;
    }

    public int getClassesSize(){
        return classes.size();
    }

    public int getTeachersSize(){
        return teachers.size();
    }
}