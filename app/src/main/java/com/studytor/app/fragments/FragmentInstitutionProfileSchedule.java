package com.studytor.app.fragments;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studytor.app.BR;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityScheduleTimetable;
import com.studytor.app.adapters.ScheduleEntryRepresentation;
import com.studytor.app.adapters.ScheduleSelectRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileScheduleBinding;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.interfaces.Functions;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleSection;
import com.studytor.app.repositories.models.ScheduleUnit;
import com.studytor.app.viewmodel.FragmentInstitutionProfileSchedulesViewModel;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileSchedule extends Fragment{

    private FragmentInstitutionProfileSchedulesViewModel viewModel;
    private FragmentInstitutionProfileScheduleBinding binding;

    int institutionId;


    public void setup(int institutionId){
        this.institutionId = institutionId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileSchedulesViewModel.class);


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_schedule, container, false);

        viewModel.setup(institutionId);

        binding.setObj(viewModel.getObservable());
        binding.setViewModel(viewModel);


        return binding.getRoot();
    }

    @BindingAdapter("setupRecycleView")
    public static void setupRecyclerView(final RecyclerView recyclerView, final Schedule schedule){

        if(schedule != null && schedule.getLessonplans() != null && schedule.getLessonplans().size() > 0) {

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);

            List<ScheduleEntryRepresentation> arr = new ArrayList<>();

            //Display RecyclerView
            ScheduleSelectRecyclerViewAdapter a = new ScheduleSelectRecyclerViewAdapter(schedule.getLessonplans());
            recyclerView.setAdapter(a);

        }

    }

    @BindingAdapter("bindImage")
    public static void bindImage(final ImageView imageView, final int resId){

        imageView.setImageResource(resId);
        imageView.invalidate();

    }

    @BindingAdapter("buildScheduleSections")
    public static void buildScheduleSections(final LinearLayout linearLayout, final List<ScheduleSection> sections){

        if(sections != null && sections.size() > 0 && linearLayout.getChildCount() <= 0){
            linearLayout.removeAllViews();

            View v = new View(linearLayout.getContext());
            linearLayout.addView(v);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,0,0, (int)Functions.convertDpToPixel(16, linearLayout.getContext()));
            lp.height = (int)Functions.convertDpToPixel(1, linearLayout.getContext());
            v.setLayoutParams(lp);
            v.setBackgroundColor(linearLayout.getContext().getResources().getColor(R.color.divider_item_decoration));

            for(ScheduleSection s : sections){

                LayoutInflater layoutInflater = LayoutInflater.from(linearLayout.getContext());
                ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.view_schedule_list_item_section, linearLayout, false);



                binding.setVariable(BR.section, s);



                linearLayout.addView(binding.getRoot());
            }
            linearLayout.invalidate();

        }

    }

    @BindingAdapter("buildScheduleUnits")
    public static void buildScheduleUnits(final FlowLayout flowLayout, final List<ScheduleUnit> scheduleUnits){

        if(scheduleUnits != null && scheduleUnits.size() > 0){
            flowLayout.removeAllViews();
            for(final ScheduleUnit u : scheduleUnits){
                FrameLayout frame = new FrameLayout(flowLayout.getContext());

                TextView temp = new TextView(new ContextThemeWrapper(flowLayout.getContext(), R.style.ScheduleListBadge), null, 0);
                temp.setText(u.getName());

                frame.addView(temp);

                //Apply frame padding
                Resources r = flowLayout.getResources();
                frame.setPadding(
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, r.getDisplayMetrics()),
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics()),
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, r.getDisplayMetrics()),
                        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics())
                );

                frame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(flowLayout.getContext(), ActivityScheduleTimetable.class);
                        intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_URL, u.getUrl());
                        intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_NAME, u.getName());
                        flowLayout.getContext().startActivity(intent);
                    }
                });

                flowLayout.addView(frame);
                flowLayout.postInvalidate();
            }
        }

    }

    @BindingAdapter("expand")
    public static void expand(final LinearLayout ll, final int visiblity){
        if(visiblity == View.VISIBLE){
            expand(ll);
        }else{
            collapse(ll);
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

}
