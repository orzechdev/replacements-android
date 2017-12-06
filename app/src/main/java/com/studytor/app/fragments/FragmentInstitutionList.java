package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.activities.ActivityInstitutionProfile;
import com.studytor.app.adapters.InstitutionRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionListBinding;
import com.studytor.app.globals.Global;
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.viewmodel.FragmentInstitutionListViewModel;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.viewmodel.ActivityMainViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentInstitutionList extends Fragment {

    private FragmentInstitutionListViewModel viewModel;
    private FragmentInstitutionListBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionListViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentInstitutionListViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_list, container, false);

        final FragmentInstitutionListViewModel.Observable observable = viewModel.getObservable();
        binding.setObservable(observable);
        binding.setViewModel(viewModel);

        viewModel.setup();

        setupObservablesForBinding();

        return binding.getRoot();
    }

    private void setupObservablesForBinding(){
        viewModel.getInstitutionList().observe(this, new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> singleInstitutions) {
                binding.getObservable().institutionList.set(singleInstitutions);
                binding.getObservable().isRefreshing.set(false);
            }
        });
    }

    @BindingAdapter("imageRes")
    public static void bindImage(ImageView view, int r) {
        view.setImageResource(r);
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(CircleImageView view, String url) {
        if(url != null && url.length() > 0) {
            System.out.println("Picasso painted this picture : " + url);
            Picasso.with(view.getContext()).load(url).into(view);
        }
    }

    @BindingAdapter("setupRecyclerView")
    public static void setupRecyclerView(final RecyclerView view, List<SingleInstitution> data){
        final List<SingleInstitution> list = data;
        if(list != null && list.size() > 0){

            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            view.setLayoutManager(layoutManager);
            InstitutionRecyclerViewAdapter adapter = new InstitutionRecyclerViewAdapter(list);
            view.setAdapter(adapter);

            //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
            ItemClickSupport.addTo(view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    InstitutionRecyclerViewAdapter tempAdapter = (InstitutionRecyclerViewAdapter) recyclerView.getAdapter();
                    Intent intent = new Intent(view.getContext(), ActivityInstitutionProfile.class);
                    intent.putExtra(ApplicationConstants.INTENT_INSTITUTION_ID, list.get(position).getId());
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });

        }
    }
}
