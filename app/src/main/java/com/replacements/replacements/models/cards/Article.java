package com.replacements.replacements.models.cards;

import android.graphics.drawable.Drawable;
import android.media.Image;

import java.util.ArrayList;

/**
 * Created by Dawid on 2015-09-27.
 */
public class Article extends Object{
    public String title;
    public String text;
    public Drawable drawable;
    public Article(String title, String text, Drawable drawable){
        this.title = title;
        this.text = text;
        this.drawable = drawable;
    }
}
