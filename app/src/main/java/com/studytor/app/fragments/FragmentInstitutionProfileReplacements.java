package com.studytor.app.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studytor.app.R;
import com.studytor.app.adapters.ReplacementsListRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileReplacementsBinding;
import com.studytor.app.models.ReplacementTask;
import com.studytor.app.viewmodel.FragmentInstitutionProfileReplacementsViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileReplacements extends Fragment{

    private FragmentInstitutionProfileReplacementsViewModel viewModel;
    private FragmentInstitutionProfileReplacementsBinding binding;

    private RecyclerView recyclerView;
    private NestedScrollView nestedScroll;
    private RecyclerView.Adapter mAdapter;
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

        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.recycler_view);
        nestedScroll = (NestedScrollView) binding.getRoot().findViewById(R.id.nestedScroll);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<ReplacementTask> items = new ArrayList<>();
        items.add(new ReplacementTask(1,"1","1","repl 1",1,1));
        items.add(new ReplacementTask(2,"2","2","repl 2",2,2));
        items.add(new ReplacementTask(3,"3","3","repl 3",3,3));

        //Display RecyclerView with institutions
        mAdapter = new ReplacementsListRecyclerViewAdapter(items);
        recyclerView.setAdapter(mAdapter);

        return binding.getRoot();
    }

}
