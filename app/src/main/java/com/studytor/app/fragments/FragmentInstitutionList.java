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
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.viewmodel.FragmentInstitutionListViewModel;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.viewmodel.ActivityMainViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dawid on 19.07.2017.
 */

public class FragmentInstitutionList extends Fragment {

    public static Context context;

    private FragmentInstitutionListViewModel viewModel;
    private FragmentInstitutionListBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout errorMessage;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionListViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentInstitutionListViewModel(viewModel);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_list, container, false);

        final FragmentInstitutionListViewModel.Observable observable = viewModel.getObservable();
        binding.setObservable(observable);
        binding.setHandlers(viewModel.getHandlers());

        viewModel.setup(getActivity().getApplicationContext());

        //Pull to refresh implementation
        swipeRefreshLayout = (SwipeRefreshLayout) binding.getRoot().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.requestRepositoryUpdate();
            }
        });


        //Recycler view implementation
        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.recycler_view);
        errorMessage = (RelativeLayout) binding.getRoot().findViewById(R.id.error_container);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Always from web for now
        viewModel.getInstitutionList().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {

                List<SingleInstitution> items = institutions;

                if(items != null && items.size() > 0){

                    //Display RecyclerView with institutions
                    errorMessage.setVisibility(View.INVISIBLE);
                    mAdapter = new InstitutionRecyclerViewAdapter(items);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setVisibility(View.VISIBLE);


                }else{

                    //Display no data screen
                    recyclerView.setVisibility(View.GONE);
                    if(errorMessage.getVisibility() != View.VISIBLE){
                        errorMessage.setVisibility(View.VISIBLE);
                        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
                        aa.setDuration(1000);
                        aa.setFillAfter(true);
                        errorMessage.startAnimation(aa);
                    }

                }
                //Stop swipeRefreshLayout refreshing animation
                swipeRefreshLayout.setRefreshing(false);
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

    @BindingAdapter("picassoImage")
    public static void picassoImage(CircleImageView view, String url) {
        System.out.println("Picasso painted this picture : " + url);
        Picasso.with(context).load("http://"+url).into(view);
    }
}
