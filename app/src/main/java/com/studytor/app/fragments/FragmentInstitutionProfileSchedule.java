package com.studytor.app.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.studytor.app.BR;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.activities.ActivitySingleLessonplan;
import com.studytor.app.adapters.NewsListRecyclerViewAdapter;
import com.studytor.app.adapters.ScheduleEntryRepresentation;
import com.studytor.app.adapters.ScheduleSelectRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileScheduleBinding;
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.repositories.models.ScheduleSection;
import com.studytor.app.repositories.models.ScheduleUnit;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionProfileSchedulesViewModel;
import com.studytor.app.viewmodel.FragmentScheduleViewModel;

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

        if(sections != null && sections.size() > 0){
            linearLayout.removeAllViews();
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
                        //TODO: Start activity with params!
                        Intent intent = new Intent(flowLayout.getContext(), ActivitySingleLessonplan.class);
                        flowLayout.getContext().startActivity(intent);
                    }
                });

                flowLayout.addView(frame);
                flowLayout.postInvalidate();
            }
        }

    }

}
