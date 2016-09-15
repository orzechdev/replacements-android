package com.replacements.replacements.helpers;

import android.webkit.JavascriptInterface;

import java.util.Observable;

/**
 * Created by Dawid on 2016-02-05.
 */
public class HtmlJSInterface extends Observable {
    private String html;

    /**
     * @return The most recent HTML received by the interface
     */
    @JavascriptInterface
    public String getHtml() {
        return this.html;
    }

    /**
     * Sets most recent HTML and notifies observers.
     *
     * @param html
     *          The full HTML of a page
     */
    @JavascriptInterface
    public void setHtml(String html) {
        this.html = html;
        setChanged();
        notifyObservers(html);
    }
}