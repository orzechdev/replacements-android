package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studytor.app.R;
import com.studytor.app.adapters.ReplacementsListRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileReplacementsBinding;
import com.studytor.app.repositories.models.ReplacementsJson;
import com.studytor.app.repositories.models.SingleReplacementJson;
import com.studytor.app.viewmodel.FragmentInstitutionProfileReplacementsViewModel;

import java.util.List;

/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileReplacements extends Fragment{

    private FragmentInstitutionProfileReplacementsViewModel viewModel;
    private FragmentInstitutionProfileReplacementsBinding binding;

    private int institutionId;

    public void setup(int institutionId){
        this.institutionId = institutionId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileReplacementsViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_replacements, container, false);

        viewModel.setup(institutionId);
        binding.setObservable(viewModel.getObservable());
        binding.setHandlers(viewModel.getHandlers());

        setupObservablesForBinding();

        return binding.getRoot();
    }

    private void setupObservablesForBinding(){
        viewModel.getReplacements().observe(this, new Observer<ReplacementsJson>() {
            @Override
            public void onChanged(@Nullable ReplacementsJson repls) {
                Log.i("FragInstProfRepls","Observer getReplacements");
                binding.getObservable().replacements.set(repls);
            }
        });
        viewModel.isRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.getObservable().isRefreshing.set(aBoolean);
                Log.i("FragInstProfRepls", "Observer isRefreshing");
                if(aBoolean != null)
                    Log.i("FragInstProfRepls", "Observer isRefreshing " + ((aBoolean)? "true" : "false"));
            }
        });
        viewModel.getSelectedDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String sDate) {
                binding.getObservable().selectedDate.set(sDate);
            }
        });
        viewModel.getDaysFromToday().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long days) {
                binding.getObservable().daysFromToday.set(days);
            }
        });
    }

    @BindingAdapter("setupRecyclerView")
    public static void setupRecyclerView(RecyclerView recyclerView, final ReplacementsJson repls){

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if(repls != null && repls.getReplacements() != null && repls.getReplacements().size() > 0){
            List<SingleReplacementJson> items = repls.getReplacements();
            //Display RecyclerView with institutions
            ReplacementsListRecyclerViewAdapter a = new ReplacementsListRecyclerViewAdapter(items);
            recyclerView.setAdapter(a);

        }
    }
}
