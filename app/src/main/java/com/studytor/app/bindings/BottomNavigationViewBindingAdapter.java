package com.studytor.app.bindings;

import android.databinding.BindingAdapter;
import android.support.design.widget.BottomNavigationView;

/**
 * Created by Dawid on 16.07.2017.
 */

public class BottomNavigationViewBindingAdapter {
    @BindingAdapter(value = "onNavigationItemSelected")
    public static void setOnNavigationItemSelectedListener(
            BottomNavigationView view, BottomNavigationView.OnNavigationItemSelectedListener listener) {
        view.setOnNavigationItemSelectedListener(listener);
    }
}
