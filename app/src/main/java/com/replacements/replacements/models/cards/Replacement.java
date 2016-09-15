package com.replacements.replacements.models.cards;

import java.util.ArrayList;

/**
 * Created by Dawid on 2015-09-27.
 */
public class Replacement {
    public String title;
    public String name;
    public String surname;
    public String email;
    public Replacement(ArrayList<Replacement> arrayList){
        this.title = arrayList.get(0).title;
        this.name = arrayList.get(0).name;
        this.surname = arrayList.get(0).surname;
        this.email = arrayList.get(0).email;
    }
}
