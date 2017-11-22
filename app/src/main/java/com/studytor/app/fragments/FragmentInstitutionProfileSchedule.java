package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.adapters.NewsListRecyclerViewAdapter;
import com.studytor.app.adapters.ScheduleEntryRepresentation;
import com.studytor.app.adapters.ScheduleSelectRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileScheduleBinding;
import com.studytor.app.databinding.FragmentScheduleBinding;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionProfileSchedulesViewModel;
import com.studytor.app.viewmodel.FragmentScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileSchedule extends Fragment{

    private FragmentInstitutionProfileSchedulesViewModel viewModel;
    private FragmentInstitutionProfileScheduleBinding binding;

    private RecyclerView.Adapter mAdapter;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileSchedulesViewModel.class);


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_schedule, container, false);


        viewModel.setup(1);

        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.RecyclerView);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getObservable().schedule.observeForever(new Observer<Schedule>() {

            @Override
            public void onChanged(@Nullable Schedule schedule) {

                if(schedule != null && schedule.getLessonplans() != null && schedule.getLessonplans().size() > 0){
                    List<ScheduleEntryRepresentation> arr = new ArrayList<>();
                    for(ScheduleLessonplan s : schedule.getLessonplans()){
                        arr.add(new ScheduleEntryRepresentation(s.getName()));
                        System.out.println("NAME XD " + s.getName());
                    }
                    //Display RecyclerView with institutions
                    mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
                    recyclerView.setAdapter(mAdapter);
                }

            }

        });


        return binding.getRoot();

    }

}
