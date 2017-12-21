package com.studytor.app.activities;

import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.studytor.app.BR;
import com.studytor.app.R;
import com.studytor.app.databinding.ActivityScheduleTimetableBinding;
import com.studytor.app.databinding.ViewSingleLessonplanBinding;
import com.studytor.app.databinding.ViewSingleLessonplanNumHourBinding;
import com.studytor.app.databinding.ViewSingleLessonplanWeekdayBinding;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.repositories.models.ScheduleTimetable;
import com.studytor.app.viewmodel.ActivityScheduleTimetableViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityScheduleTimetable extends AppCompatActivity {
    private ActivityScheduleTimetableViewModel viewModel;
    private ActivityScheduleTimetableBinding binding;

    String url, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_timetable);

        viewModel = ViewModelProviders.of(this).get(ActivityScheduleTimetableViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_timetable);

        Intent thisIntent = getIntent();
        url = thisIntent.getStringExtra(ApplicationConstants.INTENT_LESSONPLAN_URL);
        name = thisIntent.getStringExtra(ApplicationConstants.INTENT_LESSONPLAN_NAME);

        System.out.println(name + " URL: " + url);

        viewModel.setup(url, name);


        viewModel.liveData.observe(this, new Observer<ScheduleTimetable>() {
            @Override
            public void onChanged(@Nullable ScheduleTimetable scheduleTimetable) {
                //Do nothing for now?
            }
        });

        binding.setObservable(viewModel.getObservable());
        binding.setHandler(viewModel.getHandler());

        //Set current item to 0 to prevent display bugs when rotating screen
        viewModel.getObservable().currentItem.set(0);

    }

    public void goBack(View v){
        super.onBackPressed();
    }

    @BindingAdapter("setupNumsAndHours")
    public static void setupNumsAndHours(final LinearLayout ll, final ActivityScheduleTimetableViewModel.Observable observable){
        if(observable == null) return;
        if(observable.schedule.get() == null) return;
        if(observable.schedule.get().getHours() == null || observable.schedule.get().getHours().size() <= 0) return;

        ll.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ll.getContext());
        for(ScheduleTimetable.ScheduleTimetableHour d : observable.schedule.get().getHours()){
            ViewSingleLessonplanNumHourBinding b = DataBindingUtil.inflate(inflater, R.layout.view_single_lessonplan_num_hour, ll, true);
            b.setVariable(BR.num, String.valueOf(d.getNumber()));
            b.setVariable(BR.hours, d.getStart() + "\n" + d.getEnd());
        }

    }

    @BindingAdapter("setupDayList")
    public static void setupDayList(final LinearLayout ll, final ActivityScheduleTimetableViewModel.Observable observable){
        if(observable == null) return;
        if(observable.schedule.get() == null) return;
        if(observable.schedule.get().getDays() == null || observable.schedule.get().getDays().size() <= 0) return;

        ll.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ll.getContext());

        //Calculate whole screen width
        DisplayMetrics metrics = ll.getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        //Calculate left space for day names
        //left arrow + right arrow = around 120dp
        int leftWidth = width - (int)convertDpToPixel(56, ll.getContext());

        //Let's see how much elements will fit on the screen
        int baseElementWidth = (int)convertDpToPixel(220, ll.getContext());
        int maxElementCount = leftWidth/baseElementWidth;

        //Calculate width of each element to fill left space
        int maxElementWidth = leftWidth/maxElementCount;

        observable.itemsPerPage.set(maxElementCount);
        observable.maxDayTVWidth.set(maxElementWidth);
        observable.maxItemCount.set(observable.schedule.get().getDays().size());

        String[] arr = ll.getContext().getResources().getStringArray(R.array.weekdays);

        int index = 0;
        for(ScheduleTimetable.ScheduleTimetableDay d : observable.schedule.get().getDays()){
            ViewSingleLessonplanWeekdayBinding b = DataBindingUtil.inflate(inflater, R.layout.view_single_lessonplan_weekday, ll, true);
            //b.setWidth(maxElementWidth);
            b.setText(arr[d.getDay()]);
            b.getRoot().getLayoutParams().width = maxElementWidth;

            b.getRoot().setPadding(0,0,0,0);
            if(maxElementCount > 1){
                b.getRoot().setPadding((int)convertDpToPixel(16, ll.getContext()), 0,0,0);
            }else{
                b.getRoot().setPadding(0,0,(int)convertDpToPixel(60, ll.getContext()),0);
            }

            b.invalidateAll();
            index++;
        }

    }

    @BindingAdapter("setupLessons")
    public static void setupLessons(final GridLayout gl, final ActivityScheduleTimetableViewModel.Observable observable){
        if(observable == null) return;
        if(observable.schedule.get() == null) return;
        if(observable.schedule.get().getDays() == null || observable.schedule.get().getDays().size() <= 0) return;

        gl.removeAllViews();
        gl.setRowCount(observable.schedule.get().getHours().size());
        gl.setColumnCount(observable.schedule.get().getDays().size());
        LayoutInflater inflater = LayoutInflater.from(gl.getContext());


        //Calculate whole screen width
        DisplayMetrics metrics = gl.getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        //Calculate left space for lesson entries
        //number + hours + padding = around 120dp
        int leftWidth = width - (int)convertDpToPixel(56, gl.getContext());

        //Let's see how much elements will fit on the screen
        int baseElementWidth = (int)convertDpToPixel(220, gl.getContext());
        int maxElementCount = leftWidth/baseElementWidth;

        //Calculate width of each element to fill left space
        int maxElementWidth = leftWidth/maxElementCount;

        observable.itemsPerPage.set(maxElementCount);
        observable.maxItemWidth.set(maxElementWidth);
        observable.maxItemCount.set(observable.schedule.get().getDays().size());

        for(ScheduleTimetable.ScheduleTimetableDay d : observable.schedule.get().getDays()){
            if(d.getLessons().size() >= 0){

                /*
                * NEW CODE BASED ON ARRAY OF NULLS
                * EVERYTHING IS A NULL EXCEPT THE HOURS WHEN THERE IS A LESSON
                * CLEAN AND SIMPLE AS FUCK
                 */
                List<ScheduleTimetable.ScheduleTimetableLesson> arr = new ArrayList<>();
                for(int i = 0; i < observable.schedule.get().getHours().size(); i++){
                    arr.add(null);
                }

                for(ScheduleTimetable.ScheduleTimetableLesson l : d.getLessons()){
                    arr.set(l.getNumber()-1, l);
                }

                for(ScheduleTimetable.ScheduleTimetableLesson l : arr){
                    if(arr == null){
                        inflater.inflate(R.layout.view_single_lessonplan_placeholder, gl, true);
                        View v = gl.getChildAt(gl.getChildCount() - 1);

                        if (v != null) v.getLayoutParams().width = maxElementWidth;
                    }else{
                        ViewSingleLessonplanBinding b = DataBindingUtil.inflate(inflater, R.layout.view_single_lessonplan, gl, true);
                        b.setLesson(l);
                        b.getRoot().getLayoutParams().width = maxElementWidth;
                    }
                }

                //TODO: THIS IS OLD CODE, IT SHOULD BE DELETED AFTER TESTING THAT ONE ABOVE
                /*int index = 0;
                for(int i = 0; i < arrSize; i++){

                    //Draw items before first lesson
                    if(i == 0) {
                        for (int j = 1; j < arr.get(i).getNumber(); j++) {
                            inflater.inflate(R.layout.view_single_lessonplan_placeholder, gl, true);
                            View v = gl.getChildAt(gl.getChildCount() - 1);

                            if (v != null) v.getLayoutParams().width = maxElementWidth;
                            index++;
                        }
                    }

                    //Draw items related to lessons
                    System.out.println(arr.get(i).getNumber() + " DRAWING XD");
                    if(arr.get(i).getNumber() == index){
                        ViewSingleLessonplanBinding b = DataBindingUtil.inflate(inflater, R.layout.view_single_lessonplan, gl, true);
                        b.setLesson(arr.get(i));
                        b.getRoot().getLayoutParams().width = maxElementWidth;
                        index ++;
                    }else{
                        inflater.inflate(R.layout.view_single_lessonplan_placeholder, gl, true);
                        View v = gl.getChildAt(gl.getChildCount() - 1);

                        if (v != null) v.getLayoutParams().width = maxElementWidth;
                        index++;
                    }

                    //Draw items after last lesson
                    if(i == arrSize - 1){
                        for(int j = index; j < observable.schedule.get().getHours().size(); j++){
                            inflater.inflate(R.layout.view_single_lessonplan_placeholder, gl, true);
                            View v = gl.getChildAt(gl.getChildCount() - 1);

                            if (v != null) v.getLayoutParams().width = maxElementWidth;
                        }
                    }


                }*/
                /* END OF OLD SHITTY CODE! */

            }
        }
    }

    @BindingAdapter("android:scrollX")
    public static void scrollX(final View v, final int scroll){
        ValueAnimator va = ValueAnimator.ofInt(v.getScrollX(), scroll);
        va.setInterpolator(new FastOutSlowInInterpolator());
        va.setDuration(1000);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v.setScrollX((Integer)valueAnimator.getAnimatedValue());
            }
        });

        va.start();
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
