package com.replacements.replacements.models;

import java.util.ArrayList;
import java.util.Arrays;

public class ReplacementTask {
    private long id;
    private String ver;
    private String number;
    private String[] array_number;
    private int first_number;
    private int last_number;
    private String replacement;
    private long default_integer;
    private long class_number;
    private boolean today;
    private boolean tomorrow;
    private boolean is_replacements;
    private boolean is_empty;

    public ReplacementTask(long id, String ver, String number, String replacement, long default_integer, long class_number) {
        if (number != null) {
            this.id = id;
            this.ver = ver;
            this.number = number;
            this.array_number = number.split(",");
            this.first_number = (array_number.length > 0 && !number.equals("")) ? Integer.parseInt(array_number[0]) : 0;
            this.last_number = (array_number.length > 0 && !number.equals("")) ? Integer.parseInt(array_number[array_number.length - 1]) : 0;
            this.replacement = replacement;
            this.default_integer = default_integer;
            this.class_number = class_number;
            today = false;
            tomorrow = false;
            is_replacements = true;
            is_empty = false;
        }else{
            this.id = id;
            this.ver = ver;
            this.number = "";
            this.first_number = 0;
            this.last_number = 0;
            this.replacement = replacement;
            this.default_integer = 0;
            this.class_number = 0;
            today = false;
            tomorrow = false;
            is_replacements = true;
            is_empty = false;
        }
    }
    public ReplacementTask(long id, String ver, String number, String replacement, long default_integer, long class_number, boolean today, boolean tomorrow, boolean is_replacements) {
        this.id = id;
        this.ver = ver;
        this.number = number;
        this.array_number = number.split(",");
        this.first_number = (array_number.length > 0 && !number.equals(""))? Integer.parseInt(array_number[0]) : 0;
        this.last_number = (array_number.length > 0 && !number.equals(""))? Integer.parseInt(array_number[array_number.length - 1]) : 0;
        this.replacement = replacement;
        this.default_integer = default_integer;
        this.class_number = class_number;
        this.today = today;
        this.tomorrow = tomorrow;
        this.is_replacements = is_replacements;
        this.is_empty = false;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFirstNumber() {
        return first_number;
    }

    public void setFirstNumber(int first_number) {
        this.first_number = first_number;
    }

    public int getLastNumber() {
        return last_number;
    }

    public void setLastNumber(int last_number) {
        this.last_number = last_number;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public long getDefaultInteger() {
        return default_integer;
    }

    public void setDefaultInteger(long default_integer) {
        this.default_integer = default_integer;
    }

    public long getClassNumber() {
        return class_number;
    }

    public void setClassNumber(long class_number) {
        this.class_number = class_number;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public boolean isTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(boolean tomorrow) {
        this.tomorrow = tomorrow;
    }
//
//    public class Number {
//        private int number;
//
//        public Number(int number) {
//            this.number = number;
//        }
//
//        public int getNumber() {
//            return number;
//        }
//        public void setNumber(int number) {
//            this.number = number;
//        }
//    }

    public boolean isReplacements() {
        return is_replacements;
    }

    public void setIsReplacements(boolean is_replacements) {
        this.is_replacements = is_replacements;
    }

    public boolean isEmpty() {
        return is_empty;
    }

    public void setIsEmpty(boolean is_empty) {
        this.is_empty = is_empty;
    }
}