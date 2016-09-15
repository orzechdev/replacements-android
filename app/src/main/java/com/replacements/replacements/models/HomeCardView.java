package com.replacements.replacements.models;

import com.replacements.replacements.models.cards.Article;
import com.replacements.replacements.models.cards.Note;
import com.replacements.replacements.models.cards.Replacement;

import java.util.Date;

/**
 * Created by Dawid on 2015-09-27.
 */
public class HomeCardView {
    private int contentType;

    private Date date;
    private int institution;
    private int author;
    private String url_header;
    private String url_content;

    private Note note;
    private Article article;
    private Replacement replacement;

    //Constructors
    public HomeCardView(Date date, int institution, int author, String url_header, String url_content, Note note){
        contentType = 0;
        this.date = date;
        this.institution = institution;
        this.author = author;
        this.url_header = url_header;
        this.url_content = url_content;
        this.note = note;
    }
    public HomeCardView(Date date, int institution, int author, String url_header, String url_content, Article article){
        contentType = 1;
        this.date = date;
        this.institution = institution;
        this.author = author;
        this.url_header = url_header;
        this.url_content = url_content;
        this.article = article;
    }
    public HomeCardView(Date date, int institution, int author, String url_header, String url_content, Replacement replacement){
        contentType = 2;
        this.date = date;
        this.institution = institution;
        this.author = author;
        this.url_header = url_header;
        this.url_content = url_content;
        this.replacement = replacement;
    }

    //Type of used constructor
    public int getContentType(){
        return contentType;
    }

    //Getters of common parameters
    public Date getDate(){
        return date;
    }
    public int getInstitution(){
        return institution;
    }
    public int getAuthor(){
        return author;
    }
    public String getUrlHeader() {
        return url_header;
    }
    public String getUrlContent() {
        return url_content;
    }

    //Getters of objects
    public Note getNote(){
        return note;
    }
    public Article getArticle(){
        return article;
    }
    public Replacement getReplacement(){
        return replacement;
    }
}
