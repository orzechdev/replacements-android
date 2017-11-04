package com.studytor.app.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.databinding.FragmentScheduleBinding;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentScheduleViewModel;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentSchedule extends Fragment {

    private FragmentScheduleViewModel viewModel;
    private FragmentScheduleBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentScheduleViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentScheduleViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);

        final FragmentScheduleViewModel.Observable observable = viewModel.getObservable();
        binding.setObservable(observable);

        viewModel.setup();

        return binding.getRoot();
    }
}
