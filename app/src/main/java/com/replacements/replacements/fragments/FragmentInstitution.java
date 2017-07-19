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
import com.replacements.replacements.databinding.FragmentInstitutionBinding;
import com.replacements.replacements.viewmodel.ActivityMainViewModel;
import com.replacements.replacements.viewmodel.FragmentInstitutionViewModel;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentInstitution extends LifecycleFragment {

    private FragmentInstitutionViewModel viewModel;
    private FragmentInstitutionBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentInstitutionViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution, container, false);

        final FragmentInstitutionViewModel.FragmentInstitutionObservable observable = viewModel.getObservable();
        binding.setObservable(observable);

        viewModel.setText("Here You will have possibility to find Your school.");

        return binding.getRoot();
    }
}
