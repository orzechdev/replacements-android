package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.activities.ActivityInstitutionProfile;
import com.studytor.app.adapters.InstitutionRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionBinding;
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.viewmodel.FragmentInstitutionListViewModel;
import com.studytor.app.models.SingleInstitution;
import com.studytor.app.viewmodel.ActivityMainViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentInstitutionList extends Fragment {

    private FragmentInstitutionListViewModel viewModel;
    private FragmentInstitutionBinding binding;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionListViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentInstitutionListViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution, container, false);

        final FragmentInstitutionListViewModel.Observable observable = viewModel.getObservable();
        binding.setObservable(observable);

        viewModel.setup(getActivity().getApplicationContext());


        //Recycler view implementation
        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Always from web for now
        viewModel.getInstitutionList().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                List<SingleInstitution> items = institutions;
                if(items != null){
                    mAdapter = new InstitutionRecyclerViewAdapter(items);
                    recyclerView.setAdapter(mAdapter);
                }else{
                    Snackbar.make(getActivity().findViewById(R.id.main_content), getResources().getString(R.string.snackbar_no_internet_connection), 2000)
                            .setAction(getResources().getString(R.string.snackbar_try_again), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                }
            }
        });

        //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                InstitutionRecyclerViewAdapter tempAdapter = (InstitutionRecyclerViewAdapter) mAdapter;
                Toast.makeText(getContext(), "CLICKED " + String.valueOf(tempAdapter.getItemAt(position).getName()), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ActivityInstitutionProfile.class);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }

    @BindingAdapter("imageRes")
    public static void bindImage(ImageView view, int r) {
        view.setImageResource(r);
    }
}
