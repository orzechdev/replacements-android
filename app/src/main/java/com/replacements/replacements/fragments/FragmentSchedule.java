package com.replacements.replacements.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.replacements.replacements.R;
import com.replacements.replacements.activities.ActivityMain;
import com.replacements.replacements.databinding.FragmentScheduleBinding;
import com.replacements.replacements.viewmodel.ActivityMainViewModel;
import com.replacements.replacements.viewmodel.FragmentScheduleViewModel;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentSchedule extends LifecycleFragment {

    private FragmentScheduleViewModel viewModel;
    private FragmentScheduleBinding binding;

    public static final String ARG_POSITION = "test";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentScheduleViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentScheduleViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);

        final FragmentScheduleViewModel.FragmentScheduleObservable observable = viewModel.getObservable();
        binding.setObservable(observable);

        viewModel.setText("Here You will have Your plans.");

        return binding.getRoot();
    }
}
