package com.studytor.app.repositories.models;

import android.arch.persistence.room.Entity;
import android.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by przemek19980102 on 22.11.2017.
 */

@Entity
public class ScheduleTimetable {

    @SerializedName("hours")
    private List<ScheduleTimetableHour> hours;

    @SerializedName("days")
    private List<ScheduleTimetableDay> days;

    private String url;
    private String name;

    public List<ScheduleTimetableHour> getHours() {
        return hours;
    }

    public void setHours(List<ScheduleTimetableHour> hours) {
        this.hours = hours;
    }

    public List<ScheduleTimetableDay> getDays() {
        return days;
    }

    public void setDays(List<ScheduleTimetableDay> days) {
        this.days = days;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if(url != null)this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null)this.name = name;
    }

    public class ScheduleTimetableDay{

        @SerializedName("day")
        private int day;

        @SerializedName("lessons")
        private List<ScheduleTimetableLesson> lessons;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public List<ScheduleTimetableLesson> getLessons() {
            return lessons;
        }

        public void setLessons(List<ScheduleTimetableLesson> lessons) {
            this.lessons = lessons;
        }
    }

    public class ScheduleTimetableLesson{

        @SerializedName("number")
        private int number;

        @SerializedName("subject")
        private String subject;

        @SerializedName("teacher")
        private String teacher;

        @SerializedName("room")
        private String room;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }
    }

    public class ScheduleTimetableHour{

        @SerializedName("number")
        private int number;

        @SerializedName("start")
        private String start;

        @SerializedName("end")
        private String end;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

}
